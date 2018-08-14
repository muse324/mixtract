package net.muse.pedb.data;

import net.muse.data.*;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;

public class PEDBGroup extends MXGroup {


	public PEDBGroup(NoteData beginNote, NoteData endNote, GroupType type) {
		super(beginNote, endNote, type);
	}

	@Override
	public void extractApex() {
		if (!isModified())
			return;
		setModified(false);
		// do nothing
	}
	
	
	public void setTopNote(MXNoteData n) {
		topNote = n;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override
	public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

}
