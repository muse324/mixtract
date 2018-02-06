package net.muse.mixtract.gui;

import java.io.IOException;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.TuneData;
import net.muse.gui.*;
import net.muse.mixtract.data.MXTuneData;

public class MXMainFrame extends MainFrame {

	private static final long serialVersionUID = 1L;

	private JButton analyzeButton = null;

	private ButtonGroup pianorollViewerGroup;

	private JRadioButton realtimeViewButton = null;

	private JRadioButton scoreViewButton = null;

	public MXMainFrame(Mixtract mixtract) throws IOException {
		super(mixtract);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#getPianoroll()
	 */
	@Override
	public MXPianoroll getPianoroll() {
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
	@Override
	public void setTarget(TuneData target) {
		super.setTarget(target);
		getAnalyzeButton().setEnabled(true);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#createGroupingPanel()
	 */
	@Override
	protected GroupingPanel createGroupingPanel() {
		return new MXGroupingPanel();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#createPianoRollPane()
	 */
	@Override
	protected PianoRoll createPianoRoll(MuseApp main) {
		assert main instanceof Mixtract;
		return new MXPianoroll((Mixtract) main);
	}

	@Override
	protected JToolBar getJToolBar() {
		JToolBar jToolBar = super.getJToolBar();
		jToolBar.add(getScoreViewButton()); // Generated
		jToolBar.add(getRealtimeViewButton()); // Generated
		jToolBar.add(getAnalyzeButton()); // Generated
		jToolBar.add(getTempoSettingPanel());
		return jToolBar;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MainFrame#initialize()
	 */
	@Override
	protected void initialize() {
		super.initialize();
		getScoreViewButton().setSelected(true);
	}

	/**
	 * This method initializes analyzeButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getAnalyzeButton() {
		if (analyzeButton == null) {
			analyzeButton = new JButton("Analyze");
			analyzeButton.setEnabled(false);
			analyzeButton.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							assert data instanceof MXTuneData;
							main.analyzeStructure((MXTuneData) data, null);
							main.notifySetTarget();
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
	private JRadioButton getRealtimeViewButton() {
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
	private JRadioButton getScoreViewButton() {
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

}
