package net.muse.misc;

import java.util.List;

import javax.swing.JLabel;

/**
 * グループ編集に関するイベントを受け取るためのリスナーインタフェースです． グループ編集処理に関連するクラスは、このインタフェースを実装します。
 * さらに、それらのクラスによって作成されたオブジェクトは、コンポーネントの addGroupEditListener
 * メソッドを使用することによってコンポーネントに登録されます． イベントが発生すると、イベント内容に合わせて，オブジェクトの selectGroup,
 * addGroup, editGroup, deleteGroup メソッドが呼び出されます。
 * @author Mitsuyo Hashida
 * @since 2007.9.6
 */
public interface GroupEditListener<L extends JLabel, G extends MuseObject> {

	/**
	 * グループ全体が選択されたときに呼び出されるメソッドです．
	 * @param g
	 *        選択されたグループラベル
	 */
	public void selectGroup(L g);

	/**
	 * グループが追加されたときに呼び出されるメソッドです．
	 * @param groupList
	 *        追加されるグループリスト
	 */
	public void addGroup(List<G> groupList);

	/** グループが変更されたときに呼び出されるメソッドです． */
	public void editGroup(L g);

	/** グループが削除されたときに呼び出されるメソッドです． */
	public void deleteGroup(L g);

}
