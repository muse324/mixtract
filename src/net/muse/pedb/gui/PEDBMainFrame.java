package net.muse.pedb.gui;

import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;

import net.muse.app.Mixtract;
import net.muse.mixtract.gui.MXMainFrame;

public class PEDBMainFrame extends MXMainFrame {
	private static String WINDOW_TITLE = "PEDB Editor";

	public PEDBMainFrame(Mixtract mixtract) throws IOException {
		super(mixtract);
	}

	protected String getWindowTitle() {
		return WINDOW_TITLE;
	}

	@Override protected JDesktopPane getDesktop() {
		JDesktopPane d = super.getDesktop();
		d.remove(getPhraseEditorPanel());
		return d;
	}

	@Override protected JPanel getTuneViewPanel() {
		JPanel p = super.getTuneViewPanel();
		p.remove(getCurveSplitPane());
		return p;
	}
}
