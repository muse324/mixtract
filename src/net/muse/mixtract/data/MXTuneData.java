package net.muse.mixtract.data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.sound.midi.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import jp.crestmuse.cmx.filewrappers.*;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.*;
import net.muse.data.*;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.command.GroupAnalyzer;
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

	private static int MAXIMUM_MIDICHANNEL = 16;

	private static int durationOffset = 100;

	private static final String STRUCTURE_FILENAME = "structure.dat";

	private static final String SCOREDATA_FILENAME = "score.dat";

	private static boolean segmentGroupnoteLine = false;

	/** MusicXML */
	private MusicXMLWrapper xml;

	/** DeviationInstanceXML */
	private DeviationInstanceWrapper dev;

	/** 声部ごとの音符情報 */
	private ArrayList<NoteData> notelist = new ArrayList<NoteData>();

	/** 声部ごとのフレーズ構造(二分木) */
	private List<Group> rootGroup = new ArrayList<Group>();

	/** テンポ情報 */
	private ArrayList<Integer> bpmlist = new ArrayList<Integer>();

	/** 入力ファイル */
	private File inputFile;
	/** 出力ファイル */
	private File outputFile;
	/** MIDI出力用イベントリスト */
	private LinkedList<NoteScheduleEvent> noteScheduleEventList = new LinkedList<NoteScheduleEvent>();

	private double tempoListEndtime;

	/** 楽曲全体のダイナミクスカーブ */
	private final LinkedList<Double> dynamicsList;
	/** 楽曲全体のテンポカーブ */
	private final LinkedList<Double> tempoList;
	/** 楽曲全体のアーティキュレーションカーブ */
	private final LinkedList<Double> articulationList;

	/** 楽曲に含まれる非階層グループを格納するリスト */
	private final List<Group> groupArrayList;

	/** GUI等で選択されたグループ */
	private Group selectedGroup = null;

	/** ユーザにより指定されるプライマリフレーズライン */
	private PrimaryPhraseSequence groupSequence = null;

	private int hierarchicalGroupCount;
	private int[] midiProgram = new int[MAXIMUM_MIDICHANNEL];
	private double[] volume = new double[MAXIMUM_MIDICHANNEL];

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
		NoteData.setDefaultBPM(t);
		PhraseCurve.setDefaultBPM(t);
		TempoCurve.setDefaultBPM(t);
		Mixtract.setDefaultBPM(t);
	}

	/**
	 * @param durationOffset the durationOffset to set
	 */
	public static final void setDurationOffset(int durationOffset) {
		MXTuneData.durationOffset = durationOffset;
		log.printf("duration offset = %d\n", durationOffset);
		System.out.printf("duration offset = %d\n", durationOffset);
	}

	public static void setMaximumMIDIChannel(int num) {
		MAXIMUM_MIDICHANNEL = num;
	}

	/**
	 * @param segmentGroupnoteLine セットする segmentGroupnoteLine
	 */
	public static void setSegmentGroupnoteLine(boolean segmentGroupnoteLine) {
		MXTuneData.segmentGroupnoteLine = segmentGroupnoteLine;
	}

	private static void save(BufferedImage img, File f) throws IOException {
		if (!ImageIO.write(img, "PNG", f)) {
			throw new IOException("フォーマットが対象外");
		}
	}

	public MXTuneData(File in, File out) throws IOException,
			InvalidMidiDataException {
		groupArrayList = new ArrayList<Group>();
		dynamicsList = new LinkedList<Double>();
		tempoList = new LinkedList<Double>();
		articulationList = new LinkedList<Double>();
		this.inputFile = in;
		this.outputFile = out;
		readfile();
	}

	/**
	 * 非階層のグループを登録します。
	 *
	 * @param group
	 */
	public void addGroupArrayList(Group group) {
		// 重複するグループがあれば処理中断
		for (Group g : groupArrayList) {
			if (g.nearlyEquals(group))
				return;
			// TODO 複数声部に未対応
		}

		// ----------------------------------
		// TODO 未検証
		final PrimaryPhraseSequence seq = new PrimaryPhraseSequence(group);
		if (groupArrayList.size() <= 0) {
			groupSequence = seq;
		} else {
			NoteData st = group.getBeginGroupNote().getNote();
			NoteData ed = group.getEndGroupNote().getNote();
			// 前後にgroup sequence がある場合
			if (st.hasPrevious() && st.previous().equals(groupSequence.end()
					.getGroup().getEndGroupNote().getNote())) {
				groupSequence.end().setNext(seq);
				seq.setPrevious(groupSequence);
			} else if (ed.hasNext() && ed.next().equals(groupSequence.root()
					.getGroup().getBeginGroupNote().getNote())) {
				groupSequence.root().setPrevious(seq);
				seq.setNext(groupSequence);
			}
		}
		// ----------------------------------
		groupArrayList.add(group);
	}

	public void calculateHierarchicalParameters() {
		initializeParameters();
		for (final Group root : rootGroup) {
			// tempo
			tempoListEndtime = root.getTimeValue();
			if (root.getTempoCurve().getParamlist().size() > 0)
				calculateHierarchicalParameters(root);

			if (isDebug()) {
				System.out.println("------ parameter lists calculation ------");
				for (int i = 0; i < tempoList.size(); i++) {
					System.out.println(String.format(
							"%d: dyn2: %f, tempo2: %f, artc: %f", i,
							dynamicsList.get(i), tempoList.get(i),
							articulationList.get(i)));
				}
				System.out.println("---------------------------");
			}
		}
	}

	/**
	 * @param fc
	 */
	public void createNewOutputFile(JFileChooser fc) {
		File name = fc.getSelectedFile();
		if (!name.getName().endsWith(Mixtract.getProjectFileExtension()))
			outputFile = new File(fc.getCurrentDirectory(), name.getName()
					+ Mixtract.getProjectFileExtension());
		outputFile.mkdir();
	}

	/**
	 * @param group
	 */
	public void deleteGroupFromData(Group group) {
		// 非階層グループから削除
		if (groupArrayList.contains(group)) {
			groupArrayList.remove(group);
			return;
		}
		// 階層グループから削除
		for (Group g : getRootGroup()) {
			if (g.equals(group) && g.getType() == GroupType.NOTE) {
				// 最上階層が階層化されていない場合は削除できない
				if (!g.hasChild()) {
					JOptionPane.showConfirmDialog(null,
							"The whole note sequence can't be deleted.");
					return;
				}
			}
			deleteHierarchicalGroup(group, g);
		}
	}

	/**
	 * @return the articulationList
	 */
	public final LinkedList<Double> getArticulationList() {
		return articulationList;
	}

	public ArrayList<Integer> getBPM() {
		return bpmlist;
	}

	/**
	 * @return dynamicsList2
	 */
	public LinkedList<Double> getDynamicsList() {
		return dynamicsList;
	}

	/**
	 * @return group list 楽曲に含まれるすべてのグループリスト
	 */
	public List<Group> getGroupArrayList() {
		return groupArrayList;
	}

	/**
	 * @return the groupSequence
	 */
	public PrimaryPhraseSequence getGroupSequence() {
		return groupSequence;
	}

	/**
	 * @return inputFilename
	 */
	public String getInputFilename() {
		return inputFile.getName();
	}

	public NoteData getLastNote(int partIndex) {
		return getRootGroup(partIndex).getEndGroupNote().getNote();
	}

	public int[] getMIDIPrograms() {
		return midiProgram;
	}

	/**
	 * @return noteScheduleEventList
	 */
	public LinkedList<NoteScheduleEvent> getNoteScheduleEventList() {
		return noteScheduleEventList;
	}

	public final File getOutputFile() {
		return outputFile;
	}

	public List<Group> getRootGroup() {
		return rootGroup;
	}

	public Group getRootGroup(int partIndex) {
		return rootGroup.get(partIndex);
	}

	/**
	 * @return tempoList2
	 */
	public LinkedList<Double> getTempoList() {
		return tempoList;
	}

	/**
	 * @return
	 */
	public int getUniqueGroupIndex() {
		ArrayList<Integer> idxlist = new ArrayList<Integer>();
		for (Group g : groupArrayList) {
			if (!idxlist.contains(g.index()))
				idxlist.add(g.index());
		}
		getUniqueGroupIndex(getRootGroup(0), idxlist);
		for (int i = 0; i < idxlist.size(); i++) {
			if (!idxlist.contains(i))
				return i;
		}
		return idxlist.size();
	}

	public double[] getVolume() {
		return volume;
	}

	public void initializeNoteEvents() {
		for (int i = 0; i < getRootGroup().size(); i++)
			initializeNoteEvents(getRootGroup(i));
	}

	public void setBPM(int idx, int value) {
		bpmlist.set(idx, value);
		setDefaultBPM(value);
		if (getRootGroup() != null) {
			getRootGroup(0).getTempoCurve().apply(this, getRootGroup(0));
		}
	}

	/**
	 *
	 */
	public void setNoteScheduleEvent() {
		noteScheduleEventList.clear();
		if (selectedGroup != null)
			setNoteScheduleEvent(selectedGroup);
		else {
			for (int i = 0; i < rootGroup.size(); i++) {
				setNoteScheduleEvent(rootGroup.get(i));
			}
		}
		// log print
		Mixtract.log.println("---- noteScheduleEventList: ");
		for (NoteScheduleEvent ev : noteScheduleEventList)
			Mixtract.log.println(ev.toString());
		Mixtract.log.println("----------------------------");
	}

	/**
	 * @param group
	 */
	public void setSelectedGroup(Group group) {
		selectedGroup = group;
	}

	public void writefile() throws IOException, InvalidMidiDataException {
		// create mxt directory
		if (!outputFile.exists()) {
			outputFile.mkdir();
		} else {
			int res = JOptionPane.showConfirmDialog(null, outputFile
					.getAbsolutePath() + "\n is exist. Override?",
					"Project path confirmation",
					JOptionPane.YES_NO_CANCEL_OPTION);
			switch (res) {
			case JOptionPane.CANCEL_OPTION:
				testPrintln("cancelled.");
				return;
			case JOptionPane.NO_OPTION:
				JFileChooser fc = new JFileChooser(outputFile.getParentFile());
				res = fc.showSaveDialog(null);
				if (res == JOptionPane.NO_OPTION) {
					testPrintln("cancelled.");
					return;
				}
				createNewOutputFile(fc);
				break;
			}
		}
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
		System.out.println(tempoList);
	}

	public void writeScreenShot(Point position, Dimension size)
			throws AWTException, IOException {
		Robot robot = new Robot();
		Image img = robot.createScreenCapture(new Rectangle(position.x,
				position.y, size.width, size.height));
		File fp = new File(outputFile, "screenshot.png");
		save(createBufferedImage(img), fp);
	}

	/**
	 * create structure data
	 *
	 * @throws IOException
	 */
	public void writeTempfileCurveParameters() throws IOException {
		File fp = File.createTempFile("structure", null, outputFile);
		fp.deleteOnExit();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		for (int i = 0; i < rootGroup.size(); i++)
			writeGroupStructureData(out, rootGroup.get(i));
		out.close();

	}

	/**
	 * @throws IOException
	 */
	protected void importCMXFilesToProjectDirectory() throws IOException {
		File fp = null;
		if (dev != null || xml != null) {
			fp = new File(inputDirectory(), xml.getFileName());
			if (fp.exists())
				FileUtils.copyFileToDirectory(fp, outputFile, true);
			if (dev != null) {
				FileUtils.copyFileToDirectory(inputFile, outputFile, true);
			}
		}
	}

	protected String writeCurveParam(Group group) {
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

	protected void writeGroupStructureData(PrintWriter out, Group group) {
		if (group == null)
			return;
		writeGroupStructureData(out, group.getChildFormerGroup());
		writeGroupStructureData(out, group.getChildLatterGroup());
		out.format("%s;%s;%s\n", group, (group.hasTopNote()) ? group
				.getTopGroupNote().getNote().id() : "null", writeCurveParam(
						group));
	}

	protected void writeNoteData(PrintWriter out, NoteData note) {
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
	protected void writeScoreData() throws IOException {
		File fp = new File(outputFile, SCOREDATA_FILENAME);
		fp.createNewFile();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		// out.format("cmx=%s\n", inputFile.getName());
		out.format("cmx=%s\n", xml.getFileName());
		out.format("str=%s\n", STRUCTURE_FILENAME);
		out.format("bpm=%s\n", getBPM().toString().subSequence(1, getBPM()
				.toString().length() - 1));
		for (int i = 0; i < notelist.size(); i++)
			writeNoteData(out, notelist.get(i));
		out.close();
	}

	protected void writeSMF() throws IOException, InvalidMidiDataException {
		// SequenceとTrackの作成
		// 24tick=四分音符
		// TODO ticksperbeatの設定の仕方がよくわからん。2011.8.31
		// すべて480 でいいと思うのだけど、SMF出力すると0.5倍速になる。。
		Sequence sequence = new Sequence(Sequence.PPQ, Mixtract
				.getTicksPerBeat() * 2);
		Track track = sequence.createTrack();
		int offset = 100;
		/*
		 * テンポの設定 四分音符の長さをμsecで指定し3バイトに分解する
		 * 戻る
		 */
		MetaMessage mmsg = new MetaMessage();
		int tempo = getBPM().get(0);
		int l = 60 * 1000000 / tempo;
		mmsg.setMessage(0x51, new byte[] { (byte) (l / 65536), (byte) (l % 65536
				/ 256), (byte) (l % 256) }, 3);
		track.add(new MidiEvent(mmsg, 0));

		// set instrument
		for (int i = 0; i < midiProgram.length; i++) {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.PROGRAM_CHANGE, i, midiProgram[i],
					0);
			track.add(new MidiEvent(message, 0));
		}
		// MIDI イベントを書き込んで行く
		for (NoteScheduleEvent ev : noteScheduleEventList) {
			track.add(new MidiEvent((ShortMessage) ev.getMidiMessage(), ev
					.onset() + offset));
		}

		// write to file
		File fp = new File(outputFile, "express.mid");
		fp.createNewFile();
		MidiSystem.write(sequence, 0, fp);
	}

	/**
	 * @throws IOException
	 */
	protected void writeStructureData() throws IOException {
		File fp = new File(outputFile, STRUCTURE_FILENAME);
		fp.createNewFile();
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
				fp)));
		for (int i = 0; i < rootGroup.size(); i++)
			writeGroupStructureData(out, rootGroup.get(i));
		out.close();
	}

	/**
	 * @param note TODO
	 */
	private void addNoteScheduleEventList(NoteData note) {
		if (note == null)
			return;
		if (!noteScheduleEventList.contains(note.getNoteOn())) {
			addNoteScheduleEventList(note.getNoteOn());
			addNoteScheduleEventList(note.getNoteOff());
		}
		addNoteScheduleEventList(note.child());
	}

	private void addNoteScheduleEventList(NoteScheduleEvent note) {
		if (noteScheduleEventList.size() == 0) {
			noteScheduleEventList.add(note);
			return;
		}
		int idx = 0;
		for (NoteScheduleEvent ev : noteScheduleEventList) {
			if (note.onset() >= ev.onset()) {
				idx++;
			}
		}
		noteScheduleEventList.add(idx, note);
	}

	private void calculateHierarchicalParameters(Group group) {
		if (group == null)
			return;

		final double startTime = group.getBeginGroupNote().getNote().onset();
		final double endTime = group.getEndGroupNote().getNote().offset();
		final int st = (int) Math.round(GroupAnalyzer.rootDiv * startTime
				/ tempoListEndtime);
		final int ed = (int) Math.round(GroupAnalyzer.rootDiv * endTime
				/ tempoListEndtime);

		System.out.println(String.format("-- %s (st=%d - ed=%d)", group.name(),
				st, ed));

		calculateHierarchicalParameters(st, ed, group.getDynamicsCurve(),
				dynamicsList);
		calculateHierarchicalParameters(st, ed, group.getTempoCurve(),
				tempoList);
		calculateHierarchicalParameters(st, ed, group.getArticulationCurve(),
				articulationList);

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

		calculateHierarchicalParameters(group.getChildFormerGroup());
		calculateHierarchicalParameters(group.getChildLatterGroup());
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

	private BufferedImage createBufferedImage(Image img) {
		BufferedImage bimg = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics g = bimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
	}

	/**
	 * @param target
	 */
	private void deleteGroup(Group target) {
		if (target == null)
			return;
		deleteGroup(target.getChildFormerGroup());
		deleteGroup(target.getChildLatterGroup());
		target.setScoreNotelist(target.getScoreNotelist());
		if (target.hasChild()) {
			target.getChildFormerGroup().getEndGroupNote().setNext(target
					.getChildLatterGroup().getBeginGroupNote());
		}
		target.setChild(null, null);
	}

	/**
	 * 階層フレーズ中のグループを削除します。
	 * もし target が子階層を持っている場合，以下の処理を行います。
	 * <ol>
	 * <li>子グループをすべて削除
	 * <li>親グループを起点にし，再分析をかける
	 * </ol>
	 *
	 * @param target 削除するフレーズ
	 * @param structure 階層フレーズ
	 */
	private void deleteHierarchicalGroup(Group target, Group structure) {
		if (structure == null)
			return;

		deleteHierarchicalGroup(target, structure.getChildFormerGroup());
		deleteHierarchicalGroup(target, structure.getChildLatterGroup());
		if (structure.equals(target)) {
			// 子グループをすべて削除
			deleteGroup(target);
			target.setType(GroupType.USER);
			// 親グループの再分析
			// analyzeStructure(target);
			return;
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

	private NoteData getNoteList(int partIndex) {
		return notelist.get(partIndex);
	}

	/**
	 * @param rootGroup2
	 * @param idxlist
	 */
	private void getUniqueGroupIndex(Group glist, ArrayList<Integer> idxlist) {
		if (glist == null)
			return;
		getUniqueGroupIndex(glist.getChildFormerGroup(), idxlist);
		getUniqueGroupIndex(glist.getChildLatterGroup(), idxlist);
		if (!idxlist.contains(glist.index()))
			idxlist.add(glist.index());
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
		return rootGroup.size() > 0;
	}

	private void initializeNoteEvents(Group group) {
		if (group == null)
			return;
		if (group.hasChild()) {
			initializeNoteEvents(group.getChildFormerGroup()
					.getBeginGroupNote());
			initializeNoteEvents(group.getChildLatterGroup()
					.getBeginGroupNote());
		}
		initializeNoteEvents(group.getBeginGroupNote());
	}

	private void initializeNoteEvents(GroupNote gnote) {
		if (gnote == null)
			return;
		initializeNoteEvents(gnote.child());
		initializeNoteEvents(gnote.next());
		initializeNoteEvents(gnote.getNote());
	}

	private void initializeNoteEvents(NoteData nd) {
		if (nd == null)
			return;

		nd.setRealOnset(nd.onsetInMsec(getBPM().get(0)));
		nd.setRealOffset(nd.offsetInMsec(getBPM().get(0)));

		initializeNoteEvents(nd.child());
		initializeNoteEvents(nd.next());

	}

	/**
	 *
	 */
	private void initializeParameters() {
		tempoList.clear();
		dynamicsList.clear();
		articulationList.clear();
		for (int i = 0; i < GroupAnalyzer.rootDiv; i++) {
			tempoList.add(0.);
			dynamicsList.add(0.);
			articulationList.add(1.);
		}
	}

	/**
	 * @return
	 */
	private File inputDirectory() {
		return inputFile.getParentFile();
	}

	/**
	 *
	 */
	private void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(new CMXNoteHandler(this) {
			private NoteData cur = null;
			private Group primaryGrouplist = null;
			private int idx = 0;
			private KeyMode keyMode;
			private int fifths;
			/**
			 * TODO この定数値(120)はMusicXMLWrapperのデフォルト値。
			 * <sound>タグが存在しないとこの値が返ってくる
			 */
			private int currentBPM = 120;
			private int currentDefaultVelocity;

			@Override public void beginMeasure(Measure measure,
					MusicXMLWrapper wrapper) {
				super.beginMeasure(measure, wrapper);
				if (currentPartNumber == 1) {
					try {
						currentBPM = (currentBPM == measure.tempo())
								? currentBPM : measure.tempo();
						bpmlist.add(currentBPM);
						if (currentPartNumber == 1 && measure.number() == 1) {
							setDefaultBPM(currentBPM);
						}
						testPrintln("-----measure " + measure.number()
								+ ", tempo=" + currentBPM);
					} catch (NullPointerException e) {
					}
				}
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.data.CMXNoteHandler#beginPart(jp.crestmuse
			 * .cmx.filewrappers.MusicXMLWrapper.Part,
			 * jp.crestmuse.cmx.filewrappers.MusicXMLWrapper)
			 */
			@Override public void beginPart(Part part,
					MusicXMLWrapper wrapper) {
				super.beginPart(part, wrapper);
				int ch = part.midiChannel() - 1;
				midiProgram[ch] = part.midiProgram();
				// TODO 声部間velocityの調整 (volume[]) 決めうち。
				volume[ch] = (ch == 0) ? 1.0 : 0.7;
				currentDefaultVelocity = (int) (getDefaultVelocity()
						* volume[ch]);
				cur = null;
				testPrintln("=====part " + currentPartNumber);
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.data.CMXNoteHandler#endPart(jp.crestmuse.
			 * cmx.filewrappers.MusicXMLWrapper.Part,
			 * jp.crestmuse.cmx.filewrappers.MusicXMLWrapper)
			 */
			@Override public void endPart(Part part, MusicXMLWrapper wrapper) {
				Group g = new Group(data.getNoteList(partIndex), partIndex + 1,
						GroupType.NOTE);

				if (primaryGrouplist == null) {
					primaryGrouplist = g;
					data.setGrouplist(partIndex, g);
				} else if (segmentGroupnoteLine) {
					linkToPrimaryGroup(g.getBeginGroupNote(), primaryGrouplist
							.getBeginGroupNote());
				} else {
					data.setGrouplist(partIndex, g);
				}
				super.endPart(part, wrapper);
			}

			@Override public void processMusicData(MusicData md,
					MusicXMLWrapper wrapper) {
				if (md instanceof Note)
					readNoteData((Note) md);
				else if (md instanceof Attributes)
					readAttributes((Attributes) md);
				else if (md instanceof Direction)
					readDirections((Direction) md);
			}

			private void linkToPrimaryGroup(GroupNote note,
					GroupNote currentPrimaryNote) {
				if (note == null)
					return;
				while (currentPrimaryNote.hasNext() && note.getNote()
						.onset() >= currentPrimaryNote.next().getNote()
								.onset()) {
					currentPrimaryNote = currentPrimaryNote.next();
				}
				if (note.getNote().onset() == currentPrimaryNote.getNote()
						.onset()) {
					setChild(currentPrimaryNote, note);
					if (segmentGroupnoteLine) {
						if (note.hasPrevious())
							note.previous().setNext(null);
						note.setPrevious(null);
					}
				}
				linkToPrimaryGroup(note.next(), currentPrimaryNote);
			}

			private void readAttributes(Attributes attr) {
				keyMode = KeyMode.valueOf(attr.mode());
				fifths = attr.fifths();
			}

			private void readDirections(Direction md) {
				try {
					bpmlist.add((int) md.tempo());
				} catch (NullPointerException e) {
				}
				// TODO 他の Direction も実装する
			}

			/**
			 * @param md
			 */
			private void readNoteData(Note note) {
				NoteData nd = new NoteData(note, currentPartNumber, ++idx,
						getBPM().get(0), currentDefaultVelocity);
				nd.setKeyMode(keyMode, fifths);
				testPrintln(nd.toString());
				if (cur == null) {
					// 冒頭音
					cur = nd;
					data.setNotelist(partIndex, nd);
				} else if (note.chord()) {
					// 和音
					// 最高音へ移動
					while (cur.parent() != null)
						cur = cur.parent();
					cur.setParent(nd);
					nd.setPrevious(cur.previous());
					if (cur.equals(data.getNoteList(partIndex)))
						data.setNotelist(partIndex, nd);
					cur = nd;
				} else if (note.containsTieType("stop")) {
					// タイ
					cur.setOffset(nd.offset());
				} else {
					cur.setNext(nd);
					cur = nd;
				}
			}

			private void setChild(GroupNote parent, GroupNote note) {
				if (!parent.hasChild()) {
					parent.setChild(note);
					return;
				}
				setChild(parent.child(), note);
			}
		});
	}

	/**
	 * @param xmlFilename
	 */
	private void readCMXFile(String xmlFilename) {
		testPrintln("import CMX file");
		try {
			CMXFileWrapper cmx = CMXFileWrapper.readfile(xmlFilename);
			if (cmx instanceof DeviationInstanceWrapper) {
				dev = ((DeviationInstanceWrapper) cmx);
				xml = dev.getTargetMusicXML();
				// TODO deviation データを読み込む処理
			} else if (cmx instanceof MusicXMLWrapper) {
				xml = (MusicXMLWrapper) cmx;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * inputFilename で指定された楽曲ファイルを読み込みます。
	 * Mixtractが対応する楽曲ファイル形式は，
	 * <ul>
	 * <li>MusicXML(*.xml)
	 * <li>DeviationInstanceXML形式(*.xml)
	 * <li>Mixtractオリジナル形式(*.mxt: Mixtract.projectFileExtension()により規定)
	 * </ul>
	 * の3点です。
	 * MusicXML, DeviationInstanceXML形式が指定された場合，
	 * オリジナル形式への変換を行います。
	 *
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	private void readfile() throws IOException, InvalidMidiDataException {
		hierarchicalGroupCount = 0;
		// Mixtract独自形式の読込
		if (inputFile.isDirectory()) {
			readOriginalFile();
			outputFile = inputFile;
		} else {
			// CMX 形式からインポート
			readCMXFile(inputFile.getAbsolutePath());
			parseMusicXMLFile();
			writefile();
		}
		calculateHierarchicalParameters();
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

		NoteData nd = new NoteData(++idx, partNumber, onset, offset, noteName,
				rest, grace, tie, tval, beat);
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

	private void readOriginalFile() throws IOException {
		testPrintln("reading original format...");
		File[] files = inputFile.listFiles();
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
	 * @param curveInfo
	 * @param curve
	 */
	private void readPhraseProfile(String[] curveInfo, PhraseCurve curve) {
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
				bpmlist.add(Integer.parseInt(bpm[i].trim()));
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
	 * <li><em>ApexNote</em> 頂点音をあらわす音符ID(NoteData)
	 * <li><em>DynamicCurve</em> ダイナミクス表現のフリーカーブパラメータ
	 * <li><em>TempoCurve</em> テンポ表現のフリーカーブパラメータ
	 * </ul>
	 *
	 * @param file
	 */
	private void readStructureData(File file) {
		try {
			rootGroup.clear();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str = null;
			List<Group> glist = new ArrayList<Group>();
			while ((str = in.readLine()) != null) {
				GroupNote list = new GroupNote();
				String item[] = str.split(";");
				// group name
				String name = item[0];
				final int id = Integer.parseInt(name.substring(1));

				int partNumber = Integer.parseInt(item[1]);
				if (partNumber <= 0)
					partNumber = 1;
				String groupInfo = item[2];
				String topNoteName = item[3];
				String[] dynCurveInfo = item[4].split(",");
				String[] tmpCurveInfo = item[5].split(",");
				String[] artCurveInfo = item[6].split(",");
				Group g = null;
				if (groupInfo.charAt(0) == '[') {
					String group[] = groupInfo.split(" ");
					setGroupNotelist(list, group, 1, group.length);
					g = new Group(id, partNumber, list, GroupType.is(name
							.charAt(0)));
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
						g = new Group(g1, g2, name, partNumber);
						if (g1.equals(getRootGroup(partNumber - 1)))
							setGrouplist(partNumber - 1, g);
						glist.remove(g1);
						glist.remove(g2);
						glist.add(g);

					}
				}
				try {
					readPhraseProfile(dynCurveInfo, g.getDynamicsCurve());
					readPhraseProfile(tmpCurveInfo, g.getTempoCurve());
					readPhraseProfile(artCurveInfo, g.getArticulationCurve());
				} catch (NullPointerException e) {
					System.err.println("Irregal file format");
				}
			}
			++hierarchicalGroupCount;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param rootGroup セットする rootGroup
	 */
	private void setGrouplist(int partIndex, Group rootGroup) {
		if (partIndex >= this.rootGroup.size())
			this.rootGroup.add(rootGroup);
		else
			this.rootGroup.set(partIndex, rootGroup);
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
			for (int i = 0; i < notelist.size(); i++) {
				n = getNote(notelist.get(i), s);
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

	private void setNotelist(int partIndex, NoteData root) {
		if (partIndex >= this.notelist.size())
			notelist.add(root);
		else
			notelist.set(partIndex, root);
	}

	/**
	 * @param g
	 */
	private void setNoteScheduleEvent(final Group g) {
		if (g == null)
			return;
		if (g.hasChild()) {
			setNoteScheduleEvent(g.getChildFormerGroup());
			setNoteScheduleEvent(g.getChildLatterGroup());
		} else {
			setNoteScheduleEvent(g.getBeginGroupNote(), g.getEndGroupNote()
					.getNote().offset());
		}
	}

	private void setNoteScheduleEvent(GroupNote note, int endOffset) {
		if (note == null)
			return;
		if (note.getNote() == null)
			return;
		if (!note.getNote().rest()) {
			addNoteScheduleEventList(note.getNote());
		}
		setNoteScheduleEvent(note.child(), endOffset);
		setNoteScheduleEvent(note.next(), endOffset);
	}
}
