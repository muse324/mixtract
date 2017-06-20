package net.muse.mixtract;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import net.muse.MuseApp;
import net.muse.gui.GUIUtil;
import net.muse.mixtract.gui.MixtractCommand;

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
	protected static String mixtractLogImageFile = "mixtract-logo.png";

	public Mixtract(String[] args) throws FileNotFoundException, IOException {
		super(args);
	}
	public static void main(String[] args) {
		try {
			final Mixtract main = new Mixtract(args);
			if (!isShowGUI())
				main.readfile(main.inputFileName, main.outputFileName);
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
