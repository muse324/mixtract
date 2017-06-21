package net.muse.mixtract.gui;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.*;

import net.muse.gui.*;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.MXTuneData;

public class MXMainFrame extends MainFrame {

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#createGroupingPanel()
	 */
	@Override protected GroupingPanel createGroupingPanel() {
		return new MXGroupingPanel();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#setTarget(net.muse.mixtract.data.MXTuneData)
	 */
	@Override public void setTarget(MXTuneData target) {
		super.setTarget(target);
		getAnalyzeButton().setEnabled(true);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#initialize()
	 */
	@Override protected void initialize() {
		super.initialize();
		getScoreViewButton().setSelected(true);
	}

	private static final long serialVersionUID = 1L;

	public MXMainFrame(Mixtract mixtract) throws IOException {
		super(mixtract);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#getPianoroll()
	 */
	@Override public MXPianoroll getPianoroll() {
		if (pianoroll == null) {
			pianoroll = new MXPianoroll();
			pianoroll.setController(main);
			pianoroll.setPreferredSize(new Dimension(DEFAULT_WIDTH, KeyBoard
					.getKeyboardHeight() / 3 * 2));
		}
		return (MXPianoroll) pianoroll;
	}

	private JRadioButton scoreViewButton = null;
	private JRadioButton realtimeViewButton = null;
	private JButton analyzeButton = null;
	private ButtonGroup pianorollViewerGroup;

	@Override protected JToolBar getJToolBar() {
		if (jToolBar == null) {
			tempoValueLabel = new JLabel();
			tempoValueLabel.setText("   BPM:");
			jToolBar = new JToolBar();
			jToolBar.add(getDataSetButton()); // Generated
			jToolBar.add(getPlayButton()); // Generated
			jToolBar.add(getPauseButton()); // Generated
			jToolBar.add(getStopButton()); // Generated
			jToolBar.add(getScoreViewButton()); // Generated
			jToolBar.add(getRealtimeViewButton()); // Generated
			jToolBar.add(getAnalyzeButton()); // Generated
			jToolBar.add(getTempoSettingPanel());
		}
		return jToolBar;
	}

	/**
	 * This method initializes analyzeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAnalyzeButton() {
		if (analyzeButton == null) {
			analyzeButton = new JButton();
			analyzeButton.setText("Analyze");
			analyzeButton.setEnabled(false);
			analyzeButton.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							main.analyzeStructure(data, null);
							main.notifySetTarget();
						}
					});
		}
		return analyzeButton;
	}

	/**
	 * This method initializes scoreViewButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getScoreViewButton() {
		if (scoreViewButton == null) {
			scoreViewButton = new JRadioButton();
			scoreViewButton.setText("score view");
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

	/**
	 * This method initializes realtimeViewButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getRealtimeViewButton() {
		if (realtimeViewButton == null) {
			realtimeViewButton = new JRadioButton();
			realtimeViewButton.setText("realtime view");
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
	 * @return pianorollViewerGroup
	 */
	public ButtonGroup getPianorollViewerGroup() {
		if (pianorollViewerGroup == null)
			pianorollViewerGroup = new ButtonGroup();
		return pianorollViewerGroup;
	}

	/**
	 * @param pianorollViewerGroup セットする pianorollViewerGroup
	 */
	public void setPianorollViewerGroup(ButtonGroup pianorollViewerGroup) {
		this.pianorollViewerGroup = pianorollViewerGroup;
	}
}
