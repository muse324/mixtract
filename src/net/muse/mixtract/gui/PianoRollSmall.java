package net.muse.mixtract.gui;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import net.muse.data.NoteData;
import net.muse.gui.*;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.Group;

class PianoRollSmall extends MXPianoroll {
	private static final long serialVersionUID = 1L;
	/* 制御データ */
	private Group group; // @jve:decl-index=0:
	private int currentMousePositionX;

	/* 描画モード */
	private boolean showCurrentX;
	private int endX;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Mixtract main = new Mixtract(args);
			PianoRollSmall p = new PianoRollSmall();
			p.setController(main);
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			frame.setContentPane(p);
			frame.pack();
			frame.setVisible(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	PianoRollSmall() {
		super();
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
		setNotelist(null);
		makeNoteLabel(group);
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
			g.drawLine(currentMousePositionX, 0, currentMousePositionX, getHeight());
		}
	}

	@Override
	protected void rescaleNoteLabels() {
		axisX = (int) Math.round(getWidth() * 0.1);
		endX = (int) Math.round(getWidth() * 0.9);
		resizeLabels(getNotelist());
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

	/**
	 * @param group
	 */
	void setGroup(Group group) {
		this.group = group;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 *
	 * jp.crestmuse.mixtract.gui.PianoRoll#drawFifthsKeyText(java.awt.Graphics2D
	 * , jp.crestmuse.mixtract.gui.NoteLabel,
	 * jp.crestmuse.mixtract.gui.NoteLabel)
	 */
	@Override protected void drawFifthsKeyText(Graphics2D g2, NoteLabel cur,
			NoteLabel pre) {}

	/*
	 * (non-Javadoc)
	 * @see
	 *
	 * jp.crestmuse.mixtract.gui.PianoRoll#drawHarmonyGround(java.awt.Graphics2D
	 * , jp.crestmuse.mixtract.gui.NoteLabel,
	 * jp.crestmuse.mixtract.gui.NoteLabel)
	 */
	@Override protected void drawHarmonyGround(Graphics2D g2, NoteLabel cur,
			NoteLabel pre) {}

	/*
	 * (non-Javadoc)
	 * @see
	 *
	 * jp.crestmuse.mixtract.gui.PianoRoll#getLabelBounds(jp.crestmuse.mixtract
	 * .data.NoteData, int)
	 */
	@Override protected Rectangle getLabelBounds(NoteData nd, int offset) {
		final int h = KeyBoard.keyHeight;
		final int y = KeyBoard.getYPositionOfPitch(nd.noteNumber()) * h;
		double x, w;
		switch (getViewerMode()) {
			case REALTIME_VIEW:
				x = (int) (getAxisX() + nd.realOnset() / (getWidth() - getAxisX()));
				w = (int) (nd.duration() / (getWidth() - getAxisX()) - offset);
				break;
			default:
				double len = endX - getAxisX();
				x = getAxisX()
						+ ((nd.onset() - group.getBeginGroupNote().getNote().onset()) / (double) group
								.timeValue()) * len;
				w = (nd.timeValue() / (double) group.timeValue()) * len - offset;
				break;
		}
		return new Rectangle((int) x, y, (int) w, h);
	}

	/*
	 * (non-Javadoc)
	 * @see jp.crestmuse.mixtract.gui.PianoRoll#selectNotes()
	 */
	@Override protected void selectNotes() {
		setDisplayApex(true);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		setOpaque(true);
		setLayout(null);
		setBackground(Color.WHITE);
		setDoubleBuffered(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
