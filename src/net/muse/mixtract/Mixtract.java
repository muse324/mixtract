package net.muse.mixtract;

import java.awt.EventQueue;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;

import jp.crestmuse.cmx.filewrappers.CMXFileWrapper;
import net.muse.misc.GUIUtil;
import net.muse.misc.MuseGUIObject;
import net.muse.mixtract.command.GroupAnalyzer;
import net.muse.mixtract.data.*;
import net.muse.mixtract.gui.*;

/**
 * <h1>Mixtract</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>@ CrestMuse Project, JST</address>
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/09/20
 */
public class Mixtract extends MuseGUIObject<JFrame> {
	protected static String mixtractLogImageFile = "mixtract-logo.png";
	private static final String PROPERTY_FILENAME = "Mixtract.properties";
	private boolean isReadingStructureData;
	private String midiDeviceName;

	private String inputFileName;

	private TuneData data;
	private File musicXMLDir;
	protected File outputDir;
	private File projectDir;
	protected String outputFileName;
	private static final String projectFileExtension = ".mxt";
	private List<TuneDataListener> tdListenerList = new ArrayList<TuneDataListener>();
	private ArrayList<PhraseViewer> phraseViewList;
	private boolean doSimilaritySearch = false;
	/** 階層的フレーズ構造の分析履歴 */
	private final ArrayList<GroupAnalyzer> analyzer = new ArrayList<GroupAnalyzer>();

	public Mixtract(String[] args) throws FileNotFoundException, IOException {
		/* 初期化 */
		super();
		setPropertyFilename(PROPERTY_FILENAME);

		loadConfig();
		setOption(args);

	}

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
			final Mixtract main = new Mixtract(args);
			if (isShowGUI()) {
				// MacOSXでのJava実行環境用のシステムプロパティの設定.
				main.setupSystemPropertiesForMacOSX();

				// システム標準のL&Fを設定.
				// MacOSXならAqua、WindowsXPならLuna、Vista/Windows7ならばAeroになる.
				// Aeroの場合、メニューに表示されるニーモニックのアンダースコアはALTキーを押さないとでてこない.
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());

				MixtractCommand.setMainObject(main);

				/* sprash screen */
				main.createSplashScreen(mixtractLogImageFile);
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						main.showSplashScreen();
						main.splashScreen.setLocationRelativeTo(null);
					}
				});

				// create main frame
				main.createNewFrame();
				MixtractCommand.setJFrame(main.frame);
				main.frame.setDefaultCloseOperation(
						WindowConstants.EXIT_ON_CLOSE);
				JFrame.setDefaultLookAndFeelDecorated(false);
				main.frame.pack(); // ウィンドウサイズを最適化
				main.frame.setVisible(true); // ウィンドウを表示させる

				// 長い処理のdummy
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					GUIUtil.printConsole(e.getMessage());
				}
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						// showPanel();
						main.hideSplash();
					}
				});

			} else
				main.readfile(main.inputFileName, main.outputFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTuneDataListener(TuneDataListener l) {
		tdListenerList.add(l);
	}

	/**
	 * @param data2
	 * @param object
	 */
	public void analyzeStructure(TuneData dat, Group group) {
		GroupAnalyzer ana = new GroupAnalyzer(dat, false);
		ana.setRootGroup(group);
		ana.run();
		analyzer.add(ana);
	}

	/**
	 * @return the doSimilaritySearch
	 */
	public final boolean doSimilaritySearch() {
		return doSimilaritySearch;
	}

	/**
	 * @return
	 */
	public TuneData getData() {
		if (data == null)
			throw new NullPointerException("data is null");
		return data;
	}

	/**
	 * @return the inputFileName
	 */
	public String getInputFileName() {
		return inputFileName;
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
		return data != null;
	}

	/**
	 * MacOSXで動作しているかを判定します。
	 *
	 * @return
	 */
	public boolean isMac() {
		// MacOSXで動作しているか?
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
		if (data != null)
			data.setSelectedGroup(null);
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
		data.setSelectedGroup((b) ? g.getGroup() : null);
		for (final TuneDataListener l : tdListenerList) {
			l.selectGroup(g, b);
		}
	}

	public void notifySetTarget() {
		getPhraseViewList().clear();
		for (TuneDataListener l : tdListenerList) {
			l.setTarget(data);
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
		data = new TuneData(in, out);
		log.printf("Open file: %s", in);
		if (isShowGUI()) {
			MixtractCommand.setTarget(data);
			notifySetTarget();
		}
	}

	/**
	 * @param inputFileName the inputFileName to set
	 */
	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * @param musicXMLDir セットする musicXMLDir
	 */
	public void setMusicXMLDirectory(File musicXMLDir) {
		this.musicXMLDir = musicXMLDir;
	}

	/**
	 * @param isReadingStructureData セットする isReadingStructureData
	 */
	public final void setReadingStructureData(boolean isReadingStructureData) {
		this.isReadingStructureData = isReadingStructureData;
	}

	protected void setMaximumMIDIChannel(int ch) {}

	protected void setMidiDeviceName(String midiDeviceName) {
		this.midiDeviceName = midiDeviceName;
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.MuseObject#setOption(java.lang.String)
	 */
	@Override protected void setOption(String str)
			throws IllegalArgumentException {
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

	protected void setProjectDirectory(File dir) {
		this.projectDir = dir;
	}

	/**
	 * @param doSimilaritySearch the doSimilaritySearch to set
	 */
	protected final void setSimilaritySearch(boolean doSimilaritySearch) {
		this.doSimilaritySearch = doSimilaritySearch;
	}

	/**
	 *
	 */
	protected void setupSystemPropertiesForMacOSX() {
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

	private void createNewFrame() throws Exception {
		this.frame = new MainFrame(this);
		if (isMac()) {
			// Mac用にスクリーンメニューとアプリケーション終了(cmd-Q)のハンドリングを設定する.
			// com.apple.eawt.Applicationクラスで処理するが、MacOSX以外の実行環境では存在しないので、
			// このクラスを直接使用するとMacOSX以外で起動できなくなってしまう.
			// そのため、サポートクラスの中で処理させ、そのサポートクラスをリフレクションにより間接的に
			// 必要になったときに呼び出す.(クラスのロードに失敗したら、そのときにコケる.)
			Class<?> clz = Class.forName(
					"net.muse.mixtract.gui.MainFramePartialForMacOSX");
			Method mtd = clz.getMethod("setupScreenMenu", new Class[] {
					MainFrame.class });
			mtd.invoke(null, new Object[] { this.frame });
		}
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

	private void readfile(String inputFilename, String outFilename)
			throws IOException, InvalidMidiDataException {
		File in = new File(inputFilename);
		if (!in.exists())
			in = new File(projectDir, inputFilename);
		if (!in.exists())
			in = new File(musicXMLDir, inputFilename);
		File out = new File(outputDir, outFilename);
		readfile(in, out);
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
				inputFileName = args[i + 1];
				doSearch = false;
			} else if (key.equals("-mxt")) {
				inputFileName = args[i + 1];
				doSearch = false;
			}
		}
	}

	public enum OptionType {
		KEYBOARD_WIDTH {
			@Override void exe(Mixtract mixtract, String property) {
				KeyBoard.setKeyWidth(Integer.parseInt(property));
			}
		},
		MAXIMUM_MIDICHANNEL {
			@Override void exe(Mixtract mixtract, String property) {
				TuneData.setMaximumMIDIChannel(Integer.parseInt(property));
			}
		},
		INPUT_FILENAME {
			@Override void exe(Mixtract mixtract, String property) {
				mixtract.setInputFileName(property);
			}
		},
		OUTPUT_FILENAME {
			@Override void exe(Mixtract mixtract, String property) {
				mixtract.outputFileName = property;
			}
		},
		MIXTRACT_LOGO {
			@Override public void exe(Mixtract mixtract, String property) {
				mixtractLogImageFile = property;
			}
		},
		CMXCATALOG {
			@Override void exe(Mixtract mixtract, String property) {
				CMXFileWrapper.catalogFileName = property;
			}
		},
		MIDIDEVICE {
			@Override void exe(Mixtract mixtract, String property) {
				mixtract.setMidiDeviceName(property);
			}
		},
		MUSICXML_DIR {
			@Override void exe(Mixtract mixtract, String property) {
				mixtract.setMusicXMLDirectory(createDirectory(new File(property)
						.getAbsolutePath()));
			}
		},
		PROJECT_DIR {
			@Override void exe(Mixtract mixtract, String property) {
				mixtract.setProjectDirectory(createDirectory(new File(property)
						.getAbsolutePath()));
			}
		},
		OUTPUT_DIR {
			@Override void exe(Mixtract mixtract, String property) {
				mixtract.outputDir = createDirectory(new File(property)
						.getAbsolutePath());
			}
		},
		segmentGroupnoteLine {
			@Override public void exe(Mixtract mixtract, String property) {
				TuneData.setSegmentGroupnoteLine(Boolean.parseBoolean(
						property));
			}
		},
		SHOW_GUI {
			@Override public void exe(Mixtract mixtract, String property) {
				setShowGUI(Boolean.parseBoolean(property));
			}
		},
		READ_STRDATA_ON_READING {
			@Override public void exe(Mixtract mixtract, String property) {
				mixtract.setReadingStructureData(Boolean.parseBoolean(
						property));
			}

		},
		avoidLastRestsAsGroup {
			@Override public void exe(Mixtract mixtract, String property) {
				Group.setAvoidLastRestsFromGroup(Boolean.parseBoolean(
						property));
			}
		},
		durationOffset {
			@Override public void exe(Mixtract mixtract, String property) {
				TuneData.setDurationOffset(Integer.parseInt(property));
			}
		};
		private static File createDirectory(String path) {
			File dir = new File(path);
			if (!dir.exists())
				dir.mkdirs();
			return dir;
		}

		/**
		 * @param mixtract
		 * @param property
		 */
		abstract void exe(Mixtract mixtract, String property);
	}

}
