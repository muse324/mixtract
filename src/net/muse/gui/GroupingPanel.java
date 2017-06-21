package net.muse.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import net.muse.MuseApp;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.*;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.*;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2008/04/24
 */
public class GroupingPanel extends JPanel implements TuneDataListener {

	/**
	 * <h1>PrintGroupInfoCommand</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2010/02/15
	 */
	public static class PrintGroupInfoCommand extends MixtractCommand {

		private Group group = null;

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MixtractCommand#setGroup(jp.crestmuse.mixtract
		 * .gui.GroupLabel)
		 */
		@Override
		public void setGroup(GroupLabel groupLabel) {
			this.group = groupLabel.getGroup();
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override
		public void execute() {
			if (group == null) {
				group = _mainFrame.getGroupingPanel().getSelectedGroup()
						.getGroup();
			}
			System.out.println(String.format("Group %s\n\t%s\n\t%s\n\t%s\n",
					group.name(), group.getDynamicsCurve(), group
							.getTempoCurve(), group.getArticulationCurve()));
		}

		public PrintGroupInfoCommand(String... lang) {
			super(lang);
		}
	}

	private MixtractCommand cmd = MixtractCommand.PRINT_GROUP_INFO;
	private BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND,
			BasicStroke.JOIN_ROUND, 10.0f, dashLineList, 0.0f);
	private static final long serialVersionUID = 1L;
	static float[] dashLineList = { 10.0f, 5.0f, 5.0f, 5.0f };
	static final int LABEL_HEIGHT = 20;
	private static final int DEFAULT_HEIGHT = 100;
	private static final int LEVEL_PADDING = 3;
	private static final int DEFAULT_WIDTH = 1024;

	private MuseApp main;

	/* 格納データ */
	private MXTuneData data;
	private final ArrayList<GroupLabel> grouplist;
	private GroupLabel selectedGroup;
	private int maximumGroupLevel = 0;

	/* 描画モード */
	private ViewerMode viewerMode;
	private boolean displayMousePointer;
	private boolean groupEditable;
	private boolean drawToolTips = true;

	/** マウス制御 */
	private MouseActionListener mouseActions = null;

	protected GroupingPanel() {
		super();
		grouplist = new ArrayList<GroupLabel>();
		viewerMode = ViewerMode.SCORE_VIEW;
		initialize();
	}

	/**
	 * @param main
	 */
	public void setController(MuseApp main) {
		this.main = main;
		mouseActions = new MouseActionListener(main, this) {

			/*
			 * (non-Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mousePressed(java
			 * .awt.event.MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (!SwingUtilities.isRightMouseButton(e))
					_main.notifyDeselectGroup();
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
				if (selectedGroup == null)
					_main.notifyDeselectGroup();
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java.
			 * awt.event.MouseEvent)
			 */
			@Override
			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				if (isGroupEditable()) {
					setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
					repaint();
					return;
				}
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseMoved(java.awt
			 * .event.MouseEvent)
			 */
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu(java
			 * .awt.event.MouseEvent)
			 */
			@Override
			protected void createPopupMenu(MouseEvent e) {
				super.createPopupMenu(e);
				MixtractCommand.SET_TYPE_CRESC.setGroup(getSelectedGroup());
				MixtractCommand.SET_TYPE_DIM.setGroup(getSelectedGroup());
				MixtractCommand.PRINT_GROUP_INFO.setGroup(getSelectedGroup());
				addMenuItemOnGroupingPanel();
				getPopup().show((Component) e.getSource(), e.getX(), e.getY());
			}

		};
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
	}

	public void addGroup(Group g) {
		readTuneData();
		repaint();
	}

	public void deleteGroup(GroupLabel g) {
		readTuneData();
		repaint();
	}

	public void editGroup(GroupLabel g) {}

	/**
	 * @return the displayMousePointer
	 */
	public boolean isDisplayMousePointer() {
		return displayMousePointer;
	}

	/**
	 * @return
	 */
	public boolean isGroupEditable() {
		return groupEditable;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
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

		if (data == null || grouplist.size() == 0)
			return;
		drawHierarchyLine(g2, grouplist.get(0), null);

		if (drawToolTips) {
			drawTooltips();
		}

		if (mouseActions != null && mouseActions.isMousePressed()
			&& !mouseActions.isShiftKeyPressed() && groupEditable) {
			drawEditArea(g2);
		}
	}

	/**
	 *
	 */
	private void drawTooltips() {
		setToolTipText("Click the right button to show the contect menu.");
	}

	/**
	 * @deprecated Use {@link #readTuneData()} instead
	 */
	@Deprecated
	public void readTuneData(MXTuneData target) {
		readTuneData();
		repaint();
	}

	public void readTuneData() {
		removeAll();
		grouplist.clear();
		maximumGroupLevel = 0;
		if (data == null)
			return;
		createHierarchicalGroupLabel(data.getRootGroup(), 0, 0);
		createNonHierarchicalGroupLabel();
	}

	public void selectGroup(GroupLabel g, boolean flg) {
		if (selectedGroup != null)
			selectedGroup.setSelected(false);
		selectedGroup = (flg) ? g : null;
		cmd.setGroup(g);
		if (g != null)
			g.setSelected(flg);
		repaint();
	}

	/**
	 * @param displayMousePointer the displayMousePointer to set
	 */
	public void setDisplayMousePointer(boolean displayMousePointer) {
		this.displayMousePointer = displayMousePointer;
	}

	public void setGroupEditable(boolean groupEditable) {
		this.groupEditable = groupEditable;
	}

	public void showDetailViewer() {
		final Rectangle window = SwingUtilities.getLocalBounds(this);
		if (selectedGroup != null) {
			final JFrame f = new JFrame("Melody Information: "
										+ selectedGroup.getGroup());
			f.setLocation((int) (window.x + window.width * 0.25), window.height);
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			f.setLayout(new BorderLayout());
			f.add(MelodyFlagViewer.createNewViewer(selectedGroup.getGroup()),
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

	private void createHierarchicalGroupLabel(List<Group> glist, int index,
			int level) {
		if (index >= glist.size())
			return;
		// hierarchical group structure
		createHierarchicalGroupLabel(glist.get(index), level);
		// next part
		createHierarchicalGroupLabel(glist, index + 1, level);
	}

	private void createHierarchicalGroupLabel(Group group, int level) {
		if (group == null)
			return;

		// create a new group-label
		if (group.hasChild() || group.hasParent())
			createGroupLabel(group, level);

		createHierarchicalGroupLabel(group.getChildFormerGroup(), level + 1);
		createHierarchicalGroupLabel(group.getChildLatterGroup(), level + 1);
	}

	/**
	 * @param group
	 * @param level
	 */
	private void createGroupLabel(Group group, int level) {
		if (group == null)
			return;
		group.setHierarchy(group.hasChild() || group.hasParent());
		// if (group.getType() == GroupType.NOTE )return;
		// if(!group.hasChild())
		// return;

		maximumGroupLevel = (level > maximumGroupLevel) ? level
				: maximumGroupLevel;

		final Rectangle r = getLabelBounds(group, level);
		final GroupLabel label = createGroupLabel(group, r);
		label.setController(main);
		group.setLevel(level);

		// final Color c = label.getBackground();
		// if (MixtractCommand.getSelectedObjects().getLayerName() == null
		// || group.getLayerId().equals(
		// MixtractCommand.getSelectedObjects().getLayerName()))
		// label.setBackground(c);
		// else {
		// label.setBackground(c.brighter());
		// }
		grouplist.add(label);
		add(label); // 描画
	}

	protected GroupLabel createGroupLabel(Group group, final Rectangle r) {
		final GroupLabel label = new GroupLabel(group, r);
		return label;
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
			x = MainFrame.getXOfNote(group.realOnset())
				+ PianoRoll.getDefaultAxisX();
			w = MainFrame.getXOfNote(group.duration()) - 2;
			break;
		default:
			x = MainFrame.getXOfNote(group.onsetInTicks())
				+ PianoRoll.getDefaultAxisX();
			w = MainFrame.getXOfNote(group.offsetInTicks()
										- group.onsetInTicks()) - 2;
			break;
		}
		final Rectangle r = new Rectangle(x, y, w, LABEL_HEIGHT - LEVEL_PADDING);
		return r;
	}

	/**
	 * @param list
	 */
	private void createNonHierarchicalGroupLabel() {
		int level = maximumGroupLevel + 1;
		for (Group g : data.getGroupArrayList()) {
			if (level < g.getLevel())
				level = g.getLevel() + 1;
			createGroupLabel(g, level);
			createGroupLabel(g.getChildFormerGroup(), level + 1);
			createGroupLabel(g.getChildLatterGroup(), level + 1);
		}
	}

	/**
	 * @param g2
	 */
	private void drawEditArea(Graphics2D g2) {
		// if (!MixtractCommand.getSelectedObjects().isOveredGroup())
		// editPointX = mouseActions.getMousePoint().x;
		// final BasicStroke dashed2 = new BasicStroke(1.0f,
		// BasicStroke.CAP_BUTT,
		// BasicStroke.JOIN_MITER, 8.0f, dashLineList, 0.0f);
		// g2.setStroke(dashed2);
		// g2.drawLine(editPointX, 0, editPointX, getHeight());
	}

	/**
	 * @param g2
	 * @param child
	 * @param parent
	 */
	private void drawHierarchyLine(final Graphics2D g2, final GroupLabel child,
			GroupLabel parent) {
		if (child == null)
			return;

		if (parent != null) {
			// TODO ACII demo仕様：ユーザグループ以下の子グループは表示しない
			if (parent.getGroup().getType() == GroupType.USER) {
				child.setVisible(false);
			} else if (!parent.isVisible()) {
				child.setVisible(false);
			} else {
				g2.setStroke(stroke);
				g2.setColor(Color.black);
				final int x1 = child.getX() + child.getWidth() / 2;
				final int y1 = child.getY() + child.getHeight()
								- KeyBoard.keyHeight;
				final int x2 = parent.getX() + parent.getWidth() / 2;
				final int y2 = parent.getY() + KeyBoard.keyHeight;
				g2.drawLine(x1, y1, x2, y2);
			}
		}

		drawHierarchyLine(g2, child.getChildFormer(grouplist), child);
		drawHierarchyLine(g2, child.getChildLatter(grouplist), child);
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
	private int setLabelY(int level) {
		return LABEL_HEIGHT * level + 15;
	}

	public void setTarget(MXTuneData target) {
		if (data != target) {
			data = target;
		}
		readTuneData();
		repaint();
	}

	public void deselect(GroupLabel g) {
		if (selectedGroup != null) {
			selectedGroup.setSelected(false);
			selectedGroup = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.TuneDataListener#changeExpression(jp.crestmuse
	 * .mixtract.data.PhraseProfile.PhraseCurveType)
	 */
	public void changeExpression(PhraseCurveType type) {
		if (type == PhraseCurveType.DYNAMICS)
			return;
		for (GroupLabel l : grouplist) {
			Rectangle r = getLabelBounds(l.getGroup(), l.getGroup().getLevel());
			l.setBounds(r);
			// l.repaint();
		}
		// readTuneData();
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

	/**
	 * @return
	 */
	public boolean hasSelectedGroup() {
		return selectedGroup != null;
	}

	/**
	 * @return
	 * @return
	 */
	public GroupLabel getSelectedGroup() {
		return selectedGroup;
	}

	public static class ClearAllGroupsCommand extends MixtractCommand {
		public ClearAllGroupsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override
		public void execute() {
			getTarget().getGroupArrayList().clear();
			for (Group g : getTarget().getRootGroup())
				getTarget().deleteGroupFromData(g);
			_main.notifySetTarget();
		}

	}
}
