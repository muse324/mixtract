package net.muse.mixtract.data;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;
import net.muse.app.Mixtract;
import net.muse.data.CMXImporter;
import net.muse.data.CMXNoteHandler;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.Harmony;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.mixtract.data.curve.ArticulationCurve;
import net.muse.mixtract.data.curve.DynamicsCurve;
import net.muse.mixtract.data.curve.PhraseCurve;
import net.muse.mixtract.data.curve.TempoCurve;

/**
 * <h1>MXTuneData</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose <address>@ CrestMuse Project,
 *         JST</address> <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/09/20
 */
public class MXTuneData extends TuneData {

	private static final String SCOREDATA_FILENAME = "score.dat";
	/** 構造データのファイル名（default: structure.dat) */
	private static final String STRUCTURE_FILENAME = "structure.dat";
	private static int durationOffset = 100;
	/** ユーザにより指定されるプライマリフレーズライン */
	private PrimaryPhraseSequence groupSequence = null;

	public MXTuneData(File in, File out) throws IOException {
		super(in, out);
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
	 * @param val
	 *            the durationOffset to set
	 */
	public static final void setDurationOffset(int val) {
		MXTuneData.durationOffset = val;
		System.out.printf("duration offset = %d\n", val);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#addGroupArrayList(net.muse.data.Group)
	 */
	@Override public void addMiscGroupList(Group group) {
		// 重複するグループがあれば処理中断
		for (Group g : getMiscGroup()) {
			if (g.nearlyEquals(group))
				return;
			// TODO 複数声部に未対応
		}
		// TODO 未検証
		if (group instanceof MXGroup)
			createPrimaryPhraseSequence((MXGroup) group);
		// ----------------------------------
		getMiscGroup().add(group);
	}

	public MXNoteData getLastNote(int partIndex) {
		return (MXNoteData) getRootGroup(partIndex).getEndNote();
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

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#writeOriginalData()
	 */
	public void writeOriginalData() throws IOException {
		// -------- create structure data ---------------------
		writeStructureData();
		// -------- create expressed SMF ---------------------
		writeSMF();
	}

	/**
	 * @throws IOException
	 */
	@Override public void writeScoreData() throws IOException {
		// -------- import cmx files --------------------------
		importCMXFilesToProjectDirectory();

		// if (xml() == null)
		// return;
		File fp = new File(out(), SCOREDATA_FILENAME);
		fp.createNewFile();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		// out.format("cmx=%s\n", inputFile.getName());
		out.format("cmx=%s\n", getCMXFilename());
		out.format("str=%s\n", STRUCTURE_FILENAME);
		out.format("bpm=%s\n", getBPM().toString().subSequence(1, getBPM()
				.toString().length() - 1));
		for (Group g : getRootGroup()) {
			writeNoteData(out, (MXGroup) g);
		}
		// for (int i = 0; i < getPartwiseNotelist().size(); i++)
		// writeNoteData(out, (MXNoteData) getPartwiseNotelist().get(i));
		out.close();
	}

	private void writeNoteData(PrintWriter out, MXGroup g) {
		if (g == null)
			return;
		if (!g.hasChild())
			writeNoteData(out, (MXNoteData) g.getBeginNote());
		writeNoteData(out, g.getChildFormerGroup());
		writeNoteData(out, g.getChildLatterGroup());
	}

	private String getCMXFilename() throws IOException {
		if (xml() != null)
			return xml().getFileName();
		if (scc() != null) {
			if (scc().getFileName() != null)
				return scc().getFileName();
			try {
				scc().writefile(new File(out(), "scc.xml"));
				return "scc.xml";
			} catch (SAXException e) {
				e.printStackTrace();
			}
		}
		return "";
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

	@Deprecated PrimaryPhraseSequence getGroupSequence() {
		return groupSequence;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.data.TuneData#calculateExpressionParameters(net.muse.data.Group)
	 */
	@Override protected void calculateExpressionParameters(Group root) {
		assert root instanceof MXGroup;
		MXGroup g = (MXGroup) root;
		if (g.getTempoCurve().getParamlist().size() > 0)
			calculateHierarchicalParameters(g);
	}

	@Override protected void confirmOutputFileLocation() {
		if (!out().exists())
			out().mkdir();
		else
			dialogOutputLocation();
	}

	@Override protected CMXNoteHandler createCMXNoteHandler() {
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
	@Override protected void deleteGroup(Group target) {
		if (target == null)
			return;
		assert target instanceof MXGroup;
		MXGroup g = (MXGroup) target;
		deleteGroup(g.getChildFormerGroup());
		deleteGroup(g.getChildLatterGroup());
		target.getScoreNotelist().clear();
		// g.setScoreNotelist(target.getScoreNotelist());
		if (g.hasChild()) {
			g.getChildFormerGroup().getEndNote().setNext(g.getChildLatterGroup()
					.getBeginNote());
		}
		g.setChild(null, null);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#deleteHierarchicalGroup(net.muse.data.Group,
	 * net.muse.data.Group)
	 */
	@Override protected void deleteHierarchicalGroup(Group root, Group target) {
		if (root == null)
			return;
		if (!root.hasChild())
			return;
		assert root instanceof MXGroup;
		MXGroup r = (MXGroup) root;
		MXGroup f = r.getChildFormerGroup();
		MXGroup l = r.getChildLatterGroup();
		if (f.equals(target) || l.equals(target)) {
			// 子グループをすべて削除
			deleteHierarchicalGroup(f, f.getChildFormerGroup());
			deleteHierarchicalGroup(f, f.getChildLatterGroup());
			deleteHierarchicalGroup(l, l.getChildFormerGroup());
			deleteHierarchicalGroup(l, l.getChildLatterGroup());

			// 削除
			f.getEndNote().setNext(l.getBeginNote());
			r.setChild(null, null);
			return;
		}
		deleteHierarchicalGroup(f, target);
		deleteHierarchicalGroup(l, target);
	}

	@Override protected void initializeNoteEvents(Group group) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;
		if (group.hasChild()) {
			initializeNoteEvents(g.getChildFormerGroup().getBeginNote());
			initializeNoteEvents(g.getChildLatterGroup().getBeginNote());
		}
		initializeNoteEvents(group.getBeginNote());
	}

	@Override protected boolean isOriginalFileFormat() {
		return in().isDirectory();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneData#printGroupList(net.muse.data.Group)
	 */
	@Override protected void printGroupList(Group group) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;
		printGroupList(g.getChildFormerGroup());
		printGroupList(g.getChildLatterGroup());
		System.out.println(g);
	}

	@Override protected void readOriginalFile() throws IOException {
		butler().printConsole("reading original format...");
		super.readOriginalFile();
		String strfile = STRUCTURE_FILENAME;
		File[] files = in().listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().equals(SCOREDATA_FILENAME)) {
				String s = readScoreData(files[i]);
				if (s.length() == 0)
					strfile = s;
				continue;
			}
			if (files[i].getName().equals(strfile)) {
				readStructureData(files[i]);
				continue;
			}
			String fileType = butler().getInputFileType(files[i]);
			CMXImporter cmx = new CMXImporter(files[i], fileType, this) {
				@Override public void run() {
					// XMLならCMX形式でインポート
					if (fileType.equals("xml")) {
						readCMXFile();
					} else if (fileType.equals("midi") || fileType.equals("x-midi")) {
						// MIDIファイル
						readMIDIFile();
					}
					// 楽曲データに代入
					data().importCMXobjects(dev, xml, scc);
				}
			};
			cmx.run();
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

		final double startTime = group.getBeginNote().onset();
		final double endTime = group.getEndNote().offset();
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
	@Deprecated private void createPrimaryPhraseSequence(MXGroup group) {
		final PrimaryPhraseSequence seq = new PrimaryPhraseSequence(group);

		// 新規作成
		if (getMiscGroup().size() <= 0) {
			groupSequence = seq;
			return;
		}

		NoteData st = group.getBeginNote();
		NoteData ed = group.getEndNote();
		// 前後にgroup sequence がある場合
		if (st.hasPrevious() && st.previous().equals(groupSequence.end()
				.getGroup().getEndNote())) {
			groupSequence.end().setNext(seq);
			seq.setPrevious(groupSequence);
		} else if (ed.hasNext() && ed.next().equals(groupSequence.root()
				.getGroup().getBeginNote())) {
			groupSequence.root().setPrevious(seq);
			seq.setNext(groupSequence);
		}
	}

	private MXNoteData getNote(MXNoteData note, String id) {
		if (note == null)
			return null;
		if (note.id().equals(id))
			return note;
		if (note.hasParent())
			return getNote(note.parent(), id);
		MXNoteData n = getNote(note.next(), id);
		return (n != null) ? n : note;
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
	 * @param i
	 *            TODO
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
			fp = new File(inputDirectory(), getCMXFilename());
			if (fp.exists())
				FileUtils.copyFileToDirectory(fp, out(), true);
			if (dev() != null) {
				FileUtils.copyFileToDirectory(in(), out(), true);
			}
		}
	}

	protected MXGroup parseGroupInfo(List<MXGroup> glist, MXNoteData note,
			String name, int partNumber, String groupInfo) {
		MXGroup g = null;
		final int id = Integer.parseInt(name.substring(1));
		if (groupInfo.charAt(0) == '[') {
			String group[] = groupInfo.split(" ");
			note = parseNotelist(note, group, 1, group.length, false);
			while (note.hasPrevious())
				note = note.previous();
			g = new MXGroup(id, partNumber, note, GroupType.is(name.charAt(0)));
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

	private MXNoteData parseNotelist(MXNoteData note, String[] args, int idx,
			int size, boolean fromPrevious) {
		if (idx == size)
			return note;
		final String s = args[idx];
		switch (s.charAt(0)) {
		case '(':
			note.setChild(new MXNoteData(++idx)); // dummy
			note = parseNotelist(note.child(), args, idx, size, fromPrevious);
			break;
		case ')':
			note = parseNotelist((note.hasParent()) ? note.parent() : note,
					args, ++idx, size, fromPrevious);
			break;
		case ',':
			fromPrevious = true;
			// note.setNext(new MXNoteData(++idx)); //dummy
			// if (note.hasParent())
			// note.next().setPrevious(note.parent(), false);
			note = parseNotelist(note, args, ++idx, size, fromPrevious);
			break;
		case 'n':
			MXNoteData n = null;
			for (NoteData d : getTempralNotelist()) {
				n = (MXNoteData) d;
				if (n.id().equals(s))
					break;
			}
			if (!fromPrevious && n != null)
				note = n;
			else {
				note.setNext(n);
				fromPrevious = false;
			}
			note = parseNotelist(n, args, ++idx, size, fromPrevious);
			break;
		default:
			note = parseNotelist(note, args, ++idx, size, fromPrevious);
		}
		return note;

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
		int xmlPartNumber = Integer.parseInt(getValue(keys, 5));
		int measureNumber = Integer.parseInt(getValue(keys, 6));
		int xmlVoice = Integer.parseInt(getValue(keys, 7));
		int musePhony = 0;
		try {
			musePhony = Integer.parseInt(getValue(keys, 8));
		} catch (IllegalArgumentException e) {
			musePhony = xmlVoice;
		}

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

		MXNoteData nd = new MXNoteData(++idx, xmlPartNumber, onset, offset,
				noteName, rest, grace, tie, tval, beat);
		nd.setMeasureNumber(measureNumber);
		nd.setXMLVoice(xmlVoice);
		nd.setMusePhony(musePhony);
		nd.setVelocity(vel);
		nd.setFifths(fifths);
		nd.setChord(chordName);

		// if (pre == null || pre.partNumber() != partNumber) {
		// setPartwiseNotelist(partNumber, nd);
		// } else if (chord || preChord) {
		// nd.setPrevious(pre.previous(), false);
		// nd.setChild(pre);
		// } else {
		// pre.setNext(nd);
		// }
		setTempoListEndtime(nd.offset(), false);
		getTempralNotelist().add(nd);
		log().println(nd);
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
				File fp = new File(file.getParentFile(), xmlFilename);
				CMXImporter cmx = new CMXImporter(fp, xmlFilename, this);
				cmx.run();
				// readCMXFile(in.getAbsolutePath());
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
	protected void readStructureData(File file) {
		try {
			getRootGroup().clear();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str = null;
			List<MXGroup> glist = new ArrayList<MXGroup>();
			while ((str = in.readLine()) != null) {
				String item[] = str.split(";");
				String groupName = item[0]; // group name
				int partNumber = Integer.parseInt(item[1]);
				if (partNumber <= 0)
					partNumber = 1;
				MXNoteData note = null;
				String groupInfo = item[2];
				String topNoteName = item[3];
				String[] curvePoints = item[4].split(",");
				String[] dynCurveInfo = item[5].split(",");
				String[] tmpCurveInfo = item[6].split(",");
				String[] artCurveInfo = item[7].split(",");
				MXGroup g = parseGroupInfo(glist, note, groupName, partNumber,
						groupInfo);
				parseCurvePoints(curvePoints, 0, g.getDynamicsCurve());
				parseCurvePoints(curvePoints, 6, g.getTempoCurve());
				parseCurvePoints(curvePoints, 12, g.getArticulationCurve());
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

	private void parseCurvePoints(String[] list, int idx, PhraseCurve cv) {
		Point2D.Double st = new Point2D.Double(Double.valueOf(list[idx + 0]),
				Double.valueOf(list[idx + 1]));
		Point2D.Double tp = new Point2D.Double(Double.valueOf(list[idx + 2]),
				Double.valueOf(list[idx + 3]));
		Point2D.Double ed = new Point2D.Double(Double.valueOf(list[idx + 4]),
				Double.valueOf(list[idx + 5]));
		cv.setStart(st);
		cv.setTop(tp);
		cv.setEnd(ed);
	}

	@Override protected void setNoteScheduleEvent(final Group group) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;
		if (g.hasChild()) {
			setNoteScheduleEvent(g.getChildFormerGroup());
			setNoteScheduleEvent(g.getChildLatterGroup());
		} else {
			setNoteScheduleEvent(g.getBeginNote(), g.getBeginNote().onset(), g
					.getEndNote().offset());
		}
	}

	private Object writeCurvePointParam(MXGroup group) {
		if (group == null) {
			return "ERROR!";
		}
		String str = writeCurvePoint(group.getDynamicsCurve());
		str += ",";
		str += writeCurvePoint(group.getTempoCurve());
		str += ",";
		str += writeCurvePoint(group.getArticulationCurve());
		return str;
	}

	private String writeCurvePoint(PhraseCurve cv) {
		return String.format("%f,%f,%f,%f,%f,%f", cv.start().x, cv.start().y, cv
				.top().x, cv.top().y, cv.end().x, cv.end().y);
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

	protected void writeGroupStructureData(PrintWriter out, MXGroup group) {
		if (group == null)
			return;
		writeGroupStructureData(out, (MXGroup) group.getChildFormerGroup());
		writeGroupStructureData(out, (MXGroup) group.getChildLatterGroup());
		out.format("%s;%s;%s;%s\n", group, (group.hasTopNote()) ? group
				.getTopNote().id() : "null", writeCurvePointParam(group),
				writeCurveParam(group));
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
	 * {@link TuneData.STRUCTURE_FILENAME}で設定さらたファイル名を持つ構造データを出力します．
	 *
	 * @throws IOException
	 */
	protected void writeStructureData() throws IOException {
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

	@Override public void analyze(Group rootGroup) {
		if (rootGroup == null)
			return;
		assert rootGroup instanceof MXGroup;
		MXGroup root = (MXGroup) rootGroup;
		analyze(root.getChildFormerGroup());
		analyze(root.getChildLatterGroup());

		MXGroup g = null;
		for (Group group : getMiscGroup()) {
			if (root.nearlyEquals(group)) {
				g = (MXGroup) group;
				break;
			}
		}
		if (g != null)
			root.setChild(g.getChildFormerGroup(), g.getChildLatterGroup());
		getMiscGroup().remove(g);
	}
}
