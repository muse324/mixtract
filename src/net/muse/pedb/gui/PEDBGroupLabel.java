package net.muse.pedb.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.gui.GLMouseActionListener;
import net.muse.gui.GroupLabel;
import net.muse.gui.KeyActionListener;
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

	public PEDBGroupLabel child(ArrayList<GroupLabel> grouplist) {
		if (child() == null) {
			for (GroupLabel l : grouplist) {
				if (group().hasChild() && group().child().equals(l.group())) {
					setChild(l);
					break;
				}
			}
		}
		return (PEDBGroupLabel) child();
	}

	@Override protected KeyActionListener createKeyActionListener(
			MuseApp main) {
		return new KeyActionListener(main, this) {

			@Override public PEDBStructureEditor main() {
				return (PEDBStructureEditor) super.main();
			}

			@Override public PEDBGroupLabel owner() {
				return (PEDBGroupLabel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_H:
					setHigherGroup(owner());
					break;
				case KeyEvent.VK_ESCAPE:
					setHigherGroup(null);
				}
			}

			protected void setHigherGroup(PEDBGroupLabel owner) {
				main().getFrame().getGroupingPanel().setHigherGroup(owner);
			}

		};
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
