package net.muse.data;

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

public interface TuneDataController {
	/**
	 * inputFilename で指定された楽曲ファイルを読み込みます。
	 * <p>
	 * まず、isOriginalFileFormat()で各アプリケーションの独自形式のものであるかどうかを判別します。
	 * 独自形式なら、readOriginalFile()で読込処理を行います。各アプリケーションで実装してください。
	 * また、MuseAppシリーズでは、CrestMuseXML(CMX)形式もサポートしています。
	 * inputFileがCMX形式であれば、cmxデータとして読み込まれます。
	 * CMX形式のうち
	 * <ul>
	 * <li>MusicXML(*.xml)
	 * <li>DeviationInstanceXML形式(*.xml)
	 * </ul>
	 * であれば、xml, dev 変数にそれぞれ格納します。
	 * <ul>
	 * <li>Mixtractオリジナル形式(*.mxt: Mixtract.projectFileExtension()により規定)
	 * </ul>
	 * の3点です。
	 * MusicXML, DeviationInstanceXML形式が指定された場合，
	 * オリジナル形式への変換を行います。
	 * <ul>
	 * <li>独自形式 - readOriginalFile()
	 * <li>CreatMuseXML形式 -readCMXFile()
	 * <ul>
	 * <li>MusicXML(*.xml)
	 * <li>DeviationInstanceXML形式(*.xml)
	 * </ul>
	 * </ul>
	 *
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public void readfile() throws IOException;

	/**
	 * プロジェクト用ファイルを出力するメソッドです。
	 * 具体的な処理については下層の各アプリケーションクラスにて実装してください。
	 * {@link TuneData}クラスでは、Standard MIDI ファイル(SMF)用並びに楽譜情報ファイル用の
	 * 出力メソッドが用意されています。
	 *
	 * @see {@link TuneData.writeSMF(), TuneData.writeScoreData()}
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public void writefile() throws IOException;

	/**
	 * アプリケーションの独自ファイルを出力します．
	 * 具体的な処理については下層の各アプリケーションクラスにて実装してください。
	 * なお，MIDIファイルについては {@link writeSMF()} を呼び出すことで生成可能です．
	 * {@link TuneData.writeOriginalData()}メソッドに記述例があります。
	 *
	 * @throws IOException
	 */
	void writeOriginalData() throws IOException;

	/**
	 * アプリケーション独自の楽譜ファイルを出力するメソッドです。
	 * 具体的な処理については下層の各アプリケーションクラスにて実装してください。
	 *
	 * @throws IOException
	 */
	void writeScoreData() throws IOException;

	/**
	 * MIDIファイルを出力するメソッドです。具体的な処理については下層の各アプリケーションクラスにて実装してください。
	 * {@link TuneData.writeSMF()}メソッドに記述例があります。
	 *
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	void writeSMF() throws IOException;
}