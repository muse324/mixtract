package net.muse.pedb.gui;

import java.awt.Rectangle;

import net.muse.data.NoteData;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.gui.MXNoteLabel;
import net.muse.pedb.data.PEDBNoteData;

/**
 * parent(),child()は使いません。
 *
 * @since 2018 summer
 * @author hashida
 *
 */
public class PEDBNoteLabel extends MXNoteLabel {

	public PEDBNoteLabel(NoteData note, Rectangle r) {
		super(note, r);
	}

	@Deprecated @Override public NoteLabel child() {
		return null;
	}

	@Override public PEDBNoteData getScoreNote() {
		return (PEDBNoteData) super.getScoreNote();
	}

	@Deprecated @Override public boolean hasChild() {
		return false;
	}

	@Deprecated @Override public boolean hasParent() {
		return false;
	}

	@Override public PEDBNoteLabel next() {
		return (PEDBNoteLabel) super.next();
	}

	@Deprecated @Override public NoteLabel parent() {
		return null;
	}

	@Override public PEDBNoteLabel prev() {
		return (PEDBNoteLabel) super.prev();
	}

	@Deprecated @Override public void setChild(NoteLabel child) {
		super.setChild(child);
	}

	@Deprecated @Override public void setParent(NoteLabel parent) {
		super.setParent(parent);
	}

}
