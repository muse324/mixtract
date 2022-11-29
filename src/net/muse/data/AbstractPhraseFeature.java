/**
 *
 */
package net.muse.data;

import net.muse.misc.MuseObject;

/**
 * <h1>AbstractPhraseFeature</h1>
 * <p>
 * 旋律の特徴情報を格納するクラスです。
 * <p>
 * 細部の機能については、独自にサブクラスを定義、実装してください。
 * 実装例として{@link net.net.muse.mixtract.data.PhraseFeature}を参照してください。
 *
 * @author Mitsuyo Hashida @ Soai University
 *         <address>https://m-use.net/</address>
 *         <address>hashida@m-use.net</address>
 * @since 2018/1/5
 */
public class AbstractPhraseFeature extends MuseObject {

	protected final Group group;

	public AbstractPhraseFeature(Group group) {
		super();
		this.group = group;
	}

	public Group group() {
		return group;
	}

}
