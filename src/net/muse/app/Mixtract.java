package net.muse.app;

import java.awt.EventQueue;
import java.io.*;
import java.lang.reflect.Method;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;

import net.muse.gui.GUIUtil;
import net.muse.gui.MainFrame;
import net.muse.mixtract.command.MixtractCommand;
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
	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#createTuneData(java.io.File, java.io.File)
	 */
	@Override protected MXTuneData createTuneData(File in, File out)
			throws IOException, InvalidMidiDataException {
		return new MXTuneData(in, out);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.MuseApp#createNewFrame()
	 */
	@Override protected void createNewFrame() throws Exception {
		this.frame = new MXMainFrame(this);
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

	protected static String mixtractLogImageFile = "mixtract-logo.png";

	public Mixtract(String[] args) throws FileNotFoundException, IOException {
		super(args);
	}

	public static void main(String[] args) {
		try {
			final Mixtract main = new Mixtract(args);
			if (!isShowGUI())
				main.readfile(main.getInputFileName(), main.getOutputFileName());
			else {
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

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
