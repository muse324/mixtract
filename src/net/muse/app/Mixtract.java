package net.muse.app;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.InfoViewer;
import net.muse.gui.MainFrame;
import net.muse.mixtract.command.MixtractCommandType;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXGroupAnalyzer;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.gui.MXMainFrame;

/**
 * <h1>Mixtract</h1>
 *
 * @author Mitsuyo Hashida
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida@m-use.net</address>
 * @since 2009/09/20 at CrestMuse Project
 * @since 2017/06/17 at m-use studio / Soai University
 */
public class Mixtract extends MuseApp {

	private Mixtract(String[] args) throws FileNotFoundException, IOException {
		super(args);
	}

	public static void main(String[] args) {
		try {
			final Mixtract app = new Mixtract(args);
			app.setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override public void analyzeStructure(TuneData data, Group group) {
		assert data != null && data instanceof MXTuneData;
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroupAnalyzer ana = new MXGroupAnalyzer((MXTuneData) data, false);
		ana.setRootGroup((MXGroup) group);
		ana.run();
		getAnalyzer().add(ana);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#createTuneData(java.io.File, java.io.File)
	 */
	@Override public void createTuneData(File in, File out) throws IOException {
		setData(new MXTuneData(in, out));
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MuseGUIObject#getFrame()
	 */
	@Override public MXMainFrame getFrame() {
		return (MXMainFrame) super.getFrame();
	}

	@Override public MainFrame mainFrame() throws IOException {
		if (getFrame() == null)
			return new MXMainFrame(this);
		return getFrame();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#initialize()
	 */
	@Override protected void initialize() {
		setAppImageFile("mixtract-logo.png");
		PROPERTY_FILENAME = "Mixtract.properties";
		projectFileExtension = ".mxt";
	}

	@Override protected void setup() throws Exception {
		if (!isShowGUI()) {
			butler().readfile();
			return;
		}
		// MacOSXでのJava実行環境用のシステムプロパティの設定.
		setupSystemPropertiesForMacOSX();

		// システム標準のL&Fを設定.
		// MacOSXならAqua、WindowsXPならLuna、Vista/Windows7ならばAeroになる.
		// Aeroの場合、メニューに表示されるニーモニックのアンダースコアはALTキーを押さないとでてこない.
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());


		/* sprash screen */
		createSplashScreen(getAppImageFile());
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				showSplashScreen();
			}
		});

		// create mainFrame
		createNewFrame();
		getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
		getFrame().pack(); // ウィンドウサイズを最適化
		getFrame().setVisible(true); // ウィンドウを表示させる

		// 長い処理のdummy
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			butler().printConsole(e.getMessage());
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				// showPanel();
				hideSplash();
			}
		});
	}

	@Override
	protected void setupCommands() {
		super.setupCommands();
		for (MixtractCommandType e : MixtractCommandType.values())
			getCommandList().add(e);
	}

	private void deleteGroup(final MXGroup g) {
		if (g == null)
			return;
		deleteGroup(g.getChildFormerGroup());
		deleteGroup(g.getChildLatterGroup());

		InfoViewer d = null;
		for (InfoViewer pv : butler().getInfoViewList()) {
			if (pv.group() == g) {
				d = pv;
				break;
			}
		}
		if (d != null) {
			d.setVisible(false);
			butler().getInfoViewList().remove(d);
		}
	}
}
