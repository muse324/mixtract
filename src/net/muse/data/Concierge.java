/**
 *
 */
package net.muse.data;

import java.io.File;
import java.io.IOException;

import javax.management.openmbean.InvalidOpenTypeException;
import javax.sound.midi.InvalidMidiDataException;

import net.muse.app.MuseApp;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.MixtractCommand;

/**
 * @author hashida
 *
 */
public class Concierge extends MuseObject {

	private MuseObject obj;

	public Concierge(MuseObject obj) {
		this.obj = obj;
	}

	public void printConsole(String string) {
		printConsole(string, null);
	}

	public void printConsole(String string, File in) {
		log().println(string);
		if (in != null)
			log().printf("Open file: %s", in);
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
		printConsole("Open file: %s", in);
		if (MuseApp.isShowGUI()) {
			MixtractCommand.setTarget(app.data());
			app.notifySetTarget();
		}
	}

}
