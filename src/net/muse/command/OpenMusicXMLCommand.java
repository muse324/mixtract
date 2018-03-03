package net.muse.command;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import net.muse.app.Mixtract;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/12
 */
/**
 * @author hashida
 *
 */
public class OpenMusicXMLCommand extends MuseAppCommand {

	protected OpenMusicXMLCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		try {
			JFileChooser fc = (app() != null) ? new JFileChooser(app()
					.getMusicXMLDirectory()) : new JFileChooser();
			int res = fc.showOpenDialog(null);
			if (res == JFileChooser.APPROVE_OPTION) {
				app().butler().readfile(fc.getSelectedFile(), new File(_main
						.getProjectDirectory(), fc.getSelectedFile().getName()
								+ Mixtract.getProjectFileExtension()));
			}
		} catch (HeadlessException | IOException e1) {
			e1.printStackTrace();
		}
	}

}