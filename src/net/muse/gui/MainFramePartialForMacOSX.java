package net.muse.gui;

import java.awt.Image;
import java.lang.reflect.Method;

import com.apple.eawt.*;
import com.apple.eawt.AppEvent.*;

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
			@Override public void handleAbout(AboutEvent arg0) {
				mainFrame.onAbout();
			}
		});
		app.setQuitHandler(new QuitHandler() {
			@Override public void handleQuitRequestWith(QuitEvent arg0,
					QuitResponse arg1) {
				mainFrame.quit();
			}
		});
		app.setPreferencesHandler(new PreferencesHandler() {

			@Override public void handlePreferences(PreferencesEvent arg0) {
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
