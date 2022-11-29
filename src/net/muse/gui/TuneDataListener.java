package net.muse.gui;

import net.muse.data.TuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;

/**
 * <h1>TuneDataListener</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>@ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)</address>
 *         <address><a href="http://mixtract.m-use.net/"
 *         >http://mixtract.m-use.net</a></address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/10/25
 */
public interface TuneDataListener extends GroupEditListener<GroupLabel> {

	/**
	 * 楽曲を読み込み，内部データに変換します．
	 *
	 * @param data 楽曲データ
	 */
	void setTarget(TuneData data);

	/**
	 * 演奏表情を変更します。
	 *
	 * @param type 変更対象となる演奏表情タイプ
	 * @see {@link PhraseCurveType#DYNAMICS}
	 * @see {@link PhraseCurveType#TEMPO}
	 */
	void changeExpression(PhraseCurveType type);
}
