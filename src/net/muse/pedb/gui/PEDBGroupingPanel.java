package net.muse.pedb.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.gui.GroupLabel;
import net.muse.gui.GroupingPanel;
import net.muse.gui.KeyActionListener;
import net.muse.gui.MainFrame;
import net.muse.gui.MouseActionListener;
import net.muse.gui.PianoRoll;
import net.muse.pedb.data.PEDBTuneData;

class PEDBGroupingPanel extends GroupingPanel {
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


	@Override protected void createHierarchicalGroupLabel(Group group,
			int level) {
		if (group == null)
			return;
		createHierarchicalGroupLabel(group.child(), level + 1);
		createHierarchicalGroupLabel((Group) group.next(), level);
		// create a new group-label
		createGroupLabel(group, level);
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

	@Override protected void createNonHierarchicalGroupLabel() {
		int level = getMaximumGroupLevel() + 1;
		for (final Group g : data().getMiscGroup()) {
			if (level < g.getLevel())
				level = g.getLevel() + 1;
			createTopNoteLabel(g, level);
			if (g.hasChild())
				createTopNoteLabel(g.child(), level + 1);
			createGroupLabel(g, level);

			createGroupLabel(g.child(), level + 1);
		}
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override protected void drawHierarchyLine(final Graphics2D g2) {
		for (GroupLabel l : getGrouplist()) {
			drawHierarchyLine(g2, l, l.child(getGrouplist()));
		}
	}

	/**
	 * 頂点音ラベルを生成します。
	 *
	 * @author anan
	 * @since Oct 13th, 2018
	 */
	private void createTopNoteLabel(Group group, int level) {
		if (group == null || group.topNote == null)
			return;

		// 以下は、group が存在し、かつ当該groupに頂点音が存在する場合にのみ実行される
		final RoundRectangle2D topr = getTopNoteLabelBound(group.getTopNote(),
				level);
		final GroupLabel toplabel = new PEDBTopNoteLabel(group.getTopNote(),
				topr);
		System.out.println(toplabel);
		toplabel.setBackground(Color.red);// 色の変更
		toplabel.setController(main);
		group.setLevel(level);
		add(toplabel); // 描画
		createTopNoteLabel((Group) group.next(), level);
	}

	private void drawStructureEditLine(Graphics g) {
		MouseActionListener m = getMouseActions();
		Rectangle r = higherGroup.getBounds();
		int x = r.x + r.getSize().width / 2;
		int y = (int) r.getMaxY();
		g.drawLine(x, y, m.getMousePoint().x, m.getMousePoint().y);
	}

	/**
	 * 頂点音ラベルのサイズを求めます。
	 *
	 * @author anan
	 * @since Oct 13th, 2018
	 */
	private RoundRectangle2D getTopNoteLabelBound(NoteData topNote, int level) {
		final double y = setLabelY(level);
		double x, w;
		x = MainFrame.getXOfNote(topNote.onset()) + PianoRoll
				.getDefaultAxisX();
		w = MainFrame.getXOfNote((double) topNote.duration());
		final RoundRectangle2D r = new RoundRectangle2D.Double(x, y, w,
				LABEL_HEIGHT - LEVEL_PADDING, 20.0, 20.0);

		System.out.println("x = "+r.getX());
		System.out.println("y = "+r.getY());
		System.out.println("w = "+r.getWidth());
		System.out.println("h = "+r.getHeight());
		System.out.println("h = "+r.getArcWidth());
		System.out.println("h = "+r.getArcHeight());
		return r;
	}

}
