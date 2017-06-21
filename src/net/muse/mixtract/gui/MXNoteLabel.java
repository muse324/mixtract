package net.muse.mixtract.gui;

import java.awt.Color;
import java.awt.Rectangle;

import net.muse.data.GroupNote;
import net.muse.gui.NoteLabel;
import net.muse.gui.PartColor;
import net.muse.mixtract.data.MXNoteData;

public class MXNoteLabel extends NoteLabel {

	private static final long serialVersionUID = 1L;

	public MXNoteLabel(GroupNote note, Rectangle r) {
		super(note, r);
	}

	@Deprecated public Color getApexColor() {
		double apex = ((MXNoteData) getGroupNote().getNote()).getApexScore();
		final int c = (int) (255 * (1. - apex));
		return new Color(255, c, c);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.NoteLabel#setSelected(boolean)
	 */
	@Override public void setSelected(boolean isSelected) {
		super.setSelected(isSelected);
		if (!isSelected)
			return;
		if (!(getParent() instanceof MXPianoroll))
			return;

		MXPianoroll p = (MXPianoroll) getParent();
		if (p.displayApex) {
			MXNoteData n = (MXNoteData) getGroupNote().getNote();
			double w = 1. - n.getApexScore();
			int r = PartColor.SELECTED_COLOR.getRed();
			int c = (int) (255 * w);
			setBackground(new Color(r, c, c));
		}
		repaint();
	}

}
