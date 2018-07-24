package net.muse.app;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.muse.command.MuseAppCommandAction;
import net.muse.data.Concierge;
import net.muse.gui.MainFrame;
import net.muse.pedb.command.PEDBCommandType;
import net.muse.pedb.data.PEDBConcierge;
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
	protected Concierge createConcierge() {
		return new PEDBConcierge(this);
	}
}
