package net.muse.app;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.JFrame;

import net.muse.data.*;
import net.muse.gui.*;
import net.muse.misc.OptionType;
import net.muse.mixtract.command.MixtractCommand;
import net.muse.mixtract.data.curve.PhraseCurveType;

public abstract class MuseApp extends MuseGUIObject<JFrame> {
	private String appImageFile = "mixtract-logo.png";
	protected static String PROPERTY_FILENAME = "Mixtract.properties";
	protected static String projectFileExtension = ".mxt";

	/** 各種設定 */
	private boolean isReadingStructureData;
	private String midiDeviceName;
	private boolean doSimilaritySearch = false;

	/** 入出力ファイル */
	private String inputFileName;
	private String outputFileName;

	/** ファイル格納場所 */
	private File musicXMLDir;
	private File outputDir;
	private File projectDir;

	/** 楽曲情報 */
	private TuneData data;
	private List<TuneDataListener> tdListenerList = new ArrayList<TuneDataListener>();
	private ArrayList<PhraseViewer> phraseViewList;
	/** 階層的フレーズ構造の分析履歴 */
	private final ArrayList<GroupAnalyzer> analyzer = new ArrayList<GroupAnalyzer>();

	/**
	 * @return projectfileextension
	 */
	public static String getProjectFileExtension() {
		return projectFileExtension;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MuseApp(String[] args) throws FileNotFoundException, IOException {
		/* 初期化 */
		super();
		initialize();
		setPropertyFilename(PROPERTY_FILENAME);
		loadConfig();
		setOption(args);
	}

	/**
	 * アプリケーション起動前の初期設定を行います。下記の３パラメータについて必ず値を代入してください。
	 *
	 * @param appImageFile - アプリケーションロゴ画像の名称。画像ファイルはメインクラス(.java)と同じ場所に置いてください。
	 * @param PROPERTY_FILENAME -
	 *            アプリケーション用の環境設定ファイル名（.properties）。ファイルはプロジェクトフォルダのトップに置いてください。<br/>
	 *            cf) Mixtract.properties
	 * @param projectFileExtension - 独自ファイルを用いる場合の拡張子。".xxx"の形で記述します。 <div>ex)
	 *            <code> projectFileExtension = ".mxt";</code></div>
	 */
	protected abstract void initialize();

	public void addPhraseViewerList(PhraseViewer pv) {
		getPhraseViewList().add(pv);
	}

	public void addTuneDataListener(TuneDataListener l) {
		tdListenerList.add(l);
	}

	/**
	 * @param data2
	 * @param object
	 */
	public void analyzeStructure(TuneData data, Group group) {
		GroupAnalyzer ana = new GroupAnalyzer(data, false);
		ana.setRootGroup(group);
		ana.run();
		analyzer.add(ana);
	}

	/**
	 * @return
	 */
	public TuneData data() {
		if (data == null)
			throw new NullPointerException("data is null");
		return data;
	}

	/**
	 * @return the doSimilaritySearch
	 */
	public final boolean doSimilaritySearch() {
		return doSimilaritySearch;
	}

	/**
	 * @return midiDeviceName
	 */
	public final String getMidiDeviceName() {
		return midiDeviceName;
	}

	/**
	 * @return musicXMLDir
	 */
	public File getMusicXMLDirectory() {
		return musicXMLDir;
	}

	public ArrayList<PhraseViewer> getPhraseViewList() {
		if (phraseViewList == null) {
			phraseViewList = new ArrayList<PhraseViewer>();
		}
		return phraseViewList;
	}

	/**
	 * @return projectDir
	 */
	public File getProjectDirectory() {
		return projectDir;
	}

	/**
	 * @return
	 */
	public boolean hasTarget() {
		return data() != null;
	}

	/**
	 * MacOSXで動作しているかを判定します。
	 */
	public boolean isMac() {
		String lcOSName = System.getProperty("os.name").toLowerCase();
		return lcOSName.startsWith("mac os x");
	}

	/**
	 * @param g
	 */
	public void notifyAddGroup(Group g) {
		for (TuneDataListener l : tdListenerList) {
			l.addGroup(g);
		}
	}

	/**
	 * @param type
	 */
	public void notifyChangeHierarchicalParameters(PhraseCurveType type) {
		for (TuneDataListener l : tdListenerList) {
			l.changeExpression(type);
		}
	}

	public void notifyDeleteGroup(GroupLabel label) {
		deleteGroup(label.getGroup());
		for (final TuneDataListener l : tdListenerList) {
			l.deleteGroup(label);
		}
	}

	/**
	 *
	 */
	public void notifyDeselectGroup() {
		if (data() != null)
			data().setSelectedGroup(null);
		for (final TuneDataListener l : tdListenerList) {
			l.deselect(null);
		}
	}

	/**
	 * グループが選択/解除されたことを通知します．
	 *
	 * @param g グループラベル
	 * @param b 選択(true)/解除(false)
	 */
	public void notifySelectGroup(GroupLabel g, boolean b) {
		data().setSelectedGroup((b) ? g.getGroup() : null);
		for (final TuneDataListener l : tdListenerList) {
			l.selectGroup(g, b);
		}
	}

	public void notifySetTarget() {
		getPhraseViewList().clear();
		for (TuneDataListener l : tdListenerList) {
			l.setTarget(data());
		}
	}

	public void notifyShowCurrentX(boolean showCurrentX, int x) {
		for (CanvasMouseListener v : getPhraseViewList()) {
			v.setShowCurrentX(showCurrentX, x);
		}
	}

	public void printAllSimilarList() {
		if (doSimilaritySearch) {
			// final List<SimilarCase> list =
			// selectedGroup.getGroup().getSimilarList();
			// int i = 0;
			// for (final SimilarCase info : list) {
			// final Group g = info.getSource();
			// GUIUtil
			// .printConsole("gr." + ++i + ": " + g + info +
			// g.getScoreNotelist());
			// }
			//
			// GUIUtil.printConsole("");
		}
	}

	public void printSimilarList() {
		if (doSimilaritySearch) {
			// final List<SimilarCase> list =
			// selectedGroup.getGroup().getSimilarList();
			// int i = 0;
			// for (final SimilarCase info : list) {
			// if (info.getSimilarity() < sim.getThreshold())
			// continue;
			// final Group g = info.getSource();
			// GUIUtil
			// .printConsole("gr." + ++i + ": " + g + info +
			// g.getScoreNotelist());
			// }
			//
			// GUIUtil.printConsole("");
		}
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public void readfile(File in, File out) throws IOException,
			InvalidMidiDataException {
		setData(createTuneData(in, out));
		log.printf("Open file: %s", in);
		if (isShowGUI()) {
			MixtractCommand.setTarget(data());
			notifySetTarget();
		}
	}

	public void readfile(String inputFilename, String outFilename)
			throws IOException, InvalidMidiDataException {
		File in = new File(inputFilename);
		if (!in.exists())
			in = new File(projectDir, inputFilename);
		if (!in.exists())
			in = new File(musicXMLDir, inputFilename);
		File out = new File(getOutputDirectory(), outFilename);
		readfile(in, out);
	}

	/**
	 * @param inputFileName the inputFileName to set
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * @param dir セットする musicXMLDir
	 */
	public void setMusicXMLDirectory(File dir) {
		this.musicXMLDir = dir;
	}

	/**
	 * @param isReadingStructureData セットする isReadingStructureData
	 */
	public final void setReadingStructureData(boolean isReadingStructureData) {
		this.isReadingStructureData = isReadingStructureData;
	}

	/**
	 *
	 */
	public void setupSystemPropertiesForMacOSX() {
		if (isMac()) {
			// JFrameにメニューをつけるのではなく、一般的なOSXアプリ同様に画面上端のスクリーンメニューにする.
			System.setProperty("apple.laf.useScreenMenuBar", "true");

			// スクリーンメニュー左端に表記されるアプリケーション名を設定する
			// (何も設定しないとクラス名になる。)
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					"Mixtract");

			// これらのプロパティは「Jar Bundler」を使用してappとしてバンドル化すれば、
			// info.plistファイルの中で直接指定できる.
			// 上記の他、JavaVMへの起動パラメータ(例えば-Xmxとか。)も指定できるうえ、
			// 依存jarファイルや各種ファイルも一緒にバンドルできるので、
			// 手間を惜しまなければ、jar化したのちに、さらにapp化するとよいかもしれない.
		}
	}

	protected void createNewFrame() throws Exception {
		this.frame = mainFrame();
		if (!isMac())
			return;

		// Mac用にスクリーンメニューとアプリケーション終了(cmd-Q)のハンドリングを設定する.
		// com.apple.eawt.Applicationクラスで処理するが、MacOSX以外の実行環境では存在しないので、
		// このクラスを直接使用するとMacOSX以外で起動できなくなってしまう.
		// そのため、サポートクラスの中で処理させ、そのサポートクラスをリフレクションにより間接的に
		// 必要になったときに呼び出す.(クラスのロードに失敗したら、そのときにコケる.)
		Class<?> clz = Class.forName("net.muse.gui.MainFramePartialForMacOSX");
		Method mtd = clz.getMethod("setupScreenMenu", new Class[] {
				MainFrame.class });
		mtd.invoke(null, new Object[] { this.frame });
	}

	abstract protected MainFrame mainFrame() throws IOException;

	protected TuneData createTuneData(File in, File out) throws IOException,
			InvalidMidiDataException {
		return new TuneData(in, out);
	}

	/**
	 * @param data セットする data
	 */
	protected void setData(TuneData data) {
		this.data = data;
	}

	protected void setMaximumMIDIChannel(int ch) {}

	/**
	 * MIDIデバイス名を指定します。
	 *
	 * @param midiDeviceName
	 */
	public void setMidiDeviceName(String midiDeviceName) {
		this.midiDeviceName = midiDeviceName;
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.MuseObject#setOption(java.lang.String)
	 */
	@Override
	protected void setOption(String str) throws IllegalArgumentException {
		try {
			super.setOption(str);
		} catch (IllegalArgumentException e) {
			try {
				OptionType _cmd = OptionType.valueOf(str);
				_cmd.exe(this, config.getProperty(str));
				log.println("done.");
			} catch (IllegalArgumentException e1) {
				log.println("skipped.");
			}
		}
	}

	/**
	 * @param dir プロジェクトデータを格納するディレクトリ
	 */
	public void setProjectDirectory(File dir) {
		this.projectDir = dir;
	}

	/**
	 * @param doSimilaritySearch the doSimilaritySearch to set
	 */
	protected final void setSimilaritySearch(boolean doSimilaritySearch) {
		this.doSimilaritySearch = doSimilaritySearch;
	}

	/**
	 * @param g
	 */
	private void deleteGroup(final Group g) {
		if (g == null)
			return;
		deleteGroup(g.getChildFormerGroup());
		deleteGroup(g.getChildLatterGroup());

		PhraseViewer d = null;
		for (PhraseViewer pv : getPhraseViewList()) {
			if (pv.getGroup() == g) {
				d = pv;
				break;
			}
		}
		if (d != null) {
			d.setVisible(false);
			getPhraseViewList().remove(d);
		}
	}

	/**
	 * @param args
	 * @throws FileNotFoundException
	 */
	private void setOption(String[] args) throws FileNotFoundException {
		boolean doSearch = true;
		for (int i = 0; i < args.length; i++) {
			String key = args[i];
			if (!doSearch) {
				doSearch = true;
				continue;
			}
			if (key.equals("-debug") || key.equals("-d")) {
				DEBUG = true;
			} else if (key.equals("-c")) {
				setShowGUI(false);
			} else if (key.equals("-xml")) {
				setInputFileName(args[i + 1]);
				doSearch = false;
			} else if (key.equals("-mxt")) {
				setInputFileName(args[i + 1]);
				doSearch = false;
			}
		}
	}

	/**
	 * @return inputFileName
	 */
	public String getInputFileName() {
		return inputFileName;
	}

	/**
	 * @return outputFileName
	 */
	public String getOutputFileName() {
		return outputFileName;
	}

	/**
	 * @param outputFileName セットする outputFileName
	 */
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}

	protected File getOutputDirectory() {
		return outputDir;
	}

	/**
	 * @param outputDir 出力用ディレクトリ
	 */
	public void setOutputDirectory(File outputDir) {
		this.outputDir = outputDir;
	}

	public String getAppImageFile() {
		return appImageFile;
	}

	public void setAppImageFile(String imgFileName) {
		appImageFile = imgFileName;
	}

}
