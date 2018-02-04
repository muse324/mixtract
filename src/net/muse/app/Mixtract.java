package net.muse.app;

import java.awt.EventQueue;
import java.io.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;

import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.*;
import net.muse.mixtract.command.MixtractCommand;
import net.muse.mixtract.data.*;
import net.muse.mixtract.gui.MXMainFrame;
import net.muse.mixtract.gui.PhraseViewer;

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
			if (!isShowGUI())
				main.readfile(main.getInputFileName(), main
						.getOutputFileName());
			else {
				// MacOSXでのJava実行環境用のシステムプロパティの設定.
				main.setupSystemPropertiesForMacOSX();

				// システム標準のL&Fを設定.
				// MacOSXならAqua、WindowsXPならLuna、Vista/Windows7ならばAeroになる.
				// Aeroの場合、メニューに表示されるニーモニックのアンダースコアはALTキーを押さないとでてこない.
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());

				MixtractCommand.setMain(main);

				/* sprash screen */
				main.createSplashScreen(main.getAppImageFile());
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						main.showSplashScreen();
						main.splashScreen.setLocationRelativeTo(null);
					}
				});

				// create main frame
				main.createNewFrame();
				MixtractCommand.setJFrame(main.getFrame());
				main.getFrame().setDefaultCloseOperation(
						WindowConstants.EXIT_ON_CLOSE);
				JFrame.setDefaultLookAndFeelDecorated(false);
				main.getFrame().pack(); // ウィンドウサイズを最適化
				main.getFrame().setVisible(true); // ウィンドウを表示させる

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

			}

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
	@Override
	protected MXTuneData createTuneData(File in, File out) throws IOException,
			InvalidMidiDataException {
		return new MXTuneData(in, out);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#initialize()
	 */
	@Override
	protected void initialize() {
		setAppImageFile("mixtract-logo.png");
		PROPERTY_FILENAME = "Mixtract.properties";
		projectFileExtension = ".mxt";
	}

	@Override
	protected MainFrame mainFrame() throws IOException {
		if (getFrame() == null)
			return new MXMainFrame(this);
		return (MainFrame) getFrame();
	}

	protected void deleteGroup(final MXGroup g) {
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

	@Override
	public void analyzeStructure(TuneData data, Group group) {
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
