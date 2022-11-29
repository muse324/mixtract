package net.muse.misc;

import java.util.List;
import java.util.zip.DataFormatException;

/**
 * グループ構造を格納するクラスに必要なメソッド類を集めたインターフェースです．
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020), JAPAN
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2007.9.21
 * @version 0.1
 */
public abstract class AbstractNoteGroup<N> {
	protected int id;

	protected N st;

	protected N end;

	protected N top;

	protected int voice;

	protected int level;

	protected String name;

	protected int part;

	protected List<N> notelist;

	/**
	 * 頂点音を推定します．
	 * @param musicdata
	 */
	public abstract void estimateTopNote(Object data);

	/**
	 * 特定のグループの頂点音を推定します．
	 * @param musicdata
	 * @throws DataFormatException
	 */
	public abstract void estimateTopNote(AbstractNoteGroup<N> group, Object map)
			throws DataFormatException;

	/** グループの終了音を返します． */
	public final N getEndNote() {
		return end;
	}

	/** グループの階層レベルを取得します． */
	public final int getLevel() {
		return level;
	}

	/** グループのIDを返します． */
	public final String getName() {
		return name;
	}

	/** グループを構成する音符列を返します． */
	public final List<N> getNotelist() {
		return notelist;
	}

	/** グループの声部を返します． */
	public final int getPart() {
		return part;
	}

	/** グループの開始音を返します． */
	public final N getStartNote() {
		return st;
	}

	/** グループの頂点音を返します． */
	public final N getTopNote() {
		return top;
	}

	/** グループの声部番号を返します． */
	public final int getVoice() {
		return voice;
	}

	/** グループに頂点音が付与されていれば真を返します． */
	public final boolean hasTopNote() {
		return (top != null);
	}

	/** グループの終了音を代入します． */
	public void setEndNote(N note) {
		end = note;
		System.out.println("Create a new Group:\n   " + this);
	}

	/** グループの階層レベルを代入します． */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param name
	 *        設定する name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param notelist
	 *        設定する notelist
	 */
	public void setNotelist(List<N> notelist) {
		this.notelist = notelist;
	}

	public void setPart(int part) {
		this.part = part;
	}

	/** グループの頂点音を代入します． */
	public void setTopNote(N note) {
		top = note;
	}

	/**
	 * @param v
	 */
	public void setVoice(int v) {
		this.voice = v;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String str = name + ": (" + st + ", ";
		if (top != null)
			str += top + ", ";
		else
			str += "null, ";
		str += end + ")";
		return str;
	}

}
