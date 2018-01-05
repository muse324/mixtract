package net.muse.data;

import java.util.ArrayList;
import java.util.List;

import net.muse.mixtract.data.PhraseFeature;

/**
 * <h1>Group</h1>
 * <p>
 * Group class describes information of a phrase.
 * <p>
 * A group consists of the following definition:
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
	/** グループ最後尾において、休符をグループに含めるかどうか設定します。 */
	private static boolean INCLUDE_LAST_RESTNOTE = true;

	/** グループの種類 */
	private GroupType _type;
	/** グループの通し番号． */
	private int index;

	/** 階層レベル．最上階層(楽曲全体)を0として，下位構造に向かって正の整数で表されます． */
	private int level;
	/** 声部番号（1〜） */
	private int partNumber;
	/** このグループに含まれる音符列 */
	private GroupNote notelist = null;

	/** TODO 具体的にどう使ってるか確認する */
	protected List<NoteData> scoreNotelist;
	/** 開始音 */
	protected GroupNote beginGroupNote = null;
	/** 終了音 */
	protected GroupNote endGroupNote = null;

	/** 頂点音 TODO Mixtract用にプッシュダウンする */
	private GroupNote topGroupNote = null;
	/** フレーズ（グループ）の詳細情報を格納します。 */
	private PhraseFeature detail;

	/**
	 * @param INCLUDE_LAST_RESTNOTE セットする INCLUDE_LAST_RESTNOTE
	 */
	public static void setAvoidLastRestsFromGroup(boolean aVOID_LAST_RESTNOTE) {
		INCLUDE_LAST_RESTNOTE = aVOID_LAST_RESTNOTE;
	}

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
	 * @param id
	 * @param partNumber
	 * @param list
	 * @param type
	 */
	protected Group(int id, int partNumber, GroupNote list, GroupType type) {
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
	protected Group(NoteData notelist, int partIndex, GroupType type) {
		this(type);
		this.notelist = new GroupNote(notelist);
		this.beginGroupNote = this.notelist;
		this.partNumber = partIndex;
		setNotelist(notelist.child(), this.notelist);
		setNotelist(notelist.next(), this.notelist);
		comfirmLastRestnotesFromGroup();
	}

	protected Group(GroupType type) {
		_type = type;
		initialize();
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
	@Override
	public boolean equals(Object obj) {
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
	 * @return beginGroupNote
	 */
	public GroupNote getBeginGroupNote() {
		return beginGroupNote;
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
		return detail;
	}

	/**
	 * @return the parent
	 */
	public Group getParent() {
		return (Group) super.parent();
	}

	@Override
	public Group child() {
		return (Group) super.child();
	}

	/**
	 * @return cur
	 */
	public List<? extends NoteData> getScoreNotelist() {
		if (scoreNotelist == null)
			createScoreNoteList();
		if (hasChild()) {
			scoreNotelist.clear();
			addScoreNoteList(child().getScoreNotelist());
		} else if (scoreNotelist.size() <= 1)
			makeScoreNotelist(getBeginGroupNote().getNote());
		return scoreNotelist;
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

	public void setScoreNotelist(List<? extends NoteData> list) {
		scoreNotelist.clear();
		addScoreNoteList(list);
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
			len += timevalue(child().getBeginGroupNote());
		} else
			len += timevalue(getBeginGroupNote());

		return len;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = name() + ";" + partNumber + ";";
		if (!hasChild())
			return str + notelistToString();
		str += (hasChild()) ? child().name() : "null";
		return str;
	}

	protected void addScoreNoteList(List<? extends NoteData> list) {
		for (NoteData n : list)
			scoreNotelist.add(n);
	}

	protected void createScoreNoteList() {
		this.scoreNotelist = new ArrayList<NoteData>();
	}

	protected void initialize() {
		createScoreNoteList();
	}

	protected void makeScoreNotelist(NoteData root) {
		if (root == null)
			return;
		if (root.onset() > getEndGroupNote().getNote().onset())
			return;
		if (root.onset() >= getBeginGroupNote().getNote().onset())
			scoreNotelist.add(root);
		makeScoreNotelist(root.next());
	}

	/**
	 * グループ最後尾において、休符がグループに含まれているかをチェックします。
	 * INCLUDE_LAST_RESTNOTE が false の場合にのみ実行されます。
	 */
	private void comfirmLastRestnotesFromGroup() {
		if (INCLUDE_LAST_RESTNOTE)
			return;
		while (endGroupNote.getNote().rest()) {
			endGroupNote = endGroupNote.previous();
			endGroupNote.setNext(null);
		}
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
	protected String notelistToString() {
		return notelistName("[", beginGroupNote) + "]";
	}

	private double realOffset() {
		return getEndGroupNote().getNote().realOffset();
	}

	/**
	 * @param beginGroupNote セットする beginGroupNote
	 */
	protected void setBeginGroupNote(GroupNote beginGroupNote) {
		this.beginGroupNote = beginGroupNote;
	}

	/**
	 * @param endGroupNote セットする endGroupNote
	 */
	protected void setEndGroupNote(GroupNote endGroupNote) {
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

	protected int timevalue(GroupNote gnote) {
		if (gnote == null)
			return 0;
		int len = 0;
		while (gnote != null) {
			len += gnote.getNote().timeValue();
			gnote = gnote.next();
		}
		return len;
	}

	public int getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}
}
