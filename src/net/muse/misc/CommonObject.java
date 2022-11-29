package net.muse.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * <h1>CommonObject</h1>
 * 
 * @author Mitsuyo Hashida @ Soai University
 * @since 2012/09/07
 */
public class CommonObject {

	private boolean DEBUG = false;
	private static PrintStream log = null;
	private static final String LOG_FILENAME = "log.txt";
	private static CommonObject st = new CommonObject();
	protected boolean _assertion;

	/**
	 * 
	 */
	public CommonObject() {
		super();
		loadLogStream();
	}

	public final static void testPrint(String val) {
		if (st.isDebug())
			System.out.print(val);
		if (log != System.out)
			log.print(val);
	}

	public final static void testPrintln(String val) {
		if (st.isDebug())
			System.out.println(val);
		if (log != System.out)
			log.println(val);
	}

	protected boolean isAssertion() {
		return _assertion;
	}

	/**
	 * デバッグモードが指定されているかどうかを判別します．
	 * 
	 * @return デバッグモード（真：デバッグON）
	 */
	protected boolean isDebug() {
		return DEBUG;
	}

	/**
	 * @param parseBoolean
	 */
	public void setAssertion(boolean flg) {
		_assertion = flg;
	}

	/**
	 * デバッグモードのON/OFFを切り替えます．
	 * 
	 * @param dbg 設定するデバッグモード
	 */
	public void setDebugMode(boolean dbg) {
		DEBUG = dbg;
		if (st != null && st.isDebug() != dbg)
			st.setDebugMode(dbg);
	}

	/**
	 * 出力用ログファイルを読み込みます。
	 */
	private void loadLogStream() {
		try {
			log = new PrintStream(new File(LOG_FILENAME));
		} catch (FileNotFoundException e) {
			log = System.out;
		}
	}
}
