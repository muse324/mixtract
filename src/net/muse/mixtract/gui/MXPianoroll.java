package net.muse.mixtract.gui;

import java.awt.Component;
import java.awt.Rectangle;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.gui.GroupLabel;
import net.muse.gui.KeyActionListener;
import net.muse.gui.NoteLabel;
import net.muse.gui.PianoRoll;
import net.muse.gui.PianoRollActionListener;
import net.muse.misc.MuseObject;
import net.muse.mixtract.data.MXGroup;

public class MXPianoroll extends PianoRoll {

	private static final long serialVersionUID = 1L;
	boolean displayApex = false;

	protected MXPianoroll(Mixtract app) {
		super(app);
	}
	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#deselect(net.muse.gui.GroupLabel)
	 */
	@Override public void deselect(GroupLabel g) {
		setDisplayApex(false);
		super.deselect(g);
	}

	@Override public void editGroup(GroupLabel g) {
		assert g.group() instanceof MXGroup;
		((MXGroup) g.group()).setModified(true);
	}

	@Override public void selectGroup(GroupLabel g, boolean flg) {
		super.selectGroup(g, flg);
		setDisplayApex(true);
		assert g.group() instanceof MXGroup;
		((MXGroup) g.group()).extractApex();
	}

	/* (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#createKeyActions(net.muse.misc.MuseObject)
	 */
	protected KeyActionListener createKeyActions(MuseObject app) {
		return new KeyActionListener(app, this) {

			@Override public Mixtract app() {
				return (Mixtract) super.app();
			}

			@Override public MXPianoroll owner() {
				return (MXPianoroll) super.owner();
			}

		};
	}

	protected NoteLabel createNoteLabel(final NoteData note,
			final Rectangle r) {
		return new MXNoteLabel(note, r);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.PianoRoll#createPianoRollMouseAction(net.muse.app.MuseApp)
	 */
	@Override protected PianoRollActionListener createPianoRollMouseAction(
			MuseApp app) {
		return new PianoRollActionListener(app, this) {

		};
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#group()
	 */
	@Override protected MXGroupLabel group() {
		return (MXGroupLabel) super.group();
	}

	@Override protected void makeNoteLabel(Group group) {
		if (group == null)
			return;
		MXGroup g = (MXGroup) group;
		makeNoteLabel(g.getChildFormerGroup());
		makeNoteLabel(g.getChildLatterGroup());

		makeNoteLabel(g.getBeginNote(), false);
	}

	// protected void makeNoteLabel(MXGroup group) {
	// if (group.hasChild()) {
	// makeNoteLabel(group.getChildFormerGroup());
	// makeNoteLabel(group.getChildLatterGroup());
	// } else
	// makeNoteLabel(group.getBeginNote(), false);
	// }

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#notelist()
	 */
	protected MXNoteLabel notelist() {
		return (MXNoteLabel) super.notelist();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#selectGroup(net.muse.mixtract.data.Group)
	 */
	@Override protected void selectGroup(Group group) {
		if (group == null)
			return;
		MXGroup g = (MXGroup) group;
		selectGroup(g.getChildFormerGroup());
		selectGroup(g.getChildLatterGroup());
		for (Component c : getComponents()) {
			NoteLabel l = (NoteLabel) c;
			selectNote(l, group.getBeginNote(), group.getEndNote());
		}
	}

	/**
	 * @param b
	 */
	protected void setDisplayApex(boolean flg) {
		displayApex = flg;
	}

}
