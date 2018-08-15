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
		if (child != note) {
			child = note;
			if (child != null)
				child.setParent(this);
		}
	}

	public void setNext(SequenceData note) {
		setNext(note, true);
	}

	public void setNext(SequenceData next, boolean sync) {
		if (this.next != next) {
			this.next = next;
			if (sync && this.next != null)
				this.next.setPrevious(this);
		}
	}

	public void setParent(SequenceData note) {
		setParent(note, true);
	}

	public void setParent(SequenceData note, boolean sync) {
		if (this.parent != note) {
			this.parent = note;
			if (sync && note != null)
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
