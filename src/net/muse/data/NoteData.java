package net.muse.data;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.misc.Util;

public class NoteData extends SequenceData {
	/**
	 * MusicXML.Note クラスでの音符情報。改めてCMXからの情報を取得したい時に getXMLNote() を通じて呼び出してください。
	 */
	protected Note note;

	/** 音価の最小値。TODO 単位系は各プログラムで確認してください。 */
	private static final int minimumDuration = 50;
	/** 当該音符に割り当てられる調号 */
	protected int fifths = 0;
	/** 当該音符に割り当てられる調性 [長調 or 単調]。 */
	protected KeyMode keyMode = KeyMode.major;
	/** 当該音符に割り当てられる和声記号 */
	protected Harmony chord = Harmony.I;
	/** MIDIノートナンバー */
	protected int noteNumber;
	/**
	 * 実時間表記による開始時刻。TODO 単位を[秒]か[ミリ秒]にするかは各プログラムで確認してください。
	 */
	protected double realOnset;
	/**
	 * 実時間表記による消音時刻。TODO 単位を[秒]か[ミリ秒]にするかは各プログラムで確認してください。
	 */
	protected double realOffset;
	/** 楽譜上の発音時刻。TODO 単位系は各プログラムで確認してください。 */
	protected int onset;
	/** 楽譜上の消音時刻。TODO 単位系は各プログラムで確認してください。 */
	protected int offset;
	/** 音価（楽譜上） */
	protected int timeValue = 0;
	/** 音名 */
	protected String noteName = "";
	/**
	 * Part number begins from 1 by integer.<br>
	 * 声部番号(1～)
	 */
	protected int partNumber = 0;
	/**
	 * Measure number begins from 0 (Auftakt) by integer.<br>
	 * 小節番号(0～)
	 */
	protected int measureNumber = -1;
	/** 和音の根音であるかどうかを判別します。 */
	private boolean nonChord = false;
	/**
	 * MIDI ノートオンイベント
	 *
	 * @see {@link NoteScheduleEvent}
	 */
	protected NoteScheduleEvent noteOn;
	/**
	 * MIDI ノートオフイベント
	 *
	 * @see {@link NoteScheduleEvent}
	 */
	protected NoteScheduleEvent noteOff;
	protected final int index;
	/** 休符であるかどうかを判別します。 */
	private boolean rest = false;
	/**
	 * 小節内の拍の位置。 TODO １拍目を0.0とするか1.0とするかは各プログラムで確認してください。
	 */
	protected double beat;
	/** 声部番号。１から始まる。0の場合、声部の区別がされていないことを表す。 */
	protected int voice = 0;
	/** 装飾音であるかどうかを判別します。 */
	protected boolean grace = false;
	/** タイであるかどうかを判別します。 */
	protected boolean tied;

	/**
	 * @return
	 */
	protected static int getDefaultGraseNoteDuration() {
		return Math.round(getTicksPerBeat() / (float) 16.);
	}

	public NoteData(int index) {
		this.index = index;
	}

	public NoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		// 基本情報
		this(idx);
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
	 * @return chord
	 */
	public Harmony chord() {
		return chord;
	}

	public double duration() {
		return realOffset() - realOnset();
	}

	public int noteNumber() {
		return noteNumber;
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

	public String noteName() {
		return noteName;
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

	/**
	 * @return partNumber
	 */
	public int partNumber() {
		return partNumber;
	}

	public int measureNumber() {
		return measureNumber;
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
	 * @return the nonChord
	 */
	public final boolean isNonChord() {
		return nonChord;
	}

	protected void setNonChordNote(Harmony c) {
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

	public int velocity() {
		return noteOn.velocity();
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

	public String id() {
		return "n" + index();
	}

	/**
	 * @return index
	 */
	public int index() {
		return index;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#child()
	 */
	@Override
	public NoteData child() {
		return (NoteData) super.child();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#next()
	 */
	@Override
	public NoteData next() {
		return (NoteData) super.next();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#parent()
	 */
	@Override
	public NoteData parent() {
		return (NoteData) super.parent();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#previous()
	 */
	@Override
	public NoteData previous() {
		return (NoteData) super.previous();
	}

	public boolean rest() {
		return rest;
	}

	public double beat() {
		return beat;
	}

	/**
	 * @param partNumber セットする partNumber
	 */
	public final void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	public void setVelocity(int vel) {
		try {
			noteOn.setVelocity(vel);
		} catch (InvalidMidiDataException e) {
			JOptionPane.showMessageDialog(null, String.format(
					"invalid velocity %d for %s", vel, this));
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

	/**
	 * @param chord セットする chord
	 */
	public void setChord(Harmony chord) {
		this.chord = chord;
		setNonChordNote(chord);
	}

	protected void initialize(int partNumber, String noteName, int noteNumber,
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

	protected void createMIDINoteEvent(int bpm, int vel) {
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

	public void setOffset(int offset) {
		this.offset = offset;
		getNoteOff().setOnset(offset);
	}

}
