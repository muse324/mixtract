package net.muse.pedb.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

import javax.swing.JOptionPane;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.gui.GroupLabel;
import net.muse.gui.GroupingPanel;
import net.muse.gui.KeyActionListener;
import net.muse.gui.MainFrame;
import net.muse.gui.MouseActionListener;
import net.muse.gui.PianoRoll;
import net.muse.pedb.data.PEDBGroup;
import net.muse.pedb.data.PEDBTuneData;

class PEDBGroupingPanel extends GroupingPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private PEDBGroupLabel higherGroup;
	LinkedList<PEDBGroupLabel> sequenceGroups = new LinkedList<>();

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (higherGroup != null) {
			drawStructureEditLine(g);
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.GroupingPanel#selectGroup(net.muse.gui.GroupLabel,
	 * boolean)
	 */
	@Override public void selectGroup(GroupLabel g, boolean flg) {
		super.selectGroup(g, flg);
		connectGroups(higherGroup, (PEDBGroupLabel) g); // higherGroupが選択済の場合、今選択されたグループと養子縁組する

		sequenceGroups.clear();
		final Component[] c = getComponents();
		Group group = g.group();
		while (group.hasNext()) {
			final PEDBGroupLabel l = searchLabel((Group) group.next(), c);
			if (l != null)
				sequenceGroups.add(l);
			group = (Group) group.next();
		}
		repaint();
	}

	private void connectGroups(PEDBGroupLabel l1, PEDBGroupLabel l2) {
		if (l1 == null || l2 == null)
			return;
		if (l1.group().getLevel() == l2.group().getLevel())
			combineGroups(l1, l2, "combine");
		else
			adoptGroups(l1, l2, "adopt");
		setHigherGroup(null);
	}

	/**
	 * 二つのグループを連結し、親(上位階層)グループを生成します。
	 *
	 * @author hashida
	 * @since Oct. 14th, 2018
	 *
	 * @param l1 ひとつ目のグループラベル
	 * @param l2 ふたつ目のグループラベル
	 * @param mesg 接続関係を文字列で
	 */
	private void combineGroups(PEDBGroupLabel l1, PEDBGroupLabel l2,
			String mesg) {
		PEDBGroupLabel pre, pro;
		// 前後関係の確認
		if (l1.getX() < l2.getX()) {
			pre = l1;
			pro = l2;
		} else {
			pre = l2;
			pro = l1;
		}
		// preにもう後続グループが接続されていたら、その最後尾グループまで移動させる
		while (pre.hasNext()) {
			main().butler().printConsole(String.format("%s moved to -> %s", pre,
					pre.next()));
			pre = pre.next();
		}
		// preにもう先行グループが接続されていたら、その先頭グループまで移動させる
		while (pro.hasPrevious()) {
			main().butler().printConsole(String.format("back to %s <- %s", pro,
					pro.previous()));
			pro = pro.next();
		}
		// preとproが隣接していなかったら接続不可
		if (pre.group().getEndNote().offset() != pro.group().getBeginNote()
				.onset()) {
			cancelConnectiongGroups();
			return;
		}
		// -- 接続する -------
		main().butler().printConsole(String.format("%s -> %s %s", pre, pro,
				mesg));
		pre.setNext(pro);
		pre.group().setNext(pro.group());
		// 親グループを生成
		PEDBGroup p = new PEDBGroup(pre.group().getBeginNote(), pro.group()
				.getEndNote(), GroupType.PARENT);
		// TODO preに親がすでにあったら？ -> 中間層グループにする
		p.setChild(pre.group());
		data().getMiscGroup().remove(pre.group());
		data().getMiscGroup().remove(pro.group());
		data().addMiscGroupList(p);
		createGroupLabel(p, pre.group().getLevel());
		pre.group().changeLevel(-1);
		pro.group().changeLevel(-1);
		repaint();
	}

	private void cancelConnectiongGroups() {
		main().butler().printConsole("cancel connecting");
		main().butler().notifyDeselectGroup();
	}

	public void setHigherGroup(PEDBGroupLabel l) {
		higherGroup = l;
		main().butler().printConsole(String.format("%s is set as higher group",
				l));
		repaint();
	}

	void moveLabels(MouseEvent e, Point mousePoint, Rectangle bounds,
			boolean shiftKeyPressed, boolean mousePressed) {
		for (final PEDBGroupLabel l : sequenceGroups) {
			l.moveLabelVertical(e, mousePoint, l.getBounds(), shiftKeyPressed,
					mousePressed);
			// main().butler().printConsole(String.format("%s moved to %s", l,
			// mousePoint));
		}
		repaint();
	}

	/**
	 * ２つのグループを親子関係として縁組します
	 *
	 * @author hashida
	 * @since Oct. 14th, 2018
	 *
	 * @param l1 ひとつ目のグループラベル
	 * @param l2 ふたつ目のグループラベル
	 * @param mesg 接続関係を文字列で
	 */
	private void adoptGroups(PEDBGroupLabel l1, PEDBGroupLabel l2,
			String mesg) {
		PEDBGroupLabel parent, child;
		// 階層の上下関係を確認
		if (l1.group().getLevel() < l2.group().getLevel()) {
			parent = l1;
			child = l2;
		} else {
			parent = l2;
			child = l1;
		}

		// parentがすでに子グループを持っていたら？
		if (parent.hasChild()) {
			int res = JOptionPane.showConfirmDialog(this, String.format(
					"%sはすでに子グループ%sと接続されています。%sと付け替えますか？", parent, parent
							.child(), child));
			if (res != JOptionPane.YES_OPTION) {
				cancelConnectiongGroups();
				return;
			}
		}
		// 接続する
		main().butler().printConsole(String.format("%s -> %s %s", parent, child,
				mesg));
		parent.setChild(child);
		parent.group().setChild(child.group());
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
		for (final GroupLabel l : getGrouplist()) {
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
	 * @since Oct 13th, 2018
	 */
	private RoundRectangle2D getTopNoteLabelBound(NoteData topNote, int level) {
		final double y = setLabelY(level);
		double x, w;
		x = MainFrame.getXOfNote(topNote.onset()) + PianoRoll.getDefaultAxisX();
		w = MainFrame.getXOfNote(topNote.duration());
		final RoundRectangle2D r = new RoundRectangle2D.Double(x, y, w,
				LABEL_HEIGHT - LEVEL_PADDING, 20.0, 20.0);

		System.out.println("x = " + r.getX());
		System.out.println("y = " + r.getY());
		System.out.println("w = " + r.getWidth());
		System.out.println("h = " + r.getHeight());
		System.out.println("h = " + r.getArcWidth());
		System.out.println("h = " + r.getArcHeight());
		return r;
	}

	private PEDBGroupLabel searchLabel(Group g, Component[] components) {
		for (final Component c : components) {
			if (!(c instanceof PEDBGroupLabel))
				continue;
			final PEDBGroupLabel l = (PEDBGroupLabel) c;
			if (g.equals(l.group()))
				return l;
		}
		return null;
	}

}
