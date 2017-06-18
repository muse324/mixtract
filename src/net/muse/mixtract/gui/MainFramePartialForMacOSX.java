package net.muse.mixtract.gui;

import java.awt.Image;
import java.lang.reflect.Method;

import com.apple.eawt.*;

public class MainFramePartialForMacOSX {
	private MainFramePartialForMacOSX() {
		super();
	}

	public static void setupScreenMenu(final MainFrame mainFrame) {
		if (mainFrame == null) {
			throw new IllegalArgumentException();
		}

		Application app = Application.getApplication();
		app.setEnabledAboutMenu(true); // 「このアプリについて」のメニュー項目。デフォルトでtrue
		app.setEnabledPreferencesMenu(true); // 「環境設定」のメニュー項目、デフォルトはfalse
		// スクリーンメニューやアプリケーションのイベント通知を受け取るリスナーを設定する。
		app.addApplicationListener(new ApplicationAdapter() {
			public void handleAbout(ApplicationEvent arg0) {
				mainFrame.onAbout();
				arg0.setHandled(true); // OSXデフォルトのダイアログを表示させない
			}

			public void handleQuit(ApplicationEvent arg0) {
				mainFrame.quit();
				arg0.setHandled(true);
			}

			public void handlePreferences(ApplicationEvent arg0) {
				mainFrame.onPreference();
				arg0.setHandled(true);
			}
		});

		// Dockアイコンの設定 Leopard以降のみ
		try {
			Class<? extends Application> clz = app.getClass();
			Method mtd = clz.getMethod("setDockIconImage", new Class[] {
					Image.class });
			mtd.invoke(app, new Object[] { mainFrame.icon });

		} catch (Exception ex) {
			// サポートされていない場合は単に無視する.
		}
	}
}
