package net.muse.pedb.data;

import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;

public class PEDBGroup extends Group {

	public PEDBGroup(NoteData n, int i, GroupType type) {
		super(n, i, type);
	}

	public PEDBGroup(NoteData beginNote, NoteData endNote, GroupType type) {
		super(beginNote, endNote, type);
		setTopNote(beginNote);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	public void changeLevel(int i) {
		setLevel(getLevel() + i);
	}

	@Override public PEDBGroup child() {
		return (PEDBGroup) super.child();
	}

	@Override public PEDBNoteData getBeginNote() {
		return (PEDBNoteData) super.getBeginNote();
	}

	@Override public PEDBNoteData getEndNote() {
		return (PEDBNoteData) super.getEndNote();
	}

	@Override public PEDBGroup getParent() {
		return (PEDBGroup) super.getParent();
	}

	@Override public PEDBNoteData getTopNote() {
		return (PEDBNoteData) super.getTopNote();
	}

	@Override public String toString() {
		String str = name() + ";" + getPartNumber() + ";";
		if (!hasChild())
			return str + notelistToString();
		PEDBGroup c = child();
		while (c != null) {
			str += c.name() + (c.hasNext() ? "," : "");
			c = (PEDBGroup) c.next();
		}
		return str;
	}

}
