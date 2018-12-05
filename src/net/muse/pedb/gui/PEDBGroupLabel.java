package net.muse.pedb.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.gui.GLMouseActionListener;
import net.muse.gui.GroupLabel;
import net.muse.gui.GroupingPanel;
import net.muse.gui.KeyActionListener;
import net.muse.pedb.data.PEDBGroup;

public class PEDBGroupLabel extends GroupLabel {
	private static final long serialVersionUID = 1L;
	private static final int FORWARD = 1;
	private static final int REWIND = 2;
	private PEDBGroupLabel next;
	private PEDBGroupLabel prev;
	private PEDBTopNoteLabel topNoteLabel;

	public PEDBGroupLabel() {
		super();
	}

	public PEDBGroupLabel(Group group, Rectangle r) {
		super(group, r);
	}

	/**
	 * 階層レベルを指定した値(相対値)に変更します。
	 *
	 * @param i
	 */
	public void changeLevel(int i) {
		group().changeLevel(i);
		repaint();
	}

	@Override public PEDBGroupLabel child() {
		return (PEDBGroupLabel) super.child();
	}

	@Override public PEDBGroupLabel child(ArrayList<GroupLabel> grouplist) {
		if (child() == null) {
			for (final GroupLabel l : grouplist) {
				if (group().hasChild() && group().child().equals(l.group())) {
					setChild(l);
					break;
				}
			}
		}
		return child();
	}

	public int getLevel() {
		return group().getLevel();
	}

	@Override public PEDBGroup group() {
		return (PEDBGroup) super.group();
	}

	@Override public void moveLabelVertical(MouseEvent e, Point p, Rectangle r,
			boolean shiftKeyPressed, boolean mousePressed) {

		moveLabelVertical(e, p, getBounds(), shiftKeyPressed, mousePressed,
				REWIND);
		moveLabelVertical(e, p, getBounds(), shiftKeyPressed, mousePressed,
				FORWARD);
		if (hasChild()) {
			p.translate(0, GroupingPanel.LABEL_HEIGHT
					+ GroupingPanel.LABEL_HEIGHT_OFFSET);
			child().moveLabelVertical(e, p, child().getBounds(),
					shiftKeyPressed, mousePressed);
		}
	}

	public void moveLabelVertical(MouseEvent e, Point p, Rectangle r,
			boolean shiftKeyPressed, boolean mousePressed, int direction) {
		r.y = p.y;
		setBounds(r);

		if (direction == FORWARD && hasNext())
			next().moveLabelVertical(e, p, next().getBounds(), shiftKeyPressed,
					mousePressed, FORWARD);
		if (direction == REWIND && hasPrevious())
			previous().moveLabelVertical(e, p, previous().getBounds(),
					shiftKeyPressed, mousePressed, REWIND);
		repaint();
	}

	@Override public void repaint() {
		super.repaint();
		renameText();
	}

	@Override public void setChild(GroupLabel child) {
		super.setChild(child);
		repaint();
	}

	@Override public void setTypeShape(GroupType type) {
		super.setTypeShape(type);
		repaint();
	}

	boolean hasNext() {
		return next != null;
	}

	boolean hasPrevious() {
		return prev != null;
	}

	PEDBGroupLabel next() {
		return next;
	}

	PEDBGroupLabel previous() {
		return prev;
	}

	void setNext(PEDBGroupLabel label) {
		next = label;
		group().setNext(label.group());
		if (next.previous() != this)
			next.setPrevious(this);
	}

	void setPrevious(PEDBGroupLabel label) {
		prev = label;
		group().setPrevious(label.group());
		if (prev.next() != this)
			prev.setNext(this);

	}

	@Override protected KeyActionListener createKeyActionListener(MuseApp app) {
		return new KeyActionListener(app, this) {

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			@Override public PEDBGroupLabel self() {
				return (PEDBGroupLabel) super.self();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				super.keyPressedOption(e);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_H:
					setHigherGroup(self());
					break;
				case KeyEvent.VK_ESCAPE:
					setHigherGroup(null);
					break;
				case KeyEvent.VK_UP:
					changeLevel(-1);
					break;
				case KeyEvent.VK_DOWN:
					changeLevel(+1);
					break;
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

			@Override public PEDBGroupLabel self() {
				return (PEDBGroupLabel) super.self();
			}

			@Override protected void doubleClicked(Group gr) {
				// do nothing
			}

			@Override protected PEDBMainFrame frame() {
				return (PEDBMainFrame) super.frame();
			}
		};
	}

	private String childGroupNameText() {
		if (!group().hasChild())
			return "-";
		PEDBGroup c = group().child();
		String s = c.name();
		while (c.hasNext()) {
			final PEDBGroup n = (PEDBGroup) c.next();
			s += "->" + n.name();
			c = n;
		}
		return s;
	}

	/**
	 * グループ名を表記します。repaint()をオーバーライドして呼び出されるため、外部から明示的に呼び出す必要はありません。
	 */
	private void renameText() {
		if (group() == null)
			return;
		setText(String.format("[%d] %s:%s", group().getLevel(), group().name(),
				childGroupNameText()));
	}

	public void setTopNoteLabel(PEDBTopNoteLabel top) {
		topNoteLabel = top;
	}
}
