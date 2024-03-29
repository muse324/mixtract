package net.muse.mixtract.command;

import java.awt.Component;

import net.muse.data.Harmony;
import net.muse.gui.NoteLabel;

public class SetChordCommand extends MixtractCommand {
	private static final int defaultSize = 4;
	private static int partSize = defaultSize;
	private static int newPart;
	private static Harmony selectedChord;

	public static void setSelectedChord(Harmony c) {
		selectedChord = c;
	}

	public SetChordCommand(String... string) {
		super(string);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		for (Component c : frame().getPianoroll().getComponents()) {
			NoteLabel l = (NoteLabel) c;
			if (l.isSelected()) {
				l.getScoreNote().setChord(selectedChord);
				l.repaint();
			}
		}
	}

}
