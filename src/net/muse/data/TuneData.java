package net.muse.data;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.sound.midi.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.*;
import net.muse.app.Mixtract;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.GroupAnalyzer;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.curve.PhraseCurve;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class TuneData extends MuseObject implements TuneDataController {
	private static int MAXIMUM_MIDICHANNEL = 16;
	public static boolean segmentGroupnoteLine = false;

	/** 入力ファイル */
	private File inputFile;
	/** 出力ファイル */
	private File outputFile;

	/** 声部ごとのフレーズ構造(二分木) */
	private List<Group> rootGroup = new ArrayList<Group>();
	/** MusicXML */
	protected MusicXMLWrapper xml;
	/** テンポ情報 */
	private ArrayList<Integer> bpmlist = new ArrayList<Integer>();

	/** 声部ごとの音符情報 */
	private ArrayList<NoteData> notelist = new ArrayList<NoteData>();
	public int[] midiProgram = new int[MAXIMUM_MIDICHANNEL];
	public double[] volume = new double[MAXIMUM_MIDICHANNEL];
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

	/** DeviationInstanceXML */
	protected DeviationInstanceWrapper dev;

	protected int hierarchicalGroupCount;

	private double tempoListEndtime;
	/** MIDI出力用イベントリスト */
	private LinkedList<NoteScheduleEvent> noteScheduleEventList = new LinkedList<NoteScheduleEvent>();

	public static void setMaximumMIDIChannel(int num) {
		MAXIMUM_MIDICHANNEL = num;
	}

	/** @param segmentGroupnoteLine セットする segmentGroupnoteLine */
	public static void setSegmentGroupnoteLine(boolean segmentGroupnoteLine) {
		TuneData.segmentGroupnoteLine = segmentGroupnoteLine;
	}

	public TuneData(File in, File out) throws IOException,
			InvalidMidiDataException {
		groupArrayList = new ArrayList<Group>();
		dynamicsList = new LinkedList<Double>();
		tempoList = new LinkedList<Double>();
		articulationList = new LinkedList<Double>();
		this.inputFile = in;
		this.outputFile = out;
		readfile();
	}

	public void calculateHierarchicalParameters() {
		initializeParameters();
		for (final Group root : getRootGroup()) {
			// tempo
			tempoListEndtime = root.getTimeValue();
			if (((MXGroup) root).getTempoCurve().getParamlist().size() > 0)
				calculateHierarchicalParameters((MXGroup) root);

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

	public void createNewOutputFile(JFileChooser fc) {
		File name = fc.getSelectedFile();
		if (!name.getName().endsWith(Mixtract.getProjectFileExtension()))
			outputFile = new File(fc.getCurrentDirectory(), name.getName()
					+ Mixtract.getProjectFileExtension());
		outputFile.mkdir();
	}

	/** @return the articulationList */
	public final LinkedList<Double> getArticulationList() {
		return articulationList;
	}

	public ArrayList<Integer> getBPM() {
		return bpmlist;
	}

	/** @return dynamicsList2 */
	public LinkedList<Double> getDynamicsList() {
		return dynamicsList;
	}

	/** @return group list 楽曲に含まれるすべてのグループリスト */
	public List<Group> getGroupArrayList() {
		return groupArrayList;
	}

	/** @return inputFilename */
	public String getInputFilename() {
		return inputFile.getName();
	}

	public ArrayList<NoteData> getNotelist() {
		return notelist;
	}

	public NoteData getNoteList(int partIndex) {
		return notelist.get(partIndex);
	}

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
		return getRootGroup().get(partIndex);
	}

	/** @return tempoList2 */
	public LinkedList<Double> getTempoList() {
		return tempoList;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneDataController#readfile()
	 */
	public void readfile() throws IOException, InvalidMidiDataException {
		// システム独自形式の読込
		if (isOriginalFileFormat()) {
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

	public void setGrouplist(int partIndex, Group rootGroup) {
		if (partIndex >= this.getRootGroup().size())
			this.getRootGroup().add(rootGroup);
		else
			this.getRootGroup().set(partIndex, rootGroup);
	}

	public void setNotelist(int partIndex, NoteData root) {
		if (partIndex >= this.notelist.size())
			notelist.add(root);
		else
			notelist.set(partIndex, root);
	}

	public void setNoteScheduleEvent() {
		noteScheduleEventList.clear();
		if (selectedGroup != null)
			setNoteScheduleEvent(selectedGroup);
		else {
			for (int i = 0; i < getRootGroup().size(); i++) {
				setNoteScheduleEvent(getRootGroup().get(i));
			}
		}
		// log print
		Mixtract.log.println("---- noteScheduleEventList: ");
		for (NoteScheduleEvent ev : noteScheduleEventList)
			Mixtract.log.println(ev.toString());
		Mixtract.log.println("----------------------------");
	}

	/** @param group */
	public void setSelectedGroup(Group group) {
		selectedGroup = group;
	}

	@Override
	public void writefile() throws IOException, InvalidMidiDataException {
		// 出力ファイル (またはフォルダ）の所在を確認する
		confirmOutputFileLocation();

		// -------- create score data -------------------------
		writeScoreData();

		// -------- create expressed SMF ---------------------
		writeSMF();

		System.out.println("tempo curve list:");
		System.out.println(getTempoList());
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneDataController#writeScoreData()
	 */
	@Override
	public void writeScoreData() throws IOException {
		throw new NotImplementedException();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneDataController#writeSMF()
	 */
	public void writeSMF() throws IOException, InvalidMidiDataException {
		// SequenceとTrackの作成
		// 24tick=四分音符
		// TODO ticksperbeatの設定の仕方がよくわからん。2011.8.31
		// すべて480 でいいと思うのだけど、SMF出力すると0.5倍速になる。。
		Sequence sequence = new Sequence(Sequence.PPQ, getTicksPerBeat() * 2);
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
		// MIDI イベントを書き込んでいく
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
	 * 出力先を確認します。
	 * <p>
	 * {@code outputFile} が存在しなければ、出力先を求めるダイアログが表示されます。
	 */
	protected void confirmOutputFileLocation() {
		if (outputFile.exists())
			return;
		dialogOutputLocation();
	}

	protected CMXNoteHandler createCMXNoteHandler() {
		return new CMXNoteHandler(this);
	}

	/** 出力先をダイアログ形式でユーザに指定させます。 */
	protected void dialogOutputLocation() {
		int res = JOptionPane.showConfirmDialog(null, outputFile
				.getAbsolutePath() + "\n is exist. Override?",
				"Project path confirmation", JOptionPane.YES_NO_CANCEL_OPTION);
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
			// 出力ファイル(orフォルダ)を作成する
			createNewOutputFile(fc);
			break;
		}
	}

	/** @return inputFile */
	protected File in() {
		return inputFile;
	}

	/** @return */
	protected File inputDirectory() {
		return inputFile.getParentFile();
	}

	protected boolean isOriginalFileFormat() {
		return inputFile.isDirectory();
	}

	/** @return outputFile */
	protected File out() {
		return outputFile;
	}

	protected void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(createCMXNoteHandler());
	}

	protected void readOriginalFile() throws IOException {
		testPrintln("reading original format...(dummy)");
	}

	/** @param inputFile セットする inputFile */
	protected void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	/** @param outputFile セットする outputFile */
	protected void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	/** @param note TODO */
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

		calculateHierarchicalParameters((MXGroup) group.getChildFormerGroup());
		calculateHierarchicalParameters((MXGroup) group.getChildLatterGroup());
	}

	private void initializeParameters() {
		hierarchicalGroupCount = 0;
		tempoList.clear();
		dynamicsList.clear();
		articulationList.clear();
		for (int i = 0; i < GroupAnalyzer.rootDiv; i++) {
			tempoList.add(0.);
			dynamicsList.add(0.);
			articulationList.add(1.);
		}
	}

	protected void readCMXFile(String xmlFilename) {
		testPrintln("import CMX file");
		try {
			CMXFileWrapper cmx = CMXFileWrapper.readfile(xmlFilename);
			if (cmx instanceof DeviationInstanceWrapper) {
				dev = ((DeviationInstanceWrapper) cmx);
				xml = dev.getTargetMusicXML();
				// TODO deviation データを読み込む処理
			} else if (cmx instanceof MusicXMLWrapper) {
				xml = (MusicXMLWrapper) cmx;
			} else
				readCMXFile(cmx);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * CrestMuseXML(CMX)形式のデータから読込処理を行います。
	 * MusicXML、DeviationIncetanceWrapper形式についてはreadCMLFile(String)メソッドにてすでに格納されています。
	 * このメソッドでは、それ以外の形式についての処理を実装してください。
	 *
	 * @param cmx
	 * @see TuneData.readCMXFile(String)
	 * @see {@link CrestMuseXML:<a href="http://cmx.osdn.jp/">http://cmx.osdn.jp/</a>}
	 */
	protected void readCMXFile(CMXFileWrapper cmx) {}

	/** @param g */
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
