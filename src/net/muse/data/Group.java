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
 *         <address>@ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)</address>
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
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
	/** TODO 具体的にどう使ってるか確認する */
	protected final ArrayList<NoteData> scoreNotelist = new ArrayList<>();
	/** 開始音 */
	private NoteData beginNote = null;
	/** 終了音 */
	private NoteData endNote = null;
	/** 頂点音 */
	private NoteData topNote = null;
	/** フレーズ（グループ）の詳細情報を格納します。 */
	private PhraseFeature detail;

	/**
	 * @param beginNote
	 * @param endNote
	 */
	public Group(NoteData beginNote, NoteData endNote, GroupType type) {
		this(type);
		this.partNumber = beginNote.musePhony();
		this.setBeginNote(beginNote);
		this.setEndNote(endNote);
	}

	protected Group(GroupType type) {
		_type = type;
		initialize();
	}

	/*
	 * //追加
	 * public Group(NoteData beginNote, NoteData endNote, GroupType type,
	 * NoteData topNote) {
	 * this(type);
	 * this.partNumber = beginNote.musePhony();
	 * this.setBeginNote(beginNote);
	 * this.setEndNote(endNote);
	 * this.setTopNote(topNote);
	 * }
	 */
	/**
	 * プロジェクトファイルから読み込んだグループを生成します．
	 *
	 * @param id
	 * @param partNumber
	 * @param beginNote
	 * @param type
	 */
	protected Group(int id, int partNumber, NoteData beginNote,
			GroupType type) {
		this(type);
		index = id;
		this.partNumber = partNumber;
		setBeginNote(beginNote);
		while (beginNote.hasNext()) {
			setEndNote(beginNote.next());
			beginNote = beginNote.next();
		}
	}

	/**
	 * MusicXMLを読み込んで，声部ごとのグループを生成します．
	 *
	 * @param note
	 * @param partIndex
	 * @param type
	 */
	protected Group(NoteData note, int partIndex, GroupType type) {
		this(type);
		this.setBeginNote(note);
		this.partNumber = partIndex;
		setNotelist(note.child(), note);
		setNotelist(note.next(), note);
		comfirmLastRestnotesFromGroup();
	}

	/**
	 * @param INCLUDE_LAST_RESTNOTE セットする INCLUDE_LAST_RESTNOTE
	 */
	public static void setAvoidLastRestsFromGroup(boolean aVOID_LAST_RESTNOTE) {
		INCLUDE_LAST_RESTNOTE = aVOID_LAST_RESTNOTE;
	}

	public void addScoreNoteList() {
		if (hasChild())
			addScoreNoteList(child().getBeginNote());
		else
			addScoreNoteList(getBeginNote());
	}

	@Override public Group child() {
		return (Group) super.child();
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
		final Group g = (Group) obj;
		if (!g.getBeginNote().equals(getBeginNote()) || !g.getEndNote().equals(
				getEndNote()) || !g.getType().equals(getType()))
			// || !g.getType().equals(getType()) || !g.getLayer().equals(layer))
			return false;
		return true;
	}

	/**
	 * @return beginNote
	 */
	public NoteData getBeginNote() {
		return beginNote;
	}

	/**
	 * @return endNote
	 */
	public NoteData getEndNote() {
		return endNote;
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

	public int getPartNumber() {
		return partNumber;
	}

	/**
	 * @return cur
	 */
	public List<NoteData> getScoreNotelist() {
		// scoreNotelist.clear();
		// addScoreNoteList();
		return scoreNotelist;
	}

	/**
	 * @return
	 */
	public int getTimeValue() {
		return offsetInTicks() - onsetInTicks();
	}

	/**
	 * @return topNote
	 */
	public NoteData getTopNote() {
		return topNote;
	}

	/**
	 * @return _type
	 */
	public final GroupType getType() {
		return _type;
	}

	public final boolean hasTopNote() {
		return getTopNote() != null;
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
		if (!g.getBeginNote().equals(getBeginNote()) || !g.getEndNote().equals(
				getEndNote()))
			return false;
		return true;
	}

	public int offsetInTicks() {
		return getEndNote().offset();
	}

	public int onsetInTicks() {
		return getBeginNote().onset();
	}

	public String printInfo() {
		return String.format("Group %s\n", name());
	}

	/**
	 * @return
	 */
	public double realOnset() {
		return getBeginNote().realOnset();
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

	public void setPartNumber(int partNumber) {
		this.partNumber = partNumber;
	}

	// 追加
	/**
	 *
	 * @param setNote セットする setNote
	 */
	public void setTopNote(NoteData note) {
		this.topNote = note;
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
			len += timevalue(child().getBeginNote());
		} else
			len += timevalue(getBeginNote());

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
		str += hasChild() ? child().name() : "null";
		return str;
	}

	protected void addScoreNoteList(NoteData list) {
		if (list == null)
			return;
		scoreNotelist.add(list);
		addScoreNoteList(list.next());
	}

	protected void initialize() {
		this.getScoreNotelist().clear();
	}

	protected void makeScoreNotelist(NoteData root) {
		if (root == null)
			return;
		if (root.onset() > getEndNote().onset())
			return;
		if (root.onset() >= getBeginNote().onset())
			scoreNotelist.add(root);
		makeScoreNotelist(root.next());
	}

	/**
	 * @return
	 */
	protected String notelistToString() {
		return notelistName("[", getBeginNote()) + "]";
	}

	/**
	 * @param beginNote セットする beginNote
	 */
	protected void setBeginNote(NoteData note) {
		this.beginNote = note;
	}

	/**
	 * @param endNote セットする endNote
	 */
	protected void setEndNote(NoteData note) {
		this.endNote = note;
	}

	protected int timevalue(NoteData note) {
		if (note == null)
			return 0;
		int len = 0;
		while (note != null) {
			len += note.timeValue();
			note = note.next();
		}
		return len;
	}

	/**
	 * グループ最後尾において、休符がグループに含まれているかをチェックします。
	 * INCLUDE_LAST_RESTNOTE が false の場合にのみ実行されます。
	 */
	private void comfirmLastRestnotesFromGroup() {
		if (INCLUDE_LAST_RESTNOTE)
			return;
		while (getEndNote().rest()) {
			setEndNote(getEndNote().previous());
			getEndNote().setNext(null);
		}
	}

	private String notelistName(String str, NoteData note) {
		if (note == null)
			return str;
		if (note.offset() > getEndNote().offset())
			return str;
		if (note.onset() < getBeginNote().onset())
			return notelistName(str, note.next());
		if (str.length() > 1)
			str += ",";
		str += " " + note.id() + " ";
		if (note.hasChild())
			str += notelistName("(", note.child()) + ") ";
		return notelistName(str, note.next());
	}

	private double realOffset() {
		return getEndNote().realOffset();
	}

	private void setNotelist(NoteData note, NoteData root) {
		if (note == null) {
			setEndNote(root);
			return;
		}
		if (note.hasParent()) {
			root.setChild(note);
			root = root.child();
		} else {
			root.setNext(note);
			root = root.next();
		}
		setNotelist(note.child(), root);
		setNotelist(note.next(), root);
	}

}
