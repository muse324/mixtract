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
import net.muse.pedb.data.PEDBGroup;

public class PEDBGroupLabel extends GroupLabel {

	public PEDBGroupLabel(Group group, Rectangle r) {
		super(group, r);
	}

	public PEDBGroupLabel() {
		super();
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

	/* (非 Javadoc)
	 * @see net.muse.gui.GroupLabel#createKeyActionListener(net.muse.app.MuseApp)
	 */
	@Override protected KeyActionListener createKeyActionListener(
			MuseApp app) {
		return new KeyActionListener(app, this) {

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			@Override public PEDBGroupLabel owner() {
				return (PEDBGroupLabel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				super.keyPressedOption(e);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_H:
					setHigherGroup(owner());
					break;
				case KeyEvent.VK_ESCAPE:
					setHigherGroup(null);
				}
			}

			protected void setHigherGroup(PEDBGroupLabel owner) {
				app().getFrame().getGroupingPanel().setHigherGroup(owner);
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
			MuseApp app) {
		return new GLMouseActionListener(app, this) {
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
