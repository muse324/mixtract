package net.muse.gui;

import java.awt.Image;
import java.awt.desktop.*;
import java.lang.reflect.Method;

import com.apple.eawt.Application;

public class MainFramePartialForMacOSX {
	private MainFramePartialForMacOSX() {
		super();
	}

	public static void setupScreenMenu(final MainFrame mainFrame) {
		if (mainFrame == null) {
			throw new IllegalArgumentException();
		}

		Application app = Application.getApplication();
		app.setAboutHandler(new AboutHandler() {
			@Override
			public void handleAbout(AboutEvent e) {
				mainFrame.onAbout();
			}
		});
		app.setQuitHandler(new QuitHandler() {

			@Override
			public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
				mainFrame.quit();
			}
		});
		app.setPreferencesHandler(new PreferencesHandler() {

			@Override
			public void handlePreferences(PreferencesEvent e) {
				mainFrame.onPreference();
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
