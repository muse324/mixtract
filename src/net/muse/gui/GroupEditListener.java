package net.muse.gui;

import java.util.List;

import javax.swing.JLabel;

import net.muse.misc.MuseObject;

/**
 * GUI上で、グループ編集に関するイベントを受け取るためのリスナーインタフェースです。
 * グループ編集処理が関わるクラスにこのインタフェースを実装してください。
 * それらのクラスによって生成されたオブジェクトは、別に用意する制御系クラス内で
 * addGroupEditListenerメソッドを呼び出し、一覧に登録しておきます。
 * <p>
 * イベントが発生すると、イベント内容に合わせて，オブジェクトの selectGroup,
 * addGroup, editGroup, deleteGroup メソッドが呼び出されます。
 *
 * @author Mitsuyo Hashida
 * @since 2007.9.6
 */
public interface GroupEditListener<L extends JLabel, G extends MuseObject> {

	/**
	 * グループ全体が選択されたときに呼び出されるメソッドです．
	 *
	 * @param g
	 *            選択されたグループラベル
	 */
	public void selectGroup(L g);

	/**
	 * グループが追加されたときに呼び出されるメソッドです．
	 *
	 * @param groupList -
	 *            追加されるグループリスト
	 */
	public void addGroup(List<G> groupList);

	/**
	 * グループが変更されたときに呼び出されるメソッドです。
	 *
	 * @param g - 変更対象のGroupオブジェクト
	 */
	public void editGroup(L g);

	/**
	 * グループが削除されたときに呼び出されるメソッドです。
	 *
	 * @param g - 削除対象のGroupオブジェクト
	 */
	public void deleteGroup(L g);

}
