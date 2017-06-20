package net.muse.mixtract.gui;

import java.awt.Rectangle;

import net.muse.gui.GroupingPanel;
import net.muse.mixtract.data.Group;

public class MXGroupingPanel extends GroupingPanel {

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.GroupingPanel#createGroupLabel(net.muse.mixtract.data.Group,
	 * java.awt.Rectangle)
	 */
	@Override protected MXGroupLabel createGroupLabel(Group group, Rectangle r) {
		return new MXGroupLabel(group, r);
	}

	private static final long serialVersionUID = 1L;

}
