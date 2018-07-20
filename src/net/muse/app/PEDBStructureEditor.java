package net.muse.app;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.muse.gui.MainFrame;
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

	@Override protected MainFrame mainFrame() throws IOException {
		if (getFrame() == null)
			return new PEDBMainFrame(this);
		return (MainFrame) getFrame();
	}
}
