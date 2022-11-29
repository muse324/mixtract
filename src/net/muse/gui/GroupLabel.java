package net.muse.gui;

import java.awt.*;
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

	private GroupLabel parent;

	protected GroupLabel() {
		super();
		initialize();
	}

	protected GroupLabel(Group group, Rectangle r) {
		this();
		this.setGroup(group);
		this.setPartNumber(group.getBeginNote().musePhony());
		setLocation(r.x, r.y);
		setBounds(r);
		setTypeShape(group.getType());
	}

	public GroupLabel child() {
		return child;
	}

	public GroupLabel child(ArrayList<GroupLabel> grouplist) {
		if (child == null) {
			for (final GroupLabel l : grouplist) {
				if (group().hasChild() && group().child().equals(l.group())) {
					child = l;
					break;
				}
			}
		}
		return child;
	}

	public Group group() {
		return group;
	}

	public boolean hasChild() {
		return child() != null;
	}

	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param e
	 * @param p
	 * @param r
	 * @param shiftKeyPressed TODO
	 * @param mousePressed TODO
	 * @param src
	 */
	public void moveLabelVertical(MouseEvent e, Point p, Rectangle r,
			boolean shiftKeyPressed, boolean mousePressed) {
		r.y = p.y;
		setBounds(r);
		if (shiftKeyPressed) {
			moveLabelVertical(e, mousePressed);
		}
		repaint();
	}

	public void setChild(GroupLabel child) {
		this.child = child;
		group().setChild(child.group());
		if (child.parent() == null || !child.parent().equals(this)) {
			child.setParent(this);
		}
	}

	public void setController(MuseApp app) {
		mouseActions = createMouseActionListener(app);
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
		keyActions = createKeyActionListener(app);
		addKeyListener(keyActions);
	}

	/**
	 * @param partNumber the partNumber to set
	 */
	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	/**
	 * @param hasSelectedNoteList
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		setBackground(isSelected ? PartColor.SELECTED_COLOR
				: getCurrentColor());
		setSelectedOption(isSelected);
		repaint();
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
			setText(group().name());
			setCurrentColor(type.getColor());
			initialize();
			setBorder(null);
		}
	}

	@Override public String toString() {
		return group().name();
	}

	void setMouseOver(boolean b) {
		if (b) {
			setBackground(PartColor.MOUSE_OVER_COLOR);
		} else
			setBackground(getCurrentColor());
	}

	void showInfoViewer(MuseApp app, Group gr) {
		final InfoViewer pv = InfoViewer.create(app, gr);
		pv.setTitle(gr.name());
		app.butler().addInfoViewerList(pv);
		pv.pack();
		pv.setVisible(true);
		pv.preset();
	}

	protected KeyActionListener createKeyActionListener(MuseApp app) {
		return new KeyActionListener(app, this);
	}

	protected MouseActionListener createMouseActionListener(MuseApp app) {
		return new GLMouseActionListener(app, this);
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

	protected void moveLabelVertical(MouseEvent e, boolean mousePressed) {
		Point pc;
		if (hasChild()) {
			pc = child().getLocation();
			pc.translate(e.getX(), e.getY());
			child().moveLabel(e, pc, mousePressed);
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

	/**
	 * @param mousePosition TODO
	 */
	protected void setEditMode(Point mousePosition) {
		final Rectangle r = getBounds();
		final Rectangle st = new Rectangle(new Point(r.x, r.y), new Dimension(10,
				r.height));
		final Rectangle ed = new Rectangle(new Point(r.x + r.width - 10, r.y),
				new Dimension(15, r.height));
		final Rectangle m = new Rectangle(mousePosition, new Dimension(5, 5));

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

	protected void setGroup(Group group) {
		this.group = group;
	}

	protected void setSelectedOption(boolean isSelected) {
		setFocusable(isSelected);
		MixtractCommandType.DELETE_GROUP.command().setGroup(isSelected ? this
				: null);
		if (isSelected)
			requestFocus();
	}

	protected void setStartEdit(boolean startEdit) {
		this.startEdit = startEdit;
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
			final Point pc = child().getLocation();
			pc.translate(e.getX(), e.getY());
			moveLabel(e, pc, mousePressed);
		}
	}

	private GroupLabel parent() {
		return parent;
	}

	private void setEndEdit(boolean endEdit) {
		this.endEdit = endEdit;
	}

	private void setParent(GroupLabel label) {
		parent = label;
		group().setParent(label.group());
		if (!parent.hasChild() || !parent.child().equals(this)) {
			parent.setChild(this);
		}
	}

}
