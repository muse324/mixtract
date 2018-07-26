package net.muse.mixtract.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.TuneData;
import net.muse.gui.GroupingPanel;
import net.muse.gui.MainFrame;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class MXMainFrame extends MainFrame {

	private static final long serialVersionUID = 1L;
	private static String WINDOW_TITLE = "Mixtract";

	private JButton analyzeButton = null;

	private ButtonGroup pianorollViewerGroup;

	private JRadioButton realtimeViewButton = null;

	private JRadioButton scoreViewButton = null;

	private JInternalFrame phraseEditorPanel = null;

	private JPanel curveEditorPanel;

	private PhraseCurveEditorPanel phraseArtView;

	private PhraseCurveEditorPanel phraseDynView;

	private PhraseCurveEditorPanel phraseTempoView;

	private PhraseInfoPanel phraseInfoPanel;
	private JToolBar toolBar;
	private JDesktopPane desktop;

	public MXMainFrame(Mixtract mixtract) throws IOException {
		super(mixtract);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#getPianoroll()
	 */
	@Override public MXPianoroll getPianoroll() {
		return (MXPianoroll) super.getPianoroll();
	}

	/**
	 * @return pianorollViewerGroup
	 */
	public ButtonGroup getPianorollViewerGroup() {
		if (pianorollViewerGroup == null)
			pianorollViewerGroup = new ButtonGroup();
		return pianorollViewerGroup;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#setTarget(net.muse.mixtract.data.MXTuneData)
	 */
	@Override public void setTarget(TuneData target) {
		super.setTarget(target);
		getAnalyzeButton().setEnabled(true);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#createGroupingPanel()
	 */
	@Override protected GroupingPanel createGroupingPanel() {
		return new MXGroupingPanel();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#createPianoRollPane()
	 */
	@Override protected PianoRoll createPianoRoll(MuseApp main) {
		assert main instanceof Mixtract;
		return new MXPianoroll((Mixtract) main);
	}

	@Override protected JToolBar getJToolBar() {
		if (toolBar == null) {
			toolBar = super.getJToolBar();
			toolBar.add(getScoreViewButton()); // Generated
			toolBar.add(getRealtimeViewButton()); // Generated
			toolBar.add(getAnalyzeButton()); // Generated
			toolBar.add(getTempoSettingPanel());
		}
		return toolBar;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#initialize()
	 */
	@Override protected void initialize() {
		super.initialize();
		getScoreViewButton().setSelected(true);
	}

	@Override protected JDesktopPane getDesktop() {
		if (desktop == null) {
			desktop = super.getDesktop();
			desktop.add(getPhraseEditorPanel(), BorderLayout.WEST);
			Dimension sz = getCurveEditorPanel().getPreferredSize();
			getCurveEditorPanel().setPreferredSize(new Dimension(300,
					sz.height));
		}
		return desktop;
	}

	protected JInternalFrame getPhraseEditorPanel() {
		if (phraseEditorPanel == null) {
			phraseEditorPanel = new JInternalFrame();
			phraseEditorPanel.setClosable(false);
			phraseEditorPanel.setResizable(true);
			phraseEditorPanel.setMaximizable(false);
			phraseEditorPanel.setDefaultCloseOperation(
					WindowConstants.HIDE_ON_CLOSE);
			phraseEditorPanel.setContentPane(getCurveEditorPanel()); // Generated
			phraseEditorPanel.setVisible(true);
		}
		return phraseEditorPanel;
	}

	private JPanel getCurveEditorPanel() {
		if (curveEditorPanel == null) {
			curveEditorPanel = new JPanel();
			curveEditorPanel.setLayout(new BoxLayout(curveEditorPanel,
					BoxLayout.Y_AXIS));
			curveEditorPanel.add(getPhraseInfoPanel());
			curveEditorPanel.add(new JLabel("Dynamics"));
			curveEditorPanel.add(getPhraseDynamicsView());
			curveEditorPanel.add(new JLabel("Tempo"));
			curveEditorPanel.add(getPhraseTempoView());
			curveEditorPanel.add(new JLabel("Articulation"));
			curveEditorPanel.add(getPhraseArticulationView());
		}
		return curveEditorPanel;
	}

	private PhraseCurveEditorPanel getPhraseDynamicsView() {
		if (phraseDynView == null) {
			phraseDynView = new PhraseCurveEditorPanel(main,
					PhraseCurveType.DYNAMICS);
			butler().addTuneDataListenerList(phraseDynView);
		}
		return phraseDynView;
	}

	private PhraseCurveEditorPanel getPhraseTempoView() {
		if (phraseTempoView == null) {
			phraseTempoView = new PhraseCurveEditorPanel(main,
					PhraseCurveType.TEMPO);
			butler().addTuneDataListenerList(phraseTempoView);
		}
		return phraseTempoView;
	}

	private PhraseCurveEditorPanel getPhraseArticulationView() {
		if (phraseArtView == null) {
			phraseArtView = new PhraseCurveEditorPanel(main,
					PhraseCurveType.ARTICULATION);
			butler().addTuneDataListenerList(phraseArtView);
		}
		return phraseArtView;
	}

	private JPanel getPhraseInfoPanel() {
		if (phraseInfoPanel == null) {
			phraseInfoPanel = new PhraseInfoPanel();
			butler().addTuneDataListenerList(phraseInfoPanel);
		}
		return phraseInfoPanel;
	}

	/**
	 * This method initializes analyzeButton
	 *
	 * @return javax.swing.JButton
	 */
	protected JButton getAnalyzeButton() {
		if (analyzeButton == null) {
			analyzeButton = new JButton("Analyze");
			analyzeButton.setEnabled(false);
			analyzeButton.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							assert data instanceof MXTuneData;
							main.analyzeStructure((MXTuneData) data, null);
							main.butler().notifySetTarget(data);
						}
					});
		}
		return analyzeButton;
	}

	/**
	 * This method initializes realtimeViewButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	protected JRadioButton getRealtimeViewButton() {
		if (realtimeViewButton == null) {
			realtimeViewButton = new JRadioButton("realtime view");
			getPianorollViewerGroup().add(realtimeViewButton);
			realtimeViewButton.addItemListener(
					new java.awt.event.ItemListener() {
						public void itemStateChanged(
								java.awt.event.ItemEvent e) {
							if (((JRadioButton) e.getItem()).isSelected()) {
								getPianoroll().setViewMode(
										ViewerMode.REALTIME_VIEW);
								getGroupingPanel().setViewMode(
										ViewerMode.REALTIME_VIEW);
								getDynamicsView().setViewMode(
										ViewerMode.REALTIME_VIEW);
								getTempoView().setViewMode(
										ViewerMode.REALTIME_VIEW);
							}
						}
					});
		}
		return realtimeViewButton;
	}

	/**
	 * This method initializes scoreViewButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	protected JRadioButton getScoreViewButton() {
		if (scoreViewButton == null) {
			scoreViewButton = new JRadioButton("score view");
			getPianorollViewerGroup().add(scoreViewButton);
			scoreViewButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					if (((JRadioButton) e.getItem()).isSelected()) {
						getPianoroll().setViewMode(ViewerMode.SCORE_VIEW);
						getGroupingPanel().setViewMode(ViewerMode.SCORE_VIEW);
						getDynamicsView().setViewMode(ViewerMode.SCORE_VIEW);
						getTempoView().setViewMode(ViewerMode.SCORE_VIEW);
					}
				}
			});
		}
		return scoreViewButton;
	}

	protected String getWindowTitle() {
		return WINDOW_TITLE;
	}
}
