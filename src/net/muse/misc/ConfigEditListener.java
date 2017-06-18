package net.muse.misc;

/**
 * 環境設定に関するイベントを受け取るためのリスナーインタフェースです．
 * <p>
 * 環境設定に関連するクラスは、このインタフェースを実装します。さらに、それらのクラスによって作成されたオブジェクトは、コンポーネントの
 * addConfigEditListener メソッドを使用することによってコンポーネントに登録されます．
 * イベントが発生すると、イベント内容に合わせて，オブジェクトの editConfig メソッドが呼び出されます。
 * 
 * @author Mitsuyo Hashida
 * @since 2007.9.20
 */
public interface ConfigEditListener {
	/** 設定が変更されたときに呼び出されるメソッドです． */
	public void editConfig(String key, String value);

}
