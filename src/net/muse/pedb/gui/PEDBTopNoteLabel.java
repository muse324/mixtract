package net.muse.pedb.gui;

import java.awt.Rectangle;

import net.muse.data.NoteData;

public class PEDBTopNoteLabel extends PEDBNoteLabel {


	/* イベント制御 */
	private boolean startEdit;
	private boolean endEdit;


	//追加

	public PEDBTopNoteLabel(NoteData topNote, Rectangle r) {
		super(topNote, r);
	}
}
