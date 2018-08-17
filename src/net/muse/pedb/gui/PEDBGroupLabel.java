package net.muse.pedb.gui;

import java.awt.Rectangle;

import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.gui.GLMouseActionListener;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.MXGroupLabel;
import net.muse.pedb.data.PEDBGroup;

public class PEDBGroupLabel extends MXGroupLabel {

	public PEDBGroupLabel(MXGroup group, Rectangle r) {
		super(group, r);
	}

	@Override public PEDBGroup group() {
		return (PEDBGroup) super.group();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.MXGroupLabel#createMouseActionListener(net.muse.app
	 * .MuseApp)
	 */
	@Override protected GLMouseActionListener createMouseActionListener(
			MuseApp main) {
		return new GLMouseActionListener(main, this) {
			/*
			 * (非 Javadoc)
			 * @see
			 * net.muse.gui.GLMouseActionListener#doubleClicked(net.muse.data.
			 * Group)
			 */
			protected void doubleClicked(Group gr) {
				// do nothing
			}
		};
	}

}
