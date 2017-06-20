package net.muse.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import net.muse.MuseApp;
import net.muse.mixtract.data.GroupNote;
import net.muse.mixtract.data.NoteData;

public class NoteLabel extends GroupLabel {

	private static final long serialVersionUID = 1L;

	/* 格納データ */
	private final GroupNote note;
	private NoteLabel next = null;
	private NoteLabel prev = null;
	private NoteLabel parent = null;
	private NoteLabel child = null;
	private int offset;

	private boolean measureBeginning;

	/* イベント制御 */
	private MouseActionListener mouseActions;

	/**
	 * @return next
	 */
	public final NoteLabel next() {
		return next;
	}

	/*
	 * (non-Javadoc)
	 * @see jp.crestmuse.mixtract.gui.GroupLabel#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		if (isSelected) {
			if (((PianoRoll) getParent()).displayApex) {
				double w = 1. - note.getNote().getApexScore();
				int r = PartColor.SELECTED_COLOR.getRed();
				int c = (int) (255 * w);
				setBackground(new Color(r, c, c));
			} else
				setBackground(PartColor.SELECTED_COLOR);
			setBorder(BorderFactory.createLineBorder(PartColor.SELECTED_COLOR));
		} else {
			setBackground(getCurrentColor());
			setBorder(null);
		}
		repaint();
	}

	/**
	 * @return prev
	 */
	final NoteLabel prev() {
		return prev;
	}

	/**
	 * @param l セットする next
	 */
	final void setNext(NoteLabel l) {
		this.next = l;
		if (l != null && l.prev != this)
			l.setPrev(this);
	}

	/**
	 * @param l セットする prev
	 */
	final void setPrev(NoteLabel l) {
		this.prev = l;
		if (l != null && l.next != this)
			l.setNext(this);
	}

	protected NoteLabel(GroupNote note, Rectangle r) {
		super();
		this.note = note;
		setPartNumber(note.getNote().partNumber());
		setCurrentColor(new PartColor(note.getNote().partNumber()).getColor());
		setOpaque(true);
		setLocation(r.x, r.y);
		setBounds(r);
		setMeasureBeginning(note.getNote().beat() == 1.0);
		setDoubleBuffered(true);
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.GroupLabel#setPartNumber(int)
	 */
	@Override protected void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
		setCurrentColor(new PartColor(partNumber).getColor());
		note.getNote().setPartNumber(partNumber);
	}

	private void setMeasureBeginning(boolean b) {
		measureBeginning = b;
	}

	public GroupNote getGroupNote() {
		return note;
	}

	public NoteData getScoreNote() {
		return note.getNote();
	}

	public boolean isMeasureBeginning() {
		return measureBeginning;
	}

	@Override void setController(MuseApp main) {
		mouseActions = new MouseActionListener(main, this) {
			/*
			 * (non-Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mousePressed(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseEntered(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				NoteLabel src = (NoteLabel) e.getSource();
				src.setMouseOver(true);
				((PianoRoll) getParent()).setMouseOveredNoteLabel(src);
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				((NoteLabel) e.getSource()).setMouseOver(false);
				((NoteLabel) e.getSource()).setEditMode(getMousePoint());
				((PianoRoll) getParent()).setMouseOveredNoteLabel(null);
				repaint();
			}

			/*
			 * (non-Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseMoved(java.awt
			 * .event.MouseEvent)
			 */
			@Override public void mouseMoved(MouseEvent e) {
				((PianoRoll) getParent()).setMouseOveredNoteLabel((NoteLabel) e
						.getSource());
			}
		};
		addMouseListener(mouseActions);
		addMouseWheelListener(mouseActions);
	}

	/**
	 * @return
	 */
	public Color getApexColor() {
		double apex = note.getNote().getApexScore();
		final int c = (int) (255 * (1. - apex));
		return new Color(255, c, c);
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @return offset
	 */
	public final int getOffset() {
		return offset;
	}

	/**
	 * @param parent セットする parent
	 */
	void setParent(NoteLabel parent) {
		this.parent = parent;
	}

	/**
	 * @return parent
	 */
	public NoteLabel parent() {
		return parent;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.GroupLabel#toString()
	 */
	@Override public String toString() {
		return String.format("(%s) %s (%s) -> %s", (parent != null) ? parent
				.getGroupNote().getNote().noteName() : "null", note.getNote()
						.noteName(), (child != null) ? child.getGroupNote()
								.getNote().noteName() : "null", next());
	}

	boolean hasNext() {
		return next != null;
	}

	/**
	 * @param child セットする child
	 */
	public void setChild(NoteLabel child) {
		this.child = child;
	}

	/**
	 * @return child
	 */
	public NoteLabel child() {
		return child;
	}

	public boolean hasParent() {
		return parent != null;
	}

}
