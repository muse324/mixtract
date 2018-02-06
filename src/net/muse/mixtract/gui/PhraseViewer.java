package net.muse.mixtract.gui;

import java.awt.*;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.gui.*;
import net.muse.mixtract.data.MXGroup;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/23
 */
public class PhraseViewer extends InfoViewer implements CanvasMouseListener {

	private static final long serialVersionUID = 1L;

	private CurveViewPanel curveViewerPanel = null;
	private PianoRollSmall groupPianoRoll = null;

	private JToggleButton showDynamics = null;
	private JToggleButton showTempo = null;
	private JToggleButton showArticulation = null;

	private ButtonGroup viewSelectionGroup = new ButtonGroup();

	public PhraseViewer(MuseApp app, Group gr) {
		super(app, gr);
	}

	@Override
	public MXGroup group() {
		return (MXGroup) group;
	}

	public void setShowCurrentX(boolean showCurrentX, int x) {
		getGroupPianoRoll().setShowCurrentX(showCurrentX, x);
		getCurveViewerPanel().setShowCurrentX(showCurrentX, x);
	}

	public void setTitle(String title) {
		super.setTitle(title + " - " + this.getClass().getSimpleName());
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.InfoViewer#getJContentPane()
	 */
	@Override
	protected JPanel getJContentPane() {
		JPanel p = new JPanel(new BorderLayout());
		p.add(getCommandPanel(), BorderLayout.SOUTH); // Generated
		p.add(getJSplitPane(), BorderLayout.CENTER);
		return p;
	}

	/**
	 * @return main
	 */
	protected Mixtract main() {
		return (Mixtract) main;
	}

	protected MXMainFrame owner() {
		return (MXMainFrame) owner;
	}

	protected void preset() {
		getDynamicsRadioButton().setSelected(true); // Generated
	}

	/**
	 * This method initializes selArtButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getArticulationRadioButton() {
		JRadioButton selArtButton = new JRadioButton();
		selArtButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				System.out.println("Edit: articulation");
				getCurveViewerPanel().setCurve(group().getArticulationCurve());
				repaint();
			}
		});
		getViewSelectionGroup().add(selArtButton);
		return selArtButton;
	}

	/**
	 * This method initializes showArticulation
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getArticulationToggleButton() {
		if (showArticulation == null) {
			showArticulation = new JToggleButton();
			showArticulation.setText("A"); // Generated
			showArticulation.setSelected(true); // Generated
			showArticulation.setFont(new Font("Dialog", Font.BOLD, 8)); // Generated
			showArticulation.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged(): "
							+ ((JToggleButton) e.getSource()).isSelected());
					getCurveViewerPanel().showArticulationCurve(
							((JToggleButton) e.getSource()).isSelected());
				}
			});
		}
		return showArticulation;
	}

	/**
	 * This method initializes commandPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCommandPanel() {
		JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout());
		commandPanel.add(getResetButton(), null); // Generated
		return commandPanel;
	}

	/**
	 * This method initializes curveViewerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private CurveViewPanel getCurveViewerPanel() {
		if (curveViewerPanel == null) {
			curveViewerPanel = new CurveViewPanel(group());
			curveViewerPanel.showDynamicsCurve(getDynamicsToggleButton()
					.isSelected());
			curveViewerPanel.showTempoCurve(getTempoToggleButton()
					.isSelected());
			curveViewerPanel.showArticulationCurve(getArticulationToggleButton()
					.isSelected());
			curveViewerPanel.repaint();
		}
		return curveViewerPanel;
	}

	/**
	 * This method initializes selDynButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDynamicsRadioButton() {
		JRadioButton selDynButton = new JRadioButton();
		selDynButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				System.out.println("Edit: dynamics");
				getCurveViewerPanel().setCurve(group().getDynamicsCurve());
				repaint();
			}
		});
		getViewSelectionGroup().add(selDynButton);
		return selDynButton;
	}

	/**
	 * This method initializes showDynamics
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getDynamicsToggleButton() {
		if (showDynamics == null) {
			showDynamics = new JToggleButton();
			showDynamics.setText("D"); // Generated
			showDynamics.setSelected(true); // Generated
			showDynamics.setFont(new Font("Dialog", Font.BOLD, 8)); // Generated
			showDynamics.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged(): "
							+ ((JToggleButton) e.getSource()).isSelected());
					getCurveViewerPanel().showDynamicsCurve(((JToggleButton) e
							.getSource()).isSelected());

				}
			});
		}
		return showDynamics;
	}

	/**
	 * This method initializes groupPianoRoll
	 *
	 * @return javax.swing.JPanel
	 */
	private PianoRollSmall getGroupPianoRoll() {
		if (groupPianoRoll == null) {
			groupPianoRoll = new PianoRollSmall();
			groupPianoRoll.setController(main());
			groupPianoRoll.setGroup(group());
			groupPianoRoll.setTarget(owner().getTarget());
			groupPianoRoll.selectGroup(group());
		}
		return groupPianoRoll;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.add(getViewerTogglePanel(), BorderLayout.WEST); // Generated
		jPanel.add(getCurveViewerPanel(), BorderLayout.CENTER); // Generated
		return jPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		JPanel jPanel1 = new JPanel();
		jPanel1.setLayout(new BorderLayout());
		jPanel1.setPreferredSize(new Dimension(400, 200));
		jPanel1.add(getKeyboard(), BorderLayout.WEST);
		jPanel1.add(getGroupPianoRoll(), BorderLayout.CENTER);
		return jPanel1;
	}

	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		JSplitPane p = new JSplitPane();
		p.setOrientation(JSplitPane.VERTICAL_SPLIT);
		p.setResizeWeight(0.5D);
		p.setOneTouchExpandable(true); // Generated
		p.setDividerLocation(150); // Generated
		p.setBottomComponent(getJPanel());
		p.setTopComponent(getJPanel1());
		return p;
	}

	/**
	 * This method initializes keyboard
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getKeyboard() {
		KeyBoard k = new KeyBoard(480);
		k.setLayout(new GridBagLayout());
		k.resetKeyRegister();
		k.setKeyRegister(group().getBeginGroupNote());
		return k;
	}

	/**
	 * This method initializes resetButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getResetButton() {
		JButton resetButton = new JButton();
		resetButton.setText("Reflesh"); // Generated
		resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getCurveViewerPanel().reset();
				repaint();
			}
		});
		return resetButton;
	}

	/**
	 * This method initializes selTmpButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getTempoRadioButton() {
		JRadioButton selTmpButton = new JRadioButton();
		selTmpButton.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(java.awt.event.ItemEvent e) {
				System.out.println("Edit: tempo");
				getCurveViewerPanel().setCurve(group().getTempoCurve());
				repaint();
			}
		});
		getViewSelectionGroup().add(selTmpButton);
		return selTmpButton;
	}

	/**
	 * This method initializes showTempo
	 *
	 * @return javax.swing.JToggleButton
	 */
	private JToggleButton getTempoToggleButton() {
		if (showTempo == null) {
			showTempo = new JToggleButton();
			showTempo.setText("T"); // Generated
			showTempo.setSelected(true); // Generated
			showTempo.setFont(new Font("Dialog", Font.BOLD, 8)); // Generated
			showTempo.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("itemStateChanged(): "
							+ ((JToggleButton) e.getSource()).isSelected());
					getCurveViewerPanel().showTempoCurve(((JToggleButton) e
							.getSource()).isSelected());
				}
			});
		}
		return showTempo;
	}

	/**
	 * This method initializes viewerTogglePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getViewerTogglePanel() {
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 1; // Generated
		gridBagConstraints6.gridy = 3; // Generated
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 1; // Generated
		gridBagConstraints5.gridy = 2; // Generated
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 1; // Generated
		gridBagConstraints4.gridy = 1; // Generated
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridy = 1; // Generated
		gridBagConstraints3.anchor = GridBagConstraints.EAST; // Generated
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0; // Generated
		gridBagConstraints2.insets = new Insets(0, 5, 0, 0); // Generated
		gridBagConstraints2.gridwidth = 1; // Generated
		gridBagConstraints2.anchor = GridBagConstraints.WEST; // Generated
		gridBagConstraints2.gridy = 0; // Generated
		JLabel jLabel = new JLabel();
		jLabel.setText("Edit"); // Generated
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 0; // Generated
		gridBagConstraints1.anchor = GridBagConstraints.EAST; // Generated
		gridBagConstraints1.gridy = 3; // Generated
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0; // Generated
		gridBagConstraints.anchor = GridBagConstraints.EAST; // Generated
		gridBagConstraints.gridy = 2; // Generated

		JPanel viewerTogglePanel = new JPanel();
		viewerTogglePanel.setLayout(new GridBagLayout()); // Generated
		viewerTogglePanel.add(getDynamicsRadioButton(), gridBagConstraints3); // Generated
		viewerTogglePanel.add(getTempoRadioButton(), gridBagConstraints); // Generated
		viewerTogglePanel.add(getArticulationRadioButton(),
				gridBagConstraints1); // Generated
		viewerTogglePanel.add(jLabel, gridBagConstraints2); // Generated
		viewerTogglePanel.add(getDynamicsToggleButton(), gridBagConstraints4); // Generated
		viewerTogglePanel.add(getTempoToggleButton(), gridBagConstraints5); // Generated
		viewerTogglePanel.add(getArticulationToggleButton(),
				gridBagConstraints6); // Generated
		return viewerTogglePanel;
	}

	/**
	 * @return viewSelectionGroup
	 */
	private ButtonGroup getViewSelectionGroup() {
		if (viewSelectionGroup == null)
			viewSelectionGroup = new ButtonGroup();
		return viewSelectionGroup;
	}
} // @jve:decl-index=0:visual-constraint="26,7"
