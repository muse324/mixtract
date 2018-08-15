package net.muse.pedb.gui;

import java.awt.Rectangle;

import net.muse.data.NoteData;
import net.muse.mixtract.gui.MXNoteLabel;
import net.muse.pedb.data.PEDBNoteData;

public class PEDBNoteLabel extends MXNoteLabel {

	public PEDBNoteLabel(NoteData note, Rectangle r) {
		super(note, r);
	}

	@Override public PEDBNoteData getScoreNote() {
		return (PEDBNoteData) super.getScoreNote();
	}

}
