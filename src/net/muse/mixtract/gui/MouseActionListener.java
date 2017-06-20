package net.muse.mixtract.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import net.muse.gui.*;
import net.muse.misc.Command;
import net.muse.mixtract.Mixtract;

public class MouseActionListener extends MouseAdapter implements
		ActionListener {
	protected final Container _owner;

	private boolean mousePressed;
	private static Point mousePoint;
	private boolean shiftKeyPressed;
	private JPopupMenu popup;
	private Rectangle mouseBox;

	/* 制御オブジェクト */
	protected static Mixtract _main;
	protected MainFrame _frame;

	private Point startPoint = new Point(0, 0);
	private Point endPoint = new Point(0, 0);

	private boolean isDragging;

	public MouseActionListener(Mixtract main, Container owner) {
		super();
		_main = main;
		_owner = owner;
		_frame = (MainFrame) _main.getFrame();
	}

	/**
	 * @return the mousePoint
	 */
	public Point getMousePoint() {
		return mousePoint;
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	public boolean isShiftKeyPressed() {
		return shiftKeyPressed;
	}

	@Override public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e))
			createPopupMenu(e);
		// Group gr = _main.getSelectedObjects().getGroup();
		// if (gr == null) {
		// _owner.repaint();
		// return;
		// }
		// if (e.getClickCount() == 2) {
		// for (PhraseViewer r : _owner.getPhraseViewList(_owner)) {
		// if (r.contains(gr)) {
		// r.setVisible(true);
		// return;
		// }
		// }
		// PhraseViewer pv = new PhraseViewer(_owner.getMainFrame(_owner), gr);
		// pv.addWindowListener(_owner.getRuleWindowActions(_owner));
		// _owner.getPhraseViewList(_owner).add(pv);
		// pv.pack();
		// pv.setVisible(true);
		// }
		_owner.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.MouseAdapter#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override public void mouseDragged(MouseEvent e) {
		setMousePoint(e);
		setEndPoint(e.getPoint());
		setDragging(true);
		setShiftKeyPressed(e.isShiftDown());
		_owner.repaint();
	}

	protected void setDragging(boolean b) {
		isDragging = b;
	}

	/**
	 * @return isDragging
	 */
	protected final boolean isDragging() {
		return isDragging;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override public void mouseEntered(MouseEvent e) {
		setMousePoint(e);
		_owner.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override public void mouseExited(MouseEvent e) {
		setMousePoint(e);
		_owner.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override public void mouseMoved(MouseEvent e) {
		setMousePoint(e);
		_owner.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override public void mousePressed(MouseEvent e) {
		setShiftKeyPressed(e.isShiftDown());
		setMousePressed(true);
		setMousePoint(e);
		setStartPoint(e.getPoint());
		setEndPoint(e.getPoint());
		// _main.notifyDeselectGroup();
		_owner.repaint();
	}

	private void setStartPoint(Point point) {
		startPoint = point;
	}

	/**
	 * @return startPoint
	 */
	final Point getStartPoint() {
		return startPoint;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.MouseAdapter#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override public void mouseReleased(MouseEvent e) {
		setShiftKeyPressed(e.isShiftDown());
		setMousePressed(false);
		setMousePoint(e);
		setEndPoint(e.getPoint());
		setDragging(false);
		if (SwingUtilities.isRightMouseButton(e))
			createPopupMenu(e);
		// if (SwingUtilities.isLeftMouseButton(e)) {
		// _owner.setGroupEditable(_owner, false);
		// _owner.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		// }
		_owner.repaint();
	}

	/**
	 * Set the current mouse position in the panel.
	 * <p>
	 * マウスの現在位置を記憶します．
	 *
	 * @param e マウスイベント
	 */
	public void setMousePoint(MouseEvent e) {
		if (e.getSource() instanceof GroupingPanel) {
			mousePoint = e.getPoint();
			// mousePoint.x -= PianorollScroll.axisX;
		} else if (e.getSource() instanceof GroupLabel) {
			mousePoint = ((GroupLabel) e.getSource()).getLocation();
			mousePoint.translate(e.getX(), e.getY());
		}
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}

	/**
	 * @param mouseBox the mouseBox to set
	 */
	public void setMouseBox(Rectangle selectedMouseBox) {
		this.mouseBox = selectedMouseBox;
	}

	public void setShiftKeyPressed(boolean shiftKeyPressed) {
		this.shiftKeyPressed = shiftKeyPressed;
	}

	protected JMenuItem addMenuItem(Command command, boolean enabled) {
		JMenuItem menuItem;
		menuItem = new JMenuItem();
		menuItem.setText(command.getText());
		menuItem.setActionCommand(command.name());
		menuItem.addActionListener(this);
		menuItem.setEnabled(enabled);
		return menuItem;
		// popup.add(menuItem);
	}

	protected void addMenuItemOnGroupingPanel() {
		boolean hasSelectedGroup = false;
		if (_owner instanceof GroupingPanel)
			hasSelectedGroup = ((GroupingPanel) _owner).hasSelectedGroup();
		else if (_owner instanceof GroupLabel)
			hasSelectedGroup = ((GroupLabel) _owner).isSelected();

		// group attributes
		JMenu attrMenu = new JMenu("Set articulation");
		attrMenu.setEnabled(hasSelectedGroup);
		attrMenu.add(addMenuItem(MixtractCommand.SET_TYPE_CRESC,
				hasSelectedGroup));
		attrMenu.add(addMenuItem(MixtractCommand.SET_TYPE_DIM,
				hasSelectedGroup));
		popup.add(attrMenu);
		popup.addSeparator();

		popup.add(addMenuItem(MixtractCommand.PRINT_GROUP_INFO,
				hasSelectedGroup));
		popup.add(addMenuItem(MixtractCommand.DELETE_GROUP, hasSelectedGroup));
		popup.add(addMenuItem(MixtractCommand.CLEAR_ALLGROUPS, _main
				.hasTarget()));
		popup.addSeparator();
		popup.add(addMenuItem(MixtractCommand.PRINT_ALLGROUPS, _main
				.hasTarget()));
		// popup.add(addMenuItem(MixtractCommand.PRINT_SUBGROUPS,
		// _main.hasTarget()));
		popup.add(addMenuItem(MixtractCommand.MOUSE_DISPLAY, true));
	}

	protected void createPopupMenu(MouseEvent e) {
		popup = new JPopupMenu();
		// if (src instanceof GroupLabel) {
		// _owner.notifySelectGroup(_owner, (GroupLabel) src, true);
		// }
	}

	/**
	 * @return popup
	 */
	public final JPopupMenu getPopup() {
		return popup;
	}

	/**
	 * @return _owner
	 */
	public Container getOwner() {
		return _owner;
	}

	// private enum OwnerContainer {
	//
	// MainFrame {
	// @Override public ExpressionPanel getExpressionPanel(Container owner) {
	// return ((MainFrame) owner).getExpressionPanel();
	// }
	//
	// @Override public GroupingPanel getGroupingPanel(Container owner) {
	// return ((MainFrame) owner).getGroupingPanel();
	// }
	//
	// @Override public MainFrame getMainFrame(Container owner) {
	// return (MainFrame) owner;
	// }
	//
	// @Override public ArrayList<PhraseViewer> getPhraseViewList(Container
	// owner) {
	// return ((MainFrame) owner).getPhraseViewList();
	// }
	//
	// @Override public PianorollScroll getScoreRoll(Container owner) {
	// return ((MainFrame) owner).getPianorollScroll();
	// }
	//
	// @Override public WindowListener getRuleWindowActions(Container owner) {
	// return ((MainFrame) owner).getRuleWindowActions();
	// }
	//
	// @Override public ActionListener getUserActions(Container owner) {
	// return ((MainFrame) owner).getUserActions();
	// }
	//
	// @Override public boolean hasTargetData(Container owner) {
	// return ((MainFrame) owner).hasMusicData();
	// }
	//
	// @Override public void moveLabel(Container owner, MouseEvent e,
	// MouseActionListener mouseActions) {
	// final GroupLabel src = (GroupLabel) e.getSource();
	// if (!((MainFrame) owner).getGroupingPanel().isGroupEditable()) {
	// src.moveLabelVertical(e, mousePoint, src.getBounds(), mouseActions
	// .isShiftKeyPressed(), mouseActions.isMousePressed());
	// return;
	// }
	// src.moveLabel(e, mousePoint, mouseActions.isMousePressed());
	// }
	//
	// @Override public void notifySelectGroup(Container owner,
	// GroupLabel label, boolean b) {
	// _main.getSelectedObjects().setGroupLabel(label);
	// ((MainFrame) owner).notifySelectGroup(label, b);
	// }
	//
	// @Override public void setGroupEditable(Container owner, boolean b) {
	// ((MainFrame) owner).getGroupingPanel().setGroupEditable(b);
	// }
	//
	// @Override public void setShiftKeyPressed(Container owner,
	// boolean shiftDown) {
	// ((MainFrame) owner).getMouseActions().setShiftKeyPressed(shiftDown);
	// }
	//
	// @Override public PianorollScroll getRealtimeRoll(Container owner) {
	// return ((MainFrame) owner).getRealtimeScroll();
	// }
	// };
	//
	// public abstract ExpressionPanel getExpressionPanel(Container owner);
	//
	// public abstract PianorollScroll getRealtimeRoll(Container owner);
	//
	// public abstract GroupingPanel getGroupingPanel(Container owner);
	//
	// public abstract MainFrame getMainFrame(Container owner);
	//
	// public abstract ArrayList<PhraseViewer> getPhraseViewList(Container
	// owner);
	//
	// public abstract PianorollScroll getScoreRoll(Container owner);
	//
	// public abstract WindowListener getRuleWindowActions(Container owner);
	//
	// public abstract ActionListener getUserActions(Container owner);
	//
	// public abstract boolean hasTargetData(Container owner);
	//
	// public abstract void moveLabel(Container owner, MouseEvent e,
	// MouseActionListener mouseActions);
	//
	// public abstract void notifySelectGroup(Container owner, GroupLabel
	// label,
	// boolean b);
	//
	// public abstract void setGroupEditable(Container owner, boolean b);
	//
	// public abstract void setShiftKeyPressed(Container owner, boolean
	// shiftDown);
	// }

	public Rectangle getMouseBox() {
		return mouseBox;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
	 * )
	 */
	public void actionPerformed(ActionEvent e) {
		final Command c = MixtractCommand.create(e.getActionCommand());
		c.execute();
	}

	/**
	 * @param endPoint セットする endPoint
	 */
	void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * @return endPoint
	 */
	Point getEndPoint() {
		return endPoint;
	}

}
