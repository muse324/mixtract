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

	/**
	 * @return next
	 */
	public NoteLabel next() {
		return next;
	}

	@Override protected void setSelectedOption(boolean isSelected) {
		setBorder(!isSelected ? null
				: BorderFactory.createLineBorder(PartColor.SELECTED_COLOR));
	}

	/**
	 * @return prev
	 */
	public NoteLabel prev() {
		return prev;
	}

	/**
	 * @param l セットする next
	 */
	public final void setNext(NoteLabel l) {
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
		setPartNumber(note.musePhony());
		setCurrentColor(new PartColor(note.musePhony()).getColor());
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

	@Deprecated public NoteData getGroupNote() {
		return note;
	}

	public NoteData getScoreNote() {
		return note;
	}

	public boolean isMeasureBeginning() {
		return measureBeginning;
	}

	@Override public void setController(MuseApp main) {
		mouseActions = new NLMouseActionListener(main, this);
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
	public void setParent(NoteLabel parent) {
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

	public boolean hasNext() {
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

	public boolean hasPrevious() {
		return prev != null;
	}

}
