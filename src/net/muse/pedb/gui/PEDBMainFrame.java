package net.muse.pedb.gui;

import java.io.IOException;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.gui.MXMainFrame;

public class PEDBMainFrame extends MXMainFrame {
	private static final long serialVersionUID = 1L;
	private static String WINDOW_TITLE = "PEDB Structure Editor";
	private JToolBar toolBar;
	private JDesktopPane desktopPane;
	private JPanel tuneViewPanel;
	private JScrollPane pianorollPanel;
	private JScrollPane structurePane;

	public PEDBMainFrame(Mixtract mixtract) throws IOException {
		super(mixtract);
	}
	protected JScrollPane getStructurePane() {
		if (structurePane == null) {
			structurePane = new JScrollPane();
			structurePane.setRowHeaderView(getPartSelectorPanel());
			structurePane.setViewportView(getGroupingPanel());
//			structurePane.setHorizontalScrollBar(getTimeScrollBar());
			structurePane.setVerticalScrollBarPolicy(
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return structurePane;
	}
	protected JScrollPane getPianorollPane() {
		if (pianorollPanel == null) {
			pianorollPanel = new JScrollPane();
			pianorollPanel.setRowHeaderView(getKeyboard());
			pianorollPanel.setViewportView(getPianoroll()); // Generated
//			pianorollPanel.setHorizontalScrollBar(getTimeScrollBar());
//			pianorollPanel.setHorizontalScrollBarPolicy(
//					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			pianorollPanel.setVerticalScrollBarPolicy(
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return pianorollPanel;
	}
	protected String getWindowTitle() {
		return WINDOW_TITLE;
	}

	@Override protected JDesktopPane getDesktop() {
		if (desktopPane == null) {
			desktopPane = super.getDesktop();
			desktopPane.remove(getPhraseEditorPanel());
		}
		return desktopPane;
	}

	@Override protected JPanel getTuneViewPanel() {
		if (tuneViewPanel == null) {
//			tuneViewPanel = super.getTuneViewPanel();
			tuneViewPanel = new MainPanel(getStructurePane(),getPianorollPane());
		}
		return tuneViewPanel;
	}

	@Override protected JToolBar getJToolBar() {
		if (toolBar == null) {
			toolBar = super.getJToolBar();
			toolBar.remove(getScoreViewButton()); // Generated
			toolBar.remove(getRealtimeViewButton()); // Generated
			toolBar.remove(getAnalyzeButton()); // Generated
			toolBar.remove(getTempoSettingPanel());
		}
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
