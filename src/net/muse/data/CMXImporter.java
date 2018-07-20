package net.muse.data;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import jp.crestmuse.cmx.filewrappers.CMXFileWrapper;
import jp.crestmuse.cmx.filewrappers.DeviationInstanceWrapper;
import jp.crestmuse.cmx.filewrappers.MIDIXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;
import jp.crestmuse.cmx.processing.CMXController;

public class CMXImporter extends Concierge implements Runnable {

	private File inputFile;
	private String fileType;
	private DeviationInstanceWrapper dev;
	private MusicXMLWrapper xml;
	private SCCXMLWrapper scc;

	public CMXImporter(File in, String fileType, TuneData data) {
		super(data);
		inputFile = in;
		this.fileType = fileType;
	}

	public File in() {
		assert inputFile != null : "入力ファイルがありません";
		return inputFile;
	}

	@Override public void run() {
		// XMLならCMX形式でインポート
		if (fileType.equals("xml")) {
			readCMXFile();
			parseMusicXMLFile();
		} else if (fileType.equals("midi") || fileType.equals("x-midi")) {
			// MIDIファイル
			readMIDIFile();
			parseSCCXMLFile();
		}
		// 楽曲データに代入
		data().importCMXobjects(dev, xml, scc);
	}

	private void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(data().createCMXNoteHandler());
	}

	private void parseSCCXMLFile() {
		if (scc == null)
			return;
		try {
			scc.processNotes(data().createCMXNoteHandler());
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private void readCMXFile() {
		try {
			printConsole("import CMX file");
			CMXFileWrapper cmx = CMXController.readfile(inputFile
					.getAbsolutePath());
			if (cmx instanceof DeviationInstanceWrapper) {
				dev = ((DeviationInstanceWrapper) cmx);
				xml = dev.getTargetMusicXML();
				// TODO deviation データを読み込む処理
			} else if (cmx instanceof MusicXMLWrapper) {
				xml = (MusicXMLWrapper) cmx;
			} else if (cmx instanceof SCCXMLWrapper) {
				scc = (SCCXMLWrapper) cmx;
			} else
				readCMXFile(cmx);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * CrestMuseXML(CMX)形式のデータから読込処理を行います。
	 * MusicXML、DeviationIncetanceWrapper、SCCXMLWrapper形式については
	 * readCMLFile(String)メソッドにて処理されます。
	 * このメソッドでは、それ以外の形式についての処理を実装してください。
	 *
	 * @param cmx
	 * @see TuneData.readCMXFile(String)
	 * @see {@link CrestMuseXML:<a href=
	 *      "http://cmx.osdn.jp/">http://cmx.osdn.jp/</a>}
	 */
	private void readCMXFile(CMXFileWrapper cmx) {}

	private void readMIDIFile() {
		try {
			MIDIXMLWrapper mid = CMXController.readSMFAsMIDIXML(in()
					.getAbsolutePath());
			scc = mid.toSCCXML();
		} catch (TransformerException | IOException
				| ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}
	}

}
