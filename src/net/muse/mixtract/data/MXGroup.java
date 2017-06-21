package net.muse.mixtract.data;

import java.util.*;

import net.muse.data.*;

public class MXGroup extends Group {
	private List<MXNoteData> scoreNotelist;

	/**
	 * @param g1
	 * @param g2
	 * @param name
	 * @param partNumber
	 */
	MXGroup(Group g1, Group g2, String name, int partNumber) {
		super(g1, g2, name, partNumber);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * @param groupNoteList
	 * @param endNote
	 * @param type
	 */
	private MXGroup(GroupNote groupNoteList, GroupNote endNote,
			GroupType type) {
		super(groupNoteList, endNote, type);
		// TODO 自動生成されたコンストラクター・スタブ
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
	 * @param notelist
	 * @param partIndex
	 * @param type
	 */
	MXGroup(NoteData notelist, int partIndex, GroupType type) {
		super(notelist, partIndex, type);
	}

	/**
	 * 頂点らしさを算出します。
	 */
	@SuppressWarnings("unchecked") public void extractApex() {
		List<MXNoteData> nlist = (List<MXNoteData>) getScoreNotelist();
		// score clear
		for (MXNoteData n : nlist) {
			n.clearApexScore();
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
		for (MXNoteData n : nlist) {
			double score = n.sumTotalApexScore();
			if (score > max)
				max = score;
			if (score < min)
				min = score;
			scoreList.add(score);
		}
		double range = max - min;
		for (int i = 0; i < sz; i++) {
			nlist.get(i).setApexScore((scoreList.get(i) - min) / range);
		}

	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.Group#addScoreNoteList(java.util.List)
	 */
	@Override protected void addScoreNoteList(List<? extends NoteData> list) {
		for (NoteData n : list)
			scoreNotelist.add((MXNoteData) n);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.Group#cresteScoreNoteList()
	 */
	@Override protected void createScoreNoteList() {
		scoreNotelist = new ArrayList<MXNoteData>();
	}

}
