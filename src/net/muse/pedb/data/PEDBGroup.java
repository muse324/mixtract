package net.muse.pedb.data;

import net.muse.data.*;
import net.muse.mixtract.data.MXGroup;

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

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override
	public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

}
