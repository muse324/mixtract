package net.muse.app;

import java.awt.EventQueue;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.MainFrame;

/**
 * <h1>TestApp</h1>
 * MuseAppシリーズによるアプリケーション開発のサンプルプログラムです。
 * <p>
 * アプリケーション起動のためのメインクラスは、MuseApp クラスのサブクラスとして実装します。
 * GUI起動までの基本的なメソッドが実行されます。main() 内の記述例も参考にしてください。
 *
 * @since June 24, 2017
 * @author Mitsuyo Hashida
 */
public class TestApp extends MuseApp {

	private TestApp(String[] args) throws FileNotFoundException, IOException {
		super(args);
	}

	public static void main(String[] args) {
		try {
			TestApp app = new TestApp(args);
			app.setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override public void analyzeStructure(TuneData data, Group group) {
		throw new UnsupportedOperationException("実装してください");
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#mainFrame()
	 */
	@Override public MainFrame mainFrame() throws IOException {
		// GUIのメインフレーム(JFrameのサブクラス)を生成し、frameに格納します。
		// MainFrameはJFrameのサブクラスです。
		// 独自GUIクラスを作成することになりますので、独自にMainFrameのサブクラスを実装し、
		// ここでそのクラスをインスタンス化してください。
		if (getFrame() == null)
			return new MainFrame(this); // 独自クラスを定義したらここでそれを返す。
		return getFrame(); // このキャストは MainFrame のままで良い
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
		if (!isShowGUI()) {// isShowGUI()は、起動時にGUIを用いるかどうかを判別します。プロパティファイル内のSHOW_GUIによる値で判別します。
			butler().readfile();
			return;
		}
		// ---- GUI起動 ------------------------
		// MacOSXでのJava実行環境用のシステムプロパティの設定.
		setupSystemPropertiesForMacOSX();

		// システム標準のL&Fを設定.
		// MacOSXならAqua、WindowsXPならLuna、Vista/Windows7ならばAeroになる.
		// Aeroの場合、メニューに表示されるニーモニックのアンダースコアはALTキーを押さないとでてこない.
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// 各種実行コマンド制御を行うMuseAppComandクラスにメインクラスを登録します。
		// アプリケーション独自の制御コマンドを作成するにはMuseAppCommandクラスのサブクラスを定義してください。
		// MuseAppCommand.setMain(app);

		/* sprash screen */
		createSplashScreen(getAppImageFile());
		EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				showSplashScreen();
			}
		});

		// create mainFrame
		createNewFrame();
		getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
		getFrame().pack(); // ウィンドウサイズを最適化
		getFrame().setVisible(true); // ウィンドウを表示させる

		// 3秒後にスプラッシュスクリーンを非表示にする
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			butler().printConsole(e.getMessage());
		}
		EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				hideSplash();
			}
		});
	}
}
