package net.muse.gui;

import javax.swing.*;

import net.muse.misc.MuseObject;

/*
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 * <address>https://m-use.net/</address>
 * <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/01/30
 */
public abstract class MuseGUIObject<F extends JFrame> extends MuseObject {

	private static boolean showGUI = true;
	private F frame;
	private JWindow splashScreen;

	protected MuseGUIObject() {
		super();
	}

	protected void createSplashScreen(String path) {
		ImageIcon img = new ImageIcon(getClass().getResource(path));
		JLabel splashLabel = new JLabel(img);
		splashLabel.setBorder(BorderFactory.createEtchedBorder());
		if (splashScreen == null) {
			splashScreen = new JWindow(getFrame());
			splashScreen.getContentPane().add(splashLabel);
		}
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
		if (splashScreen != null) {
			splashScreen.setVisible(false);
			splashScreen.dispose();
		}
	}

	/**
	 * アプリケーション起動時にスプラッシュスクリーンを数秒表示します。
	 */
	protected void showSplashScreen() {
		if (splashScreen == null)
			return;
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

	public void setFrame(F frame) {
		this.frame = frame;
	}

}
