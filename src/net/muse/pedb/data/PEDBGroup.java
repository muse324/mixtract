package net.muse.pedb.data;

import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.mixtract.data.MXNoteData;

public class PEDBGroup extends Group {

	public PEDBGroup(NoteData n, int i, GroupType type) {
		super(n, i, type);
	}

	public PEDBGroup(NoteData beginNote, NoteData endNote, GroupType type) {
		super(beginNote, endNote, type);
	}

	@Override public PEDBGroup child() {
		return (PEDBGroup) super.child();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	public void setTopNote(MXNoteData n) {
		topNote = n;//カーソルが音符に合わせた時(クリックではない)、その音符の情報値を出力する。
		System.out.println(n);
	}

}
