package net.muse.mixtract.data;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.data.*;
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
public class MXNoteData extends NoteData {

	/** MusicXML.Note */
	private Note note;

	/** 声部番号。１から始まる。0の場合、声部の区別がされていないことを表す。 */
	private int voice = 0;
	/** 装飾音であるかどうかを判別します。 */
	private boolean grace = false;
	/** タイであるかどうかを判別します。 */
	private boolean tied;
	/** 当該音符に適用された保科理論ベースの頂点情報(ルール)の一覧 */
	private final ArrayList<ApexInfo> apexlist = new ArrayList<ApexInfo>();
	/** 頂点らしさを表すスコア。値のとる範囲は各プログラムで確認してください。 */
	private double apexScore;

	/**
	 * @return
	 */
	private static int getDefaultGraseNoteDuration() {
		return Math.round(getTicksPerBeat() / (float) 16.);
	}

	MXNoteData(int i, int partNumber, int onset, int offset, String noteName,
			boolean rest, boolean grace, boolean tie, int tval, double beat) {
		// 基本情報
		super(i);
		initialize(partNumber, noteName, Util.getNoteNumber(noteName), 0, grace,
				tie, rest, beat, chord);
		realOnset = this.onset = onset;
		realOffset = this.offset = offset;
		this.timeValue = tval;
		timeValue = (grace) ? getDefaultGraseNoteDuration() : offset - onset;

		// ノートイベント
		createMIDINoteEvent(getDefaultBPM(), getDefaultVelocity());
	}

	/**
	 * @param note {@link MusicXMLWrapper}.Note object
	 * @param partNumber Part number begins from 1 by integer.<br>
	 * @param idx
	 * @param bpm
	 * @param vel
	 */
	public MXNoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		// 基本情報
		super(idx);
		this.note = note;
		initialize(partNumber, note.noteName(), (note.rest()) ? -1
				: note.notenum(), note.voice(), note.grace(), note
						.tiedTo() != null, note.rest(), note.beat(), Harmony.I);

		measureNumber = note.measure().number();
		onset = note.onset(getTicksPerBeat());
		offset = note.offset(getTicksPerBeat());
		realOnset = onsetInMsec(bpm);
		realOffset = offsetInMsec(bpm);
		timeValue = (note.grace()) ? getDefaultGraseNoteDuration()
				: note.tiedDuration(getTicksPerBeat());

		// ノートイベント
		createMIDINoteEvent(bpm, vel);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#child()
	 */
	@Override public MXNoteData child() {
		return (MXNoteData) super.child();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		NoteData cmp = (NoteData) obj;
		return partNumber == cmp.partNumber() && onset == cmp.onset()
				&& noteNumber() == cmp.noteNumber();
	}

	/**
	 * @return the apexScore
	 */
	public final double getApexScore() {
		return apexScore;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#next()
	 */
	@Override public MXNoteData next() {
		return (MXNoteData) super.next();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#parent()
	 */
	@Override public MXNoteData parent() {
		return (MXNoteData) super.parent();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#previous()
	 */
	@Override public MXNoteData previous() {
		return (MXNoteData) super.previous();
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
				child() != null, isGrace(), isTied(), fifths(), chord());
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
	 * @return note
	 */
	Note getXMLNote() {
		return note;
	}

	/**
	 * @param apexScore the apexScore to set
	 */
	final void setApexScore(double apexScore) {
		this.apexScore = apexScore;
	}

	/**
	 * @param measureNumber セットする measureNumber
	 */
	final void setMeasureNumber(int measureNumber) {
		this.measureNumber = measureNumber;
	}

	public void setOffset(int offset) {
		this.offset = offset;
		getNoteOff().setOnset(offset);
	}

	/**
	 * @param voice セットする voice
	 */
	void setVoice(int voice) {
		this.voice = voice;
	}

	// TODO 2011.09.02 使ってない様子
	@Deprecated void slide(int durationOffset) {
		setRealOffset(offset() + durationOffset);
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

	private void createMIDINoteEvent(int bpm, int vel) {
		try {
			noteOn = new NoteScheduleEvent(this, onsetInMsec(bpm),
					ShortMessage.NOTE_ON, vel);
			noteOff = new NoteScheduleEvent(this, offsetInMsec(bpm),
					ShortMessage.NOTE_OFF, vel);
		} catch (InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(null, String.format(
					"invalid MIDI data for %s", this));
		}
	}

	private void initialize(int partNumber, String noteName, int noteNumber,
			int voice, boolean grace, boolean tie, boolean rest, double beat,
			Harmony chord) {
		this.partNumber = partNumber;
		this.noteName = noteName;
		this.noteNumber = noteNumber;
		this.voice = voice;
		this.grace = grace;
		this.tied = tie;
		this.rest = rest;
		this.beat = beat;
		this.chord = chord;
		setNonChordNote(chord);
	}

	/**
	 * @return grace
	 */
	private boolean isGrace() {
		return grace;
	}

	/**
	 * @return tied
	 */
	private boolean isTied() {
		return tied;
	}

	/**
	 * @return voice
	 */
	private int voice() {
		return voice;
	}

}
