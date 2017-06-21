package net.muse.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;
import javax.xml.transform.TransformerException;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.MuseApp;
import net.muse.data.*;
import net.muse.misc.Util;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.*;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.*;
import net.muse.mixtract.gui.command.SetChordCommand;

/**
 * ピアノロール（音符部分）を描画するクラスオブジェクトです．
 *
 * @author Mitsuyo Hashida
 */
public class PianoRoll extends JPanel implements TuneDataListener,
		CanvasMouseListener {

	private static final long serialVersionUID = 1L;
	static final int DEFAULT_WIDTH = 1024;
	private static int defaultAxisX = 10;

	/**
	 * @return defaultAxisX
	 */
	public static int getDefaultAxisX() {
		return defaultAxisX;
	}

	protected int axisX = 10;

	private MuseApp main; // @jve:decl-index=0:
	/** 楽曲データ */
	private MXTuneData data; // @jve:decl-index=0:
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
	private final LinkedList<NoteLabel> selectedNoteLabels;
	private NoteLabel _notelist = null;
	private NoteLabel notelist = null;
	private NoteLabel mouseOveredNoteLabel = null;
	private int selectedVoice;
	private final Cursor defCursor = Cursor.getPredefinedCursor(
			Cursor.DEFAULT_CURSOR); // @jve:decl-index=0:

	private final Cursor hndCursor = Cursor.getPredefinedCursor(
			Cursor.HAND_CURSOR); // @jve:decl-index=0:

	protected PianoRoll() {
		super();
		selectedNoteLabels = new LinkedList<NoteLabel>();
		selectedVoice = -1;
		viewerMode = ViewerMode.REALTIME_VIEW;
		initialize();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.GroupEditListener#addGroup(net.muse.mixtract.data.
	 * Group)
	 */
	public void addGroup(Group g) {
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.TuneDataListener#changeExpression(net.muse.mixtract
	 * .data.curve.PhraseCurveType)
	 */
	public void changeExpression(PhraseCurveType type) {
		if (type == PhraseCurveType.DYNAMICS)
			return;
		resizeLabels(notelist);
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.GroupEditListener#deleteGroup(javax.swing.JLabel)
	 */
	public void deleteGroup(GroupLabel g) {
		deselect(g);
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.GroupEditListener#deselect(javax.swing.JLabel)
	 */
	public void deselect(GroupLabel g) {
		setMouseOveredNoteLabel(null);
		clearSelection();
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.GroupEditListener#editGroup(javax.swing.JLabel)
	 */
	public void editGroup(GroupLabel g) {
		throw new UnsupportedOperationException(); // TODO 実装
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

			/* 座標軸を再取得し、note labes をリスケール */
			rescaleNoteLabels();

			/* 小節線を表示 */
			drawMeasureLine(g2);
			/* 和声カラーを表示 */
			drawFifthsKeyText(g2, notelist, null);
			/* 和声カラーを表示 */
			drawHarmonyGround(g2, notelist, null);

			// drawHierarchyLine(g2, notelist, null);

			/* 音符を表示 */
			try {
				drawNotes(g2, notelist);
			} catch (final TransformerException e) {
				e.printStackTrace();
			}

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
	public void selectGroup(GroupLabel g, boolean flg) {
		selectGroup(g.getGroup());
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.CanvasMouseListener#setShowCurrentX(boolean,
	 * int)
	 */
	public void setShowCurrentX(boolean showCurrentX, int x) {}

	public void setTarget(MXTuneData target) {
		this.setData(target);
		ChangePartCommand.setPartSize(target.getRootGroup().size());
		makeNoteLabel();
		revalidate();
		repaint();
	}

	public void makeNoteLabel() {
		removeAll();
		setNotelist(null);
		if (data() != null) {
			for (Group g : data().getRootGroup()) {
				makeNoteLabel(g);
			}
			validate();
		}
		repaint();
	}

	/**
	 * @param group
	 */
	public void selectGroup(Group group) {
		clearSelection();
		selectNote(notelist, group.getScoreNotelist());
	}

	/**
	 * @param app
	 */
	public void setController(MuseApp app) {
		this.main = app;
		app.addTuneDataListener(this);
		mouseActions = new MouseActionListener(app, this) {
			private final Point pp = new Point();

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#actionPerformed
			 * (java .awt.event.ActionEvent)
			 */
			@Override public void actionPerformed(ActionEvent e) {
				JMenuItem src = (JMenuItem) e.getSource();
				String cmd = src.getActionCommand();
				if (cmd.equals(MixtractCommand.CHANGE_PART.name())) {
					int part = Integer.parseInt(src.getText());
					ChangePartCommand.setChangePartTo(part);
				} else if (cmd.equals(MixtractCommand.SET_CHORD.name())) {
					SetChordCommand.setSelectedChord(Harmony.valueOf(src
							.getText()));
				} else if (cmd.equals(MixtractCommand.SET_KEY.name())) {
					SetKeyCommand.setSelectedKey(src.getText());
				} else if (cmd.equals(MixtractCommand.SET_KEYMODE.name())) {
					SetKeyModeCommand.setSelectedKeyMode(KeyMode.valueOf(src
							.getText()));
				}
				super.actionPerformed(e);
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu
			 * (java .awt.event.MouseEvent)
			 */
			@Override public void createPopupMenu(MouseEvent e) {
				super.createPopupMenu(e);
				boolean enabled = selectedNoteLabels.size() > 0;
				getPopup().add(addMenuItem(MixtractCommand.MAKE_GROUP,
						enabled));
				getPopup().addSeparator();

				// annotate chord
				JMenu chordMenu = new JMenu("Harmony chord");
				for (Harmony c : Harmony.values())
					chordMenu.add(createChordMenuItem(c));
				chordMenu.setEnabled(selectedNoteLabels.size() > 0);
				getPopup().add(chordMenu);

				// change key
				JMenu keyMenu = new JMenu("Change key");
				for (int i = 0; i < 7; i++) {
					keyMenu.add(createKeyMenuItem(i));
				}
				for (int i = -5; i < 0; i++) {
					keyMenu.add(createKeyMenuItem(i));
				}
				keyMenu.setEnabled(selectedNoteLabels.size() > 0);
				getPopup().add(keyMenu);

				// change key mode
				JMenu keyModeMenu = new JMenu("Change key mode");
				keyModeMenu.setEnabled(selectedNoteLabels.size() > 0);
				for (int i = 0; i < KeyMode.values().length; i++) {
					keyModeMenu.add(createKeyModeMenuItem(i));
				}
				getPopup().add(keyModeMenu);
				// change part
				JMenu partSelectMenu = new JMenu("Change part");
				for (int i = 0; i < ChangePartCommand.partSize; i++) {
					JMenuItem item = new JMenuItem(String.valueOf(i + 1));
					item.setActionCommand(MixtractCommand.CHANGE_PART.name());
					item.addActionListener(mouseActions);
					item.setEnabled(i + 1 != selectedVoice);
					partSelectMenu.add(item);
				}
				partSelectMenu.setEnabled(enabled);
				getPopup().add(partSelectMenu);

				getPopup().show((Component) e.getSource(), e.getX(), e.getY());
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
			 * MouseEvent )
			 */
			@Override public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				if (e.isAltDown()) {
					Point cp = e.getPoint();
					final JViewport vport = (JViewport) getParent();
					Point vp = vport.getViewPosition();
					vp.translate(pp.x - cp.x, pp.y - cp.y);
					scrollRectToVisible(new Rectangle(vp, vport.getSize()));
					pp.setLocation(cp);
				} else {
					setMouseEndPoint(e);
					selectNotes();
				}
				repaint();
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.
			 * MouseEvent )
			 */
			@Override public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				setMouseOveredNoteLabel(null);
				repaint();
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * java.awt.event.MouseListener#mousePressed(java.awt.event.
			 * MouseEvent
			 * )
			 */
			@Override public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.isAltDown()) {
						setCursor(hndCursor);
						pp.setLocation(e.getPoint());
					} else {
						/* ピアノロール上で矩形の左上隅座標を取得する */
						getLeftUpperCornerAxis(e);
						selectedVoice = -1;
					}
				}
				repaint();
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * java.awt.event.MouseListener#mouseReleased(java.awt.event.
			 * MouseEvent
			 * )
			 */
			@Override public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if (e.isAltDown()) {
					setCursor(defCursor);
				} else {
					setMouseEndPoint(e);
					setMouseSelectBoxDraw(false);
					selectNotes();
					if (selectedNoteLabels.size() == 0)
						_main.notifyDeselectGroup();
				}
				repaint();
			}

			private JMenuItem createChordMenuItem(Harmony c) {
				JMenuItem item = new JMenuItem(c.name());
				item.setActionCommand(MixtractCommand.SET_CHORD.name());
				item.addActionListener(mouseActions);
				return item;
			}

			private JMenuItem createKeyMenuItem(int i) {
				JMenuItem item = new JMenuItem(Util.fifthsToString(i));
				item.setActionCommand(MixtractCommand.SET_KEY.name());
				item.addActionListener(mouseActions);
				item.setEnabled(selectedNoteLabels.size() > 0
						&& selectedNoteLabels.get(0).getScoreNote()
								.fifths() != i);
				return item;
			}

			private JMenuItem createKeyModeMenuItem(int i) {
				final KeyMode mode = KeyMode.values()[i];
				JMenuItem item = new JMenuItem(mode.name());
				item.setActionCommand(MixtractCommand.SET_KEYMODE.name());
				item.addActionListener(mouseActions);
				item.setEnabled(selectedNoteLabels.size() > 0
						&& selectedNoteLabels.get(0).getScoreNote()
								.getKeyMode() != mode);
				return item;
			}
		};
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
	}

	void setMouseOveredNoteLabel(NoteLabel src) {
		this.mouseOveredNoteLabel = src;
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
		final int h = KeyBoard.keyHeight;
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
	 * @return the notelist
	 */
	protected final NoteLabel getNotelist() {
		return notelist;
	}

	/**
	 * @param g
	 * @return
	 */
	protected double getPixelPerGroupLength(Group g) {
		double len;
		switch (viewerMode) {
		case REALTIME_VIEW:
			len = g.duration();
			break;
		default:
			len = g.getTimeValue();
		}
		return len / getWidth() / 0.9;
	}

	/**
	 * @return viewerMode
	 */
	protected final ViewerMode getViewerMode() {
		return viewerMode;
	}

	protected void makeNoteLabel(Group group) {
		if (group.hasChild()) {
			makeNoteLabel(group.getChildFormerGroup());
			makeNoteLabel(group.getChildLatterGroup());
		} else
			makeNoteLabel(group.getBeginGroupNote(), false);
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
	 * マウスの選択範囲に収まる音符を抽出する．
	 */
	protected void selectNotes() {
		if (data() == null)
			return;
		final Rectangle mouseBox = mouseActions.getMouseBox();
		if (mouseBox == null)
			return;

		if (!mouseActions.isShiftKeyPressed()) {
			selectedNoteLabels.clear();
		}
		for (Component c : getComponents()) {
			if (!(c instanceof NoteLabel))
				continue;
			final NoteLabel l = (NoteLabel) c;
			final Rectangle r = l.getBounds();
			if (r.x + r.width < mouseBox.x || mouseBox.x + mouseBox.width < r.x)
				continue;
			if (r.y + r.height < mouseBox.y || mouseBox.y
					+ mouseBox.height < r.y)
				continue;
			// ひとつめの音のvoiceで選択声部を制限する
			if (selectedVoice < 0) {
				selectedVoice = l.getScoreNote().partNumber();
				System.out.println("selected voice: " + selectedVoice);
			}
			if (l.getScoreNote().partNumber() != selectedVoice)
				continue;
			if (!selectedNoteLabels.contains(l)) {
				selectedNoteLabels.addLast(l);
				l.setSelected(true);
			} else
				l.setSelected(selectedNoteLabels.contains(l));
		}
	}

	/**
	 *
	 */
	private void clearSelection() {
		selectedNoteLabels.clear();
		selectedVoice = -1;
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
				g.fillRect(0, curHeight, width, KeyBoard.keyHeight);
			}
			curHeight += KeyBoard.keyHeight;
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

		final Group group = MixtractCommand.getSelectedObjects().getGroup();
		if (group == null || selectedNoteLabels.size() < 1)
			return;

		int x1 = selectedNoteLabels.get(0).getX();
		int y1 = selectedNoteLabels.get(0).getY() + selectedNoteLabels.get(0)
				.getHeight() / 2;
		NoteLabel n4 = selectedNoteLabels.get(selectedNoteLabels.size() - 1);
		int x4 = n4.getX() + n4.getWidth();
		int y4 = n4.getY() + n4.getHeight() / 2;

		// final Note bg = group.getBeginningNote();
		Note n1 = group.getMelodyFlagment().getFormerLastNote();
		final Note n2 = group.getMelodyFlagment().getLatterFirstNote();
		// final Note ed = group.getEndNote();
		final int keyheight = KeyBoard.keyHeight;
		// final int y1 = KeyBoard.getYPositionOfPitch(bg.notenum()) *
		// keyheight;
		final int y2 = KeyBoard.getYPositionOfPitch(n1.notenum()) * keyheight;
		final int y3 = KeyBoard.getYPositionOfPitch(n2.notenum()) * keyheight;
		// final int y4 = KeyBoard.getYPositionOfPitch(ed.notenum()) *
		// keyheight;
		final int tpb = Mixtract.getTicksPerBeat();
		// final int x1 = getX(bg.onset(tpb)) + axisX;
		final int x2 = MainFrame.getXOfNote(n1.offset(tpb)) + axisX;
		final int x3 = MainFrame.getXOfNote(n2.onset(tpb)) + axisX;
		// final int x4 = getX(ed.offset(tpb)) + axisX;
		g2.setColor(Color.MAGENTA);// TODO 定数代入
		g2.drawLine(x1, y1, x4, y4);
		g2.drawLine(x1, y1, x2, y2);
		g2.drawLine(x3, y3, x4, y4);
	}

	private void drawMouseOveredNoteInfo(Graphics2D g2) {
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
		System.out.println(str + " at " + mouseActions.getMousePoint());
		g2.drawString(str, mouseActions.getMousePoint().x - axisX, mouseActions
				.getMousePoint().y - KeyBoard.keyHeight);
	}

	/**
	 * 一つの音符をピアノロールに描画します．
	 *
	 * @param g2
	 * @param l
	 * @throws TransformerException
	 */
	private void drawNotes(final Graphics2D g2, NoteLabel l)
			throws TransformerException {
		if (l == null)
			return;
		l.setSelected(selectedNoteLabels.contains(l));
		drawNotes(g2, l.child());
		drawNotes(g2, l.next());
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
		mouseActions.setMouseBox(r);
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
			setToolTipText(
					"<html>Drag in this area to select notes. <br>The system automatically understand the specific part-number you selected first.</html>");
		else
			setToolTipText("Click the right button to show the contect menu.");
	}

	/**
	 * @param e
	 */
	private void getLeftUpperCornerAxis(MouseEvent e) {
		setMouseSelectBoxDraw(true);
		setStartPositionOfMouseBox(e);
	}

	/**
	 * @return selectedNoteLabels
	 */
	private final LinkedList<NoteLabel> getSelectedNoteLabels() {
		return selectedNoteLabels;
	}

	/**
	 *
	 */
	private void initialize() {
		setOpaque(true);
		setLayout(null);
		setBackground(Color.WHITE);
		this.setSize(new Dimension(700, 700));
		setDoubleBuffered(true);
	}

	private void makeNoteLabel(GroupNote note, boolean isChild) {
		if (note == null)
			return;
		int offset = (note.hasNext() && note.next().getNote() != null && note
				.next().getNote().noteNumber() == note.getNote().noteNumber())
						? 5 : 0;
		makeNoteLabel(note, offset, isChild);
		makeNoteLabel(note.child(), true);
		makeNoteLabel(note.next(), false);
	}

	/**
	 * @param note
	 * @param offset
	 *            同音打鍵が続いた場合の描画間隔ピクセル
	 */
	private void makeNoteLabel(final GroupNote note, int offset,
			boolean isChild) {
		if (note == null)
			return;
		// int offset = (note.hasNext() && note.next().getNote().noteNumber() ==
		// nd
		// .noteNumber()) ? 5 : 0;

		if (note.getNote() == null)
			return;

		final Rectangle r = getLabelBounds(note.getNote(), offset);
		final NoteLabel n = new NoteLabel(note, r);
		n.setController(main);
		n.setSelected(selectedNoteLabels.contains(n));
		// n.setOffset(offset);
		if (notelist() == null) {
			notelist = setNotelist(n);
		} else {
			if (isChild) {
				notelist().setChild(n);
				n.setParent(notelist());
				setNotelist(notelist().child());
			} else {
				while (notelist().hasParent()) {
					if (n.getScoreNote().hasPrevious() && n.getScoreNote()
							.previous().equals(notelist().getScoreNote()))
						break;
					setNotelist(notelist().parent());
				}
				notelist().setNext(n);
				n.setParent(notelist().parent());
				setNotelist(notelist().next());
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
	 * @param notelist2
	 * @param list
	 */
	private void selectNote(NoteLabel l, List<? extends NoteData> list) {
		if (l == null)
			return;
		if (list.contains(l.getScoreNote())) {
			l.setSelected(true);
			selectedNoteLabels.add(l);
		}
		selectNote(l.next(), list);
	}

	private void setMouseEndPoint(MouseEvent e) {
		mouseEndPoint = e.getPoint();
	}

	private void setMouseSelectBoxDraw(boolean isMouseSelectBoxDraw) {
		this.isMouseSelectBoxDraw = isMouseSelectBoxDraw;
	}

	private void setStartPositionOfMouseBox(MouseEvent e) {
		mouseEndPoint = mouseStartPoint = e.getPoint();
	}

	/**
	 * @return data
	 */
	public MXTuneData data() {
		return data;
	}

	/**
	 * @param data セットする data
	 */
	public void setData(MXTuneData data) {
		this.data = data;
	}

	/**
	 * @return _notelist
	 */
	public NoteLabel notelist() {
		return _notelist;
	}

	/**
	 * @param _notelist セットする _notelist
	 */
	public NoteLabel setNotelist(NoteLabel _notelist) {
		this._notelist = _notelist;
		return _notelist;
	}

	public static class ChangePartCommand extends MixtractCommand {
		private static final int defaultSize = 4;
		static int partSize = defaultSize;
		private static int newPart;

		private static void setChangePartTo(int part) {
			newPart = part;
		}

		private static void setPartSize(int size) {
			partSize = defaultSize;
			if (size > partSize)
				partSize = size;
		}

		public ChangePartCommand(String string) {
			super(string);
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			for (Component c : _mainFrame.getPianoroll().getComponents()) {
				NoteLabel l = (NoteLabel) c;
				if (l.isSelected()) {
					l.setPartNumber(newPart);
					l.repaint();
				}
			}
		}

	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	public static class MakeGroupCommand extends MixtractCommand {

		public MakeGroupCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			final List<NoteLabel> notes = _mainFrame.getPianoroll()
					.getSelectedNoteLabels();
			Group g = createUserGroup(notes.get(0), notes.get(notes.size()
					- 1));
			g.setIndex(_target.getUniqueGroupIndex());
			_target.addGroupArrayList(g);
			_main.notifyAddGroup(g);
			_main.analyzeStructure(_target, null);
			_main.notifySetTarget();
			// // ArrayList<Group> newlist = createUserGroup();
			// _target.addUserGroup(g);
			// _target.getGroupList(true);
			// // _target.setLatestGroupAnalysis(GTTMAnalyzer.run(_target,
			// // GTTMAnalyzer
			// // .doScoreAnalysis(), false));
			// _mainFrame.setTargetTuneData(_target);
			// _mainFrame.notifyAddGroup(g);
			// // getGroupingPanel().setTune(data);
			// GUIUtil.printConsole("new group:" + g);
		}

		/**
		 * ユーザにより範囲選択された音符群に対し，グループを作成します．
		 * <p>
		 * 休符等により音符間が隣接しない場合，複数のグループを作成します．
		 * 意図せずグループが分割された場合は，combineGroups()を用いて接続します．
		 *
		 * @param begin
		 * @param end
		 * @return
		 * @see combineGroups()
		 */
		private Group createUserGroup(NoteLabel begin, NoteLabel end) {
			// 巻き戻し
			while (begin.prev() != null && begin.prev().isSelected())
				begin = begin.prev();
			// ユーザグループを構成する音符列を生成する
			GroupNote groupNoteList = createUserGroupNotelist(begin, end, null,
					null, false);
			while (groupNoteList.hasParent())
				groupNoteList = groupNoteList.parent();
			GroupNote endNote = groupNoteList;
			// 巻き戻し
			while (groupNoteList.hasPrevious()) {
				groupNoteList = groupNoteList.previous();
				if (groupNoteList.equals(begin.getGroupNote()))
					break;
			}
			return new Group(groupNoteList, endNote, GroupType.USER);
		}

		/**
		 * @param l
		 * @param le
		 * @param list
		 * @param glist
		 * @param isChild
		 * @return
		 */
		private GroupNote createUserGroupNotelist(NoteLabel l, NoteLabel le,
				GroupNote list, GroupNote glist, boolean isChild) {
			if (l == null)
				return list;
			if (glist == null) {
				glist = new GroupNote(l.getScoreNote());
			}
			if (list != null) {
				if (isChild) {
					list.setChild(glist);
					glist.setParent(list);
					list = list.child();
				} else {
					while (list.hasParent()) {
						if (glist.getNote().hasPrevious() && glist.getNote()
								.previous().equals(list.getNote()))
							break;
						list = list.parent();
					}
					list.setNext(glist);
					glist.setParent(list.parent(), false);
					glist.setPrevious(list);
				}
			}
			list = glist;
			list = createUserGroupNotelist(l.child(), le, list, glist.child(),
					true);
			if (l == le)
				return list;
			return createUserGroupNotelist(l.next(), le, list, glist.next(),
					false);
		}
	}

	/**
	 * <h1>SetKeyCommand</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose <address>CrestMuse Project,
	 *         JST</address> <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/12/21
	 */
	public static class SetKeyCommand extends MixtractCommand {

		private static String newKey = null;

		/**
		 * @param text
		 */
		private static void setSelectedKey(String text) {
			newKey = text;
		}

		/**
		 * @param lang
		 */
		public SetKeyCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			for (Component c : _mainFrame.getPianoroll().getComponents()) {
				NoteLabel l = (NoteLabel) c;
				if (l.isSelected()) {
					l.getScoreNote().setFifths(Util.valueOfFifths(newKey));
					l.repaint();
				}
			}
		}

	}

	/**
	 * <h1>SetKeyModeCommand</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose <address>CrestMuse Project,
	 *         JST</address> <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/12/21
	 */
	public static class SetKeyModeCommand extends MixtractCommand {

		private static KeyMode newMode;

		/**
		 * @param valueOf
		 */
		static void setSelectedKeyMode(KeyMode mode) {
			newMode = mode;
		}

		/**
		 * @param lang
		 */
		public SetKeyModeCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			for (Component c : _mainFrame.getPianoroll().getComponents()) {
				NoteLabel l = (NoteLabel) c;
				if (l.isSelected()) {
					l.getScoreNote().setKeyMode(newMode, l.getScoreNote()
							.fifths());
					l.repaint();
				}
			}
		}

	}

} // @jve:decl-index=0:visual-constraint="10,10"
