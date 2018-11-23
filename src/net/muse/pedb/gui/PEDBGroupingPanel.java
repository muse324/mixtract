package net.muse.pedb.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
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

	protected PEDBGroupingPanel(MuseApp app) {
		super(app);
	}

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
		if (g instanceof PEDBTopNoteLabel)
			return;
		connectGroups(higherGroup, (PEDBGroupLabel) g); // higherGroupが選択済の場合、今選択されたグループと養子縁組する

		sequenceGroups.clear();
		final Component[] c = getComponents();
		Group group = g.group();
		addSequencePrevAndNext(c, group);
		while (group.hasChild()) {
			final PEDBGroupLabel l = searchLabel((Group) group.child(), c);
			if (l != null)
				sequenceGroups.add(l);
			addSequencePrevAndNext(c, l.group());
			group = (Group) group.child();
		}

		repaint();
	}

	private void addSequencePrevAndNext(final Component[] c, Group group) {
		while (group.hasPrevious()) {
			final PEDBGroupLabel l = searchLabel((Group) group.previous(), c);
			if (l != null)
				sequenceGroups.add(l);
			group = (Group) group.previous();
		}
		while (group.hasNext()) {
			final PEDBGroupLabel l = searchLabel((Group) group.next(), c);
			if (l != null)
				sequenceGroups.add(l);
			group = (Group) group.next();
		}
	}

	public void setHigherGroup(PEDBGroupLabel l) {
		higherGroup = l;
		butler().printConsole(String.format("%s is set as higher group", l));
		repaint();
	}

	@Override protected PEDBGroupLabel createGroupLabel(Group group,
			Rectangle r) {
		return new PEDBGroupLabel(group, r);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.GroupingPanel#createKeyActionListener(net.muse.app.MuseApp)
	 */
	@Override protected KeyActionListener createKeyActionListener(MuseApp app) {
		return new KeyActionListener(app, this) {

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			@Override public PEDBGroupingPanel owner() {
				return (PEDBGroupingPanel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				super.keyPressedOption(e);
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
	 * ２つのグループを親子関係として縁組します
	 *
	 * @author hashida
	 * @since Oct. 14th, 2018
	 *
	 * @param parent 親となるグループラベル
	 * @param child 子となるグループラベル
	 * @param mesg 接続関係を文字列で
	 */
	private void adoptGroups(PEDBGroupLabel parent, PEDBGroupLabel child,
			String mesg) {
		// parentがすでに子グループを持っていたら？
		if (parent.hasChild()) {
			final int res = JOptionPane.showConfirmDialog(this, String.format(
					"%sはすでに子グループ%sと接続されています。%sと付け替えますか？", parent, parent
							.child(), child));
			if (res != JOptionPane.YES_OPTION) {
				cancelConnectiongGroups();
				return;
			}
		}
		// 接続する
		app().butler().printConsole(String.format("%s -> %s %s", parent, child,
				mesg));
		parent.setChild(child);
	}

	/**
	 * グループ接続を取りやめます。
	 */
	private void cancelConnectiongGroups() {
		app().butler().printConsole("cancel connecting");
		app().butler().notifyDeselectGroup();
	}

	/**
	 * 二つのグループを前後に連結し、親(上位階層)グループを生成します。
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
		pre = moveToLastGroupOf(pre);
		// preにもう先行グループが接続されていたら、その先頭グループまで移動させる
		pro = moveToFirstGroupOf(pro);
		// preとproが隣接していなかったら接続不可
		if (pre.group().getEndNote().offset() != pro.group().getBeginNote()
				.onset()) {
			cancelConnectiongGroups();
			return;
		}
		// -- preとproを接続する -------
		app().butler().printConsole(String.format("%s -> %s %s", pre, pro,
				mesg));
		pre.setNext(pro);

		// 親グループを生成
		final PEDBGroup p = new PEDBGroup(pre.group().getBeginNote(), pro
				.group().getEndNote(), GroupType.PARENT);
		p.setIndex(data().getUniqueGroupIndex());
		// TODO pre/proに親がすでにあったら？ -> 中間層グループにする
		p.setChild(pre.group());
		data().getMiscGroup().remove(pre.group());
		data().getMiscGroup().remove(pro.group());
		data().addMiscGroupList(p);
		createGroupLabel(p, pre.group().getLevel());
		pre.changeLevel(+1);
		pro.changeLevel(+1);
		repaint();
	}

	/**
	 * proにもう先行グループが接続されていたら、その先頭グループまで移動させます。
	 *
	 * @author hashida
	 * @since Oct. 17th, 2018
	 */
	private PEDBGroupLabel moveToFirstGroupOf(PEDBGroupLabel pro) {
		if (!pro.hasPrevious())
			return pro;
		app().butler().printConsole(String.format("back to %s <- %s", pro, pro
				.previous()));
		return moveToFirstGroupOf(pro.previous());
	}

	/**
	 * preにもう後続グループが接続されていたら、その最後尾グループまで移動させます。
	 *
	 * @author hashida
	 * @since Oct. 17th, 2018
	 */
	private PEDBGroupLabel moveToLastGroupOf(PEDBGroupLabel pre) {
		if (!pre.hasNext())
			return pre;
		app().butler().printConsole(String.format("%s forward to -> %s", pre,
				pre.next()));
		return moveToLastGroupOf(pre.next());
	}

	/**
	 * ふたつのグループラベルを接続します。
	 *
	 * @param l1
	 * @param l2
	 */
	private void connectGroups(PEDBGroupLabel l1, PEDBGroupLabel l2) {
		if (l1 == null || l2 == null)
			return;
		if (isConditionOfGroupAdopting(l1, l2))
			adoptGroups(l1, l2, "adopt");
		else if (isConditionOfGroupAdopting(l2, l1))
			adoptGroups(l2, l1, "adopt");
		else if (isConditionOfGroupConnecting(l1, l2))
			combineGroups(l1, l2, "combine");
		setHigherGroup(null);
	}

	/**
	 * 二つのグループラベルが親子階層として接続できるか判定します。
	 *
	 * @param l1
	 * @param l2
	 * @return
	 */
	private boolean isConditionOfGroupAdopting(PEDBGroupLabel l1,
			PEDBGroupLabel l2) {
		PEDBGroup g1 = l1.group();
		PEDBGroup g2 = l2.group();
		// g1がg2を内包する
		if (g1.onsetInTicks() <= g2.onsetInTicks() && g1.offsetInTicks() >= g2
				.offsetInTicks())
			return true;
		return false;
	}

	private boolean isConditionOfGroupConnecting(PEDBGroupLabel l1,
			PEDBGroupLabel l2) {
		if (l1.getLevel() == l2.getLevel()) {
			app().butler().printConsole("レベルが同じ");
			return true;
		}
		PEDBGroup g1 = l1.group();
		PEDBGroup g2 = l2.group();
		if (g1.offsetInTicks() == g2.onsetInTicks()) {
			app().butler().printConsole("２音が連続");
			return true;
		}
		if (g1.getEndNote().onset() <= g2.getBeginNote().onset() && g1
				.offsetInTicks() >= g2.getBeginNote().onset()) {
			app().butler().printConsole("g2開始音がg1終了音と同時刻帯");
			return true;
		}
		return false;
	}

	/**
	 * 頂点音ラベルを生成します。
	 *
	 * @author anan
	 * @since Oct 13th, 2018
	 */
	public void createTopNoteLabel(Group group, int level) {
		if (group == null || group.topNote == null)
			return;

		// 以下は、group が存在し、かつ当該groupに頂点音が存在する場合にのみ実行される
		final RoundRectangle2D topr = getTopNoteLabelBound(group.getTopNote(),
				level);
		PEDBTopNoteLabel toplabel = new PEDBTopNoteLabel(group.getTopNote(),
				topr,group);
		toplabel.setController(app());
		group.setLevel(level);
		add(toplabel); // 描画
		System.out.println(group.getScoreNotelist());
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
	public RoundRectangle2D getTopNoteLabelBound(NoteData topNote, int level) {
		final double y = setLabelY(level);
		double x, w;
		x = MainFrame.getXOfNote(topNote.onset()) + PianoRoll.getDefaultAxisX();
		w = MainFrame.getXOfNote(topNote.duration());
		final RoundRectangle2D r = new RoundRectangle2D.Double(x, y, w,
				LABEL_HEIGHT - LEVEL_PADDING, 3.0, 3.0);
		/*
		 * System.out.println("x = "+r.getX());
		 * System.out.println("y = "+r.getY());
		 * System.out.println("w = "+r.getWidth());
		 * System.out.println("h = "+r.getHeight());
		 * System.out.println("arcw = "+r.getArcWidth());
		 * System.out.println("arch = "+r.getArcHeight());
		 */
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
