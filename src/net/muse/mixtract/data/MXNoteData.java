package net.muse.mixtract.data;

import java.util.ArrayList;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;
import net.muse.data.NoteData;
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

	/** 当該音符に適用された保科理論ベースの頂点情報(ルール)の一覧 */
	private final ArrayList<ApexInfo> apexlist = new ArrayList<ApexInfo>();
	/** 頂点らしさを表すスコア。値のとる範囲は各プログラムで確認してください。 */
	private double apexScore;

	MXNoteData(int i, int partNumber, int onset, int offset, String noteName,
			boolean rest, boolean grace, boolean tie, int tval, double beat) {
		// 基本情報
		super(i);
		initialize(partNumber, noteName, Util.getNoteNumber(noteName), 0, grace,
				tie, rest, beat, chord());
		setOnset(onset);
		setOffset(offset);
		setRealOnset(onset);
		setRealOffset(offset);
		// this.timeValue = tval;
		setTimeValue((grace) ? getDefaultGraseNoteDuration() : offset - onset);

		// ノートイベント
		createMIDINoteEvent(getDefaultBPM(), getDefaultVelocity());
	}

	public MXNoteData(MusicXMLWrapper.Note note, int partNumber, int idx,
			int bpm, int vel) {
		super(note, partNumber, idx, bpm, vel);
	}

	public MXNoteData(SCCXMLWrapper.Note note, int partNumber, int idx, int bpm,
			int beat, int vel) {
		super(note, partNumber, idx, bpm, beat, vel);
	}

	public MXNoteData(int index) {
		super(index);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#child()
	 */
	@Override
	public MXNoteData child() {
		return (MXNoteData) super.child();
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
		return xmlPartNumber() == cmp.xmlPartNumber() && onset() == cmp.onset()
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
	@Override
	public MXNoteData next() {
		return (MXNoteData) super.next();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#parent()
	 */
	@Override
	public MXNoteData parent() {
		return (MXNoteData) super.parent();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#previous()
	 */
	@Override
	public MXNoteData previous() {
		return (MXNoteData) super.previous();
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
