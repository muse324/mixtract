package net.muse.mixtract.data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;

import org.apache.commons.io.FileUtils;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
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

	public NoteData getLastNote(int partIndex) {
		return getRootGroup(partIndex).getEndGroupNote().getNote();
	}

	public void setBPM(int idx, int value) {
		super.setBPM(idx, value);
		if (getRootGroup() != null) {
			((MXGroup) getRootGroup(0)).getTempoCurve().apply(this,
					getRootGroup(0));
		}
	}

	@Override
	public void writefile() throws IOException, InvalidMidiDataException {
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
		File fp = new File(out(), SCOREDATA_FILENAME);
		fp.createNewFile();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		// out.format("cmx=%s\n", inputFile.getName());
		out.format("cmx=%s\n", xml.getFileName());
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

			protected NoteData createNoteData(Note note, int partNumber,
					int idx, Integer bpm, int vel) {
				return new MXNoteData(note, partNumber, idx, bpm, vel);
			}

			protected MXTuneData data() {
				return (MXTuneData) data;
			}
		};
	}

	@Override
	protected boolean isOriginalFileFormat() {
		return in().isDirectory();
	}

	@Override
	protected void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(createCMXNoteHandler());
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

	private BufferedImage createBufferedImage(Image img) {
		BufferedImage bimg = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics g = bimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
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
		if (dev != null || xml != null) {
			fp = new File(inputDirectory(), xml.getFileName());
			if (fp.exists())
				FileUtils.copyFileToDirectory(fp, out(), true);
			if (dev != null) {
				FileUtils.copyFileToDirectory(in(), out(), true);
			}
		}
	}

	private MXGroup parseGroupInfo(List<Group> glist, GroupNote list,
			String name, int partNumber, String groupInfo) {
		MXGroup g = null;
		final int id = Integer.parseInt(name.substring(1));
		if (groupInfo.charAt(0) == '[') {
			String group[] = groupInfo.split(" ");
			setGroupNotelist(list, group, 1, group.length);
			g = new MXGroup(id, partNumber, list, GroupType.is(name.charAt(0)));
			glist.add(g);
			if (!hasGroupList())
				setGrouplist(partNumber, g);
		} else {
			String[] group = groupInfo.split(",");
			Group g1 = null, g2 = null;
			for (Group root : glist) {
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
			List<Group> glist = new ArrayList<Group>();
			while ((str = in.readLine()) != null) {
				GroupNote list = new GroupNote();
				String item[] = str.split(";");
				String name = item[0]; // group name
				int partNumber = Integer.parseInt(item[1]);
				if (partNumber <= 0)
					partNumber = 1;
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

	private void setGroupNotelist(GroupNote list, String[] args, int idx,
			int size) {
		if (idx == size)
			return;
		final String s = args[idx];
		switch (s.charAt(0)) {
		case '(':
			list.setChild(new GroupNote());
			setGroupNotelist(list.child(), args, ++idx, size);
			break;
		case ')':
			setGroupNotelist(list.parent(), args, ++idx, size);
			break;
		case ',':
			list.setNext(new GroupNote());
			if (list.hasParent())
				list.next().setParent(list.parent(), false);
			setGroupNotelist(list.next(), args, ++idx, size);
			break;
		case 'n':
			NoteData n = null;
			for (int i = 0; i < getNotelist().size(); i++) {
				n = getNote(getNoteList(i), s);
				if (n != null)
					break;
			}
			try {
				list.setNote(n);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			setGroupNotelist(list, args, ++idx, size);
			break;
		default:
			setGroupNotelist(list, args, ++idx, size);
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
		writeGroupStructureData(out, group.getChildFormerGroup());
		writeGroupStructureData(out, group.getChildLatterGroup());
		out.format("%s;%s;%s\n", group, (group.hasTopNote()) ? group
				.getTopGroupNote().getNote().id() : "null", writeCurveParam(
						group));
	}

	private void writeNoteData(PrintWriter out, MXNoteData note) {
		if (note == null)
			return;
		writeNoteData(out, note.child());
		out.format("n%s:%s:%s\n", note.index(), note, (note
				.getXMLNote() != null) ? note.getXMLNote().getXPathExpression()
						: "null");
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
		for (int i = 0; i < getRootGroup().size(); i++)
			writeGroupStructureData(out, (MXGroup) getRootGroup().get(i));
		out.close();
	}

}
