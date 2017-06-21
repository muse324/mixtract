package net.muse.gui.command;

import java.awt.Component;

import net.muse.data.KeyMode;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.gui.command.MixtractCommand;

/**
 * <h1>SetKeyModeCommand</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose <address>CrestMuse Project,
 *         JST</address> <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/12/21
 */
public class SetKeyModeCommand extends MixtractCommand {

	private static KeyMode newMode;

	/**
	 * @param valueOf
	 */
	public static void setSelectedKeyMode(KeyMode mode) {
		newMode = mode;
	}

	/**
	 * @param lang
	 */
	public SetKeyModeCommand(String... lang) {
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
				l.getScoreNote().setKeyMode(newMode, l.getScoreNote()
						.fifths());
				l.repaint();
			}
		}
	}

}