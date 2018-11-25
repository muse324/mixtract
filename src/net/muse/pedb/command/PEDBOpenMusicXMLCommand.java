package net.muse.pedb.command;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.muse.app.MuseApp;
import net.muse.command.OpenMusicXMLCommand;

public class PEDBOpenMusicXMLCommand extends OpenMusicXMLCommand {

	public PEDBOpenMusicXMLCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		try {
			final JFileChooser fc = app() != null ? new JFileChooser(app()
					.getMusicXMLDir()) : new JFileChooser();
			final int res = fc.showOpenDialog(null);
			if (res == JFileChooser.APPROVE_OPTION) {
				butler().readfile(fc.getSelectedFile(), new File(_app
						.getProjectDirectory(), fc.getSelectedFile()
								.getParentFile().getName() + MuseApp
										.getProjectFileExtension()));
			}
		} catch (HeadlessException | IOException e1) {
			e1.printStackTrace();
		}
	}
}
