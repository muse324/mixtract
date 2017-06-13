package net.muse.mixtract.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;

import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.Group;
import net.muse.mixtract.data.Group.GroupType;

public class GroupLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/* 格納データ */
	private Group group;
	private GroupLabel childFormer;
	private GroupLabel childLatter;
	private Color currentColor;
	protected int partNumber;

	/* 描画モード */
	protected boolean isSelected;
	private boolean endEdit;
	private boolean startEdit;

	/* イベント制御 */
	private MouseAdapter mouseActions;

	GroupLabel(Group group, Rectangle r) {
		this();
		this.group = group;
		this.setPartNumber(group.getBeginGroupNote().getNote().partNumber());
		setLocation(r.x, r.y);
		setBounds(r);
		setTypeShape(group.getType());
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

	protected GroupLabel() {
		super();
		initialize();
	}

	@Override public String toString() {
		return group.name();
	}

	GroupLabel getChildFormer(ArrayList<GroupLabel> grouplist) {
		if (childFormer == null) {
			for (GroupLabel l : grouplist) {
				if (group.hasChildFormer()
						&& group.getChildFormerGroup().equals(l.getGroup())) {
					childFormer = l;
					break;
				}
			}
		}
		return childFormer;
	}

	GroupLabel getChildLatter(ArrayList<GroupLabel> grouplist) {
		if (childLatter == null) {
			for (GroupLabel l : grouplist) {
				if (group.hasChildLatter()
						&& group.getChildLatterGroup().equals(l.getGroup())) {
					childLatter = l;
					break;
				}
			}
		}
		return childLatter;
	}

	/**
	 * @return gr
	 */
	public final Group getGroup() {
		return group;
	}

	void setController(Mixtract main) {
		mouseActions = new MouseActionListener(main, this) {

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu
			 * (java.awt.event.MouseEvent)
			 */
			@Override public void createPopupMenu(MouseEvent e) {
				super.createPopupMenu(e);
				MixtractCommand.SET_TYPE_CRESC.setGroup((GroupLabel) _owner);
				MixtractCommand.SET_TYPE_DIM.setGroup((GroupLabel) _owner);
				addMenuItemOnGroupingPanel();
				getPopup().show((Component) e.getSource(), e.getX(), e.getY());
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mousePressed(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				GroupLabel l = (GroupLabel) e.getSource();
				_main.notifySelectGroup(l, true);
				if (l.getCursor().getType() == Cursor.W_RESIZE_CURSOR) {
					// _owner.getPianorollPane().getPianoroll().setDrawNewArea(true);
					_frame.getGroupingPanel().setGroupEditable(true);
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
				_frame.getGroupingPanel().setGroupEditable(false);
				_frame.getGroupingPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				_frame.getPianoroll().repaint();
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
				final GroupLabel src = (GroupLabel) e.getSource();
				if (!_frame.getGroupingPanel().isGroupEditable()) {
					src.moveLabelVertical(e, getMousePoint(), src.getBounds(),
							isShiftKeyPressed(), isMousePressed());
				} else
					src.moveLabel(e, getMousePoint(), isMousePressed());
				_frame.getGroupingPanel().repaint();
				repaint();
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
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseClicked(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				final GroupLabel l = (GroupLabel) e.getSource();
				Group gr = l.getGroup();
				if (gr == null) {
					_owner.repaint();
					return;
				}
				if (e.getClickCount() == 2) {
					for (PhraseViewer r : _main.getPhraseViewList()) {
						if (r.contains(gr)) {
							r.setVisible(true);
							return;
						}
					}
					PhraseViewer pv = new PhraseViewer(_main, gr);
					pv.setTitle(gr.name());
					_main.getPhraseViewList().add(pv);
					pv.pack();
					pv.setVisible(true);
//					pv.repaint();
					pv.preset();
				}

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
				((GroupLabel) e.getSource()).setMouseOver(true);
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				((GroupLabel) e.getSource()).setMouseOver(false);
				((GroupLabel) e.getSource()).setEditMode(getMousePoint());
			}

		};
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
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
	void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if (isSelected) {
			setBackground(PartColor.SELECTED_COLOR);
		} else {
			setBackground(getCurrentColor());
		}
		repaint();
	}

	/**
	 * @param e
	 * @param p
	 * @param r
	 * @param shiftKeyPressed TODO
	 * @param mousePressed TODO
	 * @param src
	 */
	protected void moveLabelVertical(MouseEvent e, Point p, Rectangle r,
			boolean shiftKeyPressed, boolean mousePressed) {
		r.y = p.y;
		setBounds(r);
		if (shiftKeyPressed) {
			Point pc;
			if (hasChildFormer()) {
				pc = getChildFormer().getLocation();
				pc.translate(e.getX(), e.getY());
				getChildFormer().moveLabel(e, pc, mousePressed);
			}
			if (hasChildLatter()) {
				pc = getChildLatter().getLocation();
				pc.translate(e.getX(), e.getY());
				getChildLatter().moveLabel(e, pc, mousePressed);
			}
		}
		repaint();
	}

	/**
	 * @param mousePosition TODO
	 */
	protected void setEditMode(Point mousePosition) {
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

	private GroupLabel getChildFormer() {
		return childFormer;
	}

	private GroupLabel getChildLatter() {
		return childLatter;
	}

	/**
	 * @return the currentColor
	 */
	protected Color getCurrentColor() {
		return currentColor;
	}

	/**
	 * @return
	 */
	private boolean hasChildFormer() {
		return group != null && group.hasChildFormer();
	}

	/**
	 * @return
	 */
	private boolean hasChildLatter() {
		return group != null && group.hasChildLatter();
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

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	private boolean isStartEdit() {
		return startEdit;
	}

	/**
	 * @param e mouse event
	 * @param p mouse point
	 * @param mousePressed TODO
	 * @param src group/note/expression label
	 */
	private void moveLabel(MouseEvent e, Point p, boolean mousePressed) {

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
		if (mousePressed) {
			Point pc;
			GroupLabel c = null;
			if (hasChildFormer()) {
				c = getChildFormer();
				c.setStartEdit(true);
				pc = c.getLocation();
				pc.translate(e.getX(), e.getY());
				moveLabel(e, pc, mousePressed);
			}
			if (hasChildLatter()) {
				c = getChildLatter();
				c.setStartEdit(true);
				pc = c.getLocation();
				pc.translate(e.getX(), e.getY());
				moveLabel(e, pc, mousePressed);
			}
		}
	}

	/**
	 * @param currentColor the currentColor to set
	 */
	protected void setCurrentColor(Color currentColor) {
		assert currentColor != null : "Invalid color: " + currentColor;
		this.currentColor = currentColor;
		setBackground(currentColor);
	}

	private void setEndEdit(boolean endEdit) {
		this.endEdit = endEdit;
	}

	/**
	 * @param partNumber the partNumber to set
	 */
	protected void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
		// System.err.println("WARNING: GroupLabel#setPartNumber(int partNumber) is incompleted.");
		// if(group.hasChild()){
		// getChildFormer().setPartNumber(partNumber);
		// getChildLatter().setPartNumber(partNumber);
		// }
	}

	private void setStartEdit(boolean startEdit) {
		this.startEdit = startEdit;
	}

}
