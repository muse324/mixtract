package net.muse.mixtract.data;

/**
 * <h1>PrimaryPhraseSequence</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/11/26
 */
@Deprecated
public class PrimaryPhraseSequence {
	private MXGroup group = null;
	private PrimaryPhraseSequence next = null;
	private PrimaryPhraseSequence prev = null;

	/**
	 * @param g
	 */
	public PrimaryPhraseSequence(MXGroup group) {
		this.group = group;
	}

	/**
	 * @return
	 */
	public PrimaryPhraseSequence end() {
		if (hasNext())
			return next.end();
		return this;
	}

	/**
	 * @return the group
	 */
	public MXGroup getGroup() {
		return group;
	}

	/**
	 * @return
	 */
	public boolean hasNext() {
		return next != null;
	}

	/**
	 * @return
	 */
	public boolean hasPrevious() {
		return prev != null;
	}

	/**
	 * @return the next
	 */
	public final PrimaryPhraseSequence next() {
		return next;
	}

	/**
	 * @return the prev
	 */
	public final PrimaryPhraseSequence previous() {
		return prev;
	}

	/**
	 * @return
	 */
	public PrimaryPhraseSequence root() {
		if (hasPrevious())
			return prev.root();
		return this;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(MXGroup group) {
		this.group = group;
	}

	/**
	 * @param next the next to set
	 */
	public final void setNext(PrimaryPhraseSequence next) {
		this.next = next;
	}

	/**
	 * @param prev the prev to set
	 */
	public final void setPrevious(PrimaryPhraseSequence prev) {
		this.prev = prev;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (group == null)
			return "null";
		return String.format("%s -> %s", group.name(), next());
	}
}
