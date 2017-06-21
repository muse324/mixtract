package net.muse.mixtract.gui;

import net.muse.gui.GroupLabel;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.data.Group;
import net.muse.mixtract.data.MXGroup;

public class MXPianoroll extends PianoRoll {

	boolean displayApex = false;

	MXPianoroll() {
		super();
	}

	private static final long serialVersionUID = 1L;

	/**
	 * @param b
	 */
	protected void setDisplayApex(boolean flg) {
		displayApex = flg;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#deselect(net.muse.gui.GroupLabel)
	 */
	@Override public void deselect(GroupLabel g) {
		setDisplayApex(false);
		super.deselect(g);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#selectGroup(net.muse.mixtract.data.Group)
	 */
	@Override public void selectGroup(Group group) {
		setDisplayApex(true);
		((MXGroup) group).extractApex();
		super.selectGroup(group);
	}

}
