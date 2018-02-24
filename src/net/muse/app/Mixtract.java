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
import net.muse.mixtract.command.MixtractCommand;
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

	public static void main(String[] args) {
		try {
			final Mixtract main = new Mixtract(args);
			if (!isShowGUI()) {
				main.butler().readfile();
				return;
			}
			// MacOSXでのJava実行環境用のシステムプロパティの設定.
			main.setupSystemPropertiesForMacOSX();

			// システム標準のL&Fを設定.
			// MacOSXならAqua、WindowsXPならLuna、Vista/Windows7ならばAeroになる.
			// Aeroの場合、メニューに表示されるニーモニックのアンダースコアはALTキーを押さないとでてこない.
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			MixtractCommand.setMain(main);

			/* sprash screen */
			main.createSplashScreen(main.getAppImageFile());
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					main.showSplashScreen();
				}
			});

			// create main frame
			main.createNewFrame();
			main.getFrame().setDefaultCloseOperation(
					WindowConstants.EXIT_ON_CLOSE);
			JFrame.setDefaultLookAndFeelDecorated(false);
			main.getFrame().pack(); // ウィンドウサイズを最適化
			main.getFrame().setVisible(true); // ウィンドウを表示させる

			// 長い処理のdummy
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				main.butler().printConsole(e.getMessage());
			}
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					// showPanel();
					main.hideSplash();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Mixtract(String[] args) throws FileNotFoundException, IOException {
		super(args);
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
	 * @see net.muse.app.MuseApp#initialize()
	 */
	@Override protected void initialize() {
		setAppImageFile("mixtract-logo.png");
		PROPERTY_FILENAME = "Mixtract.properties";
		projectFileExtension = ".mxt";
	}

	@Override protected MainFrame mainFrame() throws IOException {
		if (getFrame() == null)
			return new MXMainFrame(this);
		return (MainFrame) getFrame();
	}

	protected void deleteGroup(final MXGroup g) {
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

	@Override public void analyzeStructure(TuneData data, Group group) {
		assert data != null && data instanceof MXTuneData;
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroupAnalyzer ana = new MXGroupAnalyzer((MXTuneData) data, false);
		ana.setRootGroup((MXGroup) group);
		ana.run();
		analyzer.add(ana);
	}
}
