package net.muse.mixtract.data;

import java.util.ArrayList;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import javax.swing.JOptionPane;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.data.SequenceData;
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
public class NoteData extends SequenceData {

	/** 音価の最小値。TODO 単位系は各プログラムで確認してください。 */
	private static final int minimumDuration = 50;

	/** MusicXML.Note */
	private Note note;

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

	/** 楽譜上の発音時刻。TODO 単位系は各プログラムで確認してください。 */
	private int onset;

	/** 楽譜上の消音時刻。TODO 単位系は各プログラムで確認してください。 */
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

	/** 声部番号。１から始まる。0の場合、声部の区別がされていないことを表す。 */
	private int voice = 0;
	/** 音名 */
	private String noteName = "";
	/** MIDIノートナンバー */
	private int noteNumber;
	/** 装飾音であるかどうかを判別します。 */
	private boolean grace = false;
	/** タイであるかどうかを判別します。 */
	private boolean tied;
	private final int index;
	/** 休符であるかどうかを判別します。 */
	private boolean rest = false;
	/**
	 * 小節内の拍の位置。 TODO １拍目を0.0とするか1.0とするかは各プログラムで確認してください。
	 */
	private double beat;
	/**
	 * 実時間表記による開始時刻。TODO 単位を[秒]か[ミリ秒]にするかは各プログラムで確認してください。
	 */
	private double realOnset;
	/**
	 * 実時間表記による消音時刻。TODO 単位を[秒]か[ミリ秒]にするかは各プログラムで確認してください。
	 */
	private double realOffset;
	/** 当該音符に適用された保科理論ベースの頂点情報(ルール)の一覧 */
	private final ArrayList<ApexInfo> apexlist = new ArrayList<ApexInfo>();
	/** 頂点らしさを表すスコア。値のとる範囲は各プログラムで確認してください。 */
	private double apexScore;
	/** 当該音符に割り当てられる和声記号 */
	private Harmony chord = Harmony.I;
	/** 当該音符に割り当てられる調性 [長調 or 単調]。 */
	private KeyMode keyMode = KeyMode.major;
	/** 当該音符に割り当てられる調号 */
	private int fifths = 0;
	/** 和音の根音であるかどうかを判別します。 */
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
	NoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		this.note = note;
		// 基本情報
		index = idx;
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

	public double beat() {
		return beat;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#child()
	 */
	@Override public NoteData child() {
		return (NoteData) super.child();
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

	public int measureNumber() {
		return measureNumber;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#next()
	 */
	@Override public NoteData next() {
		return (NoteData) super.next();
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

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#parent()
	 */
	@Override public NoteData parent() {
		return (NoteData) super.parent();
	}

	/**
	 * @return partNumber
	 */
	public int partNumber() {
		return partNumber;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#previous()
	 */
	@Override public NoteData previous() {
		return (NoteData) super.previous();
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
	@Override public String toString() {
		return String.format(
				"idx=%d, on=%d, n=%s, beat=%1f, tval=%d, p=%d, m=%d, voice=%d/off=%d, vel=%d, rest=%b, chd=%b, grc=%b, tie=%b, fifths=%d, harmony=%s",
				index(), onset(), noteName(), beat(), timeValue(), partNumber(),
				measureNumber(), voice(), offset(), velocity(), rest(),
				child() != null, isGrace(), isTied(), fifths(), chord());
	}

	public int velocity() {
		return noteOn.velocity();
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

	void setOffset(int offset) {
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

	private void initialize(int partNumber,
			String noteName,
			int noteNumber,
			int voice,
			boolean grace,
			boolean tie,
			boolean rest,
			double beat,
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

}
