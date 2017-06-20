package net.muse.gui;

import net.muse.mixtract.data.TuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;

/**
 * <h1>TuneDataListener</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>@ CrestMuse Project, JST</address>
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/10/25
 */
public interface TuneDataListener extends GroupEditListener<GroupLabel>{

	/**
	 * 楽曲を読み込み，内部データに変換します．
	 *
	 * @param target 楽曲データ
	 */
	void setTarget(TuneData target);

	/**
	 * 演奏表情を変更します。
	 *
	 * @param type 変更対象となる演奏表情タイプ
	 * @see {@link PhraseCurveType#DYNAMICS}
	 * @see {@link PhraseCurveType#TEMPO}
	 */
	void changeExpression(PhraseCurveType type);

}
