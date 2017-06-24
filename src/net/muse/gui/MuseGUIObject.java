package net.muse.gui;

import javax.swing.*;

import net.muse.misc.MuseObject;

/*
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 * <address>http://www.m-use.net/</address>
 * <address>hashida@kwansei.ac.jp</address>
 * @since 2009/01/30
 */
public abstract class MuseGUIObject<F extends JFrame> extends MuseObject {

	private static boolean showGUI = true;
	protected F frame;
	protected JLabel splashLabel;
	protected JWindow splashScreen;

	/**
	 *
	 */
	public MuseGUIObject() {
		super();
	}

	protected void createSplashScreen(String path) {
		ImageIcon img = new ImageIcon(getClass().getResource(path));
		splashLabel = new JLabel(img);
		splashLabel.setBorder(BorderFactory.createEtchedBorder());
		splashScreen = new JWindow(getFrame());
		splashScreen.getContentPane().add(splashLabel);
		splashScreen.setLocationRelativeTo(null);
	}

	public F getFrame() {
		return frame;
	}

	/**
	 * アプリケーション起動時にスプラッシュスクリーンを数秒表示します。<br/>
	 * showSplashScreen()が呼び出された後、自動で呼び出されるので、プログラミング時に明示的に呼び出す必要はありません。
	 */
	protected void hideSplash() {
		splashScreen.setVisible(false);
		splashScreen.dispose();
		splashLabel = null;
	}

	/**
	 * アプリケーション起動時にスプラッシュスクリーンを数秒表示します。
	 */
	protected void showSplashScreen() {
		splashScreen.setAlwaysOnTop(true);
		splashScreen.pack();
		splashScreen.setVisible(true);
	}

	/**
	 * @param showGUI セットする showGUI
	 */
	public static void setShowGUI(boolean showGUI) {
		MuseGUIObject.showGUI = showGUI;
	}

	/**
	 * 起動時にGUIを用いるかどうかを判別します。プロパティファイル内のSHOW_GUIによる値で判別します。
	 *
	 * @return showGUI
	 */
	public static boolean isShowGUI() {
		return showGUI;
	}

}
