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

	void setHigherGroup(PEDBGroupLabel l) {
		higherGroup = l;
		main().butler().printConsole(String.format("%s is set as higher group",
				l));
		repaint();

	}

	@Override protected void createGroupLabel(Group group, int level) {
		super.createGroupLabel(group, level);

		// 追加 〜頂点〜
		if (group != null && group.topNote != null) {
			final Rectangle topr = getLabelBound(group.topNote, level);
			final PEDBTopNoteLabel toplabel = createTopNoteLabel(group
					.getTopNote(), topr);
			toplabel.setBackground(Color.red);// 色の変更
			toplabel.setController(main);
			group.setLevel(level);
			add(toplabel); // 描画
		}

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

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override protected void drawHierarchyLine(final Graphics2D g2) {
		for (final GroupLabel l : getGrouplist()) {
			drawHierarchyLine(g2, l, l.child(getGrouplist()));
		}
	}

	/**
	 * 頂点音ラベルを生成します。
	 *
	 * @since Oct. 13th, 2018
	 * @author anan
	 */
	private PEDBTopNoteLabel createTopNoteLabel(NoteData topNote,
			Rectangle topr) {
		final PEDBTopNoteLabel label = new PEDBTopNoteLabel(topNote, topr);
		return label;
	}

	/**
	 * 階層構造のあるグループを曲線で結びつけ描画します。
	 */
	private void drawStructureEditLine(Graphics g) {
		final MouseActionListener m = getMouseActions();
		final Rectangle r = higherGroup.getBounds();
		final int x = r.x + r.getSize().width / 2;
		final int y = (int) r.getMaxY();
		g.drawLine(x, y, m.getMousePoint().x, m.getMousePoint().y);
	}

	/**
	 * 頂点音ラベルのサイズを求めます。
	 *
	 * @author anan
	 *
	 * @param topNote
	 * @param level
	 * @return
	 */
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
