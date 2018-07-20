package net.muse.gui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;

import net.muse.app.MuseApp;
import net.muse.data.NoteData;

public class NoteLabel extends GroupLabel {

	private static final long serialVersionUID = 1L;

	/* 格納データ */
	private final NoteData note;
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

	@Override protected void setSelectedOption(boolean isSelected) {
		setBorder(!isSelected ? null
				: BorderFactory.createLineBorder(PartColor.SELECTED_COLOR));
	}

	/**
	 * @return prev
	 */
	public final NoteLabel prev() {
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

	protected NoteLabel(NoteData note, Rectangle r) {
		super();
		this.note = note;
		setPartNumber(note.xmlPartNumber());
		setCurrentColor(new PartColor(note.xmlPartNumber()).getColor());
		setOpaque(true);
		setLocation(r.x, r.y);
		setBounds(r);
		setMeasureBeginning(note.beat() == 1.0);
		setDoubleBuffered(true);
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.GroupLabel#setPartNumber(int)
	 */
	@Override public void setPartNumber(int partNumber) {
		super.setPartNumber(partNumber);
		setCurrentColor(new PartColor(partNumber).getColor());
		note.setXMLPartNumber(partNumber);
	}

	private void setMeasureBeginning(boolean b) {
		measureBeginning = b;
	}

	public NoteData getGroupNote() {
		return note;
	}

	public NoteData getScoreNote() {
		return note;
	}

	public boolean isMeasureBeginning() {
		return measureBeginning;
	}

	@Override void setController(MuseApp main) {
		mouseActions = new MouseActionListener(main, this) {
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
				.getGroupNote().noteName() : "null", note.noteName(),
				(child != null) ? child.getGroupNote().noteName() : "null",
				next());
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
