package net.muse.mixtract.data;


/**
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/08/31
 */
public class GroupNote {
	private NoteData note = null;
	private GroupNote next = null;
	private GroupNote prev = null;
	private GroupNote parent = null;
	private GroupNote child = null;

	public GroupNote(NoteData note) {
		super();
		this.note = note;
	}

	GroupNote() {
		super();
	}

	/**
	 * @return child
	 */
	public GroupNote child() {
		return child;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return note.equals(((GroupNote) obj).getNote());
	}

	/**
	 * @return note
	 */
	public NoteData getNote() {
		return note;
	}

	public boolean hasChild() {
		return child != null;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 1;
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

	public String id() {
		return (note != null) ? note.id() : "null";
	}

	/**
	 * @return next
	 */
	public GroupNote next() {
		return next;
	}

	/**
	 * @return parent
	 */
	public GroupNote parent() {
		return parent;
	}

	/**
	 * @return
	 */
	public GroupNote previous() {
		return prev;
	}

	/**
	 * @param child セットする child
	 */
	public void setChild(GroupNote note) {
		if (this.child != note) {
			this.child = note;
			if (note != null)
				this.child.setParent(this);
		}
	}

	/**
	 * @param note セットする note
	 */
	public void setNext(GroupNote note) {
		setNext(note, true);
	}

	public void setNote(NoteData note) {
		this.note = note;
	}

	/**
	 * @param parent セットする parent
	 */
	public void setParent(GroupNote note) {
		setParent(note, true);
	}

	/**
	 * @param parent セットする parent
	 */
	public void setParent(GroupNote note, boolean link) {
		if (this.parent != note) {
			this.parent = note;
			if (link && note != null)
				this.parent.setChild(this);
		}
	}

	/**
	 * @param prev セットする prev
	 */
	public void setPrevious(GroupNote prev) {
		setPrevious(prev, true);
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (note == null)
			return "null";
		return String.format("%s -> (%s) %s (%s) -> %s",
				(hasPrevious()) ? previous().id() : "null",
				(hasParent()) ? parent().id() : "null", id(),
				(hasChild()) ? child().id() : "null",
				(hasNext()) ? next().id() : "null");
	}

	public void setNext(GroupNote note, boolean link) {
		if (this.next != note) {
			this.next = note;
			if (link && this.next != null)
				this.next.setPrevious(this);
		}
	}

	public void setPrevious(GroupNote previous, boolean link) {
		if (this.prev != previous) {
			this.prev = previous;
			if (link && this.prev != null)
				this.prev.setNext(this);
		}
	}

}
