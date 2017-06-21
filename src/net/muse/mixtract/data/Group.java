package net.muse.mixtract.data;

import java.util.ArrayList;
import java.util.List;

import net.muse.data.SequenceData;
import net.muse.mixtract.data.curve.*;

/**
 * <h1>Group</h1>
 * <p>
 * Group class describes the information to construct a phrase.
 * <p>
 * A group is consist of the following definition:
 * <ol>
 * <li>A group has <i>childFormerGroup</i> & <i>childLatterGroup</i>, or
 * <li>A group has a note sequence
 * </ol>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>@ CrestMuse Project, JST</address>
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/10/23
 */
public class Group extends SequenceData {

	private static boolean AVOID_LAST_RESTNOTE = true;

	/**
	 * @param aVOID_LAST_RESTNOTE セットする aVOID_LAST_RESTNOTE
	 */
	public static void setAvoidLastRestsFromGroup(boolean aVOID_LAST_RESTNOTE) {
		AVOID_LAST_RESTNOTE = aVOID_LAST_RESTNOTE;
	}

	/** グループの種類 */
	private GroupType _type;
	/** グループの通し番号． */
	private int index;

	/** 階層レベル．最上階層(楽曲全体)を0として，下位構造に向かって正の整数で表されます． */
	private int level;
	private int partNumber;

	private Group childFormerGroup = null;
	private Group childLatterGroup = null;
	private Group parent = null;

	private GroupNote notelist = null;
	private List<NoteData> scoreNotelist;
	private GroupNote beginGroupNote = null;
	private GroupNote topGroupNote = null;
	private GroupNote endGroupNote = null;
	private GroupNote centerNote;

	private DynamicsCurve dynamicsCurve;
	private TempoCurve tempoCurve;
	private ArticulationCurve articulationCurve;

	private PhraseFeature flag;

	/**
	 * @param groupNoteList
	 * @param endNote
	 */
	public Group(GroupNote groupNoteList, GroupNote endNote, GroupType type) {
		this(type);
		this.partNumber = groupNoteList.getNote().partNumber();
		this.beginGroupNote = groupNoteList;
		this.endGroupNote = endNote;
	}

	/**
	 * プロジェクトファイルから読み込んだグループを生成します．
	 *
	 * @param g1
	 * @param g2
	 * @param name
	 * @param partNumber
	 */
	Group(Group g1, Group g2, String name, int partNumber) {
		this(GroupType.is(name.charAt(0)));
		index = Integer.parseInt(name.substring(1));
		this.partNumber = partNumber;
		beginGroupNote = g1.getBeginGroupNote();
		endGroupNote = g2.getEndGroupNote();
		setChild(g1, g2);
	}

	/**
	 * プロジェクトファイルから読み込んだグループを生成します．
	 *
	 * @param id
	 * @param partNumber
	 * @param list
	 * @param type
	 */
	Group(int id, int partNumber, GroupNote list, GroupType type) {
		this(type);
		index = id;
		this.partNumber = partNumber;
		notelist = list;
		beginGroupNote = list;
		while (list.hasNext()) {
			endGroupNote = list.next();
			list = list.next();
		}
	}

	/**
	 * MusicXMLを読み込んで，声部ごとのグループを生成します．
	 *
	 * @param notelist
	 * @param partIndex
	 * @param type
	 */
	Group(NoteData notelist, int partIndex, GroupType type) {
		this(type);
		this.notelist = new GroupNote(notelist);
		this.beginGroupNote = this.notelist;
		this.partNumber = partIndex;
		setNotelist(notelist.child(), this.notelist);
		setNotelist(notelist.next(), this.notelist);
		avoidLastRestnotesFromGroup();
	}

	private Group(GroupType type) {
		_type = type;
		dynamicsCurve = (DynamicsCurve) PhraseCurve.createPhraseProfile(
				PhraseCurveType.DYNAMICS);
		tempoCurve = (TempoCurve) PhraseCurve.createPhraseProfile(
				PhraseCurveType.TEMPO);
		articulationCurve = (ArticulationCurve) PhraseCurve.createPhraseProfile(
				PhraseCurveType.ARTICULATION);
		scoreNotelist = new ArrayList<NoteData>();
	}

	/**
	 * 対象グループの実時間所要時間を返します。
	 */
	public double duration() {
		return realOffset() - realOnset();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override public boolean equals(Object obj) {
		if (obj == null)
			return false;
		Group g = (Group) obj;
		if (!g.getBeginGroupNote().equals(getBeginGroupNote()) || !g
				.getEndGroupNote().equals(getEndGroupNote()) || !g.getType()
						.equals(getType()))
			// || !g.getType().equals(getType()) || !g.getLayer().equals(layer))
			return false;
		return true;
	}

	/**
	 * 頂点らしさを算出します。
	 */
	public void extractApex() {
		List<NoteData> nlist = getScoreNotelist();
		// score clear
		for (NoteData n : nlist) {
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
			ApexInfo.applyRule(h.rule(), nlist, 0, sz);
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

	/**
	 * @return articulationCurve
	 */
	public final ArticulationCurve getArticulationCurve() {
		return articulationCurve;
	}

	/**
	 * @return beginGroupNote
	 */
	public GroupNote getBeginGroupNote() {
		return beginGroupNote;
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

	public Group getChildFormerGroup() {
		return childFormerGroup;
	}

	public Group getChildLatterGroup() {
		return childLatterGroup;
	}

	/**
	 * @return dynamicsCurve
	 */
	public DynamicsCurve getDynamicsCurve() {
		return dynamicsCurve;
	}

	/**
	 * @return endGroupNote
	 */
	public GroupNote getEndGroupNote() {
		return endGroupNote;
	}

	public int getLevel() {
		return level;
	}

	public PhraseFeature getMelodyFlagment() {
		return flag;
	}

	/**
	 * @return the parent
	 */
	public final Group getParent() {
		return parent;
	}

	/**
	 * @return cur
	 */
	public final List<NoteData> getScoreNotelist() {
		if (hasChild()) {
			scoreNotelist.clear();
			scoreNotelist.addAll(getChildFormerGroup().getScoreNotelist());
			scoreNotelist.addAll(getChildLatterGroup().getScoreNotelist());
		} else if (scoreNotelist.size() <= 1)
			makeScoreNotelist(getBeginGroupNote().getNote());
		return scoreNotelist;
	}

	/**
	 * @return tempoCurve
	 */
	public TempoCurve getTempoCurve() {
		return tempoCurve;
	}

	/**
	 * @return
	 */
	public int getTimeValue() {
		return offsetInTicks() - onsetInTicks();
	}

	/**
	 * @return topGroupNote
	 */
	public GroupNote getTopGroupNote() {
		return topGroupNote;
	}

	/**
	 * @return _type
	 */
	public final GroupType getType() {
		return _type;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.SequenceData#hasChild()
	 */
	@Override public boolean hasChild() {
		return childFormerGroup != null && childLatterGroup != null;
	}

	public final boolean hasChildFormer() {
		return childFormerGroup != null;
	}

	public final boolean hasChildLatter() {
		return childLatterGroup != null;
	}

	public final boolean hasPhraseCurve() {
		return dynamicsCurve != null && tempoCurve != null
				&& articulationCurve != null;
	}

	public final boolean hasTopNote() {
		return topGroupNote != null;
	}

	/**
	 * @return the index
	 */
	public final int index() {
		return index;
	}

	public String name() {
		return _type.name().charAt(0) + String.valueOf(index);
	}

	/**
	 * @param g
	 * @return
	 */
	public boolean nearlyEquals(Group g) {
		if (!g.getBeginGroupNote().equals(beginGroupNote) || !g
				.getEndGroupNote().equals(getEndGroupNote()))
			return false;
		return true;
	}

	public int offsetInTicks() {
		return endGroupNote.getNote().offset();
	}

	public int onsetInTicks() {
		return beginGroupNote.getNote().onset();
	}

	/**
	 * @return
	 */
	public double realOnset() {
		return getBeginGroupNote().getNote().realOnset();
	}

	public void setChild(Group g1, Group g2) {
		setChildFormer(g1);
		setChildLatter(g2);
	}

	/**
	 * @param b
	 */
	public void setHierarchy(boolean b) {}

	/**
	 * @param i
	 */
	public void setIndex(int i) {
		index = i;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param scoreNotelist the scoreNotelist to set
	 */
	public final void setScoreNotelist(List<NoteData> scoreNotelist) {
		this.scoreNotelist.clear();
		this.scoreNotelist.addAll(scoreNotelist);
	}

	/**
	 * @param note
	 */
	public void setType(GroupType type) {
		_type = type;
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

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		String str = name() + ";" + partNumber + ";";
		if (!hasChild())
			return str + notelistToString();
		str += (hasChildFormer()) ? getChildFormerGroup().name() : "null";
		str += ",";
		str += (hasChildLatter()) ? getChildLatterGroup().name() : "null";
		return str;
	}

	/**
	 *
	 */
	private void avoidLastRestnotesFromGroup() {
		if (!AVOID_LAST_RESTNOTE)
			return;
		while (endGroupNote.getNote().rest()) {
			endGroupNote = endGroupNote.previous();
			endGroupNote.setNext(null);
		}
	}

	private void makeScoreNotelist(NoteData root) {
		// if (root == null) { // 実時間計算(仮)
		// createDefaultRealtimeNotelist();
		// return;
		// }
		if (root == null)
			return;
		if (root.onset() > getEndGroupNote().getNote().onset())
			return;
		if (root.onset() >= getBeginGroupNote().getNote().onset())
			scoreNotelist.add(root);
		makeScoreNotelist(root.next());
	}

	private String notelistName(String str, GroupNote note) {
		if (note == null)
			return str + ") ";
		if (str.length() > 1)
			str += ",";
		str += " " + note.id() + " ";
		if (note.hasChild())
			str += notelistName("(", note.child());
		return notelistName(str, note.next());
	}

	/**
	 * @return
	 */
	private String notelistToString() {
		return notelistName("[", beginGroupNote) + "]";
	}

	private double realOffset() {
		return getEndGroupNote().getNote().realOffset();
	}

	private void searchCenterGroupNote(int targetTime, GroupNote note) {
		if (note == null)
			return;
		if (note.getNote().onset() >= targetTime)
			return;
		centerNote = note;
		searchCenterGroupNote(targetTime, note.next());
	}

	/**
	 * @param beginGroupNote セットする beginGroupNote
	 */
	private void setBeginGroupNote(GroupNote beginGroupNote) {
		this.beginGroupNote = beginGroupNote;
	}

	/**
	 * @param g
	 */
	private void setChildFormer(Group g) {
		childFormerGroup = g;
		if (g != null) {
			childFormerGroup.setParent(this);
			setBeginGroupNote(g.getBeginGroupNote());
		}
	}

	/**
	 * @param g
	 */
	private void setChildLatter(Group g) {
		childLatterGroup = g;
		if (g != null) {
			childLatterGroup.setParent(this);
			setEndGroupNote(g.getEndGroupNote());
		}
	}

	/**
	 * @param endGroupNote セットする endGroupNote
	 */
	private void setEndGroupNote(GroupNote endGroupNote) {
		this.endGroupNote = endGroupNote;
	}

	private void setNotelist(NoteData note, GroupNote notelist) {
		if (note == null) {
			endGroupNote = notelist;
			return;
		}
		GroupNote n = new GroupNote(note);
		if (note.hasParent()) {
			notelist.setChild(n);
			notelist = notelist.child();
		} else {
			notelist.setNext(n);
			notelist = notelist.next();
		}
		setNotelist(note.child(), notelist);
		setNotelist(note.next(), notelist);
	}

	private void setParent(Group g) {
		parent = g;
	}

	private int timevalue(GroupNote gnote) {
		if (gnote == null)
			return 0;
		int len = 0;
		while (gnote != null) {
			len += gnote.getNote().timeValue();
			gnote = gnote.next();
		}
		return len;
	}
}
