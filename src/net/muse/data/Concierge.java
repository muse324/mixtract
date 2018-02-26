/**
 *
 */
package net.muse.data;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;

import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import net.muse.app.MuseApp;
import net.muse.command.MuseAppCommand;
import net.muse.gui.GUIUtil;
import net.muse.gui.InfoViewer;
import net.muse.gui.MainFrame;
import net.muse.gui.TuneDataListener;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.MixtractCommand;

/**
 * @author hashida
 *
 */
public class Concierge extends MuseObject implements TuneDataController {

	protected MuseObject obj;
	private List<TuneDataListener> tdListenerList;
	private ArrayList<InfoViewer> infoViewList;

	public Concierge(MuseObject obj) {
		this.obj = obj;
	}

	public void addInfoViewerList(InfoViewer pv) {
		getInfoViewList().add(pv);
	}

	public void addTuneDataListenerList(TuneDataListener l) {
		getTdListenerList().add(l);
	}

	public ArrayList<InfoViewer> getInfoViewList() {
		if (infoViewList == null) {
			infoViewList = new ArrayList<InfoViewer>();
		}
		return infoViewList;
	}

	/**
	 * 入力ファイルのファイルタイプを調べます．
	 *
	 * @param in
	 *
	 * @see {@link https://hacknote.jp/archives/5320/}
	 * @return (String) ファイルの種類
	 */
	public String getInputFileType(File in) {
		MimeUtil.registerMimeDetector(
				"eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(in);
		if (mimeTypes.isEmpty())
			return null;
		Iterator<?> iterator = mimeTypes.iterator();
		MimeType mimeType = (MimeType) iterator.next();
		String fileType = mimeType.getSubType();
		return fileType;
	}

	public List<TuneDataListener> getTdListenerList() {
		if (tdListenerList == null) {
			tdListenerList = new ArrayList<TuneDataListener>();
		}
		return tdListenerList;
	}

	public void keyPressed(KeyEvent e) {
		MuseAppCommand c = null;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_G:
			printConsole("make group");
			c = MixtractCommand.create(MixtractCommand.MAKE_GROUP.name());
			break;
		case KeyEvent.VK_BACK_SPACE:
			printConsole("delete group");
			c = MixtractCommand.create(MixtractCommand.DELETE_GROUP.name());
			break;
		default:
			printConsole(e.getSource().getClass().getName()
					+ ": key pressed: ");
		}
		if (c != null) {
			c.setFrame((MainFrame) app().getFrame());
			c.setMain(app());
			c.setTarget(app().data());
			c.run();
		}
	}

	public void notifySetTarget(TuneData data) {
		getInfoViewList().clear();
		for (TuneDataListener l : getTdListenerList()) {
			l.setTarget(data);
		}
	}

	public void printConsole(String string, Object... args) {
		log().println(string);
		GUIUtil.printConsole(string);
		for (Object obj : args) {
			if (obj instanceof File) {
				log().printf("Open file: %s", (File) obj);
				return;
			}
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.TuneDataController#readfile()
	 */
	public void readfile() throws IOException {
		File in = new File(app().getInputFileName());
		if (!in.exists())
			in = new File(app().projectDir, app().getInputFileName());
		if (!in.exists())
			in = new File(app().musicXMLDir, app().getInputFileName());
		File out = new File(app().getOutputDirectory(), app()
				.getOutputFileName());
		readfile(in, out);
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public void readfile(File in, File out) throws IOException {
		MuseApp app = app();
		app.createTuneData(in, out);
		readfile(in, app.data());
		printConsole(String.format("Open file: %s", in));
		if (MuseApp.isShowGUI()) {
			notifySetTarget(app.data());
		}
	}

	@Override public void writefile() throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override public void writeOriginalData() throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override public void writeScoreData() throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override public void writeSMF() throws IOException {
		// TODO 自動生成されたメソッド・スタブ

	}

	private void readfile(File in, TuneData data) throws IOException {
		// システム独自形式の読込
		if (data.isOriginalFileFormat()) {
			data.readOriginalFile();
			return;
		}
		// ファイルの種類を調べる
		String fileType = getInputFileType(in);
		if (fileType == null)
			return;

		// CMX対応形式の読み込み
		CMXImporter cmx = new CMXImporter(in, fileType, data);
		cmx.run();
	}

	protected MuseApp app() {
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼びs出してください";
		return (MuseApp) obj;
	}

	protected TuneData data() {
		assert obj instanceof TuneData : "obj がTuneDataクラス系ではありません";
		return (TuneData) obj;
	}

}
