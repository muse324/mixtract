package net.muse.mixtract.command;

import java.awt.Component;

import net.muse.gui.NoteLabel;

public class ChangePartCommand extends MixtractCommand {
	private static final int defaultSize = 4;
	public static int partSize = defaultSize;
	private static int newPart;

	public static void setChangePartTo(int part) {
		newPart = part;
	}

	public static void setPartSize(int size) {
		partSize = defaultSize;
		if (size > partSize)
			partSize = size;
	}

	public ChangePartCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		for (Component c : frame().getPianoroll().getComponents()) {
			NoteLabel l = (NoteLabel) c;
			if (l.isSelected()) {
				l.setPartNumber(newPart);
				l.repaint();
			}
		}
	}

}