package net.muse.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JPanel;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.app.MuseApp;
import net.muse.data.Concierge;
import net.muse.data.Group;
import net.muse.data.Harmony;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.misc.MuseObject;
import net.muse.misc.Util;
import net.muse.mixtract.command.ChangePartCommand;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.ViewerMode;

/**
 * ピアノロール（音符部分）を描画するクラスオブジェクトです．
 *
 * @author Mitsuyo Hashida
 */
public class PianoRoll extends JPanel implements TuneDataListener,
		CanvasMouseListener {

	private static final int DEFAULT_NOTELABEL_OFFSET = 5;
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WIDTH = 1024;
	private static int defaultAxisX = 10;

	protected int axisX = 10;

	private final MuseApp main; // @jve:decl-index=0:

	/** 楽曲データ */
	private TuneData data; // @jve:decl-index=0:

	/* 各種描画モード */
	private ViewerMode viewerMode; // @jve:decl-index=0:
	private boolean isMouseSelectBoxDraw;
	private boolean drawToolTips = true;
	private boolean drawMelodyLine = false;
	/* マウス制御 */
	private MouseActionListener mouseActions; // @jve:decl-index=0:
	private Point mouseEndPoint;
	private Point mouseStartPoint;
	/* 格納データ */
	final LinkedList<NoteLabel> selectedNoteLabels = new LinkedList<NoteLabel>();
	protected NoteLabel notelist = null;
	protected NoteLabel mouseOveredNoteLabel = null;
	private int selectedVoice;
	final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR); // @jve:decl-index=0:
	final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); // @jve:decl-index=0:
	private GroupLabel selectedGroup;

	protected PianoRoll(MuseApp main) {
		super();
		this.main = main;
		setSelectedVoice(-1);
		viewerMode = ViewerMode.REALTIME_VIEW;
		initialize();
		setController();
	}

	/**
	 * @return defaultAxisX
	 */
	static int getDefaultAxisX() {
		return defaultAxisX;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.GroupEditListener#addGroup(net.muse.mixtract.data.
	 * Group)
	 */
	@Override public void addGroup(Group g) {
		setFocusable(false);
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.TuneDataListener#changeExpression(net.muse.mixtract
	 * .data.curve.PhraseCurveType)
	 */
	@Override public void changeExpression(PhraseCurveType type) {
		if (type == PhraseCurveType.DYNAMICS)
			return;
		resizeLabels(notelist());
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.GroupEditListener#deleteGroup(javax.swing.JLabel)
	 */
	@Override public void deleteGroup(GroupLabel g) {
		deselect(g);
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.GroupEditListener#deselect(javax.swing.JLabel)
	 */
	@Override public void deselect(GroupLabel g) {
		setMouseOveredNoteLabel(null);
		clearSelection();
		setFocusable(false);
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.GroupEditListener#editGroup(javax.swing.JLabel)
	 */
	@Override public void editGroup(GroupLabel g) {
		throw new UnsupportedOperationException(); // TODO 実装
	}

	public MouseActionListener getMouseActions() {
		return mouseActions;
	}

	/**
	 * @return selectedNoteLabels
	 */
	public final LinkedList<NoteLabel> getSelectedNoteLabels() {
		return selectedNoteLabels;
	}

	public int getSelectedVoice() {
		return selectedVoice;
	}

	public void makeNoteLabel() {
		removeAll();
		this.notelist = null;
		if (data() == null)
			return;
		for (Group g : data().getRootGroup()) {
			makeNoteLabel(g);
		}
		// 巻き戻す
		while (notelist.hasPrevious())
			notelist = notelist.prev();
		validate();
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override public void paintComponent(final Graphics g) {
		/* おまじない */
		final Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		final RenderingHints qualityHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);

		/* 背景の塗り絵 */
		drawBackgroundKeyboard(g2, getWidth());

		/* （オプション）座標軸を左の鍵盤パネルから少し右にずらす */
		// g2.translate(axisX, 0);
		/* 楽譜が読み込まれてないなら以下の描画は不要 */
		if (data() != null) {

			/* 座標軸を再取得し、note labels をリスケール */
			rescaleNoteLabels();

			/* 小節線を表示 */
			drawMeasureLine(g2);
			/* 和声カラーを表示 */
			drawFifthsKeyText(g2, notelist, null);
			/* 和声カラーを表示 */
			drawHarmonyGround(g2, notelist, null);

			/* その他カスタマイズ描画 */
			drawOptionalInfo(g2);
			/* TODO（実装中）タイムライン表示 */
			drawTimeline(g2);
			// 以下コメントアウト中
			//
			// /* グループ線 */
			// if (_frame.getEditMode() ==
			// JPopEMainFrame.GROUPING_EDIT_MODE)
			// drawGroupCurves(g2,
			// groupList.iterator());
			//

			/* 選択されたグループの旋律外形を描画する */
			if (drawMelodyLine)
				drawMelodyFlagmentOfSelectedGroup(g2);

			/* マウスオーバー状態の音符情報を表示 */
			drawMouseOveredNoteInfo(g2);
		}

		/* 選択範囲の矩形を描く． */
		if (isMouseSelectBoxDraw)
			drawSelectedMouseBox(g2);

		if (drawToolTips) {
			drawTooltips();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.components.GroupEditListener#selectGroup(javax
	 * .swing.JLabel, boolean)
	 */
	@Override public void selectGroup(GroupLabel g, boolean flg) {
		clearSelection();
		setSelectedGroup(g);
		selectGroup(g.group());
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.CanvasMouseListener#setShowCurrentX(boolean,
	 * int)
	 */
	@Override public void setShowCurrentX(boolean showCurrentX, int x) {}

	@Override public void setTarget(TuneData target) {
		this.setData(target);
		ChangePartCommand.setPartSize(target.getRootGroup().size());
		makeNoteLabel();
		revalidate();
		repaint();
	}

	/**
	 * @param scoreView
	 */
	public void setViewMode(ViewerMode mode) {
		this.viewerMode = mode;
		if (data() != null) {
			makeNoteLabel();
			revalidate();
		}
		repaint();
	}

	/**
	 * @param e
	 */
	void getLeftUpperCornerAxis(MouseEvent e) {
		setMouseSelectBoxDraw(true);
		setStartPositionOfMouseBox(e);
	}

	void selectNotes() {
		selectedNoteLabels.clear();
		for (Component c : getComponents()) {
			if (!(c instanceof NoteLabel))
				continue;
			NoteLabel l = (NoteLabel) c;
			if (l.isSelected()) {
				selectedNoteLabels.addLast(l);
			}
		}
	}

	void setMouseEndPoint(MouseEvent e) {
		mouseEndPoint = e.getPoint();
	}

	void setMouseOveredNoteLabel(NoteLabel src) {
		this.mouseOveredNoteLabel = src;
		repaint();
	}

	void setMouseSelectBoxDraw(boolean isMouseSelectBoxDraw) {
		this.isMouseSelectBoxDraw = isMouseSelectBoxDraw;
	}

	void setSelectedVoice(int selectedVoice) {
		this.selectedVoice = selectedVoice;
	}

	protected Concierge butler() {
		return main.butler();
	}

	protected NoteLabel createNoteLabel(final NoteData note,
			final Rectangle r) {
		return new NoteLabel(note, r);
	}

	protected PianoRollActionListener createPianoRollMouseAction(MuseApp app) {
		return new PianoRollActionListener(app, this);
	}

	/**
	 * @param g2
	 * @param notelist2
	 * @param object
	 */
	protected void drawFifthsKeyText(Graphics2D g2, NoteLabel cur,
			NoteLabel pre) {
		if (cur == null)
			return;
		final int fifths = cur.getScoreNote().fifths();
		if (pre == null || fifths != pre.getScoreNote().fifths()) {
			g2.drawString(Util.fifthsToString(fifths), cur.getX() + 1, 24);
		}
		drawFifthsKeyText(g2, cur.next(), cur);
	}

	protected void drawHarmonyGround(Graphics2D g2, NoteLabel cur,
			NoteLabel pre) {
		if (cur == null)
			return;

		if (pre != null) {
			Harmony c = cur.getScoreNote().chord();
			if (c != pre.getScoreNote().chord()) {
				g2.setColor(c.color());
				g2.drawLine(cur.getX(), 0, cur.getX(), getHeight());
			}
		}
		drawHarmonyGround(g2, cur.next(), cur);
	}

	protected void drawMouseOveredNoteInfo(Graphics2D g2) {
		if (mouseOveredNoteLabel == null)
			return;
		final NoteData nd = mouseOveredNoteLabel.getScoreNote();
		String str = nd.noteName() + "(" + nd.velocity() + ")" + nd.onset()
				+ "-" + nd.offset();
		switch (viewerMode) {
		case REALTIME_VIEW:
			str = String.format("%s (%s): v%d / %.0f[%.0f-%.0f]", nd.noteName(),
					nd.chord(), nd.velocity(), nd.realOffset() - nd.realOnset(),
					nd.realOnset(), nd.realOffset());
			break;
		default:
			str = String.format("%s (%s): %d-%d", nd.noteName(), nd.chord(), nd
					.onset(), nd.offset());
			break;
		}
		System.out.println(str + " at " + getMouseActions().getMousePoint());
		g2.drawString(str, getMouseActions().getMousePoint().x - axisX,
				getMouseActions().getMousePoint().y - main.getFrame()
						.getKeyboard().getKeyHeight());
	}

	/**
	 * ピアノロール画面にオリジナル情報を描画します。サブクラスで実装してください。
	 *
	 * @param g2
	 */
	protected void drawOptionalInfo(Graphics2D g2) {}

	/**
	 * マウスの選択範囲に収まる音符を色付けする．
	 */
	protected void encloseNotes() {
		if (data() == null)
			return;
		final Rectangle mouseBox = getMouseActions().getMouseBox();
		if (mouseBox == null)
			return;

		for (Component c : getComponents()) {
			if (!(c instanceof NoteLabel))
				continue;
			final NoteLabel l = (NoteLabel) c;
			final Rectangle r = l.getBounds();
			// 横方向
			if (r.x + r.width < mouseBox.x || mouseBox.x
					+ mouseBox.width < r.x) {
				l.setSelected(false);
				continue;
			}
			// 縦方向
			if (r.y + r.height < mouseBox.y || mouseBox.y
					+ mouseBox.height < r.y) {
				l.setSelected(false);
				continue;
			}
			// ひとつめの音のvoiceで選択声部を制限する
			if (getSelectedVoice() < 0) {
				setSelectedVoice(l.getScoreNote().musePhony());
				System.out.println("selected voice: " + getSelectedVoice());
			}
			if (l.getScoreNote().musePhony() != getSelectedVoice())
				continue;
			l.setSelected(true);
		}
	}

	/**
	 * @return axisX
	 */
	protected int getAxisX() {
		return axisX;
	}

	/**
	 * @param nd
	 * @param offset
	 *            同音打鍵時の描画領域確保（ピクセル）
	 * @return
	 */
	protected Rectangle getLabelBounds(final NoteData nd, final int offset) {
		if (nd == null)
			return null;
		final int h = main().getFrame().getKeyboard().getKeyHeight();
		final int y = KeyBoard.getYPositionOfPitch(nd.noteNumber()) * h;
		int x, w;
		switch (viewerMode) {
		case REALTIME_VIEW:
			x = MainFrame.getXOfNote(nd.realOnset());
			w = MainFrame.getXOfNote(nd.duration()) - offset;
			break;
		default:
			x = MainFrame.getXOfNote(nd.onset());
			w = MainFrame.getXOfNote(nd.timeValue()) - offset;
			break;
		}
		return new Rectangle(x + axisX, y, w, h);
	}

	/**
	 * @return viewerMode
	 */
	protected final ViewerMode getViewerMode() {
		return viewerMode;
	}

	/**
	 * @return selectedGroup
	 */
	protected GroupLabel group() {
		return selectedGroup;
	}

	/**
	 * @return main
	 */
	protected MuseApp main() {
		return main;
	}

	protected void makeNoteLabel(Group group) {
		if (group.hasChild()) {
			makeNoteLabel(group.child());
		} else
			makeNoteLabel(group.getBeginNote(), false);
	}

	protected void makeNoteLabel(NoteData note, boolean isChild) {
		if (note == null)
			return;
		int offset = existSamePitchNoteJustBefore(note)
				? DEFAULT_NOTELABEL_OFFSET
				: 0;
		makeNoteLabel(note, offset, isChild);
		makeNoteLabel(note.child(), true);
		makeNoteLabel(note.next(), false);
	}

	/**
	 * @param note
	 * @param offset
	 *            同音打鍵が続いた場合の描画間隔ピクセル
	 */
	protected void makeNoteLabel(final NoteData note, int offset,
			boolean isChild) {
		if (note == null)
			return;

		final Rectangle r = getLabelBounds(note, offset);
		final NoteLabel n = createNoteLabel(note, r);
		n.setController(main());
		n.setSelected(getSelectedNoteLabels().contains(n));
		if (notelist == null) {
			notelist = n;
		} else {
			if (isChild) {
				notelist.setChild(n);
				n.setParent(notelist);
				this.notelist = notelist.child();
			} else {
				while (notelist.hasParent()) {
					if (n.getScoreNote().hasPrevious() && n.getScoreNote()
							.previous().equals(notelist.getScoreNote()))
						break;
					notelist = notelist.parent();
				}
				notelist.setNext(n);
				n.setParent(notelist.parent());
				notelist = notelist.next();
			}
		}
		add(n);

		if (r.x + r.width > getWidth()) {
			Dimension sz = getPreferredSize();
			sz.width = r.x + r.width + 10;
			setPreferredSize(sz);
			repaint();
		}
	}

	/**
	 * @return notelist
	 */
	protected NoteLabel notelist() {
		return notelist;
	}

	protected void rescaleNoteLabels() {}

	protected void resizeLabels(NoteLabel label) {
		if (label == null)
			return;
		Rectangle r = getLabelBounds(label.getScoreNote(), label.getOffset());
		label.setBounds(r);
		resizeLabels(label.child());
		resizeLabels(label.next());
	}

	/**
	 * @param group
	 */
	protected void selectGroup(Group group) {
		if (group == null)
			return;
		selectGroup(group.child());
		for (Component c : getComponents()) {
			NoteLabel l = (NoteLabel) c;
			selectNote(l, group.getBeginNote(), group.getEndNote());
		}
	}

	/**
	 * @param notelist2
	 * @param note
	 * @param end
	 */
	protected void selectNote(NoteLabel l, NoteData note, NoteData end) {
		if (note == null)
			return;
		if (l.getScoreNote().equals(note)) {
			l.setSelected(true);
			selectedNoteLabels.add(l);
		}
		selectNote(l, note.child(), end);
		selectNote(l, note.next(), end);
	}

	/**
	 *
	 */
	private void clearSelection() {
		for (NoteLabel l : selectedNoteLabels)
			l.setSelected(false);
		selectedNoteLabels.clear();
		setSelectedVoice(-1);
		setSelectedGroup(null);
		repaint();
	}

	private KeyActionListener createKeyActions(MuseObject app) {
		return new KeyActionListener(app, this);
	}

	/**
	 * @return data
	 */
	private TuneData data() {
		return data;
	}

	/**
	 * キーボードを薄く描画します．
	 *
	 * @param g
	 * @param width
	 */
	private void drawBackgroundKeyboard(final Graphics2D g, int width) {
		int curHeight = 0;
		for (int i = 0; i < KeyBoard.maximumKeyRegister; i++) {
			if (KeyBoard.whiteMidiKey.contains(KeyBoard.getYPositionOfPitch(i)
					% 12)) {
				// 白鍵
				g.setColor(Color.lightGray);
				g.drawLine(0, curHeight, width, curHeight);
			} else {
				// 黒鍵
				g.setColor(Color.getHSBColor((float) 0.5, (float) 0.,
						(float) 0.9));
				g.fillRect(0, curHeight, width, main.getFrame().getKeyboard()
						.getKeyHeight());
			}
			curHeight += main.getFrame().getKeyboard().getKeyHeight();
		}
	}

	/**
	 * @param g
	 * @param onset
	 */
	private void drawMeasureLine(final Graphics2D g) {
		for (Component src : getComponents()) {
			NoteLabel n = (NoteLabel) src;
			final Rectangle r = n.getBounds();
			// draw measure line
			if (!n.hasParent() && n.isMeasureBeginning()) {
				g.setColor(Color.DARK_GRAY);
				g.drawLine(r.x, getHeight(), r.x, 0);
				g.drawString(String.valueOf(n.getScoreNote().measureNumber()),
						r.x + 1, 12);
			}
		}
	}

	/**
	 * @param g2
	 */
	private void drawMelodyFlagmentOfSelectedGroup(Graphics2D g2) {

		if (group() == null || selectedNoteLabels.size() < 1)
			return;

		int x1 = selectedNoteLabels.get(0).getX();
		int y1 = selectedNoteLabels.get(0).getY() + selectedNoteLabels.get(0)
				.getHeight() / 2;
		NoteLabel n4 = selectedNoteLabels.get(selectedNoteLabels.size() - 1);
		int x4 = n4.getX() + n4.getWidth();
		int y4 = n4.getY() + n4.getHeight() / 2;

		// final Note bg = group.getBeginningNote();
		Note n1 = selectedGroup.group().getMelodyFlagment().getFormerLastNote();
		final Note n2 = selectedGroup.group().getMelodyFlagment()
				.getLatterFirstNote();
		// final Note ed = group.getEndNote();
		final int keyheight = main.getFrame().getKeyboard().getKeyHeight();
		// final int y1 = KeyBoard.getYPositionOfPitch(bg.notenum()) *
		// keyheight;
		final int y2 = KeyBoard.getYPositionOfPitch(n1.notenum()) * keyheight;
		final int y3 = KeyBoard.getYPositionOfPitch(n2.notenum()) * keyheight;
		// final int y4 = KeyBoard.getYPositionOfPitch(ed.notenum()) *
		// keyheight;
		final int tpb = main.getTicksPerBeat();
		// final int x1 = getX(bg.onset(tpb)) + axisX;
		final int x2 = MainFrame.getXOfNote(n1.offset(tpb)) + axisX;
		final int x3 = MainFrame.getXOfNote(n2.onset(tpb)) + axisX;
		// final int x4 = getX(ed.offset(tpb)) + axisX;
		g2.setColor(Color.MAGENTA);// TODO 定数代入
		g2.drawLine(x1, y1, x4, y4);
		g2.drawLine(x1, y1, x2, y2);
		g2.drawLine(x3, y3, x4, y4);
	}

	/**
	 * @param g2
	 */
	private void drawSelectedMouseBox(Graphics2D g2) {

		final int sx = (mouseStartPoint.x < mouseEndPoint.x) ? mouseStartPoint.x
				: mouseEndPoint.x;
		final int sy = (mouseStartPoint.y < mouseEndPoint.y) ? mouseStartPoint.y
				: mouseEndPoint.y;
		final int w = (mouseStartPoint.x < mouseEndPoint.x) ? mouseEndPoint.x
				- sx : mouseStartPoint.x - sx;
		final int h = (mouseStartPoint.y < mouseEndPoint.y) ? mouseEndPoint.y
				- sy : mouseStartPoint.y - sy;
		final Rectangle r = new Rectangle(sx, sy, w, h);
		getMouseActions().setMouseBox(r);
		// if (MixtractCommand.getSelectedObjects() != null) {
		// MixtractCommand.getSelectedObjects().setMouseBox(r);
		// }
		g2.setColor(Color.black);
		g2.drawRect(sx, sy, w, h);
	}

	private void drawTimeline(Graphics2D g2) {
		// if (!timelinePlayer.isRunning())
		// return;
		//
		// int t = getWidthOfNote(timelinePlayer.getCurrentTime() * timeRate
		// / BEAT_PIXEL * 1.7);
		// GUIUtil.printConsole("#pianoroll current time = " + t);
		// g2.setColor(Color.black);
		// g2.drawLine(t, 0, t, DEFAULT_HEIGHT);
	}

	/**
	 */
	private void drawTooltips() {
		if (selectedNoteLabels.size() == 0)
			setToolTipText("<html>Drag in this area to select notes. <br>"
					+ "The system automatically understand the specific part-number you selected first.</html>");
		else
			setToolTipText(
					"<html>Click the right button - show the contect menu.<br>"
							+ "Press `g': make a group");
	}

	private boolean existSamePitchNoteJustBefore(NoteData note) {
		NoteLabel l = notelist();
		if (l == null)
			return false;
		while (l.hasPrevious()) {
			NoteData before = l.getScoreNote();
			if (!before.rest() && before.noteNumber() == note.noteNumber()
					&& before.offset() == note.onset())
				return true;
			l = l.prev();
		}
		return false;
	}

	/**
	 *
	 */
	private void initialize() {
		setOpaque(true);
		setLayout(null);
		setBackground(Color.WHITE);
		// this.setSize(new Dimension(700, 700));
		setDoubleBuffered(true);
	}

	private void setController() {
		butler().addTuneDataListenerList(this);
		setMouseActions(createPianoRollMouseAction(main));
		addMouseListener(getMouseActions());
		addMouseMotionListener(getMouseActions());
		addKeyListener(createKeyActions(main));
	}

	/**
	 * @param target セットする data
	 */
	private void setData(TuneData target) {
		this.data = target;
	}

	private void setMouseActions(MouseActionListener mouseActions) {
		this.mouseActions = mouseActions;
	}

	/**
	 * @param selectedGroup セットする selectedGroup
	 */
	private void setSelectedGroup(GroupLabel selectedGroup) {
		this.selectedGroup = selectedGroup;
	}

	private void setStartPositionOfMouseBox(MouseEvent e) {
		mouseEndPoint = mouseStartPoint = e.getPoint();
	}

} // @jve:decl-index=0:visual-constraint="10,10"
