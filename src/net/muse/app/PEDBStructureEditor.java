package net.muse.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.muse.command.MuseAppCommandAction;
import net.muse.gui.MainFrame;
import net.muse.pedb.command.PEDBCommandType;
import net.muse.pedb.data.PEDBConcierge;
import net.muse.pedb.data.PEDBTuneData;
import net.muse.pedb.gui.PEDBMainFrame;

public class PEDBStructureEditor extends Mixtract {

	public PEDBStructureEditor(String[] args) throws FileNotFoundException,
			IOException {
		super(args);
	}

	public static void main(String[] args) {
		try {
			final PEDBStructureEditor main = new PEDBStructureEditor(args);
			main.setup();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#createTuneData(java.io.File, java.io.File)
	 */
	@Override
	public void createTuneData(File in, File out) throws IOException {
		setData(new PEDBTuneData(in, out));
	}

	@Override
	protected void setupCommands() {
		super.setupCommands();
		for (PEDBCommandType e : PEDBCommandType.values())
			getCommandList().add((MuseAppCommandAction) e);
	}

	@Override
	protected MainFrame mainFrame() throws IOException {
		if (getFrame() == null)
			return new PEDBMainFrame(this);
		return (MainFrame) getFrame();
	}

	@Override
	protected PEDBConcierge createConcierge() {
		return new PEDBConcierge(this);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MuseGUIObject#getFrame()
	 */
	@Override
	public PEDBMainFrame getFrame() {
		return (PEDBMainFrame) super.getFrame();
	}
}
