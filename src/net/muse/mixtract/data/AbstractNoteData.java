package net.muse.mixtract.data;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.misc.MuseObject;
import net.muse.misc.Util;

public abstract class AbstractNoteData extends MuseObject {

	private static final int minimumDuration = 50;

	/**
	 * @return
	 */
	private static int getDefaultGraseNoteDuration() {
		return Math.round(getTicksPerBeat() / (float) 16.);
	}

	private double beat;

	/** 和音(上) **/
	private AbstractNoteData parent = null;
	/** 和音(下) **/
	private AbstractNoteData child = null;
	/** 後続音 */
	private AbstractNoteData next = null;
	/** 先行音 */
	private AbstractNoteData prev = null;

	private Harmony chord = Harmony.I;

	private int fifths = 0;

	private boolean grace = false;
	private int index;
	private KeyMode keyMode = KeyMode.major;
	/**
	 * Measure number begins from 0 (Auftakt) by integer.<br>
	 * 小節番号(0～)
	 */
	private int measureNumber = -1;
	private boolean nonChord = false;
	/** MusicXML.Note */
	private Note note;
	private String noteName = "";
	private int noteNumber;
	/**
	 * MIDI ノートオフイベント
	 *
	 * @see {@link NoteScheduleEvent}
	 */
	private NoteScheduleEvent noteOff;
	/**
	 * MIDI ノートオンイベント
	 *
	 * @see {@link NoteScheduleEvent}
	 */
	private NoteScheduleEvent noteOn;
	/** 消音時刻 */
	private int offset;
	/** 発音時刻 */
	private int onset;
	/**
	 * Part number begins from 1 by integer.<br>
	 * 声部番号(1～)
	 */
	private int partNumber = 0;
	private double realOffset;
	private double realOnset;
	private boolean rest = false;
	private boolean tied;
	/** 音価（楽譜上） */
	private int timeValue = 0;
	/** 内声番号(1～) */
	private int voice = 0;

	public AbstractNoteData(int i, int partNumber, int onset, int offset,
			String noteName, boolean rest, boolean grace, boolean tie, int tval,
			double beat) {
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
			noteOff = new NoteScheduleEvent(this, offsetInMsec(getDefaultBPM()),
					ShortMessage.NOTE_OFF, getDefaultOffVelocity());
		} catch (InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(null, String.format(
					"invalid MIDI data for %s", this));
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
	public AbstractNoteData(Note note, int partNumber, int idx, int bpm,
			int vel) {
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
		timeValue = (note.grace()) ? getDefaultGraseNoteDuration()
				: note.tiedDuration(getTicksPerBeat());
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

	public abstract AbstractNoteData child();

	/**
	 * @return chord
	 */
	public Harmony chord() {
		return chord;
	}

	public double duration() {
		return realOffset() - realOnset();
	}

	@Override public boolean equals(Object obj) {
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
	 * @return the keyMode
	 */
	public final KeyMode getKeyMode() {
		return keyMode;
	}

	/**
	 * @return noteOff
	 */
	public final NoteScheduleEvent getNoteOff() {
		return noteOff;
	}

	/**
	 * @return noteOn
	 */
	public final NoteScheduleEvent getNoteOn() {
		return noteOn;
	}

	/**
	 * @return note
	 */
	public Note getXMLNote() {
		return note;
	}

	@Override public int hashCode() {
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

	/** 音符の通し番号（ID）を返します。 */
	public String id() {
		return "n" + index();
	}

	/**
	 * @return index
	 */
	public int index() {
		return index;
	}

	/** 装飾音符であるかどうかを判別します。 */
	public boolean isGrace() {
		return grace;
	}

	/**
	 * @return the nonChord
	 */
	public final boolean isNonChord() {
		return nonChord;
	}

	/**
	 * @return tied
	 */
	public boolean isTied() {
		return tied;
	}

	public int measureNumber() {
		return measureNumber;
	}

	public abstract AbstractNoteData next();

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

	public abstract AbstractNoteData parent();

	/**
	 * @return partNumber
	 */
	public int partNumber() {
		return partNumber;
	}

	public abstract AbstractNoteData previous();

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

	public abstract void setChild(AbstractNoteData note);

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

	/** 小節番号を指定します。 */
	public final void setMeasureNumber(int measureNumber) {
		this.measureNumber = measureNumber;
	}

	public void setNext(AbstractNoteData note) {
		if (this.next != note) {
			this.next = note;
			if (this.next != null)
				this.next.setPrevious(this);
		}
	}

	public void setNonChordNote(Harmony c) {
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
	 * 離鍵時刻を指定します。
	 */
	public void setOffset(int offset) {
		this.offset = offset;
		getNoteOff().setOnset(offset);
	}

	public abstract void setParent(AbstractNoteData note);

	/**
	 * @param partNumber セットする partNumber
	 */
	public final void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public void setPrevious(AbstractNoteData prev) {
		setPrevious(prev, true);
	}

	public void setPrevious(AbstractNoteData prev, boolean sync) {
		if (this.prev != prev) {
			this.prev = prev;
			if (sync && prev != null)
				this.prev.setNext(this);
		}
	}

	public void setRealOffset(double offset) {
		realOffset = (offset < realOnset + minimumDuration) ? realOnset
				+ minimumDuration : offset;
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
			JOptionPane.showMessageDialog(null, String.format(
					"invalid velocity %d for %s", vel, this));
		}
	}

	/**
	 * 声部番号を指定します。
	 *
	 * @param voice - int型
	 */
	public void setVoice(int voice) {
		this.voice = voice;
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

	public int velocity() {
		return noteOn.velocity();
	}

	/**
	 * @return voice
	 */
	public int voice() {
		return voice;
	}

	@Deprecated void slide(int durationOffset) {
		setRealOffset(offset() + durationOffset);
	}
}
