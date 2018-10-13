package net.muse.pedb.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

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

public class PEDBGroupingPanel extends GroupingPanel {
	int i = 0;// 追加
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

		// 頂点音ラベルを生成する
		createTopLabel(group, level);
		// create a new group-label
		createGroupLabel(group, level);

		createHierarchicalGroupLabel(group.child(), level + 1);
	}

	@Override protected void createNonHierarchicalGroupLabel() {
		int level = getMaximumGroupLevel() + 1;
		for (final Group g : data().getMiscGroup()) {
			if (level < g.getLevel())
				level = g.getLevel() + 1;
			createTopLabel(g, level);
			if (g.hasChild())
				createTopLabel(g.child(), level + 1);
			createGroupLabel(g, level);

			createGroupLabel(g.child(), level + 1);

		}
	}

	protected void createTopLabel(Group group, int level) {
		if (group != null && group.topNote != null) {
			final Rectangle topr = getLabelBound(group.getTopNote(), level);
			final GroupLabel toplabel = createTopNoteLabel(group.getTopNote(),
					topr);
			System.out.println(toplabel);
			toplabel.setBackground(Color.red);// 色の変更
			toplabel.setController(main);
			group.setLevel(level);
			add(toplabel); // 描画
		}
	}

	private PEDBTopNoteLabel createTopNoteLabel(NoteData topNote,
			Rectangle topr) {
		final PEDBTopNoteLabel label = new PEDBTopNoteLabel(topNote, topr);
		return label;
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
				}
			}

		};
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override protected void drawHierarchyLine(final Graphics2D g2) {
		for (final GroupLabel l : getGrouplist()) {
			drawHierarchyLine(g2, l, l.child(getGrouplist()));
		}
	}

	private void drawStructureEditLine(Graphics g) {
		final MouseActionListener m = getMouseActions();
		final Rectangle r = higherGroup.getBounds();
		final int x = r.x + r.getSize().width / 2;
		final int y = (int) r.getMaxY();
		g.drawLine(x, y, m.getMousePoint().x, m.getMousePoint().y);
	}

	// 追加
	private Rectangle getLabelBound(NoteData topNote, int level) {
		final int y = setLabelY(level);
		int x, w;
		x = MainFrame.getXOfNote(topNote.realOnset()) + PianoRoll
				.getDefaultAxisX();
		w = MainFrame.getXOfNote((int) topNote.duration());
		final Rectangle r = new Rectangle(x, y, w, LABEL_HEIGHT
				- LEVEL_PADDING);
		return r;
	}

}
