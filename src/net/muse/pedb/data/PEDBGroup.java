package net.muse.pedb.data;

import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;

public class PEDBGroup extends MXGroup {


	public PEDBGroup(NoteData beginNote, NoteData endNote, GroupType type) {
		super(beginNote, endNote, type);
	}

	@Override public void extractApex() {
		if (!isModified())
			return;
		setModified(false);
		// do nothing
	}
	
	
	public void setTopNote(MXNoteData n) {
		topNote = n;//カーソルが音符に合わせた時(クリックではない)、その音符の情報値を出力する。
		System.out.println(n);
	}

}
