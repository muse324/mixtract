package net.muse.pedb.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.gui.GroupingPanel;
import net.muse.gui.KeyActionListener;
import net.muse.gui.MouseActionListener;
import net.muse.pedb.data.PEDBTuneData;

public class PEDBGroupingPanel extends GroupingPanel {

	private PEDBGroupLabel higherGroup;

	protected void createHierarchicalGroupLabel(Group group, int level) {
		if (group == null)
			return;
		createHierarchicalGroupLabel(group.child(), level + 1);
		createHierarchicalGroupLabel((Group) group.next(), level);
		// create a new group-label
		createGroupLabel(group, level);
	}

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
			readTuneData();
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
		return new PEDBGroupLabel(group, r);
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
				case KeyEvent.VK_R:
					readTuneData();
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
