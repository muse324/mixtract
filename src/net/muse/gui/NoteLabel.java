package net.muse.gui;

import java.awt.Rectangle;

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

	protected NoteLabel(NoteData note, Rectangle r) {
		super();
		this.note = note;
		setPartNumber(note.musePhony());
		setCurrentColor(new PartColor(note.musePhony()).getColor());
		setOpaque(true);
		setLocation(r.x, r.y);
		setBounds(r);
		setMeasureBeginning(note.beat() == 1.0);
		setDoubleBuffered(true);
	}

	/**
	 * @return child
	 */
	public NoteLabel child() {
		return child;
	}

	/**
	 * @return offset
	 */
	public final int getOffset() {
		return offset;
	}

	public NoteData getScoreNote() {
		return note;
	}

	public boolean hasNext() {
		return next != null;
	}

	public boolean hasParent() {
		return parent != null;
	}

	public boolean hasPrevious() {
		return prev != null;
	}

	public boolean isMeasureBeginning() {
		return measureBeginning;
	}

	/**
	 * @return next
	 */
	public NoteLabel next() {
		return next;
	}

	/**
	 * @return parent
	 */
	public NoteLabel parent() {
		return parent;
	}

	/**
	 * @return prev
	 */
	public NoteLabel prev() {
		return prev;
	}

	/**
	 * @param child セットする child
	 */
	public void setChild(NoteLabel child) {
		this.child = child;
	}

	protected MouseActionListener createMouseActionListener(MuseApp main) {
		return new NLMouseActionListener(main, this);
	}
	/**
	 * @param l セットする next
	 */
	public final void setNext(NoteLabel l) {
		this.next = l;
		if (l != null && l.prev != this)
			l.setPrev(this);
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * @param parent セットする parent
	 */
	public void setParent(NoteLabel parent) {
		this.parent = parent;
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

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.GroupLabel#toString()
	 */
	@Override public String toString() {
		return String.format("(%s) %s (%s) -> %s", (parent != null) ? parent
				.getScoreNote().noteName() : "null", note.noteName(),
				(child != null) ? child.getScoreNote().noteName() : "null",
				next());
	}

	/**
	 * @param l セットする prev
	 */
	final void setPrev(NoteLabel l) {
		this.prev = l;
		if (l != null && l.next != this)
			l.setNext(this);
	}

	@Override protected void setSelectedOption(boolean isSelected) {
		setBorder(!isSelected ? null
				: BorderFactory.createLineBorder(PartColor.SELECTED_COLOR));
	}

	private void setMeasureBeginning(boolean b) {
		measureBeginning = b;
	}

}
