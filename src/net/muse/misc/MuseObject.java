package net.muse.misc;

import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import javax.swing.JFileChooser;

import net.muse.data.Concierge;

public abstract class MuseObject {

	/** デバッグモード */
	protected static boolean DEBUG = true;
	protected static final String LOG_FILENAME = "log.txt";

	private static String PROPERTY_FILENAME;

	protected final Properties config = new Properties();

	/** MIDI分解能 */
	private int _ticksPerBeat = 480;

	/** 標準のMIDIヴェロシティ値 （64） */
	private static int _defaultVelocity = 64;

	private static boolean _assertion;

	/** オフヴェロシティ値（127） */
	private static int _defaultOffVelocity = 127;

	private static int _defaultBPM;

	private PrintStream log;
	/** コンシェルジュ（ファサードクラス） **/
	private Concierge butler = null;

	public final static void errPrintln(String val) {
		if (DEBUG)
			System.err.println(val);
	}

	/**
	 * @return the _defaultBPM
	 */
	public static int getDefaultBPM() {
		return _defaultBPM;
	}

	/**
	 * @return the _defaultOffVelocity
	 */
	public static int getDefaultOffVelocity() {
		return _defaultOffVelocity;
	}

	public static int getDefaultVelocity() {
		return _defaultVelocity;
	}

	/**
	 * 設定されているMIDI分解能を取得します．
	 */
	public int getTicksPerBeat() {
		return _ticksPerBeat;
	}

	public static boolean isAssertion() {
		return _assertion;
	}

	/**
	 * デバッグモードが指定されているかどうかを判別します．
	 *
	 * @return デバッグモード（真：デバッグON）
	 */
	public static boolean isDebug() {
		return DEBUG;
	}

	/**
	 * デバッグモードのON/OFFを切り替えます．
	 *
	 * @param dbg 設定するデバッグモード
	 */
	public static void setDebugMode(boolean dbg) {
		DEBUG = dbg;
	}

	/**
	 * @param parseInt
	 */
	public static void setDefaultBPM(int bpm) {
		_defaultBPM = bpm;
	}

	/**
	 * 標準のMIDIオフヴェロシティ値を変更します．
	 *
	 * @param vel 設定するMIDIオフヴェロシティ値（整数）
	 */
	public static void setDefaultOffVelocity(int vel) {
		if (isAssertion()) {
			assert vel >= 0 && vel <= 127 : "Invalid velocity:" + vel;
		}
		_defaultOffVelocity = vel;
	}

	/**
	 * 標準のMIDIヴェロシティ値を変更します．
	 *
	 * @param vel 設定するMIDIヴェロシティ値（整数）
	 */
	public static void setDefaultVelocity(int vel) {
		if (isAssertion()) {
			assert vel >= 0 && vel <= 127 : "Invalid velocity:" + vel;
		}
		_defaultVelocity = vel;
	}

	/**
	 * MIDI分解能を変更します．
	 *
	 * @param tpb 設定するMIDI分解能（整数）
	 */
	public void setTicksPerBeat(int tpb) {
		if (isAssertion()) {
			assert tpb >= 0 : "Invalid ticks per beat:" + tpb;
		}
		_ticksPerBeat = tpb;
	}

	/**
	 * @return the pROPERTY_FILENAME
	 */
	protected static String getPropertyFilename() {
		return PROPERTY_FILENAME;
	}

	/**
	 * @param filename the pROPERTY_FILENAME to set
	 */
	protected static void setPropertyFilename(String filename) {
		PROPERTY_FILENAME = filename;
	}

	/**
	 * @param flg
	 */
	static void setAssertion(boolean flg) {
		_assertion = flg;
	}

	protected MuseObject() {
		super();
		try {
			log = new PrintStream(new File(LOG_FILENAME));
		} catch (FileNotFoundException e) {
			log = System.out;
		}
	}

	/**
	 * 入力するファイル名の文字列が空であるかどうかを確認します。 空の場合、表示されるファイル選択画面により選択することができます。
	 */
	protected String confirmInputFilename(String filename)
			throws HeadlessException {
		if (filename == null || filename.length() == 0) {
			JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed(true);
			final int returnVal = fc.showOpenDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				System.exit(0);
				System.out.println("Quit the program.");
			}
			return fc.getSelectedFile().getAbsolutePath();
		}
		return filename;
	}

	/**
	 * 出力するファイル名の文字列が空であるかどうかを確認します。 空の場合、表示されるファイル選択画面により選択することができます。
	 */
	protected String confirmOutputFilename(String filename)
			throws HeadlessException {
		if (filename == null || filename.length() == 0) {
			JFileChooser fc = new JFileChooser();
			fc.setAcceptAllFileFilterUsed(true);
			final int returnVal = fc.showSaveDialog(null);
			if (returnVal != JFileChooser.APPROVE_OPTION) {
				System.exit(0);
				System.out.println("Quit the program.");
			}
			return fc.getSelectedFile().getAbsolutePath();
		}
		return filename;
	}

	/**
	 * .properties ファイルを読み込み、アプリケーションの設定情報を取り込みます。
	 *
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void loadConfig() throws FileNotFoundException, IOException {
		config.load(new FileInputStream(getPropertyFilename()));

		for (Object str : config.keySet()) {
			butler().printConsole("set " + str + ": " + config.getProperty(
					(String) str) + "...");
			setOption((String) str);
			butler().printConsole("");
		}
	}

	protected void setOption(String str) throws IllegalArgumentException {
		OptionType _cmd = OptionType.valueOf(str);
		_cmd.exe(this, config.getProperty(str));
		butler().printConsole("done.");
	}

	public PrintStream log() {
		if (log == null) {
			try {
				log = new PrintStream(new File(LOG_FILENAME));
			} catch (FileNotFoundException e) {
				log = System.out;
			}
		}
		return log;
	}

	public void setLog(PrintStream log) {
		this.log = log;
	}

	public Concierge butler() {
		if (butler == null)
			butler = new Concierge(this);
		return butler;
	}

}
