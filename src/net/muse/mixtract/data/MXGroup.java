package net.muse.mixtract.data;

import java.util.ArrayList;
import java.util.List;

import net.muse.data.*;
import net.muse.mixtract.data.curve.*;

/**
 * 演奏デザイン支援ツールMixtractに必要なグループ情報を定義します。
 *
 * @see Group
 * @author hashida
 */
public class MXGroup extends Group {
	private DynamicsCurve dynamicsCurve;
	private TempoCurve tempoCurve;
	private ArticulationCurve articulationCurve;
	/** グループ中央付近にある音符。 */
	private GroupNote centerNote;
	private MXGroup childFormerGroup = null;
	private MXGroup childLatterGroup = null;

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
	 * プロジェクトファイルから読み込んだグループを生成します．
	 *
	 * @param g1
	 * @param g2
	 * @param name
	 * @param partNumber
	 */
	MXGroup(MXGroup g1, MXGroup g2, String name, int partNumber) {
		super(GroupType.is(name.charAt(0)));
		setIndex(Integer.parseInt(name.substring(1)));
		setPartNumber(partNumber);
		beginGroupNote = g1.getBeginGroupNote();
		endGroupNote = g2.getEndGroupNote();
		setChild(g1, g2);
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

	public GroupNote getCenterGroupNote() {
		if (centerNote == null) {
			// onset length
			int len = getEndGroupNote().getNote().onset() - onsetInTicks();
			int targetTime = len / 2;
			searchCenterGroupNote(targetTime, getBeginGroupNote());
			if (hasChild())
				searchCenterGroupNote(targetTime, getChildLatterGroup()
						.getBeginGroupNote());
		}
		return centerNote;
	}

	private void searchCenterGroupNote(int targetTime, GroupNote note) {
		if (note == null)
			return;
		if (note.getNote().onset() >= targetTime)
			return;
		centerNote = note;
		searchCenterGroupNote(targetTime, note.next());
	}

	public MXGroup getChildFormerGroup() {
		return childFormerGroup;
	}

	public MXGroup getChildLatterGroup() {
		return childLatterGroup;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Group#getScoreNotelist()
	 */
	@Override
	public List<? extends NoteData> getScoreNotelist() {
		if (scoreNotelist == null)
			createScoreNoteList();
		if (hasChild()) {
			scoreNotelist.clear();
			addScoreNoteList(getChildFormerGroup().getScoreNotelist());
			addScoreNoteList(getChildLatterGroup().getScoreNotelist());
		} else if (scoreNotelist.size() <= 1)
			makeScoreNotelist(getBeginGroupNote().getNote());
		return scoreNotelist;
	}

	@Override
	public boolean hasChild() {
		return childFormerGroup != null && childLatterGroup != null;
	}

	public final boolean hasChildFormer() {
		return childFormerGroup != null;
	}

	public final boolean hasChildLatter() {
		return childLatterGroup != null;
	}

	/**
	 * @param g
	 */
	private void setChildFormer(MXGroup g) {
		childFormerGroup = g;
		if (g != null) {
			childFormerGroup.setParent(this);
			setBeginGroupNote(g.getBeginGroupNote());
		}
	}

	/**
	 * @param g
	 */
	private void setChildLatter(MXGroup g) {
		childLatterGroup = g;
		if (g != null) {
			childLatterGroup.setParent(this);
			setEndGroupNote(g.getEndGroupNote());
		}
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = name() + ";" + getPartNumber() + ";";
		if (!hasChild())
			return str + notelistToString();
		str += (hasChildFormer()) ? getChildFormerGroup().name() : "null";
		str += ",";
		str += (hasChildLatter()) ? getChildLatterGroup().name() : "null";
		return str;
	}

	public int timeValue() {
		int len = 0;
		if (hasChild()) {
			len += timevalue(getChildFormerGroup().getBeginGroupNote());
			len += timevalue(getChildLatterGroup().getBeginGroupNote());
		} else
			len += timevalue(getBeginGroupNote());

		return len;
	}

	public void setChild(MXGroup g1, MXGroup g2) {
		setChildFormer(g1);
		setChildLatter(g2);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Group#getParent()
	 */
	@Override
	public MXGroup getParent() {
		return (MXGroup) super.getParent();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Group#printInfo()
	 */
	@Override
	public String printInfo() {
		return String.format("Group %s\n\t%s\n\t%s\n\t%s\n", name(),
				getDynamicsCurve(), getTempoCurve(), getArticulationCurve());
	}
}
