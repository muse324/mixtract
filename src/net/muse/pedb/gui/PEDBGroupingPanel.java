package net.muse.pedb.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.gui.KeyActionListener;
import net.muse.gui.MouseActionListener;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.MXGroupingPanel;
import net.muse.pedb.data.PEDBTuneData;

public class PEDBGroupingPanel extends MXGroupingPanel {

	private PEDBGroupLabel higherGroup;

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (higherGroup != null) {
			drawStructureEditLine(g);
		}
	}

	@Override public void selectGroup(GroupLabel g, boolean flg) {
		super.selectGroup(g, flg);
		if (higherGroup != null && g != null) {
			main().butler().printConsole(String.format("%s -> %s connect",
					higherGroup, g));
			higherGroup.setChild(g);
			higherGroup.group().setChild(getSelectedGroup().group());
			setHigherGroup(null);
			repaint();
		}
	}

	public void setHigherGroup(PEDBGroupLabel l) {
		higherGroup = l;
		main().butler().printConsole(String.format("%s is set as higher group",
				l));
		repaint();

	}

	@Override protected PEDBGroupLabel createGroupLabel(Group group,
			Rectangle r) {
		return new PEDBGroupLabel((MXGroup) group, r);
	}

	@Override protected KeyActionListener createKeyActionListener(
			MuseApp main) {
		return new KeyActionListener(main, this) {

			@Override public PEDBStructureEditor main() {
				return (PEDBStructureEditor) super.main();
			}

			@Override public PEDBGroupingPanel owner() {
				return (PEDBGroupingPanel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					setHigherGroup(null);
					break;
				}
			}

		};
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override protected void drawHierarchyLine(final Graphics2D g2) {
		for (GroupLabel l : getGrouplist()) {
			drawHierarchyLine(g2, l, l.child(getGrouplist()));
		}
	}

	private void drawStructureEditLine(Graphics g) {
		MouseActionListener m = getMouseActions();
		Rectangle r = higherGroup.getBounds();
		int x = r.x + r.getSize().width / 2;
		int y = (int) r.getMaxY();
		g.drawLine(x, y, m.getMousePoint().x, m.getMousePoint().y);
	}

}
