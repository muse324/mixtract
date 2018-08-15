package net.muse.mixtract.data;


import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.misc.MuseObject;
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
public class NoteData extends MuseObject {

	private static final int minimumDuration = 50;

	/** MusicXML.Note */
	private Note note;

	/** 後続音 */
	private NoteData next = null;
	/** 先行音 */
	private NoteData prev = null;
	/** 和音(上) **/
	private NoteData parent = null;
	/** 和音(下) **/
	private NoteData child = null;

	/**
	 * MIDI ノートオンイベント
	 *
	 * @see {@link NoteScheduleEvent}
	 */
	private NoteScheduleEvent noteOn;

	/**
	 * MIDI ノートオフイベント
	 *
	 * @see {@link NoteScheduleEvent}
	 */
	private NoteScheduleEvent noteOff;

	/** 発音時刻 */
	private int onset;

	/** 消音時刻 */
	private int offset;

	/** 音価（楽譜上） */
	private int timeValue = 0;

	/**
	 * Part number begins from 1 by integer.<br>
	 * 声部番号(1～)
	 */
	private int partNumber = 0;
	/**
	 * Measure number begins from 0 (Auftakt) by integer.<br>
	 * 小節番号(0～)
	 */
	private int measureNumber = -1;
	/** 内声番号(1～) */
	private int voice = 0;
	private String noteName = "";
	private int noteNumber;
	private boolean grace = false;
	private boolean tied;
	private final int index;
	private boolean rest = false;
	private double beat;
	private double realOnset;
	private double realOffset;
	/** 保科理論による頂点情報 */
	private final ArrayList<ApexInfo> apexlist = new ArrayList<ApexInfo>();
	private double apexScore;
	private Harmony chord = Harmony.I;
	private KeyMode keyMode = KeyMode.major;
	private int fifths = 0;

	private boolean nonChord = false;
	/**
	 * @return
	 */
	private static int getDefaultGraseNoteDuration() {
		return Math.round(getTicksPerBeat() / (float) 16.);
	}

	NoteData(int i, int partNumber, int onset, int offset, String noteName,
			boolean rest, boolean grace, boolean tie, int tval, double beat) {
		// 基本情報
		index = i;
		realOnset = this.onset = onset;
		realOffset = this.offset = offset;
		this.partNumber = partNumber;
		this.noteName = noteName;
		this.noteNumber = Util.getNoteNumber(noteName);
		this.timeValue = tval;
		this.grace = grace;
		this.tied = tie;
		timeValue = (grace) ? getDefaultGraseNoteDuration() : offset - onset;
		this.rest = rest;
		this.beat = beat;
		this.chord = Harmony.I;

		// ノートイベント
		try {
			noteOn = new NoteScheduleEvent(this, onsetInMsec(getDefaultBPM()),
					ShortMessage.NOTE_ON, getDefaultVelocity());
			noteOff = new NoteScheduleEvent(this,
					offsetInMsec(getDefaultBPM()), ShortMessage.NOTE_OFF,
					getDefaultOffVelocity());
		} catch (InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(null,
					String.format("invalid MIDI data for %s", this));
		}
		setNonChordNote(chord);
	}

	/**
	 * @param note {@link MusicXMLWrapper}.Note object
	 * @param partNumber Part number begins from 1 by integer.<br>
	 * @param idx
	 * @param bpm
	 * @param vel
	 */
	NoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		this.note = note;
		// 基本情報
		index = idx;
		this.partNumber = partNumber;
		measureNumber = note.measure().number();
		noteName = note.noteName();
		noteNumber = (note.rest()) ? -1 : note.notenum();
		voice = note.voice();
		onset = note.onset(getTicksPerBeat());
		offset = note.offset(getTicksPerBeat());
		realOnset = onsetInMsec(bpm);
		realOffset = offsetInMsec(bpm);
		timeValue = (note.grace()) ? getDefaultGraseNoteDuration() : note
				.tiedDuration(getTicksPerBeat());
		rest = note.rest();
		beat = note.beat();
		chord = Harmony.I;

		// 装飾系
		grace = note.grace();

		// ノートイベント
		try {
			noteOn = new NoteScheduleEvent(this, onsetInMsec(bpm),
					ShortMessage.NOTE_ON, vel);
			noteOff = new NoteScheduleEvent(this, offsetInMsec(bpm),
					ShortMessage.NOTE_OFF, vel);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		setNonChordNote(chord);
	}

	public double beat() {
		return beat;
	}

	/**
	 * @return down
	 */
	public NoteData child() {
		return child;
	}

	/**
	 * @return chord
	 */
	public Harmony chord() {
		return chord;
	}

	public double duration() {
		return realOffset() - realOnset();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		NoteData cmp = (NoteData) obj;
		return partNumber == cmp.partNumber() && onset == cmp.onset()
				&& noteNumber() == cmp.noteNumber();
	}

	/**
	 * @return
	 */
	public int fifths() {
		return fifths;
	}

	/**
	 * @return the apexScore
	 */
	public final double getApexScore() {
		return apexScore;
	}

	/**
	 * @return the keyMode
	 */
	public final KeyMode getKeyMode() {
		return keyMode;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
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

	public int measureNumber() {
		return measureNumber;
	}

	public NoteData next() {
		return next;
	}

	public String noteName() {
		return noteName;
	}

	public int noteNumber() {
		return noteNumber;
	}

	/**
	 * 消音時刻をTicksで返します．
	 *
	 * @return offset
	 */
	public final int offset() {
		return offset;
	}

	public double offsetInMsec(int bpm) {
		return offset() * Util.bpmToBeatTime(bpm) / getTicksPerBeat();
	}

	/**
	 * 発音時刻をTicksで返します．
	 *
	 * @return onset
	 */
	public final int onset() {
		return onset;
	}

	public double onsetInMsec(int bpm) {
		return onset() * Util.bpmToBeatTime(bpm) / (double) getTicksPerBeat();
	}

	/**
	 * @return partNumber
	 */
	public int partNumber() {
		return partNumber;
	}

	/**
	 * @return prev
	 */
	public NoteData previous() {
		return prev;
	}

	/**
	 * @return realOffset
	 */
	public final double realOffset() {
		return realOffset;
	}

	/**
	 * @return realOnset
	 */
	public final double realOnset() {
		return realOnset;
	}

	public boolean rest() {
		return rest;
	}

	/**
	 * @param chord セットする chord
	 */
	public void setChord(Harmony chord) {
		this.chord = chord;
		setNonChordNote(chord);
	}

	/**
	 * @param value
	 */
	public void setFifths(int value) {
		fifths = value;
	}

	/**
	 * @param keyMode
	 * @param fifths
	 */
	public void setKeyMode(KeyMode keyMode, int fifths) {
		this.keyMode = keyMode;
		this.fifths = fifths;
		setNonChordNote(chord);
	}

	/**
	 * @param partNumber セットする partNumber
	 */
	public final void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public void setRealOffset(double offset) {
		realOffset = (offset < realOnset + minimumDuration)	? realOnset + minimumDuration
															: offset;
		noteOff.setOnset((long) realOffset);
	}

	public void setRealOnset(double onset) {
		realOnset = onset;
		noteOn.setOnset((long) onset);
	}

	public void setVelocity(int vel) {
		try {
			noteOn.setVelocity(vel);
		} catch (InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(null,
					String.format("invalid velocity %d for %s", vel, this));
		}
	}

	/**
	 * 楽譜上の音価をdivisionで表します。
	 * MusicXML形式の＜note＞-＜duration＞タグに相当します。作曲モードの場合にのみ値の更新が可能です。
	 * 表情付けモードにおいては，<code>duration()</code>の初期値として参照されるよう，値を固定しておく必要があります。
	 *
	 * @return 楽譜上の音価
	 */
	public int timeValue() {
		return timeValue;
	}

	public double timeValueInMsec(int bpm) {
		return timeValue() * Util.bpmToBeatTime(bpm) / getTicksPerBeat();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("idx=%d, on=%d, n=%s, beat=%1f, tval=%d, p=%d, m=%d, voice=%d/off=%d, vel=%d, rest=%b, chd=%b, grc=%b, tie=%b, fifths=%d, harmony=%s",
						index(), onset(), noteName(), beat(), timeValue(),
						partNumber(), measureNumber(), voice(), offset(),
						velocity(), rest(), child != null, isGrace(), isTied(),
						fifths(), chord());
	}

	public int velocity() {
		return noteOn.velocity();
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

	private void setNonChordNote(Harmony c) {
		nonChord = true;
		int[] notes = keyMode.noteIntervals(c);
		int scale = (noteNumber - fifths) % 12;
		for (int i = 0; i < notes.length; i++) {
			if (notes[i] == scale) {
				nonChord = false;
				return;
			}
		}
	}

	/**
	 * @return voice
	 */
	private int voice() {
		return voice;
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
	 * @return noteOff
	 */
	final NoteScheduleEvent getNoteOff() {
		return noteOff;
	}

	/**
	 * @return noteOn
	 */
	final NoteScheduleEvent getNoteOn() {
		return noteOn;
	}

	/**
	 * @return note
	 */
	Note getXMLNote() {
		return note;
	}

	String id() {
		return "n" + index();
	}

	/**
	 * @return index
	 */
	int index() {
		return index;
	}

	/**
	 * @return the nonChord
	 */
	final boolean isNonChord() {
		return nonChord;
	}

	/**
	 * @return up
	 */
	NoteData parent() {
		return parent;
	}

	/**
	 * @param apexScore the apexScore to set
	 */
	final void setApexScore(double apexScore) {
		this.apexScore = apexScore;
	}

	/**
	 * @param child セットする child
	 */
	void setChild(NoteData note) {
		if (this.child != note) {
			this.child = note;
			this.child.setParent(this);
		}
	}

	/**
	 * @param measureNumber セットする measureNumber
	 */
	final void setMeasureNumber(int measureNumber) {
		this.measureNumber = measureNumber;
	}

	/**
	 * @param next セットする next
	 */
	void setNext(NoteData next) {
		if (this.next != next) {
			this.next = next;
			if (this.next != null)
				this.next.setPrevious(this);
		}
	}

	void setOffset(int offset) {
		this.offset = offset;
		getNoteOff().setOnset(offset);
	}

	/**
	 * @param note セットする parent
	 */
	void setParent(NoteData note) {
		if (this.parent != note) {
			this.parent = note;
			this.parent.setChild(this);
		}
	}

	/**
	 * @param prev セットする prev
	 */
	void setPrevious(NoteData prev) {
		setPrevious(prev, true);
	}

	void setPrevious(NoteData prev, boolean sync) {
		if (this.prev != prev) {
			this.prev = prev;
			if (sync && prev != null)
				this.prev.setNext(this);
		}
	}

	/**
	 * @param voice セットする voice
	 */
	void setVoice(int voice) {
		this.voice = voice;
	}

	// TODO 2011.09.02 使ってない様子
	@Deprecated
	void slide(int durationOffset) {
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

}
