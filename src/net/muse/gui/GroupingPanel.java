package net.muse.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.muse.app.MuseApp;
import net.muse.command.MuseAppCommand;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.mixtract.command.MixtractCommand;
import net.muse.mixtract.command.MixtractCommandType;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.MelodyFlagViewer;
import net.muse.mixtract.gui.ViewerMode;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2008/04/24
 */
public class GroupingPanel extends JPanel implements TuneDataListener {
	private static final int LABEL_HEIGHT_OFFSET = 15;
	private static final long serialVersionUID = 1L;
	static float[] dashLineList = { 10.0f, 5.0f, 5.0f, 5.0f };
	protected static final int LABEL_HEIGHT = 20;
	private static final int DEFAULT_HEIGHT = 100;
	protected static final int LEVEL_PADDING = 3;
	private static final int DEFAULT_WIDTH = 1024;
	private final MuseAppCommand cmd = MixtractCommand.create(
			MixtractCommandType.PRINT_GROUP_INFO);
	private final BasicStroke stroke = new BasicStroke(1.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dashLineList,
			0.0f);

	protected MuseApp main;

	/* 格納データ */
	private TuneData data;
	private final ArrayList<GroupLabel> grouplist;
	private GroupLabel selectedGroup;
	private int maximumGroupLevel = 0;

	/* 描画モード */
	private ViewerMode viewerMode;
	private boolean displayMousePointer;
	private final boolean drawToolTips = true;

	/** マウス制御 */
	private MouseActionListener mouseActions = null;
	private NoteLabel mouseOveredNoteLabel;

	protected GroupingPanel() {
		super();
		grouplist = new ArrayList<>();
		viewerMode = ViewerMode.SCORE_VIEW;
		initialize();
	}

	@Override public void addGroup(Group g) {
		readTuneData();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.TuneDataListener#changeExpression(jp.crestmuse
	 * .mixtract.data.PhraseProfile.PhraseCurveType)
	 */
	@Override public void changeExpression(PhraseCurveType type) {
		if (type == PhraseCurveType.DYNAMICS)
			return;
		for (final GroupLabel l : getGrouplist()) {
			final Rectangle r = getLabelBounds(l.group(), l.group().getLevel());
			l.setBounds(r);
			// l.repaint();
		}
		// readTuneData();
		repaint();
	}

	@Override public void deleteGroup(GroupLabel g) {
		readTuneData();
		repaint();
	}

	@Override public void deselect(GroupLabel g) {
		if (selectedGroup != null) {
			selectedGroup.setSelected(false);
			selectedGroup = null;
		}
	}

	@Override public void editGroup(GroupLabel g) {}

	public MouseActionListener getMouseActions() {
		return mouseActions;
	}

	/**
	 * @return
	 * @return
	 */
	public GroupLabel getSelectedGroup() {
		return selectedGroup;
	}

	/**
	 * @return
	 */
	public boolean hasSelectedGroup() {
		return selectedGroup != null;
	}

	/**
	 * @return the displayMousePointer
	 */
	public boolean isDisplayMousePointer() {
		return displayMousePointer;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Graphics2D g2 = (Graphics2D) g;

		if (mouseActions != null) {
			final Point mousePoint = mouseActions.getMousePoint();
			if (displayMousePointer && mousePoint != null)
				g2.drawString("(" + mousePoint.x + ", " + mousePoint.y + ")",
						mousePoint.x, mousePoint.y);
		}
		if (selectedGroup != null)
			selectedGroup.setBackground(PartColor.SELECTED_COLOR);

		if (drawToolTips) {
			drawTooltips();
		}

		if (data == null || getGrouplist().size() == 0)
			return;
		drawHierarchyLine(g2);

		if (mouseActions.isGroupEditable()) {
			drawEditArea(g2);
		}

		/* マウスオーバー状態の音符情報を表示 */
		drawMouseOveredNoteInfo(g2);
	}

	protected void drawMouseOveredNoteInfo(Graphics2D g2) {
		if (mouseOveredNoteLabel == null)
			return;
		final NoteData nd = mouseOveredNoteLabel.getScoreNote();
		String str = nd.noteName() + "(" + nd.velocity() + ")" + nd.onset()
				+ "-" + nd.offset();
		str = switchViewerMode(nd);
		System.out.println(str + " at " + getMouseActions().getMousePoint());
		g2.drawString(str, getMouseActions().getMousePoint().x - PianoRoll
				.getDefaultAxisX(), getMouseActions().getMousePoint().y - main
						.getFrame().getKeyboard().getKeyHeight());
	}

	private String switchViewerMode(NoteData nd) {
		return String.format("%s (%s): %d-%d", nd.noteName(), nd.chord(), nd
				.onset(), nd.offset());
	}

	public void readTuneData() {
		removeAll();
		getGrouplist().clear();
		setMaximumGroupLevel(0);
		if (data == null)
			return;
		createHierarchicalGroupLabel(data.getRootGroup(), 0, 0);
		createNonHierarchicalGroupLabel();
	}

	/**
	 * @deprecated Use {@link #readTuneData()} instead
	 */
	@Deprecated public void readTuneData(MXTuneData target) {
		readTuneData();
		repaint();
	}

	/* (非 Javadoc)
	 * @see net.muse.gui.GroupEditListener#selectGroup(javax.swing.JLabel, boolean)
	 */
	@Override public void selectGroup(GroupLabel g, boolean flg) {
		if (selectedGroup != null)
			selectedGroup.setSelected(false);
		selectedGroup = flg ? g : null;
		cmd.setGroup(g);
		if (g != null)
			g.setSelected(flg);
		repaint();
	}

	/**
	 * @param main
	 */
	public void setController(MuseApp main) {
		this.main = main;
		mouseActions = createMouseActionListener(main);
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
		addKeyListener(createKeyActionListener(main));
	}

	/**
	 * @param displayMousePointer the displayMousePointer to set
	 */
	public void setDisplayMousePointer(boolean displayMousePointer) {
		this.displayMousePointer = displayMousePointer;
	}

	@Override public void setTarget(TuneData target) {
		if (data != target) {
			data = target;
		}
		readTuneData();
		repaint();
	}

	/**
	 * @param scoreView
	 */
	public void setViewMode(ViewerMode mode) {
		this.viewerMode = mode;
		readTuneData();
		revalidate();
		repaint();
	}

	public void showDetailViewer() {
		final Rectangle window = SwingUtilities.getLocalBounds(this);
		if (selectedGroup != null) {
			final JFrame f = new JFrame("Melody Information: " + selectedGroup
					.group());
			f.setLocation((int) (window.x + window.width * 0.25),
					window.height);
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			f.setLayout(new BorderLayout());
			f.add(MelodyFlagViewer.createNewViewer(selectedGroup.group()),
					BorderLayout.CENTER);
			f.pack();
			f.setVisible(true);
		}
	}

	public void transferExpressionOfMostSimilarGroup() {
		// ExpressionMaker.setMode(Mode.DIVIDE_BY_TOPNOTE_WITH_SIMILARITY);
		// final Group err =
		// ExpressionMaker.transferTo(selectedGroup.getGroup());
		// if (err != null) {
		// // 転写できなかった事例のパラメータを表示
		// final Rectangle window = SwingUtilities.getLocalBounds(this);
		// final JFrame f = new JFrame("Couldn't transfer this phrase " + err);
		// f.setLocation((int) (window.x + window.width * 0.25), window.height);
		// f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// f.setLayout(new BorderLayout());
		// f.add(MelodyFlagViewer.createNewViewer(err), BorderLayout.CENTER);
		// f.pack();
		// f.setVisible(true);
		// } else {
		// GUIUtil.printConsole(selectedGroup.getGroup().getDeviationList()
		// .toString());
		// }
	}

	void setMouseActions(MouseActionListener mouseActions) {
		this.mouseActions = mouseActions;
	}

	/**
	 * @param group
	 * @param level
	 */
	protected void createGroupLabel(Group group, int level) {
		if (group == null)
			return;
		group.setHierarchy(group.hasChild() || group.hasParent());

		setMaximumGroupLevel(level > getMaximumGroupLevel() ? level
				: getMaximumGroupLevel());

		final Rectangle r = getLabelBounds(group, level);
		final GroupLabel label = createGroupLabel(group, r);
		label.setController(main);
		group.setLevel(level);

		getGrouplist().add(label);
		add(label); // 描画
		createGroupLabel((Group) group.next(), level);
	}

	protected GroupLabel createGroupLabel(Group group, final Rectangle r) {
		return new GroupLabel(group, r);
	}

	/**
	 * create a hierarchical group label
	 *
	 * @author hashida
	 *
	 * @param group
	 * @param level
	 */
	protected void createHierarchicalGroupLabel(Group group, int level) {
		if (group == null)
			return;

		// create a new group-label
		createGroupLabel(group, level);
		createHierarchicalGroupLabel(group.child(), level + 1);
	}

	protected KeyActionListener createKeyActionListener(MuseApp main) {
		return new KeyActionListener(main, this) {

			@Override public MuseApp main() {
				return super.main();
			}

			@Override public GroupingPanel owner() {
				return (GroupingPanel) super.owner();
			}

		};
	}

	protected MouseActionListener createMouseActionListener(MuseApp main) {
		return new MouseActionListener(main, this) {

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java.
			 * awt.event.MouseEvent)
			 */
			@Override public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				if (isGroupEditable()) {
					setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
					repaint();
					return;
				}
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
				if (!SwingUtilities.isRightMouseButton(e))
					main().butler().notifyDeselectGroup();
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseReleased(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if (selectedGroup == null)
					main().butler().notifyDeselectGroup();
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu(
			 * java
			 * .awt.event.MouseEvent)
			 */
			@Override protected void createPopupMenu(MouseEvent e) {
				super.createPopupMenu(e);
				MixtractCommandType.SET_TYPE_CRESC.command().setGroup(
						getSelectedGroup());
				MixtractCommandType.SET_TYPE_DIM.command().setGroup(
						getSelectedGroup());
				MixtractCommandType.PRINT_GROUP_INFO.command().setGroup(
						getSelectedGroup());
				addMenuItemOnGroupingPanel();
				getPopup().show((Component) e.getSource(), e.getX(), e.getY());
			}

		};
	}

	/**
	 * @param list
	 */
	protected void createNonHierarchicalGroupLabel() {
		int level = getMaximumGroupLevel() + 1;
		for (final Group g : data.getMiscGroup()) {
			if (level < g.getLevel())
				level = g.getLevel() + 1;
			createGroupLabel(g, level);

			createGroupLabel(g.child(), level + 1);
		}
	}

	protected TuneData data() {
		return data;
	}

	/**
	 * @param g2
	 */
	protected void drawEditArea(Graphics2D g2) {
		main.butler().printConsole("eeee");
		// if (!MixtractCommand.getSelectedObjects().isOveredGroup())
		// editPointX = mouseActions.getMousePoint().x;
		// final BasicStroke dashed2 = new BasicStroke(1.0f,
		// BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_MITER, 8.0f, dashLineList, 0.0f);
		// g2.setStroke(dashed2);
		// g2.drawLine(editPointX, 0, editPointX, getHeight());
	}

	protected void drawHierarchyLine(final Graphics2D g2) {
		for (final GroupLabel l : getGrouplist()) {
			drawHierarchyLine(g2, l, l.child(getGrouplist()));
		}
	}

	/**
	 * @param g2
	 * @param parent
	 * @param child
	 */
	protected void drawHierarchyLine(final Graphics2D g2, GroupLabel parent,
			final GroupLabel child) {
		if (parent == null)
			return;
		if (child == null)
			return;

		drawLine(g2, parent, child);

		drawHierarchyLine(g2, child, child.child(getGrouplist()));
	}

	protected void drawLine(final Graphics2D g2, GroupLabel parent,
			final GroupLabel child) {
		// TODO ACII demo仕様：ユーザグループ以下の子グループは表示しない
		if (parent.group().getType() == GroupType.USER) {
			child.setVisible(false);
		} else if (!parent.isVisible()) {
			child.setVisible(false);
		} else {
			g2.setStroke(stroke);
			g2.setColor(Color.black);
			final int x1 = child.getX() + child.getWidth() / 2;
			final int keyHeight = main.getFrame().getKeyboard().getKeyHeight();
			final int y1 = child.getY() + child.getHeight() - keyHeight;
			final int x2 = parent.getX() + parent.getWidth() / 2;
			final int y2 = parent.getY() + keyHeight;
			g2.drawLine(x1, y1, x2, y2);
		}
	}

	protected ArrayList<GroupLabel> getGrouplist() {
		return grouplist;
	}

	protected int getMaximumGroupLevel() {
		return maximumGroupLevel;
	}

	protected MuseApp main() {
		return main;
	}

	protected void setData(TuneData data) {
		this.data = data;
	}

	protected void setMain(MuseApp main) {
		this.main = main;
	}

	protected void setMaximumGroupLevel(int maximumGroupLevel) {
		this.maximumGroupLevel = maximumGroupLevel;
	}

	private void createHierarchicalGroupLabel(List<Group> glist, int index,
			int level) {
		if (index >= glist.size())
			return;
		// hierarchical group structure
		createHierarchicalGroupLabel(glist.get(index), level);
		// next part
		createHierarchicalGroupLabel(glist, index + 1, level);
	}

	/**
	 *
	 */
	private void drawTooltips() {
		setToolTipText(
				"<html>Click the right button to show the contect menu.<br>"
						+ "Press `backspace': delete the selected group.");
	}

	/**
	 * @param group
	 * @param level
	 * @return
	 */
	private Rectangle getLabelBounds(Group group, int level) {
		final int y = setLabelY(level);
		int x, w;
		switch (viewerMode) {
		case REALTIME_VIEW:
			x = MainFrame.getXOfNote(group.realOnset()) + PianoRoll
					.getDefaultAxisX();
			w = MainFrame.getXOfNote(group.duration()) - 2;
			break;
		default:
			x = MainFrame.getXOfNote(group.onsetInTicks()) + PianoRoll
					.getDefaultAxisX();
			w = MainFrame.getXOfNote(group.offsetInTicks() - group
					.onsetInTicks()) - 2;
			break;
		}
		final Rectangle r = new Rectangle(x, y, w, LABEL_HEIGHT
				- LEVEL_PADDING);
		return r;
	}

	private void initialize() {
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		setOpaque(true);
		setLayout(null);
		setBackground(Color.white);
		setDoubleBuffered(true);
		setBorder(null);
	}

	/**
	 * @param gr
	 * @return
	 */
	protected int setLabelY(int level) {
		return LABEL_HEIGHT * level + LABEL_HEIGHT_OFFSET;
	}

	void setMouseOveredNoteLabel(NoteLabel src) {
		this.mouseOveredNoteLabel = src;
		repaint();
	}
}
