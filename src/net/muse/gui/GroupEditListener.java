package net.muse.gui;

import javax.swing.JLabel;

import net.muse.data.Group;
import net.muse.data.NoteData;

/**
 * グループ編集に関するイベントを受け取るためのリスナーインタフェースです。
 * <p>
 * グループ編集処理に関連するクラスは、このインタフェースを実装します。 さらに、それらのクラスによって作成されたオブジェクトは、コンポーネントの
 * addGroupEditListener メソッドを使用することによってコンポーネントに登録されます．
 * イベントが発生すると、イベント内容に合わせて，オブジェクトの selectGroup, addGroup, editGroup, deleteGroup
 * メソッドが呼び出されます。
 *
 * @author Mitsuyo Hashida
 * @since 2007.9.6
 */
public interface GroupEditListener<L extends JLabel> {

	/**
	 * グループが追加されたときに呼び出されるメソッドです．
	 *
	 * @param g
	 *            追加されるグループ
	 */
	public void addGroup(Group g);

	/** グループが削除されたときに呼び出されるメソッドです． */
	public void deleteGroup(L g);

	/** グループが変更されたときに呼び出されるメソッドです． */
	public void editGroup(L g);

	/**
	 * グループ全体が選択されたときに呼び出されるメソッドです．
	 *
	 * @param g
	 *            選択されたグループラベル
	 * @param flg
	 *            選択時：true, 選択解除時：false
	 */
	public void selectGroup(L g, boolean flg);

	/**
	 * グループの選択状態が解除されたときに呼び出されるメソッドです．
	 */
	public void deselect(L g);

	/**
	 * グループの頂点音が選択された時に呼び出されるメソッドです。
	 *
	 * @param scoreNote
	 * @param b
	 */
	public void selectTopNote(NoteData note, boolean b);
}
