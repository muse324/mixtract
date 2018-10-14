package net.muse.pedb.gui;

import java.awt.Rectangle;

import net.muse.data.NoteData;
import net.muse.gui.NoteLabel;
import net.muse.pedb.data.PEDBNoteData;

/**
 * parent(),child()は使いません。
 *
 * @since 2018 summer
 * @author hashida
 *
 */
public class PEDBNoteLabel extends NoteLabel {
	private static final long serialVersionUID = 1L;

	public PEDBNoteLabel(NoteData note, Rectangle r) {
		super(note, r);
	}

	@Override public PEDBNoteData getScoreNote() {
		return (PEDBNoteData) super.getScoreNote();
	}

	@Override public PEDBNoteLabel next() {
		return (PEDBNoteLabel) super.next();
	}

	@Override public PEDBNoteLabel prev() {
		return (PEDBNoteLabel) super.prev();
	}

}
