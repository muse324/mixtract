package net.muse.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.mixtract.command.MixtractCommandType;

public class GroupLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	/* 格納データ */
	private Group group;
	private GroupLabel child;
	protected int partNumber;

	/* 描画モード */
	private boolean isSelected;
	private Color currentColor;

	/* イベント制御 */
	private MouseAdapter mouseActions;
	private KeyActionListener keyActions;
	private boolean startEdit;
	private boolean endEdit;

	protected GroupLabel() {
		super();
		initialize();
	}

	protected GroupLabel(Group group, Rectangle r) {
		this();
		this.group = group;
		this.setPartNumber(group.getBeginNote().xmlPartNumber());
		setLocation(r.x, r.y);
		setBounds(r);
		setTypeShape(group.getType());
	}

	public Group group() {
		return group;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param type
	 */
	public void setTypeShape(GroupType type) {
		switch (type) {
		case CRESC:
		case DIM:
			setText(type.name());
			setOpaque(false);
			setForeground(Color.black);
			setBorder(BorderFactory.createLineBorder(Color.black));
			break;
		default:
			setText(group.name());
			setCurrentColor(type.getColor());
			initialize();
			setBorder(null);
		}
	}

	@Override public String toString() {
		return group.name();
	}

	protected GroupLabel child(ArrayList<GroupLabel> grouplist) {
		if (child == null) {
			for (GroupLabel l : grouplist) {
				if (group().hasChild() && group().child().equals(l.group())) {
					child = l;
					break;
				}
			}
		}
		return child;
	}

	protected class GLMouseActionListener extends MouseActionListener {

		public GLMouseActionListener(MuseApp main, Container owner) {
			super(main, owner);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu
		 * (java.awt.event.MouseEvent)
		 */
		@Override public void createPopupMenu(MouseEvent e) {
			super.createPopupMenu(e);
			MixtractCommandType.SET_TYPE_CRESC.self().setGroup(self());
			MixtractCommandType.SET_TYPE_DIM.self().setGroup(self());
			addMenuItemOnGroupingPanel();
			getPopup().show((Component) e.getSource(), e.getX(), e.getY());
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseClicked(java
		 * .awt.event.MouseEvent)
		 */
		@Override public void mouseClicked(MouseEvent e) {
			super.mouseClicked(e);
			Group gr = self().group();
			if (gr == null) {
				self().repaint();
				return;
			}
			if (e.getClickCount() == 2) {
				for (InfoViewer r : main().butler().getInfoViewList()) {
					if (r.contains(gr)) {
						r.setVisible(true);
						return;
					}
				}
				showInfoViewer(main(), gr);
			}
			repaint();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseDragged(java
		 * .awt.event.MouseEvent)
		 */
		@Override public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			if (!frame().getGroupingPanel().isGroupEditable()) {
				self().moveLabelVertical(e, getMousePoint(), self().getBounds(),
						isShiftKeyPressed(), isMousePressed());
			} else
				self().moveLabel(e, getMousePoint(), isMousePressed());
			frame().getGroupingPanel().repaint();
			repaint();
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseEntered(java
		 * .awt.event.MouseEvent)
		 */
		@Override public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
			self().setMouseOver(true);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java
		 * .awt.event.MouseEvent)
		 */
		@Override public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
			self().setMouseOver(false);
			self().setEditMode(getMousePoint());
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseMoved(java.awt
		 * .event.MouseEvent)
		 */
		@Override public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
			setEditMode(getMousePoint());
			repaint();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mousePressed(java
		 * .awt.event.MouseEvent)
		 */
		@Override public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			main().notifySelectGroup(self(), true);
			if (self().getCursor().getType() == Cursor.W_RESIZE_CURSOR) {
				frame().getGroupingPanel().setGroupEditable(true);
			}
			repaint();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseReleased(java
		 * .awt.event.MouseEvent)
		 */
		@Override public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			frame().getGroupingPanel().setGroupEditable(false);
			frame().getGroupingPanel().setCursor(new Cursor(
					Cursor.DEFAULT_CURSOR));
			frame().getPianoroll().repaint();
			repaint();
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.gui.MouseActionListener#owner()
		 */
		@Override public GroupLabel self() {
			return (GroupLabel) super.self();
		}
	}

	void setController(MuseApp main) {
		mouseActions = createMouseActionListener(main);
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
		keyActions = new KeyActionListener(main, this) {

			/*
			 * (非 Javadoc)
			 * @see
			 * java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
			 */
			@Override public void keyPressed(KeyEvent e) {
				main().butler().keyPressed(e);
			}

		};
		addKeyListener(keyActions);
	}

	protected GLMouseActionListener createMouseActionListener(MuseApp main) {
		return new GLMouseActionListener(main, this);
	}

	/**
	 * @param mousePosition TODO
	 */
	void setEditMode(Point mousePosition) {
		Rectangle r = getBounds();
		Rectangle st = new Rectangle(new Point(r.x, r.y), new Dimension(10,
				r.height));
		Rectangle ed = new Rectangle(new Point(r.x + r.width - 10, r.y),
				new Dimension(15, r.height));
		Rectangle m = new Rectangle(mousePosition, new Dimension(5, 5));

		// GUIUtil.printConsole(r.toString() + "<>" + mousePosition);
		if (SwingUtilities.isRectangleContainingRectangle(st, m)) {
			setStartEdit(true);
			setEndEdit(false);
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		} else if (SwingUtilities.isRectangleContainingRectangle(ed, m)) {
			setStartEdit(false);
			setEndEdit(true);
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		} else {
			setStartEdit(false);
			setEndEdit(false);
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	void setMouseOver(boolean b) {
		if (b) {
			setBackground(PartColor.MOUSE_OVER_COLOR);
		} else
			setBackground(getCurrentColor());
	}

	/**
	 * @param hasSelectedNoteList
	 */
	protected void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		setBackground(isSelected ? PartColor.SELECTED_COLOR
				: getCurrentColor());
		setSelectedOption(isSelected);
		repaint();
	}

	/**
	 * @return the currentColor
	 */
	protected Color getCurrentColor() {
		return currentColor;
	}

	/**
	 * @param e mouse event
	 * @param p mouse point
	 * @param mousePressed TODO
	 * @param src group/note/expression label
	 */
	protected void moveLabel(MouseEvent e, Point p, boolean mousePressed) {

		final Rectangle r = getBounds();
		// if (!getGroupingPanel().isGroupEditable()) {
		// moveLabelVertical(e, p, r);
		// return;
		// }
		// パネル上でグループの表示位置を変更する（横方向）
		if (isStartEdit()) {
			if (p.x > r.x + r.width) {
				// r.x = r.x + r.width;
				r.width = 1;
			} else {
				r.width -= e.getX();
				r.x = p.x;
			}
		} else if (isEndEdit()) {
			if (p.x > r.x) {
				r.width = p.x - r.x;
			} else {
				r.width = 1;
			}
		}
		setBounds(r);
		moveChildLabel(e, mousePressed);
	}

	/**
	 * @param currentColor the currentColor to set
	 */
	protected void setCurrentColor(Color currentColor) {
		assert currentColor != null : "Invalid color: " + currentColor;
		this.currentColor = currentColor;
		setBackground(currentColor);
	}

	protected void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	protected void setSelectedOption(boolean isSelected) {
		setFocusable(isSelected);
		MixtractCommandType.DELETE_GROUP.self().setGroup(isSelected ? this
				: null);
		if (isSelected)
			requestFocus();
	}

	protected void setStartEdit(boolean startEdit) {
		this.startEdit = startEdit;
	}

	private GroupLabel child() {
		return child;
	}

	private boolean hasChild() {
		return child != null;
	}

	/**
	 *
	 */
	private void initialize() {
		setOpaque(true);
		setForeground(Color.white);
	}

	private boolean isEndEdit() {
		return endEdit;
	}

	private boolean isStartEdit() {
		return startEdit;
	}

	private void moveChildLabel(MouseEvent e, boolean mousePressed) {
		if (mousePressed && hasChild()) {
			child().setStartEdit(true);
			Point pc = child().getLocation();
			pc.translate(e.getX(), e.getY());
			moveLabel(e, pc, mousePressed);
		}
	}

	private void moveLabelVertical(MouseEvent e, boolean mousePressed) {
		Point pc;
		if (hasChild()) {
			pc = child().getLocation();
			pc.translate(e.getX(), e.getY());
			child().moveLabel(e, pc, mousePressed);
		}
	}

	/**
	 * @param e
	 * @param p
	 * @param r
	 * @param shiftKeyPressed TODO
	 * @param mousePressed TODO
	 * @param src
	 */
	private void moveLabelVertical(MouseEvent e, Point p, Rectangle r,
			boolean shiftKeyPressed, boolean mousePressed) {
		r.y = p.y;
		setBounds(r);
		if (shiftKeyPressed) {
			moveLabelVertical(e, mousePressed);
		}
		repaint();
	}

	private void setEndEdit(boolean endEdit) {
		this.endEdit = endEdit;
	}

	private void showInfoViewer(MuseApp app, Group gr) {
		InfoViewer pv = InfoViewer.create(app, gr);
		pv.setTitle(gr.name());
		app.butler().addInfoViewerList(pv);
		pv.pack();
		pv.setVisible(true);
		pv.preset();
	}

}
