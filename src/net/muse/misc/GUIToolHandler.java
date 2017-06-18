package net.muse.misc;

import java.awt.Container;

import javax.swing.JMenuBar;

public interface GUIToolHandler {
//	public void createButton(final JComponent obj, String label,
//			String actionCommand, boolean editable, ActionListener target);

	public JMenuBar createMenuBar();
	public Container createContentPane();
}
