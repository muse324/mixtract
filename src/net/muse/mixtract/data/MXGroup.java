package net.muse.mixtract.data;

import java.util.ArrayList;

import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.Harmony;
import net.muse.data.NoteData;
import net.muse.mixtract.data.curve.ArticulationCurve;
import net.muse.mixtract.data.curve.DynamicsCurve;
import net.muse.mixtract.data.curve.PhraseCurve;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.data.curve.TempoCurve;

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
	private NoteData centerNote;
	private MXGroup childFormerGroup = null;
	private MXGroup childLatterGroup = null;

	/**
	 * @param beginNote
	 * @param endNote
	 * @param type
	 */
	public MXGroup(NoteData beginNote, NoteData endNote, GroupType type) {
		super(beginNote, endNote, type);
	}

	/**
	 * @param id
	 * @param partNumber
	 * @param note
	 * @param type
	 */
	MXGroup(int id, int partNumber, NoteData note, GroupType type) {
		super(id, partNumber, note, type);
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
		setBeginNote(g1.getBeginNote());
		setEndNote(g2.getEndNote());
		setChild(g1, g2);
	}

	/**
	 * @param notelist
	 * @param partIndex
	 * @param type
	 */
	MXGroup(NoteData notelist, int partIndex, GroupType type) {
		super(notelist, partIndex, type);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Group#addScoreNoteList()
	 */
	@Override public void addScoreNoteList() {
		if (!hasChild()) {
			getScoreNotelist().clear();
			addScoreNoteList((MXNoteData) getBeginNote());
			return;
		}
		if (hasChildFormer()) {
			getChildFormerGroup().getScoreNotelist().clear();
			getChildFormerGroup().addScoreNoteList();
		}
		if (hasChildLatter()) {
			getChildLatterGroup().getScoreNotelist().clear();
			getChildLatterGroup().addScoreNoteList();
		}
	}

	/**
	 * 頂点らしさを算出します。
	 */
	public void extractApex() {
		ArrayList<MXNoteData> nlist = new ArrayList<MXNoteData>();
		extractNotes(nlist);
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

	public MXGroup getChildFormerGroup() {
		return childFormerGroup;
	}

	public MXGroup getChildLatterGroup() {
		return childLatterGroup;
	}

	/**
	 * @return dynamicsCurve
	 */
	public DynamicsCurve getDynamicsCurve() {
		return dynamicsCurve;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Group#getParent()
	 */
	@Override public MXGroup getParent() {
		return (MXGroup) super.getParent();
	}

	/**
	 * @return tempoCurve
	 */
	public TempoCurve getTempoCurve() {
		return tempoCurve;
	}

	@Override public boolean hasChild() {
		return childFormerGroup != null && childLatterGroup != null;
	}

	public final boolean hasChildFormer() {
		return childFormerGroup != null;
	}

	public final boolean hasChildLatter() {
		return childLatterGroup != null;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Group#printInfo()
	 */
	@Override public String printInfo() {
		return String.format("Group %s\n\t%s\n\t%s\n\t%s\n", name(),
				getDynamicsCurve(), getTempoCurve(), getArticulationCurve());
	}

	public void setChild(MXGroup g1, MXGroup g2) {
		setChildFormer(g1);
		setChildLatter(g2);
		if (g1 != null)
			g1.getEndNote().setNext(null);
		if (g2 != null)
			g2.getBeginNote().setPrevious(null);
	}

	public int timeValue() {
		int len = 0;
		if (!hasChild())
			return timevalue(getBeginNote());

		len += timevalue(getChildFormerGroup().getBeginNote());
		len += timevalue(getChildLatterGroup().getBeginNote());

		return len;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		String str = name() + ";" + getPartNumber() + ";";
		if (!hasChild())
			return str + notelistToString();
		str += (hasChildFormer()) ? getChildFormerGroup().name() : "null";
		str += ",";
		str += (hasChildLatter()) ? getChildLatterGroup().name() : "null";
		return str;
	}

	private void addNote(ArrayList<MXNoteData> nlist, MXNoteData n) {
		if (n == null)
			return;
		nlist.add(n);
		addNote(nlist, n.next());
	}

	private void addScoreNoteList(MXNoteData n) {
		if (n == null)
			return;
		scoreNotelist.add(n);
		addScoreNoteList(n.next());
	}

	private void extractNotes(ArrayList<MXNoteData> nlist) {
		if (!hasChild()) {
			addNote(nlist, (MXNoteData) getBeginNote());
			return;
		}
		if (hasChildFormer())
			getChildFormerGroup().extractNotes(nlist);
		if (hasChildLatter())
			getChildLatterGroup().extractNotes(nlist);
	}

	NoteData getCenterGroupNote() {
		if (centerNote == null) {
			// onset length
			int len = getEndNote().onset() - onsetInTicks();
			int targetTime = len / 2;
			searchCenterGroupNote(targetTime, getBeginNote());
			if (hasChild())
				searchCenterGroupNote(targetTime, getChildLatterGroup()
						.getBeginNote());
		}
		return centerNote;
	}

	private final boolean hasPhraseCurve() {
		return dynamicsCurve != null && tempoCurve != null
				&& articulationCurve != null;
	}

	private void searchCenterGroupNote(int targetTime, NoteData note) {
		if (note == null)
			return;
		if (note.onset() >= targetTime)
			return;
		centerNote = note;
		searchCenterGroupNote(targetTime, note.next());
	}

	/**
	 * @param g
	 */
	private void setChildFormer(MXGroup g) {
		childFormerGroup = g;
		if (g != null) {
			childFormerGroup.setParent(this);
			setBeginNote(g.getBeginNote());
		}
	}

	/**
	 * @param g
	 */
	private void setChildLatter(MXGroup g) {
		childLatterGroup = g;
		if (g != null) {
			childLatterGroup.setParent(this);
			setEndNote(g.getEndNote());
		}
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
