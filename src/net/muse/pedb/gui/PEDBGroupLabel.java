package net.muse.pedb.gui;

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
import net.muse.gui.KeyActionListener;
import net.muse.pedb.data.PEDBGroup;

public class PEDBGroupLabel extends GroupLabel {

	private static final long serialVersionUID = 1L;
	private PEDBGroupLabel next;
	private PEDBGroupLabel prev;

	public PEDBGroupLabel(Group group, Rectangle r) {
		super(group, r);
	}

	public PEDBGroupLabel() {
		super();
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
		return (PEDBGroupLabel) child();
	}

	@Override public PEDBGroup group() {
		return (PEDBGroup) super.group();
	}

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

			@Override protected void doubleClicked(Group gr) {
				// do nothing
			}

			@Override public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				if (!isGroupEditable()) {
					frame().getGroupingPanel().moveLabels(e, getMousePoint(),
							self().group().getLevel(), self().getBounds(),
							isShiftKeyPressed(), isMousePressed());
				}
			}

			@Override protected PEDBMainFrame frame() {
				return (PEDBMainFrame) super.frame();
			}

			@Override public PEDBGroupLabel self() {
				return (PEDBGroupLabel) super.self();
			}
		};
	}

	void setNext(PEDBGroupLabel label) {
		next = label;
		if (next.previous() != label)
			label.setPrevious(next);

	}

	void setPrevious(PEDBGroupLabel label) {
		prev = label;
		if (prev.next() != label)
			label.setNext(prev);

	}

	PEDBGroupLabel previous() {
		return prev;
	}

	PEDBGroupLabel next() {
		return next;
	}

	boolean hasNext() {
		return next != null;
	}

	boolean hasPrevious() {
		return prev != null;
	}

	@Override public void setTypeShape(GroupType type) {
		super.setTypeShape(type);
		repaint();
	}

	/**
	 * グループ名を表記します。repaint()をオーバーライドして呼び出されるため、外部から明示的に呼び出す必要はありません。
	 */
	private void renameText() {
		if(group()==null)return;
		setText(String.format("[%d] %s:%s", group().getLevel(), group().name(),
				childGroupNameText()));
	}

	private String childGroupNameText() {
		if (!group().hasChild())
			return "-";
		PEDBGroup c = group().child();
		String s = c.name();
		while (c.hasNext()) {
			PEDBGroup n = (PEDBGroup) c.next();
			s += "->" + n.name();
			c = n;
		}
		return s;
	}

	/**
	 * 階層レベルを指定した値に変更します。
	 *
	 * @param i
	 */
	public void changeLevel(int i) {
		group().changeLevel(i);
		repaint();
	}

	@Override public void setChild(GroupLabel child) {
		super.setChild(child);
		repaint();
	}

	@Override public PEDBGroupLabel child() {
		return (PEDBGroupLabel) super.child();
	}

	@Override public void repaint() {
		super.repaint();
		renameText();
	}

	public int getLevel() {
		return group().getLevel();
	}
}
