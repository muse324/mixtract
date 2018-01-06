package net.muse.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.mixtract.command.MixtractCommand;

public class GroupLabel extends JLabel {

	private static final long serialVersionUID = 1L;

	/* 格納データ */
	private Group group;
	private Color currentColor;
	protected int partNumber;

	/* 描画モード */
	protected boolean isSelected;
	private boolean endEdit;
	private boolean startEdit;

	/* イベント制御 */
	private MouseAdapter mouseActions;

	private GroupLabel child;

	private KeyActionListener keyActions;

	protected GroupLabel(Group group, Rectangle r) {
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

	@Override
	public String toString() {
		return group.name();
	}

	void setController(MuseApp main) {
		mouseActions = new MouseActionListener(main, this) {

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu
			 * (java.awt.event.MouseEvent)
			 */
			@Override
			public void createPopupMenu(MouseEvent e) {
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
			@Override
			public void mousePressed(MouseEvent e) {
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
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				_frame.getGroupingPanel().setGroupEditable(false);
				_frame.getGroupingPanel().setCursor(new Cursor(
						Cursor.DEFAULT_CURSOR));
				_frame.getPianoroll().repaint();
				repaint();
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseDragged(java
			 * .awt.event.MouseEvent)
			 */
			@Override
			public void mouseDragged(MouseEvent e) {
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
			@Override
			public void mouseMoved(MouseEvent e) {
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
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				final GroupLabel l = (GroupLabel) e.getSource();
				Group gr = l.group();
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
					showPhraseViewer(_main, gr);
				}
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseEntered(java
			 * .awt.event.MouseEvent)
			 */
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				((GroupLabel) e.getSource()).setMouseOver(true);
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java
			 * .awt.event.MouseEvent)
			 */
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				((GroupLabel) e.getSource()).setMouseOver(false);
				((GroupLabel) e.getSource()).setEditMode(getMousePoint());
			}

		};
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
		keyActions = new KeyActionListener(main, this) {

			/*
			 * (非 Javadoc)
			 * @see
			 * java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_BACK_SPACE:
					GUIUtil.printConsole("delete group (dummy)");
					MixtractCommand.DELETE_GROUP.execute();
					break;
				default:
					GUIUtil.printConsole("Key pressed: ");
				}
			}

		};
		addKeyListener(keyActions);
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
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		setFocusable(isSelected);
		MixtractCommand.DELETE_GROUP.setGroup(isSelected ? this : null);
		setBackground(isSelected ? PartColor.SELECTED_COLOR
				: getCurrentColor());
		if (isSelected)
			requestFocus();
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
			moveLabelVertical(e, mousePressed);
		}
		repaint();
	}

	protected void moveLabelVertical(MouseEvent e, boolean mousePressed) {
		Point pc;
		if (hasChild()) {
			pc = child().getLocation();
			pc.translate(e.getX(), e.getY());
			child().moveLabel(e, pc, mousePressed);
		}
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

	/**
	 * @return the currentColor
	 */
	protected Color getCurrentColor() {
		return currentColor;
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

	protected void moveChildLabel(MouseEvent e, boolean mousePressed) {
		if (mousePressed) {
			Point pc;
			GroupLabel c = null;
			if (hasChild()) {
				c = child();
				child().setStartEdit(true);
				pc = child().getLocation();
				pc.translate(e.getX(), e.getY());
				moveLabel(e, pc, mousePressed);
			}
		}
	}

	private GroupLabel child() {
		return child;
	}

	private boolean hasChild() {
		return child != null;
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
		// System.err.println("WARNING: GroupLabel#setPartNumber(int partNumber)
		// is incompleted.");
		// if(group.hasChild()){
		// getChildFormer().setPartNumber(partNumber);
		// getChildLatter().setPartNumber(partNumber);
		// }
	}

	protected void setStartEdit(boolean startEdit) {
		this.startEdit = startEdit;
	}

	private void showPhraseViewer(MuseApp app, Group gr) {
		PhraseViewer pv = createPhraseViewer(app, gr);
		pv.setTitle(gr.name());
		app.addPhraseViewerList(pv);
		pv.pack();
		pv.setVisible(true);
		pv.preset();
	}

	protected PhraseViewer createPhraseViewer(MuseApp app, Group gr) {
		return new PhraseViewer(app, gr);
	}

	public Group group() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public GroupLabel child(ArrayList<GroupLabel> grouplist) {
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

}
