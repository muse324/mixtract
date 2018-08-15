package net.muse.command;

import java.awt.Component;

import net.muse.gui.NoteLabel;
import net.muse.misc.Util;

/**
 * <h1>SetKeyCommand</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose <address>CrestMuse Project,
 *         JST</address> <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/12/21
 */
public class SetKeyCommand extends MuseAppCommand {

	private static String newKey = null;

	/**
	 * @param text
	 */
	public static void setSelectedKey(String text) {
		newKey = text;
	}

	/**
	 * @param lang
	 */
	public SetKeyCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		for (Component c : frame().getPianoroll().getComponents()) {
			NoteLabel l = (NoteLabel) c;
			if (l.isSelected()) {
				l.getScoreNote().setFifths(Util.valueOfFifths(newKey));
				l.repaint();
			}
		}
	}

}