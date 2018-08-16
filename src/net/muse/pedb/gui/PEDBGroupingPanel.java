package net.muse.pedb.gui;

import java.awt.Rectangle;

import net.muse.data.Group;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.MXGroupingPanel;
import net.muse.pedb.data.PEDBTuneData;

public class PEDBGroupingPanel extends MXGroupingPanel {

	@Override protected PEDBGroupLabel createGroupLabel(Group group,
			Rectangle r) {
		return new PEDBGroupLabel((MXGroup) group, r);
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

}
