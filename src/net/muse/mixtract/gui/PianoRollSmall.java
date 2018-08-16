package net.muse.mixtract.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import net.muse.app.Mixtract;
import net.muse.data.NoteData;
import net.muse.gui.GroupLabel;
import net.muse.gui.KeyBoard;
import net.muse.gui.NoteLabel;

class PianoRollSmall extends MXPianoroll {
	private static final long serialVersionUID = 1L;
	/* 制御データ */
	private int currentMousePositionX;

	/* 描画モード */
	private boolean showCurrentX;
	private int endX;

	PianoRollSmall(Mixtract main) {
		super(main);
		initialize();
		setViewMode(ViewerMode.SCORE_VIEW);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#deleteGroup(jp.crestmuse.mixtract
	 * .gui.GroupLabel)
	 */
	@Override public void deleteGroup(GroupLabel g) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#deselect(jp.crestmuse.mixtract.
	 * gui.GroupLabel)
	 */
	@Override public void deselect(GroupLabel g) {
		setDisplayApex(true);
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see jp.crestmuse.mixtract.gui.PianoRoll#makeNoteLabel()
	 */
	@Override public void makeNoteLabel() {
		removeAll();
		this.notelist = null;
		makeNoteLabel(group().group());
		validate();
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#paintComponent(java.awt.Graphics)
	 */
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (showCurrentX) {
			g.setColor(Color.magenta);
			((Graphics2D) g).setStroke(new BasicStroke(0.5f));
			g.drawLine(currentMousePositionX, 0, currentMousePositionX,
					getHeight());
		}
	}

	@Override public void setShowCurrentX(boolean showCurrentX, int x) {
		this.showCurrentX = showCurrentX;
		this.currentMousePositionX = x;
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#setViewMode(jp.crestmuse.mixtract
	 * .gui.ViewerMode)
	 */
	@Override public void setViewMode(ViewerMode mode) {
		super.setViewMode(ViewerMode.SCORE_VIEW); // 常に
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#drawFifthsKeyText(java.awt.Graphics2D
	 * , jp.crestmuse.mixtract.gui.NoteLabel,
	 * jp.crestmuse.mixtract.gui.NoteLabel)
	 */
	@Override protected void drawFifthsKeyText(Graphics2D g2, NoteLabel cur,
			NoteLabel pre) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#drawHarmonyGround(java.awt.Graphics2D
	 * , jp.crestmuse.mixtract.gui.NoteLabel,
	 * jp.crestmuse.mixtract.gui.NoteLabel)
	 */
	@Override protected void drawHarmonyGround(Graphics2D g2, NoteLabel cur,
			NoteLabel pre) {}

	/*
	 * (non-Javadoc)
	 * @see jp.crestmuse.mixtract.gui.PianoRoll#selectNotes()
	 */
	@Override protected void encloseNotes() {
		setDisplayApex(true);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PianoRoll#getLabelBounds(jp.crestmuse.mixtract
	 * .data.NoteData, int)
	 */
	@Override protected Rectangle getLabelBounds(NoteData nd, int offset) {
		final int h = main().getFrame().getKeyboard().getKeyHeight();
		final int y = KeyBoard.getYPositionOfPitch(nd.noteNumber()) * h;
		double x, w;
		switch (getViewerMode()) {
		case REALTIME_VIEW:
			x = (int) (getAxisX() + nd.realOnset() / (getWidth() - getAxisX()));
			w = (int) (nd.duration() / (getWidth() - getAxisX()) - offset);
			break;
		default:
			double len = endX - getAxisX();
			x = getAxisX() + ((nd.onset() - group().group().getBeginNote()
					.onset()) / (double) group().group().timeValue()) * len;
			w = (nd.timeValue() / (double) group().group().timeValue()) * len
					- offset;
			break;
		}
		return new Rectangle((int) x, y, (int) w, h);
	}

	@Override protected void rescaleNoteLabels() {
		axisX = (int) Math.round(getWidth() * 0.1);
		endX = (int) Math.round(getWidth() * 0.9);
		resizeLabels(notelist());
	}

	/**
	 * This method initializes this
	 */
	protected void initialize() {
		setOpaque(true);
		setLayout(null);
		setBackground(Color.WHITE);
		setDoubleBuffered(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
