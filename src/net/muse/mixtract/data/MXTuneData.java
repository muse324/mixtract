package net.muse.mixtract.data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;

import org.apache.commons.io.FileUtils;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;
import net.muse.app.Mixtract;
import net.muse.data.*;
import net.muse.mixtract.data.curve.*;

/**
 * <h1>MXTuneData</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>@ CrestMuse Project, JST</address>
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/09/20
 */
public class MXTuneData extends TuneData {

	private static final String SCOREDATA_FILENAME = "score.dat";

	private static final String STRUCTURE_FILENAME = "structure.dat";
	private static int durationOffset = 100;
	/** ユーザにより指定されるプライマリフレーズライン */
	private PrimaryPhraseSequence groupSequence = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Mixtract.setShowGUI(false);
		String str[] = {};
		Mixtract.main(str);
	}

	public static void setDefaultBPM(int t) {
		ApexInfo.setDefaultBPM(t);
		ArticulationCurve.setDefaultBPM(t);
		DynamicsCurve.setDefaultBPM(t);
		ExGTTMRules.setDefaultBPM(t);
		PhraseFeature.setDefaultBPM(t);
		MXNoteData.setDefaultBPM(t);
		PhraseCurve.setDefaultBPM(t);
		TempoCurve.setDefaultBPM(t);
		Mixtract.setDefaultBPM(t);
	}

	/**
	 * @param val the durationOffset to set
	 */
	public static final void setDurationOffset(int val) {
		MXTuneData.durationOffset = val;
		log.printf("duration offset = %d\n", val);
		System.out.printf("duration offset = %d\n", val);
	}

	private static void save(BufferedImage img, File f) throws IOException {
		if (!ImageIO.write(img, "PNG", f)) {
			throw new IOException("フォーマットが対象外");
		}
	}

	public MXTuneData(File in, File out) throws IOException,
			InvalidMidiDataException {
		super(in, out);

	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#addGroupArrayList(net.muse.data.Group)
	 */
	@Override
	public void addGroupArrayList(Group group) {
		// 重複するグループがあれば処理中断
		for (Group g : getMiscGroup()) {
			if (g.nearlyEquals(group))
				return;
			// TODO 複数声部に未対応
		}
		// TODO 未検証
		assert group instanceof MXGroup;
		createPrimaryPhraseSequence((MXGroup) group);
		// ----------------------------------
		getMiscGroup().add(group);
	}

	public NoteData getLastNote(int partIndex) {
		return getRootGroup(partIndex).getEndGroupNote();
	}

	public MXGroup getRootGroup(int partIndex) {
		assert super.getRootGroup(partIndex) instanceof MXGroup;
		return (MXGroup) super.getRootGroup(partIndex);
	}

	public void setBPM(int idx, int value) {
		super.setBPM(idx, value);
		if (getRootGroup() != null) {
			assert getRootGroup(0) instanceof MXGroup;
			MXGroup g = (MXGroup) getRootGroup(0);
			g.getTempoCurve().apply(this, g);
		}
	}

	@Override
	public void writefile() throws IOException {
		// 出力ファイル (またはフォルダ）の所在を確認する
		confirmOutputFileLocation();

		// -------- import cmx files --------------------------
		importCMXFilesToProjectDirectory();

		// -------- create score data -------------------------
		writeScoreData();

		// -------- create structure data ---------------------
		writeStructureData();

		// -------- create expressed SMF ---------------------
		writeSMF();

		// -------- create screen shot ---------------------
		// writeScreenShot();

		System.out.println("tempo curve list:");
		System.out.println(getTempoList());
	}

	/**
	 * @throws IOException
	 */
	@Override
	public void writeScoreData() throws IOException {
		if (xml() == null)
			return;
		File fp = new File(out(), SCOREDATA_FILENAME);
		fp.createNewFile();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		// out.format("cmx=%s\n", inputFile.getName());
		out.format("cmx=%s\n", xml().getFileName());
		out.format("str=%s\n", STRUCTURE_FILENAME);
		out.format("bpm=%s\n", getBPM().toString().subSequence(1, getBPM()
				.toString().length() - 1));
		for (int i = 0; i < getNotelist().size(); i++)
			writeNoteData(out, (MXNoteData) getNoteList(i));
		out.close();
	}

	public void writeScreenShot(Point position, Dimension size)
			throws AWTException, IOException {
		Robot robot = new Robot();
		Image img = robot.createScreenCapture(new Rectangle(position.x,
				position.y, size.width, size.height));
		File fp = new File(out(), "screenshot.png");
		save(createBufferedImage(img), fp);
	}

	/**
	 * create structure data
	 *
	 * @throws IOException
	 */
	public void writeTempfileCurveParameters() throws IOException {
		File fp = File.createTempFile("structure", null, out());
		fp.deleteOnExit();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		for (int i = 0; i < getRootGroup().size(); i++)
			writeGroupStructureData(out, (MXGroup) getRootGroup().get(i));
		out.close();

	}

	PrimaryPhraseSequence getGroupSequence() {
		return groupSequence;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.data.TuneData#calculateExpressionParameters(net.muse.data.Group)
	 */
	@Override
	protected void calculateExpressionParameters(Group root) {
		assert root instanceof MXGroup;
		MXGroup g = (MXGroup) root;
		if (g.getTempoCurve().getParamlist().size() > 0)
			calculateHierarchicalParameters(g);
	}

	@Override
	protected void confirmOutputFileLocation() {
		if (!out().exists())
			out().mkdir();
		else
			dialogOutputLocation();
	}

	@Override
	protected CMXNoteHandler createCMXNoteHandler() {
		return new CMXNoteHandler(this) {
			protected MXGroup createGroup(NoteData n, int i, GroupType type) {
				return new MXGroup(n, i, type);
			}

			protected NoteData createNoteData(MusicXMLWrapper.Note note,
					int partNumber, int idx, Integer bpm, int vel) {
				return new MXNoteData(note, partNumber, idx, bpm, vel);
			}

			protected NoteData createNoteData(SCCXMLWrapper.Note note,
					int partNumber, int idx, Integer bpm, int beat, int vel) {
				return new MXNoteData(note, partNumber, idx, bpm, beat, vel);
			}

			protected MXTuneData data() {
				return (MXTuneData) data;
			}
		};
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#deleteGroup(net.muse.data.Group)
	 */
	@Override
	protected void deleteGroup(Group target) {
		if (target == null)
			return;
		assert target instanceof MXGroup;
		MXGroup g = (MXGroup) target;
		deleteGroup(g.getChildFormerGroup());
		deleteGroup(g.getChildLatterGroup());
		g.setScoreNotelist(target.getScoreNotelist());
		if (g.hasChild()) {
			g.getChildFormerGroup().getEndGroupNote().setNext(g
					.getChildLatterGroup().getBeginGroupNote());
		}
		g.setChild(null, null);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#deleteHierarchicalGroup(net.muse.data.Group,
	 * net.muse.data.Group)
	 */
	@Override
	protected void deleteHierarchicalGroup(Group target, Group structure) {
		if (structure == null)
			return;
		assert structure instanceof MXGroup;
		MXGroup str = (MXGroup) structure;
		deleteHierarchicalGroup(target, str.getChildFormerGroup());
		deleteHierarchicalGroup(target, str.getChildLatterGroup());
		if (str.equals(target)) {
			// 子グループをすべて削除
			deleteGroup(target);
			target.setType(GroupType.USER);
			// 親グループの再分析
			// analyzeStructure(target);
			return;
		}
	}

	@Override
	protected void initializeNoteEvents(Group group) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;
		if (group.hasChild()) {
			initializeNoteEvents(g.getChildFormerGroup().getBeginGroupNote());
			initializeNoteEvents(g.getChildLatterGroup().getBeginGroupNote());
		}
		initializeNoteEvents(group.getBeginGroupNote());
	}

	@Override
	protected boolean isOriginalFileFormat() {
		return in().isDirectory();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#printGroupList(net.muse.data.Group)
	 */
	@Override
	protected void printGroupList(Group group) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;
		printGroupList(g.getChildFormerGroup());
		printGroupList(g.getChildLatterGroup());
		System.out.println(g);
	}

	@Override
	protected void readOriginalFile() throws IOException {
		testPrintln("reading original format...");
		File[] files = in().listFiles();
		for (int i = 0; i < files.length; i++) {
			String strfile = STRUCTURE_FILENAME;
			if (files[i].getName().equals(SCOREDATA_FILENAME)) {
				String s = readScoreData(files[i]);
				if (s.length() == 0)
					strfile = s;
				continue;
			}
			if (files[i].getName().equals(strfile))
				readStructureData(files[i]);
		}

	}

	/**
	 * @param st
	 * @param ed
	 * @param curve
	 * @param list
	 */
	private void calculateHierarchicalParameters(final int st, final int ed,
			final PhraseCurve curve, final LinkedList<Double> list) {
		final double div = curve.getDivision();
		for (int i = st; i < ed; i++) {
			final int idx = (int) Math.round(div * (i - st) / (ed - st));
			if (idx < curve.getParamlist().size()) {
				final double noteT2 = curve.getParamlist().get(idx);
				final double logValue;
				switch (curve.getType()) {
				case ARTICULATION:
					logValue = list.get(i) * noteT2;
					break;
				default:
					logValue = list.get(i) + noteT2;
				}
				list.set(i, logValue);
			}
		}
	}

	private void calculateHierarchicalParameters(MXGroup group) {
		if (group == null)
			return;

		final double startTime = group.getBeginGroupNote().onset();
		final double endTime = group.getEndGroupNote().offset();
		final int st = (int) Math.round(MXGroupAnalyzer.rootDiv * startTime
				/ getTempoListEndtime());
		final int ed = (int) Math.round(MXGroupAnalyzer.rootDiv * endTime
				/ getTempoListEndtime());

		System.out.println(String.format("-- %s (st=%d - ed=%d)", group.name(),
				st, ed));

		calculateHierarchicalParameters(st, ed, group.getDynamicsCurve(),
				getDynamicsList());
		calculateHierarchicalParameters(st, ed, group.getTempoCurve(),
				getTempoList());
		calculateHierarchicalParameters(st, ed, group.getArticulationCurve(),
				getArticulationList());

		// final TempoCurve tempoCurve = group.getTempoCurve();
		// final int divT = tempoCurve.getDivision();// groupの分割数
		// final DynamicsCurve dynamicsCurve = group.getDynamicsCurve();
		// final int divD = dynamicsCurve.getDivision();// groupの分割数
		// for (int i = st; i < ed; i++) {
		// final int idxT = divT * (i - st) / (ed - st);
		// final int idxD = divD * (i - st) / (ed - st);
		// if (idxT < tempoCurve.getLogValueData().size()) {
		// final double noteT2 = tempoCurve.getLogValueData().get(idxT);
		// final double tempoLogValue = getTempoList().get(i) + noteT2;
		// getTempoList().set(i, tempoLogValue);
		// }
		// if (idxD < dynamicsCurve.getLogValueData().size()) {
		// final double noteD2 = dynamicsCurve.getLogValueData().get(idxD);
		// final double dynamicsLogValue = getDynamicsList().get(i) + noteD2;
		// getDynamicsList().set(i, dynamicsLogValue);
		// }
		// }

		calculateHierarchicalParameters((MXGroup) group.getChildFormerGroup());
		calculateHierarchicalParameters((MXGroup) group.getChildLatterGroup());
	}

	private BufferedImage createBufferedImage(Image img) {
		BufferedImage bimg = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics g = bimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
	}

	/**
	 * TODO 未検証
	 * <p>
	 * グループ構造の基礎となるプライマリフレーズラインを構成します。
	 * <ul>
	 * <li>g1の最終音とg2の開始音は連続しており、重複や入れ子を許さない
	 * </ul>
	 *
	 * @param group
	 */
	private void createPrimaryPhraseSequence(MXGroup group) {
		final PrimaryPhraseSequence seq = new PrimaryPhraseSequence(group);

		// 新規作成
		if (getMiscGroup().size() <= 0) {
			groupSequence = seq;
			return;
		}

		NoteData st = group.getBeginGroupNote();
		NoteData ed = group.getEndGroupNote();
		// 前後にgroup sequence がある場合
		if (st.hasPrevious() && st.previous().equals(groupSequence.end()
				.getGroup().getEndGroupNote())) {
			groupSequence.end().setNext(seq);
			seq.setPrevious(groupSequence);
		} else if (ed.hasNext() && ed.next().equals(groupSequence.root()
				.getGroup().getBeginGroupNote())) {
			groupSequence.root().setPrevious(seq);
			seq.setNext(groupSequence);
		}
	}

	private NoteData getNote(NoteData list, String id) {
		if (list == null)
			return null;
		if (list.id().equals(id))
			return list;
		NoteData n = getNote(list.parent(), id);
		if (n == null)
			n = getNote(list.next(), id);
		return n;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#getUniqueGroupIndex(net.muse.data.Group,
	 * java.util.ArrayList)
	 */
	protected void getUniqueGroupIndex(Group glist,
			ArrayList<Integer> idxlist) {
		if (glist == null)
			return;
		assert glist instanceof MXGroup;
		MXGroup g = (MXGroup) glist;
		getUniqueGroupIndex(g.getChildFormerGroup(), idxlist);
		getUniqueGroupIndex(g.getChildLatterGroup(), idxlist);
		if (!idxlist.contains(g.index()))
			idxlist.add(g.index());
	}

	/**
	 * @param keys
	 * @param i TODO
	 * @return
	 */
	private String getValue(String[] keys, int i) {
		return keys[i].split("=")[1];
	}

	/**
	 * @return
	 */
	private boolean hasGroupList() {
		return getRootGroup().size() > 0;
	}

	private void importCMXFilesToProjectDirectory() throws IOException {
		File fp = null;
		if (dev() != null || xml() != null) {
			fp = new File(inputDirectory(), xml().getFileName());
			if (fp.exists())
				FileUtils.copyFileToDirectory(fp, out(), true);
			if (dev() != null) {
				FileUtils.copyFileToDirectory(in(), out(), true);
			}
		}
	}

	private MXGroup parseGroupInfo(List<MXGroup> glist, NoteData list,
			String name, int partNumber, String groupInfo) {
		MXGroup g = null;
		final int id = Integer.parseInt(name.substring(1));
		if (groupInfo.charAt(0) == '[') {
			String group[] = groupInfo.split(" ");
			parseGroupNotelist(list, group, 1, group.length);
			g = new MXGroup(id, partNumber, list, GroupType.is(name.charAt(0)));
			glist.add(g);
			if (!hasGroupList())
				setGrouplist(partNumber, g);
		} else {
			String[] group = groupInfo.split(",");
			MXGroup g1 = null, g2 = null;
			for (MXGroup root : glist) {
				if (root.name().equals(group[0])) {
					g1 = root;
					continue;
				}
				if (root.name().equals(group[1])) {
					g2 = root;
					continue;
				}
			}
			if (g1 != null && g2 != null) {
				g = new MXGroup(g1, g2, name, partNumber);
				if (g1.equals(getRootGroup(partNumber - 1)))
					setGrouplist(partNumber - 1, g);
				glist.remove(g1);
				glist.remove(g2);
				glist.add(g);
			}
		}
		return g;
	}

	private void parseGroupNotelist(NoteData list, String[] args, int idx,
			int size) {
		if (idx == size)
			return;
		final String s = args[idx];
		switch (s.charAt(0)) {
		case '(':
			list.setChild(new GroupNote());
			parseGroupNotelist(list.child(), args, ++idx, size);
			break;
		case ')':
			parseGroupNotelist(list.parent(), args, ++idx, size);
			break;
		case ',':
			list.setNext(new GroupNote());
			if (list.hasParent())
				list.next().setParent(list.parent(), false);
			parseGroupNotelist(list.next(), args, ++idx, size);
			break;
		case 'n':
			NoteData n = null;
			for (int i = 0; i < getNotelist().size(); i++) {
				n = getNote(getNoteList(i), s);
				if (n != null)
					break;
			}
			list = n;
			parseGroupNotelist(list, args, ++idx, size);
			break;
		default:
			parseGroupNotelist(list, args, ++idx, size);
		}
	}

	/**
	 * @param curveInfo
	 * @param curve
	 */
	private void parsePhraseProfile(String[] curveInfo, PhraseCurve curve) {
		ArrayList<Double> val = curve.getParamlist();
		val.clear();
		for (String s : curveInfo) {
			if (s.equals("EOF"))
				break;
			val.add(Double.parseDouble(s));
		}
		curve.setDivision(val.size());
		return;
	}

	private void readNoteData(int idx, BufferedReader in, String str,
			NoteData pre, boolean preChord) throws IOException {
		if (str == null)
			return;
		String[] val = str.split(":");
		String[] type = val[1].split("/");
		String[] keys = type[0].split(", ");

		// score note information
		int onset = Integer.parseInt(getValue(keys, 1));
		String noteName = getValue(keys, 2);
		double beat = Double.parseDouble(getValue(keys, 3));
		int tval = Integer.parseInt(getValue(keys, 4));
		int partNumber = Integer.parseInt(getValue(keys, 5));
		int measureNumber = Integer.parseInt(getValue(keys, 6));
		int voice = Integer.parseInt(getValue(keys, 7));

		// rendering information
		keys = type[1].split(", ");
		int offset = Integer.parseInt(getValue(keys, 0));
		int vel = Integer.parseInt(getValue(keys, 1));
		boolean rest = Boolean.parseBoolean(getValue(keys, 2));
		boolean chord = Boolean.parseBoolean(getValue(keys, 3));
		boolean grace = Boolean.parseBoolean(getValue(keys, 4));
		boolean tie = Boolean.parseBoolean(getValue(keys, 5));
		int fifths = Integer.parseInt(getValue(keys, 6));
		Harmony chordName = Harmony.valueOf(getValue(keys, 7));

		MXNoteData nd = new MXNoteData(++idx, partNumber, onset, offset,
				noteName, rest, grace, tie, tval, beat);
		nd.setMeasureNumber(measureNumber);
		nd.setVoice(voice);
		nd.setVelocity(vel);
		nd.setFifths(fifths);
		nd.setChord(chordName);

		if (pre == null || pre.partNumber() != partNumber) {
			setNotelist(partNumber, nd);
		} else if (chord || preChord) {
			nd.setPrevious(pre.previous(), false);
			nd.setChild(pre);
		} else {
			pre.setNext(nd);
		}
		log.println(nd.toString());
		readNoteData(idx, in, in.readLine(), nd, chord);

	}

	private String readScoreData(File file) throws IOException {
		BufferedReader in = null;
		String strfile = "";
		try {
			in = new BufferedReader(new FileReader(file));

			// 1行目：インポートしたCMXファイルの読込（あれば）
			String str = in.readLine();
			String xmlFilename = str.split("=")[1];
			if (!str.startsWith("cmx"))
				throw new InvalidObjectException(xmlFilename);
			if (xmlFilename != null && xmlFilename.length() > 0) {
				readCMXFile(new File(file.getParentFile(), xmlFilename)
						.getAbsolutePath());
				// parseMusicXMLFile();
			}
			// 2行目：構造ファイル名取得
			strfile = in.readLine().split("=")[1];

			// 3行目：BPM情報
			String[] bpm = in.readLine().split("=")[1].split(",");
			for (int i = 0; i < bpm.length; i++) {
				getBPM().add(Integer.parseInt(bpm[i].trim()));
			}

			// 4行目以降：音符情報
			readNoteData(0, in, in.readLine(), null, false);
		} catch (InvalidObjectException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
		}
		return strfile;
	}

	/**
	 * <h2>書式</h2>
	 * <code>GroupName;PartNumber;Data;ApexNote;DynamicCurve;TempoCurve</code>
	 * <ul>
	 * <li><em>GroupName</em>
	 * <li><em>PartNumber</em> 0から始まるパート番号(int)
	 * <li><em>Data</em> ふたつの下位グループ，あるいは最下層の音符列
	 * <li><em>ApexNote</em> 頂点音をあらわす音符ID(MXNoteData)
	 * <li><em>DynamicCurve</em> ダイナミクス表現のフリーカーブパラメータ
	 * <li><em>TempoCurve</em> テンポ表現のフリーカーブパラメータ
	 * <li><em>ArticulationCurve</em> アーティキュレーションのフリーカーブパラメータ
	 * </ul>
	 *
	 * @param file
	 */
	private void readStructureData(File file) {
		try {
			getRootGroup().clear();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str = null;
			List<MXGroup> glist = new ArrayList<MXGroup>();
			while ((str = in.readLine()) != null) {
				String item[] = str.split(";");
				String name = item[0]; // group name
				int partNumber = Integer.parseInt(item[1]);
				if (partNumber <= 0)
					partNumber = 1;
				NoteData list = new NoteData(partNumber - 1);
				String groupInfo = item[2];
				String topNoteName = item[3];
				String[] dynCurveInfo = item[4].split(",");
				String[] tmpCurveInfo = item[5].split(",");
				String[] artCurveInfo = item[6].split(",");
				MXGroup g = parseGroupInfo(glist, list, name, partNumber,
						groupInfo);
				try {
					parsePhraseProfile(dynCurveInfo, g.getDynamicsCurve());
					parsePhraseProfile(tmpCurveInfo, g.getTempoCurve());
					parsePhraseProfile(artCurveInfo, g.getArticulationCurve());
				} catch (NullPointerException e) {
					System.err.println("Irregal file format");
				}
			}
			in.close();
			++hierarchicalGroupCount;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setNoteScheduleEvent(final Group group) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;
		if (g.hasChild()) {
			setNoteScheduleEvent(g.getChildFormerGroup());
			setNoteScheduleEvent(g.getChildLatterGroup());
		} else {
			setNoteScheduleEvent(g.getBeginGroupNote(), g.getEndGroupNote()
					.offset());
		}
	}

	private String writeCurveParam(MXGroup group) {
		if (group == null) {
			return "ERROR!";
		}

		String str = "";
		for (double v : group.getDynamicsCurve().getParamlist()) {
			str += (int) (v * 10000) / 10000. + ",";
		}
		str += "EOF;";
		for (double v : group.getTempoCurve().getParamlist()) {
			str += (int) (v * 10000) / 10000. + ",";
		}
		str += "EOF;";
		for (double v : group.getArticulationCurve().getParamlist()) {
			str += (int) (v * 10000) / 10000. + ",";
		}
		str += "EOF";
		System.out.println(str);
		return str;
	}

	private void writeGroupStructureData(PrintWriter out, MXGroup group) {
		if (group == null)
			return;
		writeGroupStructureData(out, (MXGroup) group.getChildFormerGroup());
		writeGroupStructureData(out, (MXGroup) group.getChildLatterGroup());
		out.format("%s;%s;%s\n", group, (group.hasTopNote()) ? group
				.getTopGroupNote().id() : "null", writeCurveParam(group));
	}

	private void writeNoteData(PrintWriter out, MXNoteData note) {
		if (note == null)
			return;
		writeNoteData(out, note.child());
		out.format("n%s:%s:%s\n", note.index(), note, (note
				.getXMLNote() != null) ? note.getXMLNote().getXPathExpression()
						: (note.getSCCNote() != null) ? note.getSCCNote()
								.getXPathExpression() : "null");
		writeNoteData(out, note.next());
	}

	/**
	 * @throws IOException
	 */
	private void writeStructureData() throws IOException {
		File fp = new File(out(), STRUCTURE_FILENAME);
		fp.createNewFile();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		// hierarchical groups
		for (int i = 0; i < getRootGroup().size(); i++)
			writeGroupStructureData(out, (MXGroup) getRootGroup().get(i));
		// non-hierarchical groups
		for (int i = 0; i < getMiscGroup().size(); i++)
			writeGroupStructureData(out, (MXGroup) getMiscGroup().get(i));
		out.close();
	}
}
