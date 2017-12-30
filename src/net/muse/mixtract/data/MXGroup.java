package net.muse.mixtract.data;

import java.util.*;

import net.muse.data.*;
import net.muse.mixtract.data.curve.*;

public class MXGroup extends Group {
	private DynamicsCurve dynamicsCurve;

	private TempoCurve tempoCurve;

	private ArticulationCurve articulationCurve;
	/**
	 * @param groupNoteList
	 * @param endNote
	 * @param type
	 */
	public MXGroup(GroupNote groupNoteList, GroupNote endNote, GroupType type) {
		super(groupNoteList, endNote, type);
	}
	/**
	 * @param notelist
	 * @param partIndex
	 * @param type
	 */
	public MXGroup(NoteData notelist, int partIndex, GroupType type) {
		super(notelist, partIndex, type);
	}

	/**
	 * @param g1
	 * @param g2
	 * @param name
	 * @param partNumber
	 */
	MXGroup(Group g1, Group g2, String name, int partNumber) {
		super(g1, g2, name, partNumber);
	}

	/**
	 * @param id
	 * @param partNumber
	 * @param list
	 * @param type
	 */
	MXGroup(int id, int partNumber, GroupNote list, GroupType type) {
		super(id, partNumber, list, type);
	}

	/**
	 * 頂点らしさを算出します。
	 */
	public void extractApex() {
		List<? extends NoteData> nlist = getScoreNotelist();
		// score clear
		for (NoteData n : nlist) {
			assert n instanceof MXNoteData;
			((MXNoteData) n).clearApexScore();
		}

		final int sz = nlist.size();
		ApexInfo.applyRule(ApexInfo.LONGER_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.SAME_TIMEVALUE_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.HIGHER_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.SAME_PITCH_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.BEGIN_NOTE, this);
		ApexInfo.applyRule(ApexInfo.END_NOTE, this);
		ApexInfo.applyRule(ApexInfo.MOUNTAIN_PROGRESS, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.VALLEY_PROGRESS, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.ZIGZAG_PROGRESS1, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.ZIGZAG_PROGRESS2, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.UPPER_STETCH_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.LOWER_STETCH_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.UPPER_PROGRESS_NOTE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.LOWER_PROGRESS_NOTE, nlist, 0, sz);
		for (Harmony h : Harmony.values()) {
			ApexInfo.SingleChordRule.applyRule(h, nlist, 0, sz);
		}
		ApexInfo.applyRule(ApexInfo.CHORD_CHANGE, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.CADENTZ_I, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.CADENTZ_I6, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.CADENTZ_VI, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.APPOGGIATURA_LONGER, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.APPOGGIATURA_SAME, nlist, 0, sz);
		ApexInfo.applyRule(ApexInfo.APPOGGIATURA_SHORTER, nlist, 0, sz);
		// summarize
		double max = -1000.;
		double min = 1000.;
		ArrayList<Double> scoreList = new ArrayList<Double>();
		for (NoteData n : nlist) {
			assert n instanceof MXNoteData;
			double score = ((MXNoteData) n).sumTotalApexScore();
			if (score > max)
				max = score;
			if (score < min)
				min = score;
			scoreList.add(score);
		}
		double range = max - min;
		for (int i = 0; i < sz; i++) {
			((MXNoteData) nlist.get(i)).setApexScore((scoreList.get(i) - min)
					/ range);
		}
	}

	/**
	 * @return articulationCurve
	 */
	public final ArticulationCurve getArticulationCurve() {
		return articulationCurve;
	}


	/**
	 * @return dynamicsCurve
	 */
	public DynamicsCurve getDynamicsCurve() {
		return dynamicsCurve;
	}

	/**
	 * @return tempoCurve
	 */
	public TempoCurve getTempoCurve() {
		return tempoCurve;
	}

	public final boolean hasPhraseCurve() {
		return dynamicsCurve != null && tempoCurve != null
				&& articulationCurve != null;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.Group#addScoreNoteList(java.util.List)
	 */
	@Override
	protected void addScoreNoteList(List<? extends NoteData> list) {
		for (NoteData n : list)
			scoreNotelist.add((MXNoteData) n);
	}

	protected void initialize() {
		dynamicsCurve = (DynamicsCurve) PhraseCurve.createPhraseProfile(
				PhraseCurveType.DYNAMICS);
		tempoCurve = (TempoCurve) PhraseCurve.createPhraseProfile(
				PhraseCurveType.TEMPO);
		articulationCurve = (ArticulationCurve) PhraseCurve.createPhraseProfile(
				PhraseCurveType.ARTICULATION);
	}
}
