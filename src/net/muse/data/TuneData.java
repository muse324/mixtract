package net.muse.data;

import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.activation.FileTypeMap;
import javax.sound.midi.*;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import jp.crestmuse.cmx.filewrappers.*;
import jp.crestmuse.cmx.processing.CMXController;
import net.muse.app.Mixtract;
import net.muse.gui.GUIUtil;
import net.muse.misc.MuseObject;
import net.muse.mixtract.data.MXGroupAnalyzer;

public class TuneData extends MuseObject implements TuneDataController {
	private static int MAXIMUM_MIDICHANNEL = 16;
	static boolean segmentGroupnoteLine = false;

	/** 入力ファイル */
	private File inputFile;
	/** 出力ファイル */
	private File outputFile;

	/** MusicXML */
	private MusicXMLWrapper xml;
	/** DeviationInstanceXML */
	private DeviationInstanceWrapper dev;
	/** SCCXMLWrapper */
	private SCCXMLWrapper scc;

	/** 拍子記号のリスト（変拍子対応） */
	private ArrayList<BeatInfo> beatInfoList = new ArrayList<BeatInfo>();
	/** 声部ごとのフレーズ構造(二分木) */
	private List<Group> rootGroup = new ArrayList<Group>();
	/** 楽曲に含まれる非階層グループを格納するリスト */
	private final List<Group> groupArrayList;

	/** テンポ情報 */
	private ArrayList<Integer> bpmlist = new ArrayList<Integer>();
	private double tempoListEndtime;
	/** 声部ごとの音符情報 */
	private ArrayList<NoteData> notelist = new ArrayList<NoteData>();
	int[] midiProgram = new int[MAXIMUM_MIDICHANNEL];
	double[] volume = new double[MAXIMUM_MIDICHANNEL];

	/** 楽曲全体のダイナミクスカーブ */
	private final LinkedList<Double> dynamicsList;
	/** 楽曲全体のテンポカーブ */
	private final LinkedList<Double> tempoList;
	/** 楽曲全体のアーティキュレーションカーブ */
	private final LinkedList<Double> articulationList;

	/** GUI等で選択されたグループ */
	private Group selectedGroup = null;

	protected int hierarchicalGroupCount;

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

	/**
	 * 非階層のグループを登録します。
	 *
	 * @param group
	 */
	public void addGroupArrayList(Group group) {
		// 重複するグループがあれば処理中断
		for (Group g : getGroupArrayList()) {
			if (g.nearlyEquals(group))
				return;
			// TODO 複数声部に未対応
		}
		// ----------------------------------
		getGroupArrayList().add(group);
	}

	public void calculateExpressionParameters() {
		initializeParameters();
		for (final Group root : getRootGroup()) {
			// tempo
			tempoListEndtime = root.getTimeValue();
			calculateExpressionParameters(root);

			if (isDebug()) {
				System.out.println("------ parameter lists calculation ------");
				for (int i = 0; i < getTempoList().size(); i++) {
					System.out.println(String.format(
							"%d: dyn2: %f, tempo2: %f, artc: %f", i,
							getDynamicsList().get(i), getTempoList().get(i),
							getArticulationList().get(i)));
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

	public void deleteGroupFromData(Group group) {
		// 非階層グループから削除
		if (getGroupArrayList().contains(group)) {
			getGroupArrayList().remove(group);
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

	public int[] getMIDIPrograms() {
		return midiProgram;
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
		Group g = getRootGroup().get(partIndex);
		return g;
	}

	/** @return tempoList2 */
	public LinkedList<Double> getTempoList() {
		return tempoList;
	}

	/**
	 * @return
	 */
	public int getUniqueGroupIndex() {
		ArrayList<Integer> idxlist = new ArrayList<Integer>();
		for (Group g : getGroupArrayList()) {
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

	public void printAllGroups() {
		GUIUtil.printConsole("Hierarchical group list:");
		for (Group g : getRootGroup()) {
			printGroupList(g);
		}
		GUIUtil.printConsole("Non hierarchical group list:");
		for (Group g : getGroupArrayList()) {
			printGroupList(g);
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneDataController#readfile()
	 */
	public void readfile() throws IOException {
		// システム独自形式の読込
		if (isOriginalFileFormat()) {
			readOriginalFile();
			outputFile = inputFile;
		} else {
			// ファイルの種類を判定する
			FileTypeMap filetypeMap = FileTypeMap.getDefaultFileTypeMap();
			String mimetype = filetypeMap.getContentType(in());
			if (mimetype.equals("audio/midi") || mimetype.equals(
					"application/octet-stream")) {
				// MIDIファイルを読み込む
				readMIDIFile();
				parseSCCXMLFile();
			} else if (mimetype.equals("application/xml")) {
				// CMX 形式からインポート
				readCMXFile(inputFile.getAbsolutePath());
				parseMusicXMLFile();
				writefile();
			}
		}
		calculateExpressionParameters();
	}

	public void setBPM(int idx, int value) {
		if (idx < getBPM().size())
			getBPM().set(idx, value);
		setDefaultBPM(value);
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
	public void writefile() throws IOException {
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
		JOptionPane.showMessageDialog(null,
				"TuneDataのサブクラスにて、writeScoreData()をオーバーライド実装してください。");
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneDataController#writeSMF()
	 */
	public void writeSMF() throws IOException {
		if (scc != null) {
			File fp = new File(inputDirectory(), scc.getFileName());
			if (fp.exists())
				FileUtils.copyFileToDirectory(fp, out(), true);
		}

		try {
			// SequenceとTrackの作成
			// 24tick=四分音符
			// TODO ticksperbeatの設定の仕方がよくわからん。2011.8.31
			// すべて480 でいいと思うのだけど、SMF出力すると0.5倍速になる。。
			Sequence sequence = new Sequence(Sequence.PPQ, getTicksPerBeat()
					* 2);
			Track track = sequence.createTrack();
			int offset = 100;
			/*
			 * テンポの設定 四分音符の長さをμsecで指定し3バイトに分解する
			 * 戻る
			 */
			MetaMessage mmsg = new MetaMessage();
			int tempo = getBeginningBPM();
			int l = 60 * 1000000 / tempo;
			mmsg.setMessage(0x51, new byte[] { (byte) (l / 65536), (byte) (l
					% 65536 / 256), (byte) (l % 256) }, 3);
			track.add(new MidiEvent(mmsg, 0));

			// set instrument
			for (int i = 0; i < midiProgram.length; i++) {
				ShortMessage message = new ShortMessage();
				message.setMessage(ShortMessage.PROGRAM_CHANGE, i,
						midiProgram[i], 0);
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
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TuneDataのサブクラスにて、calculateExpressionParameters()をオーバーライド実装してください。
	 *
	 * @param Group root
	 */
	protected void calculateExpressionParameters(final Group root) {}

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

	/**
	 * @param target
	 */
	protected void deleteGroup(Group target) {
		if (target == null)
			return;
		deleteGroup(target.child());
		target.setScoreNotelist(target.getScoreNotelist());
		target.setChild(null);
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
	protected void deleteHierarchicalGroup(Group target, Group structure) {
		if (structure == null)
			return;

		deleteHierarchicalGroup(target, structure.child());
		if (structure.equals(target)) {
			// 子グループをすべて削除
			deleteGroup(target);
			target.setType(GroupType.USER);
			// 親グループの再分析
			// analyzeStructure(target);
			return;
		}
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

	protected ArrayList<NoteData> getNotelist() {
		return notelist;
	}

	protected NoteData getNoteList(int partIndex) {
		return notelist.get(partIndex);
	}

	protected double getTempoListEndtime() {
		return tempoListEndtime;
	}

	/** @return inputFile */
	protected File in() {
		return inputFile;
	}

	protected void initializeNoteEvents(Group group) {
		if (group == null)
			return;
		initializeNoteEvents(group.child().getBeginGroupNote());
		initializeNoteEvents(group.getBeginGroupNote());
	}

	protected void initializeNoteEvents(GroupNote gnote) {
		if (gnote == null)
			return;
		initializeNoteEvents(gnote.child());
		initializeNoteEvents(gnote.next());
		initializeNoteEvents(gnote.getNote());
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

	/**
	 * 登録されているすべてのグループ情報を出力します。
	 *
	 * @param g 各声部のトップグループ
	 */
	protected void printGroupList(Group g) {
		if (g == null)
			return;
		printGroupList(g.child());
		System.out.println(g);
	}

	protected void readCMXFile(String xmlFilename) throws IOException {
		testPrintln("import CMX file");
		CMXFileWrapper cmx = CMXController.readfile(xmlFilename);
		if (cmx instanceof DeviationInstanceWrapper) {
			dev = ((DeviationInstanceWrapper) cmx);
			xml = dev.getTargetMusicXML();
			// TODO deviation データを読み込む処理
		} else if (cmx instanceof MusicXMLWrapper) {
			xml = (MusicXMLWrapper) cmx;
		} else if (cmx instanceof SCCXMLWrapper) {
			scc = (SCCXMLWrapper) cmx;
		} else
			readCMXFile(cmx);
	}

	protected void readOriginalFile() throws IOException {
		testPrintln("reading original format...(dummy)");
	}

	protected void setGrouplist(int partIndex, Group rootGroup) {
		if (partIndex >= this.getRootGroup().size())
			this.getRootGroup().add(rootGroup);
		else
			this.getRootGroup().set(partIndex, rootGroup);
	}

	protected void setNotelist(int partIndex, NoteData root) {
		if (partIndex >= this.notelist.size())
			notelist.add(root);
		else
			notelist.set(partIndex, root);
	}

	protected void setNoteScheduleEvent(GroupNote note, int endOffset) {
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

	private int getBeginningBPM() {
		try {
			return getBPM().get(0);
		} catch (IndexOutOfBoundsException e) {
			return 120;
		}
	}

	/**
	 * @param rootGroup2
	 * @param idxlist
	 */
	protected void getUniqueGroupIndex(Group glist,
			ArrayList<Integer> idxlist) {
		if (glist == null)
			return;
		getUniqueGroupIndex(glist.child(), idxlist);
		if (!idxlist.contains(glist.index()))
			idxlist.add(glist.index());
	}

	private void initializeNoteEvents(NoteData nd) {
		if (nd == null)
			return;

		nd.setRealOnset(nd.onsetInMsec(getBeginningBPM()));
		nd.setRealOffset(nd.offsetInMsec(getBeginningBPM()));

		initializeNoteEvents(nd.child());
		initializeNoteEvents(nd.next());

	}

	private void initializeParameters() {
		hierarchicalGroupCount = 0;
		tempoList.clear();
		dynamicsList.clear();
		articulationList.clear();
		for (int i = 0; i < MXGroupAnalyzer.rootDiv; i++) {
			tempoList.add(0.);
			dynamicsList.add(0.);
			articulationList.add(1.);
		}
	}

	private void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(createCMXNoteHandler());
	}

	private void parseSCCXMLFile() {
		if (scc == null)
			return;
		try {
			scc.processNotes(createCMXNoteHandler());
		} catch (TransformerException e) {
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
	 * @see {@link CrestMuseXML:<a href=
	 *      "http://cmx.osdn.jp/">http://cmx.osdn.jp/</a>}
	 */
	private void readCMXFile(CMXFileWrapper cmx) {}

	private void readMIDIFile() {
		try {
			MIDIXMLWrapper mid = CMXController.readSMFAsMIDIXML(in()
					.getAbsolutePath());
			scc = mid.toSCCXML();
		} catch (TransformerException | IOException
				| ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}

	/** @param inputFile セットする inputFile */
	private void setInputFile(File inputFile) {
		this.inputFile = inputFile;
	}

	protected void setNoteScheduleEvent(final Group g) {
		if (g == null)
			return;
		setNoteScheduleEvent(g.child());
		setNoteScheduleEvent(g.getBeginGroupNote(), g.getEndGroupNote()
				.getNote().offset());
	}

	/** @param outputFile セットする outputFile */
	private void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	private void setTempoListEndtime(double tempoListEndtime) {
		this.tempoListEndtime = tempoListEndtime;
	}

	protected MusicXMLWrapper xml() {
		return xml;
	}

	protected void setXml(MusicXMLWrapper xml) {
		this.xml = xml;
	}

	protected DeviationInstanceWrapper dev() {
		return dev;
	}

	protected void setDev(DeviationInstanceWrapper dev) {
		this.dev = dev;
	}

	public BeatInfo getBeatInfoList(int measure) {
		for (BeatInfo b : beatInfoList) {
			if (b.measure() == measure)
				return b;
		}
		return null;
	}

	public void setBeatInfo(int measure, int beats, int beatType) {
		for (BeatInfo b : beatInfoList) {
			if (b.measure() == measure)
				return;
		}
		beatInfoList.add(new BeatInfo(measure, beats, beatType));
	}

}
