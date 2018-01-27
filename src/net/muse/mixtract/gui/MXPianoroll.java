package net.muse.mixtract.gui;

import java.awt.Rectangle;

import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.GroupNote;
import net.muse.gui.*;
import net.muse.mixtract.data.MXGroup;

public class MXPianoroll extends PianoRoll {

	private static final long serialVersionUID = 1L;

	boolean displayApex = false;

	MXPianoroll() {
		super();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#deselect(net.muse.gui.GroupLabel)
	 */
	@Override
	public void deselect(GroupLabel g) {
		setDisplayApex(false);
		super.deselect(g);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#selectGroup(net.muse.mixtract.data.Group)
	 */
	@Override
	public void selectGroup(Group group) {
		setDisplayApex(true);
		((MXGroup) group).extractApex();
		super.selectGroup(group);
	}

	protected NoteLabel createNoteLabel(final GroupNote note,
			final Rectangle r) {
		return new MXNoteLabel(note, r);
	}

	/**
	 * @param b
	 */
	protected void setDisplayApex(boolean flg) {
		displayApex = flg;
	}

	protected void makeNoteLabel(MXGroup group) {
		if (group.hasChild()) {
			makeNoteLabel(group.getChildFormerGroup());
			makeNoteLabel(group.getChildLatterGroup());
		} else
			makeNoteLabel(group.getBeginGroupNote(), false);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.PianoRoll#createPianoRollMouseAction(net.muse.app.MuseApp)
	 */
	@Override
	protected PianoRollAction createPianoRollMouseAction(MuseApp app) {
		return new PianoRollAction(app, this) {

		};
	}

}
