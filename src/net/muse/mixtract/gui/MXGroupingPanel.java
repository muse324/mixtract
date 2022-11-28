package net.muse.mixtract.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.gui.GroupingPanel;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXTuneData;

public class MXGroupingPanel extends GroupingPanel {

	protected MXGroupingPanel(MuseApp app) {
		super(app);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.GroupingPanel#createGroupLabel(net.muse.mixtract.data.Group,
	 * java.awt.Rectangle)
	 */
	@Override protected MXGroupLabel createGroupLabel(Group group,
			Rectangle r) {
		return new MXGroupLabel((MXGroup) group, r);
	}

	private static final long serialVersionUID = 1L;

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.GroupingPanel#createHierarchicalGroupLabel(net.muse.data.
	 * Group, int)
	 */
	@Override protected void createHierarchicalGroupLabel(Group group,
			int level) {
		if (group == null)
			return;
		assert group instanceof MXGroup;
		MXGroup g = (MXGroup) group;

		// create a new group-label
		// if (g.hasChild() || g.hasParent())
		createGroupLabel(g, level);

		createHierarchicalGroupLabel(g.getChildFormerGroup(), level + 1);
		createHierarchicalGroupLabel(g.getChildLatterGroup(), level + 1);
	}

	protected void createNonHierarchicalGroupLabel() {
		int level = getMaximumGroupLevel() + 1;
		for (Group group : data().getMiscGroup()) {
			assert group instanceof MXGroup;
			MXGroup g = (MXGroup) group;
			if (level < g.getLevel())
				level = g.getLevel() + 1;
			createGroupLabel(g, level);
			createGroupLabel(g.getChildFormerGroup(), level + 1);
			createGroupLabel(g.getChildLatterGroup(), level + 1);
		}
	}

	protected void drawHierarchyLine(final Graphics2D g2, GroupLabel parent,
			final GroupLabel child) {
		if (parent == null)
			return;
		if (child == null)
			return;

		drawLine(g2, parent, child);

		drawHierarchyLine(g2, child, ((MXGroupLabel) child).getChildFormer(
				getGrouplist()));
		drawHierarchyLine(g2, child, ((MXGroupLabel) child).getChildLatter(
				getGrouplist()));
	}

	protected MXTuneData data() {
		return (MXTuneData) super.data();
	}

	protected void drawHierarchyLine(final Graphics2D g2) {
		for (GroupLabel l : getGrouplist()) {
			drawHierarchyLine(g2, l, ((MXGroupLabel) l).getChildFormer(
					getGrouplist()));
			drawHierarchyLine(g2, l, ((MXGroupLabel) l).getChildLatter(
					getGrouplist()));
		}
	}

}
