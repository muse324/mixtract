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
import net.muse.command.MuseAppCommandType;
import net.muse.gui.*;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.MixtractCommandType;

/**
 * @author hashida
 *
 */
public class Concierge extends MuseObject implements TuneDataController {

	protected MuseObject obj;
	private List<TuneDataListener> tdListenerList;
	private ArrayList<InfoViewer> infoViewList;
	protected MuseAppCommand c = null;
	private boolean isPlayed = false;

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
		c = null;
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		MuseApp app = (MuseApp) obj;
		printConsole(String.format("PEDBConcierge: %d", e.getKeyCode()));
		switch (e.getKeyCode()) {
		case KeyEvent.VK_G:
			printConsole("make group");
			c = app.searchCommand(MixtractCommandType.MAKE_GROUP);
			break;
		case KeyEvent.VK_BACK_SPACE:
			printConsole("delete group");
			c = app.searchCommand(MixtractCommandType.DELETE_GROUP);
			break;
		case KeyEvent.VK_SPACE:
			printConsole((!isPlayed) ? "play" : "stop");
			c = app.searchCommand((!isPlayed) ? MuseAppCommandType.PLAY
					: MuseAppCommandType.STOP);
			setPlayed(!isPlayed);
			break;
		}
		if (c != null) {
			c.setFrame((MainFrame) app().getFrame());
			c.setApp(app());
			c.setTarget(app().data());
			c.run();
		}
	}

	/**
	 * グループが選択/解除されたことを通知します．
	 *
	 * @param g グループラベル
	 * @param b 選択(true)/解除(false)
	 */
	public void notifySelectGroup(GroupLabel g, boolean b) {
		app().data().setSelectedGroup((b) ? g.group() : null);
		for (TuneDataListener l : getTdListenerList()) {
			l.selectGroup(g, b);
		}
	}

	public void notifyDeselectGroup() {
		if (app().data() != null)
			app().data().setSelectedGroup(null);
		for (final TuneDataListener l : getTdListenerList()) {
			l.deselect(null);
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
			in = new File(app().getProjectDirectory(), app().getInputFileName());
		if (!in.exists())
			in = new File(app().getMusicXMLDir(), app().getInputFileName());
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
		app().createTuneData(in, out);
		readfile(in, app().data());
		printConsole(String.format("Open file: %s", in));
		if (MuseApp.isShowGUI()) {
			notifySetTarget(app().data());
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
		assert fileType != null;

		// CMX対応形式の読み込み
		CMXImporter cmx = new CMXImporter(in, fileType, data);
		cmx.run();
	}

	protected MuseApp app() {
		if (obj instanceof MuseAppCommand)
			return (MuseApp) (((MuseAppCommand) obj).app());
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		return (MuseApp) obj;
	}

	protected TuneData data() {
		assert obj instanceof TuneData : "obj がTuneDataクラス系ではありません";
		return (TuneData) obj;
	}

	public void notifyStartPlaying(TuneData data) {
		if (data == null)
			return;
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		if (!data.getRootGroup(0).hasChild())
			data.initializeNoteEvents();
		data.setNoteScheduleEvent();
		((MuseApp) obj).synthe().notifyStartPlaying(data.getInputFilename());
	}

	public void notifyStopPlaying() {
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		((MuseApp) obj).synthe().notifyStopPlaying();
	}

	/**
	 * @return isPlayed
	 */
	public boolean isPlayed() {
		return isPlayed;
	}

	/**
	 * @param isPlayed セットする isPlayed
	 */
	public void setPlayed(boolean isPlayed) {
		this.isPlayed = isPlayed;
	}

}
