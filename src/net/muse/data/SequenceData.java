package net.muse.data;

import net.muse.misc.MuseObject;

public abstract class SequenceData extends MuseObject {
	/** 後続音 */
	private SequenceData next = null;
	/** 先行音 */
	private SequenceData prev = null;
	/** 和音(下) **/
	private SequenceData child = null;
	/** 和音(上) **/
	private SequenceData parent = null;

	public SequenceData child() {
		return child;
	}

	public boolean hasChild() {
		return child != null;
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

	public SequenceData next() {
		return next;
	}

	public SequenceData parent() {
		return parent;
	}

	public SequenceData previous() {
		return prev;
	}

	/**
	 * @param child セットする child
	 */
	public void setChild(SequenceData note) {
		if (this.child != note) {
			this.child = note;
			this.child.setParent(this);
		}
	}

	/**
	 * @param next セットする next
	 */
	public void setNext(SequenceData next) {
		if (this.next != next) {
			this.next = next;
			if (this.next != null)
				this.next.setPrevious(this);
		}
	}

	/**
	 * @param parent セットする parent
	 */
	public void setParent(SequenceData note) {
		if (this.parent != note) {
			this.parent = note;
			this.parent.setChild(this);
		}
	}

	public void setPrevious(SequenceData prev) {
		setPrevious(prev, true);
	}

	public void setPrevious(SequenceData prev, boolean sync) {
		if (this.prev != prev) {
			this.prev = prev;
			if (sync && prev != null)
				this.prev.setNext(this);
		}
	}
}
