package net.muse.pedb.data;

import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;

public class PEDBGroup extends MXGroup {

	public PEDBGroup(NoteData n, int i, GroupType type) {
		super(n, i, type);
	}

	public PEDBGroup(NoteData beginNote, NoteData endNote, GroupType type) {
		super(beginNote, endNote, type);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	@Override public void extractApex() {
		if (!isModified())
			return;
		setModified(false);
		// do nothing
	}

	public void setTopNote(MXNoteData n) {
		topNote = n;
	}

}
