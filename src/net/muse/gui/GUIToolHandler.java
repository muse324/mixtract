package net.muse.gui;

import java.awt.Container;

import javax.swing.JMenuBar;

public interface GUIToolHandler {
	public JMenuBar createMenuBar();

	public Container createContentPane();
}
