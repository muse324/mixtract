package net.muse.pedb.gui;

import java.io.IOException;

import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.TuneData;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.MXMainFrame;

public class PEDBMainFrame extends MXMainFrame {
	private static final long serialVersionUID = 1L;
	private static String WINDOW_TITLE = "PEDB Structure Editor";
	private JToolBar toolBar;
	private JDesktopPane desktopPane;
	private JPanel tuneViewPanel;
	private JScrollPane pianorollPanel;
	private JScrollPane structurePane;
	private JSlider zoomBar;
	private JTextField zoomText;

	public PEDBMainFrame(Mixtract mixtract) throws IOException {
		super(mixtract);
	}

	protected JScrollPane getStructurePane() {
		if (structurePane == null) {
			structurePane = new JScrollPane();
			structurePane.setRowHeaderView(getPartSelectorPanel());
			structurePane.setViewportView(getGroupingPanel());
			// structurePane.setHorizontalScrollBar(getTimeScrollBar());
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
			// pianorollPanel.setHorizontalScrollBar(getTimeScrollBar());
			// pianorollPanel.setHorizontalScrollBarPolicy(
			// JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
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
			// tuneViewPanel = super.getTuneViewPanel();
			tuneViewPanel = new MainPanel(getPartSelectorPanel(),
					getGroupingPanel(), getKeyboard(), getPianoroll());
		}
		return tuneViewPanel;
	}

	@Override protected JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = super.getToolBar();
			toolBar.remove(getScoreViewButton()); // Generated
			toolBar.remove(getRealtimeViewButton()); // Generated
			toolBar.remove(getAnalyzeButton()); // Generated
			toolBar.remove(getTempoSettingPanel());
			toolBar.add(getZoomText());
			toolBar.add(getZoomBar());
		}
		return toolBar;
	}

	private JTextField getZoomText() {
		if (zoomText == null) {
			zoomText = new JTextField();
			zoomText.setText(String.format("%d", pixelperbeat));
			zoomText.setEditable(false);
		}
		return zoomText;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#quit()
	 */
	protected void quit() {
		System.exit(0);
	}

	private JSlider getZoomBar() {
		if (zoomBar == null) {
			zoomBar = new JSlider();
			zoomBar.addChangeListener(new ChangeListener() {

				@Override public void stateChanged(ChangeEvent e) {
					JSlider s = (JSlider) e.getSource();
					int v = s.getValue();
					pixelperbeat = v;
					getGroupingPanel().changeExpression(PhraseCurveType.TEMPO);
					getPianoroll().changeExpression(PhraseCurveType.TEMPO);
					getZoomText().setText(String.format("%d", v));
				}
			});
		}
		return zoomBar;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXMainFrame#setTarget(net.muse.data.TuneData)
	 */
	@Override public void setTarget(TuneData target) {
		super.setTarget(target);
		getZoomBar().setValue(pixelperbeat);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.MXMainFrame#createPianoRoll(net.muse.app.MuseApp)
	 */
	@Override protected PianoRoll createPianoRoll(MuseApp main) {
		assert main instanceof Mixtract;
		return (PEDBPianoroll) new PEDBPianoroll((Mixtract) main);
	}
}
