package net.muse.pedb.gui;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.TuneData;
import net.muse.gui.MainFrame;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.pedb.command.PEDBCommandType;
import net.muse.pedb.data.PEDBConcierge;

public class PEDBMainFrame extends MainFrame {
	private static final long serialVersionUID = 1L;

	private static String WINDOW_TITLE = "PEDB Structure Editor";
	private JToolBar toolBar;
	private JDesktopPane desktopPane;
	private JPanel tuneViewPanel;
	private JScrollPane pianorollPanel;
	private JScrollPane structurePane;
	private JSlider zoomBar;
	private JTextField zoomText;
	private JButton refreshButton;

	public PEDBMainFrame(PEDBStructureEditor app) throws IOException {
		super(app);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#butler()
	 */
	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	@Override public PEDBGroupingPanel getGroupingPanel() {
		return (PEDBGroupingPanel) super.getGroupingPanel();
	}

	@Override public PEDBPianoroll getPianoroll() {
		return (PEDBPianoroll) super.getPianoroll();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXMainFrame#setTarget(net.muse.data.TuneData)
	 */
	@Override public void setTarget(TuneData target) {
		super.setTarget(target);
		getZoomBar().setValue(pixelperbeat);
	}

	@Override protected PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

	@Override protected PEDBGroupingPanel createGroupingPanel() {
		return new PEDBGroupingPanel(app());
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.MXMainFrame#createPianoRoll(net.muse.app.MuseApp)
	 */
	@Override protected PianoRoll createPianoRoll(MuseApp app) {
		return new PEDBPianoroll(app);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXMainFrame#getDesktop()
	 */
	@Override protected JDesktopPane getDesktop() {
		if (desktopPane == null) {
			desktopPane = super.getDesktop();
		}
		return desktopPane;
	}

	@Override protected JMenuItem getImportXMLMenu() {
		final JMenuItem m = super.getImportXMLMenu();
		m.setActionCommand(PEDBCommandType.PEDBIMPORT_MUSICXML.name());
		return m;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#getPianorollPane()
	 */
	@Override protected JScrollPane getPianorollPane() {
		if (pianorollPanel == null) {
			pianorollPanel = new JScrollPane();
			pianorollPanel.setRowHeaderView(getKeyboard());
			pianorollPanel.setViewportView(getPianoroll()); // Generated
			pianorollPanel.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return pianorollPanel;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#getStructurePane()
	 */
	@Override protected JScrollPane getStructurePane() {
		if (structurePane == null) {
			structurePane = new JScrollPane();
			structurePane.setRowHeaderView(getPartSelectorPanel());
			structurePane.setViewportView(getGroupingPanel());
			structurePane.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return structurePane;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXMainFrame#getToolBar()
	 */
	@Override protected JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = super.getToolBar();
			toolBar.remove(getTempoSettingPanel());
			toolBar.add(getZoomText());
			toolBar.add(getZoomBar());
			toolBar.add(getRereshButton());
		}
		return toolBar;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#getTuneViewPanel()
	 */
	@Override protected JPanel getTuneViewPanel() {
		if (tuneViewPanel == null) {
			tuneViewPanel = new MainPanel(getPartSelectorPanel(),
					getGroupingPanel(), getKeyboard(), getPianoroll());
		}
		return tuneViewPanel;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXMainFrame#getWindowTitle()
	 */
	@Override protected String getWindowTitle() {
		return WINDOW_TITLE;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#quit()
	 */
	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#quit()
	 */
	@Override protected void quit() {
		System.exit(0);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#readfile(java.io.File)
	 */
	@Override protected void readfile(File f) throws IOException {
		butler().readfile(f, app().getProjectDirectory());
	}

	private JButton getRereshButton() {
		if (refreshButton == null) {
			refreshButton = new JButton("Reresh");
			refreshButton.addActionListener(e -> {
				getGroupingPanel().readTuneData();
				repaint();
			});
		}
		return refreshButton;
	}

	/**
	 * 描画用のズームバーを表示します。
	 */
	private JSlider getZoomBar() {
		if (zoomBar == null) {
			zoomBar = new JSlider(1, 200);
			zoomBar.addChangeListener(e -> {
				final JSlider s = (JSlider) e.getSource();
				final int v = s.getValue();
				pixelperbeat = v;
				getGroupingPanel().changeExpression(PhraseCurveType.TEMPO);
				final PEDBPianoroll p = getPianoroll();
				p.changeExpression(PhraseCurveType.TEMPO);
				getZoomText().setText(String.format("%d", v));
				getZoomText().setSize(getZoomText().getPreferredSize());
				Rectangle r = null;
				for (final Component c : p.getComponents()) {
					if (!(c instanceof PEDBNoteLabel))
						continue;
					if (r == null || c.getBounds().x > r.x) {
						r = c.getBounds();
						continue;
					}
					p.resize(r);
				}
			});
		}
		return zoomBar;
	}

	/**
	 * 表示倍率（pixelperbeat）の値を表示します。テキストフィールドに直接代入することはできません。
	 */
	private JTextField getZoomText() {
		if (zoomText == null) {
			zoomText = new JTextField();
			zoomText.setText(String.format("%d", pixelperbeat));
			zoomText.setEditable(false);
		}
		return zoomText;
	}

}
