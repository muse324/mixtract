package net.muse.app;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import net.muse.gui.GUIUtil;
import net.muse.mixtract.command.MixtractCommand;

/**
 * <h1>TestApp</h1>
 * MuseAppシリーズによるアプリケーション開発のサンプルプログラムです。
 *
 * @author hashida
 */
public class TestApp extends MuseApp {

	public static void main(String[] args) {
		try {
			TestApp main = new TestApp(args);
			if (!isShowGUI()) // GUIを使わずメインエンジンのみテストする場合に使用。GUI設定は

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

				MixtractCommand.setMainObject(main);

				/* sprash screen */
				main.createSplashScreen("mixtract-logo.png");
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
						// main.hideSplash();
					}
				});

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TestApp(String[] args) throws FileNotFoundException, IOException {
		super(args);
	}

	/* (非 Javadoc)
	 * @see net.muse.app.MuseApp#initialize()
	 */
	@Override
	protected void initialize() {
		appImageFile = "mixtract-logo.png";
		PROPERTY_FILENAME = "Mixtract.properties";
		projectFileExtension = ".mxt";
	}

}
