package net.muse.data;

/**
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/08/31
 */
public class GroupNote extends SequenceData {
	private NoteData note = null;

	public GroupNote(NoteData note) {
		super();
		this.note = note;
	}

	public GroupNote() {
		super();
	}

	/**
	 * @return child
	 */
	@Override public GroupNote child() {
		return (GroupNote) super.child();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(Object obj) {
		return note.equals(((GroupNote) obj).getNote());
	}

	/**
	 * @return note
	 */
	public NoteData getNote() {
		return note;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override public int hashCode() {
		return 1;
	}

	public String id() {
		return (note != null) ? note.id() : "null";
	}

	@Override public GroupNote next() {
		return (GroupNote) super.next();
	}

	@Override public GroupNote parent() {
		return (GroupNote) super.parent();
	}

	@Override public GroupNote previous() {
		return (GroupNote) super.previous();
	}

	public void setNote(NoteData note) {
		this.note = note;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		if (note == null)
			return "null";
		return String.format("%s -> (%s) %s (%s) -> %s", (hasPrevious())
				? previous().id() : "null", (hasParent()) ? parent().id()
						: "null", id(), (hasChild()) ? child().id() : "null",
				(hasNext()) ? next().id() : "null");
	}

}
