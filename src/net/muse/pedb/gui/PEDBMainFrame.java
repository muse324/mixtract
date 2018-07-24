package net.muse.pedb.gui;

import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.gui.MXMainFrame;

public class PEDBMainFrame extends MXMainFrame {
	private static final long serialVersionUID = 1L;
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

	@Override protected JToolBar getJToolBar() {
		JToolBar toolBar = super.getJToolBar();
		toolBar.remove(getScoreViewButton()); // Generated
		toolBar.remove(getRealtimeViewButton()); // Generated
		toolBar.remove(getAnalyzeButton()); // Generated
		toolBar.remove(getTempoSettingPanel());
		return toolBar;
	}

	/* (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXMainFrame#createPianoRoll(net.muse.app.MuseApp)
	 */
	@Override
	protected PianoRoll createPianoRoll(MuseApp main) {
		assert main instanceof Mixtract;
		return (PEDBPianoroll) new PEDBPianoroll((Mixtract) main);
	}
}
