package net.muse.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.muse.app.MuseApp;
import net.muse.command.MuseAppCommand;
import net.muse.command.MuseAppCommandAction;
import net.muse.mixtract.command.MixtractCommandType;

/**
 * MuseApp GUI システムに共通するマウスアクションを集めたものです。
 *
 * @author Mitsuyo Hashida / M-USE Lab.
 */
public class MouseActionListener extends MouseAdapter implements
		ActionListener {
	/* マウス座標 */
	private static Point mousePoint;

	/* 制御オブジェクト */
	private final MuseApp _main;
	private final Container _self;
	private final MainFrame _frame;
	private JPopupMenu popup;

	/* マウスドラッグによる矩形範囲の座標 */
	private Point startPoint = new Point(0, 0);
	private Point endPoint = new Point(0, 0);
	private Rectangle mouseBox;

	/* マウスのステータス */
	private boolean mousePressed;
	private boolean shiftKeyPressed;
	private boolean isDragging;
	private boolean altKeyPressed;

	public MouseActionListener(MuseApp main, Container owner) {
		super();
		_main = main;
		_self = owner;
		_frame = (MainFrame) main().getFrame();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
	 * )
	 */
	public void actionPerformed(ActionEvent e) {
		MuseAppCommand c = main().searchCommand(e.getActionCommand());
		if (c == null)
			return;
		// final MuseAppCommand c = MuseAppCommand.create(e.getActionCommand());
		c.setFrame(frame());
		c.setMain(main());
		c.setTarget(e.getSource());
		c.run();
	}

	public Rectangle getMouseBox() {
		return mouseBox;
	}

	/**
	 * @return the mousePoint
	 */
	public Point getMousePoint() {
		return mousePoint;
	}

	/**
	 * @return popup
	 */
	public final JPopupMenu getPopup() {
		return popup;
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
		_self.repaint();
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
		setAltKeyPressed(e.isAltDown());
		_self.repaint();
	}

	public void setAltKeyPressed(boolean altDown) {
		this.altKeyPressed = altDown;
	}

	public boolean isAltKeyPressed() {
		return altKeyPressed;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.MouseAdapter#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override public void mouseEntered(MouseEvent e) {
		setMousePoint(e);
		_self.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override public void mouseExited(MouseEvent e) {
		setMousePoint(e);
		_self.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see java.awt.event.MouseAdapter#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override public void mouseMoved(MouseEvent e) {
		setMousePoint(e);
		_self.repaint();
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
		_self.repaint();
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
		// _self.setGroupEditable(_self, false);
		// _self.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		// }
		_self.repaint();
	}

	/**
	 * @param mouseBox the mouseBox to set
	 */
	public void setMouseBox(Rectangle selectedMouseBox) {
		this.mouseBox = selectedMouseBox;
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

	public void setShiftKeyPressed(boolean shiftKeyPressed) {
		this.shiftKeyPressed = shiftKeyPressed;
	}

	/**
	 * @return endPoint
	 */
	Point getEndPoint() {
		return endPoint;
	}

	/**
	 * @return startPoint
	 */
	final Point getStartPoint() {
		return startPoint;
	}

	/**
	 * @param endPoint セットする endPoint
	 */
	void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	protected JMenuItem addMenuItem(MuseAppCommandAction command,
			boolean enabled) {
		JMenuItem menuItem;
		menuItem = new JMenuItem();
		menuItem.setText(command.command().getText());
		menuItem.setActionCommand(command.name());
		menuItem.addActionListener(this);
		menuItem.setEnabled(enabled);
		return menuItem;
		// popup.add(menuItem);
	}

	protected void addMenuItemOnGroupingPanel() {
		boolean hasSelectedGroup = false;
		if (_self instanceof GroupingPanel)
			hasSelectedGroup = ((GroupingPanel) _self).hasSelectedGroup();
		else if (_self instanceof GroupLabel)
			hasSelectedGroup = ((GroupLabel) _self).isSelected();

		// group attributes
		JMenu attrMenu = new JMenu("Set articulation");
		attrMenu.setEnabled(hasSelectedGroup);
		attrMenu.add(addMenuItem(MixtractCommandType.SET_TYPE_CRESC,
				hasSelectedGroup));
		attrMenu.add(addMenuItem(MixtractCommandType.SET_TYPE_DIM,
				hasSelectedGroup));
		popup.add(attrMenu);
		popup.addSeparator();

		popup.add(addMenuItem(MixtractCommandType.PRINT_GROUP_INFO,
				hasSelectedGroup));
		popup.add(addMenuItem(MixtractCommandType.DELETE_GROUP,
				hasSelectedGroup));
		popup.add(addMenuItem(MixtractCommandType.CLEAR_ALLGROUPS, main()
				.hasTarget()));
		popup.addSeparator();
		popup.add(addMenuItem(MixtractCommandType.PRINT_ALLGROUPS, main()
				.hasTarget()));
		// popup.add(addMenuItem(MixtractCommand.PRINT_SUBGROUPS,
		// _main.hasTarget()));
		popup.add(addMenuItem(MixtractCommandType.MOUSE_DISPLAY, true));
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

	protected void createPopupMenu(MouseEvent e) {
		popup = new JPopupMenu();
		createCustomPopupMenu(e);
		popup.show((Component) e.getSource(), e.getX(), e.getY());
	}

	public void createCustomPopupMenu(MouseEvent e) {}

	/**
	 * @return isDragging
	 */
	protected final boolean isDragging() {
		return isDragging;
	}

	protected void setDragging(boolean b) {
		isDragging = b;
	}

	private void setStartPoint(Point point) {
		startPoint = point;
	}

	public Container self() {
		return _self;
	}

	/**
	 * @return _frame
	 */
	protected MainFrame frame() {
		return _frame;
	}

	protected MuseApp main() {
		return _main;
	}

}
