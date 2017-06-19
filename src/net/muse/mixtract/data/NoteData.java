package net.muse.mixtract.data;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.misc.Util;

/**
 * <h1>Note</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>@ CrestMuse Project, JST</address>
 *         <address> <a
 *         href="http://mixtract.m-use.net/" >http://mixtract.m-use
 *         .net</a></address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/09/20
 */
public class NoteData extends AbstractNoteData {

	NoteData(int i, int partNumber, int onset, int offset, String noteName,
			boolean rest, boolean grace, boolean tie, int tval, double beat) {
		super(i, partNumber, onset, offset, noteName, rest, grace, tie, tval,
				beat);
	}

	NoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		super(note, partNumber, idx, bpm, vel);
	}

	/** 保科理論による頂点情報 */
	private final ArrayList<ApexInfo> apexlist = new ArrayList<ApexInfo>();
	private double apexScore;
	private NoteData child;
	private NoteData next;
	private NoteData parent;
	private NoteData prev;

	/**
	 * @return down
	 */
	@Override public NoteData child() {
		return child;
	}

	/**
	 * @return the apexScore
	 */
	public final double getApexScore() {
		return apexScore;
	}

	@Override public NoteData next() {
		return next;
	}

	/**
	 * @return prev
	 */
	@Override public NoteData previous() {
		return prev;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return String.format(
				"idx=%d, on=%d, n=%s, beat=%1f, tval=%d, p=%d, m=%d, voice=%d/off=%d, vel=%d, rest=%b, chd=%b, grc=%b, tie=%b, fifths=%d, harmony=%s",
				index(), onset(), noteName(), beat(), timeValue(), partNumber(),
				measureNumber(), voice(), offset(), velocity(), rest(),
				child != null, isGrace(), isTied(), fifths(), chord());
	}

	/**
	 * @param longerNoteRule
	 */
	void addApexScore(ApexInfo rule) {
		apexlist.add(rule);
	}

	void clearApexScore() {
		apexlist.clear();
	}

	/**
	 * @param apexScore the apexScore to set
	 */
	final void setApexScore(double apexScore) {
		this.apexScore = apexScore;
	}

	/**
	 * @return
	 */
	double sumTotalApexScore() {
		int score = 0;
		for (ApexInfo a : apexlist)
			score += a.getScore();
		return score;
	}

	/**
	 * @return up
	 */
	@Override public NoteData parent() {
		return parent;
	}

	/**
	 * @param child セットする child
	 */
	@Override public void setChild(AbstractNoteData note) {
		if (this.child != note) {
			this.child = (NoteData) note;
			this.child.setParent(this);
		}
	}

	/**
	 * @param next セットする next
	 */
	@Override public void setNext(AbstractNoteData next) {
		if (this.next != next) {
			this.next = (NoteData) next;
			if (this.next != null)
				this.next.setPrevious(this);
		}
	}

	/**
	 * @param note セットする parent
	 */
	@Override public void setParent(AbstractNoteData note) {
		if (this.parent != note) {
			this.parent = (NoteData) note;
			this.parent.setChild(this);
		}
	}

	@Override public void setPrevious(AbstractNoteData prev, boolean sync) {
		if (this.prev != prev) {
			this.prev = (NoteData) prev;
			if (sync && prev != null)
				this.prev.setNext(this);
		}
	}

}
