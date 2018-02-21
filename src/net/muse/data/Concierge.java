/**
 *
 */
package net.muse.data;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.openmbean.InvalidOpenTypeException;
import javax.sound.midi.InvalidMidiDataException;

import net.muse.app.MuseApp;
import net.muse.gui.GUIUtil;
import net.muse.gui.TuneDataListener;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.MixtractCommand;

/**
 * @author hashida
 *
 */
public class Concierge extends MuseObject {

	private MuseObject obj;
	private List<TuneDataListener> tdListenerList;

	public Concierge(MuseObject obj) {
		this.obj = obj;
	}

	public void addTuneDataListenerList(TuneDataListener l) {
		getTdListenerList().add(l);
	}

	public List<TuneDataListener> getTdListenerList() {
		if (tdListenerList == null) {
			tdListenerList = new ArrayList<TuneDataListener>();
		}
		return tdListenerList;
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_G:
			printConsole("make group");
			MixtractCommand.MAKE_GROUP.execute();
			break;
		case KeyEvent.VK_BACK_SPACE:
			printConsole("delete group");
			MixtractCommand.DELETE_GROUP.execute();
			break;
		default:
			printConsole(e.getSource().getClass().getName()
					+ ": key pressed: ");
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

	public void readfile() throws IOException, InvalidMidiDataException {
		if (!(obj instanceof MuseApp))
			throw new InvalidOpenTypeException("MuseApp系のクラスオブジェクトで呼びs出してください");

		MuseApp app = (MuseApp) obj;
		File in = new File(app.getInputFileName());
		if (!in.exists())
			in = new File(app.projectDir, app.getInputFileName());
		if (!in.exists())
			in = new File(app.musicXMLDir, app.getInputFileName());
		File out = new File(app.getOutputDirectory(), app.getOutputFileName());
		readfile(in, out);
	}

	/**
	 * @param in
	 * @param out
	 * @throws IOException
	 * @throws InvalidMidiDataException
	 */
	public void readfile(File in, File out) throws IOException,
			InvalidMidiDataException {
		if (!(obj instanceof MuseApp))
			throw new InvalidOpenTypeException("MuseApp系のクラスオブジェクトで呼びs出してください");

		MuseApp app = (MuseApp) obj;
		app.setData(app.createTuneData(in, out));
		printConsole(String.format("Open file: %s", in));
		if (MuseApp.isShowGUI()) {
			MixtractCommand.setTarget(app.data());
			app.notifySetTarget();
		}
	}

}
