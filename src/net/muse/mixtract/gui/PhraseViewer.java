package net.muse.mixtract.gui;

import java.awt.*;

import javax.swing.*;

import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.Group;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/23
 */
public class PhraseViewer extends JDialog implements CanvasMouseListener {

	private static final long serialVersionUID = 1L;

	/* 制御データ */
	private Mixtract main;
	private MainFrame owner;
	private final Group group;
	@Deprecated
	private int pixelperbeat = 20;

	/* 描画モード */
	private boolean isEdited;

	/* イベント制御ボタン */
	private JButton resetButton = null;
	private ButtonGroup viewSelectionGroup = new ButtonGroup(); // @jve:decl-index=0:
	private JToggleButton showDynamics = null;
	private JToggleButton showTempo = null;
	private JToggleButton showArticulation = null;
	private JRadioButton selDynButton = null;
	private JRadioButton selTmpButton = null;
	private JRadioButton selArtButton = null;

	/* グラフィック */
	private JPanel jContentPane = null;
	private JPanel commandPanel = null;
	private JPanel jPanel = null;
	private PianoRollSmall groupPianoRoll = null;
	private KeyBoard keyboard = null;
	private JPanel jPanel1 = null;
	private JSplitPane jSplitPane = null;
	private JPanel viewerTogglePanel = null;
	private JLabel jLabel = null;
	private CurveViewPanel curveViewerPanel = null;

	/**
	 * This method initializes resetButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getResetButton() {
		if (resetButton == null) {
			resetButton = new JButton();
			resetButton.setText("Reflesh"); // Generated
			resetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getCurveViewerPanel().reset();
					repaint();
				}
			});
		}
		return resetButton;
	}

	/**
	 * This method initializes groupPianoRoll
	 *
	 * @return javax.swing.JPanel
	 */
	PianoRollSmall getGroupPianoRoll() {
		if (groupPianoRoll == null) {
			groupPianoRoll = new PianoRollSmall();
			groupPianoRoll.setController(main);
			groupPianoRoll.setGroup(group);
			groupPianoRoll.setTarget(owner.getTarget());
			groupPianoRoll.selectGroup(group);
		}
		return groupPianoRoll;
	}

	/**
	 * This method initializes keyboard
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getKeyboard() {
		if (keyboard == null) {
			keyboard = new KeyBoard();
			keyboard.setLayout(new GridBagLayout());
			keyboard.resetKeyRegister();
			keyboard.setKeyRegister(group.getBeginGroupNote());
		}
		return keyboard;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setPreferredSize(new Dimension(400, 200));
			jPanel1.add(getKeyboard(), BorderLayout.WEST);
			jPanel1.add(getGroupPianoRoll(), BorderLayout.CENTER);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			jSplitPane.setResizeWeight(0.5D);
			jSplitPane.setOneTouchExpandable(true); // Generated
			jSplitPane.setDividerLocation(150); // Generated
			jSplitPane.setBottomComponent(getJPanel());
			jSplitPane.setTopComponent(getJPanel1());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes viewerTogglePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getViewerTogglePanel() {
		if (viewerTogglePanel == null) {
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
			jLabel = new JLabel();
			jLabel.setText("Edit"); // Generated
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 0; // Generated
			gridBagConstraints1.anchor = GridBagConstraints.EAST; // Generated
			gridBagConstraints1.gridy = 3; // Generated
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridx = 0; // Generated
			gridBagConstraints.anchor = GridBagConstraints.EAST; // Generated
			gridBagConstraints.gridy = 2; // Generated
			viewerTogglePanel = new JPanel();
			viewerTogglePanel.setLayout(new GridBagLayout()); // Generated
			viewerTogglePanel
					.add(getDynamicsRadioButton(), gridBagConstraints3); // Generated
			viewerTogglePanel.add(getTempoRadioButton(), gridBagConstraints); // Generated
			viewerTogglePanel.add(getArticulationRadioButton(),
					gridBagConstraints1); // Generated
			viewerTogglePanel.add(jLabel, gridBagConstraints2); // Generated
			viewerTogglePanel.add(getDynamicsToggleButton(),
					gridBagConstraints4); // Generated
			viewerTogglePanel.add(getTempoToggleButton(), gridBagConstraints5); // Generated
			viewerTogglePanel.add(getArticulationToggleButton(),
					gridBagConstraints6); // Generated
		}
		return viewerTogglePanel;
	}

	/**
	 * This method initializes selDynButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getDynamicsRadioButton() {
		if (selDynButton == null) {
			selDynButton = new JRadioButton();
			selDynButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("Edit: dynamics");
					getCurveViewerPanel().setCurve(group.getDynamicsCurve());
					repaint();
				}
			});
			viewSelectionGroup.add(selDynButton);
		}
		return selDynButton;
	}

	/**
	 * This method initializes selTmpButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getTempoRadioButton() {
		if (selTmpButton == null) {
			selTmpButton = new JRadioButton();
			selTmpButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("Edit: tempo");
					getCurveViewerPanel().setCurve(group.getTempoCurve());
					repaint();
				}
			});
			viewSelectionGroup.add(selTmpButton);
		}
		return selTmpButton;
	}

	/**
	 * This method initializes selArtButton
	 *
	 * @return javax.swing.JRadioButton
	 */
	private JRadioButton getArticulationRadioButton() {
		if (selArtButton == null) {
			selArtButton = new JRadioButton();
			selArtButton.addItemListener(new java.awt.event.ItemListener() {
				public void itemStateChanged(java.awt.event.ItemEvent e) {
					System.out.println("Edit: articulation");
					getCurveViewerPanel()
							.setCurve(group.getArticulationCurve());
					repaint();
				}
			});
			viewSelectionGroup.add(selArtButton);
		}
		return selArtButton;
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
										+ ((JToggleButton) e.getSource())
												.isSelected());
					getCurveViewerPanel().showDynamicsCurve(
							((JToggleButton) e.getSource()).isSelected());

				}
			});
		}
		return showDynamics;
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
										+ ((JToggleButton) e.getSource())
												.isSelected());
					getCurveViewerPanel().showTempoCurve(
							((JToggleButton) e.getSource()).isSelected());
				}
			});
		}
		return showTempo;
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
										+ ((JToggleButton) e.getSource())
												.isSelected());
					getCurveViewerPanel().showArticulationCurve(
							((JToggleButton) e.getSource()).isSelected());
				}
			});
		}
		return showArticulation;
	}

	/**
	 * This method initializes curveViewerPanel
	 *
	 * @return javax.swing.JPanel
	 */
	CurveViewPanel getCurveViewerPanel() {
		if (curveViewerPanel == null) {
			curveViewerPanel = new CurveViewPanel(group);
			curveViewerPanel.showDynamicsCurve(getDynamicsToggleButton()
					.isSelected());
			curveViewerPanel
					.showTempoCurve(getTempoToggleButton().isSelected());
			curveViewerPanel
					.showArticulationCurve(getArticulationToggleButton()
							.isSelected());
			curveViewerPanel.repaint();
		}
		return curveViewerPanel;
	}

	/**
	 * @param main.getFrame()
	 * @param group
	 */
	PhraseViewer(Mixtract main, Group group) {
		super(main.getFrame());
		this.main = main;
		this.owner = (MainFrame) main.getFrame();
		this.group = group;
		setController(main);
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Dialog#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		super.setTitle(title + " - " + this.getClass().getSimpleName());
	}

	/**
	 * @param gr
	 * @return
	 */
	boolean contains(Group gr) {
		return gr.equals(group);
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @return the isEdited
	 */
	boolean isEdited() {
		return isEdited;
	}

	void setController(Mixtract main) {
		getCurveViewerPanel().setController(main);
	}

	/**
	 * @param isEdited the isEdited to set
	 */
	protected void setEdited(boolean isEdited) {
		this.isEdited = isEdited;
	}

	/**
	 * This method initializes commandPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCommandPanel() {
		if (commandPanel == null) {
			commandPanel = new JPanel();
			commandPanel.setLayout(new FlowLayout());
			commandPanel.add(getResetButton(), null); // Generated
		}
		return commandPanel;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getCommandPanel(), BorderLayout.SOUTH); // Generated
			jContentPane.add(getJSplitPane(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BorderLayout());
			jPanel.add(getViewerTogglePanel(), BorderLayout.WEST); // Generated
			jPanel.add(getCurveViewerPanel(), BorderLayout.CENTER); // Generated
		}
		return jPanel;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("group name");
	}

	public void setShowCurrentX(boolean showCurrentX, int x) {
		getGroupPianoRoll().setShowCurrentX(showCurrentX, x);
		getCurveViewerPanel().setShowCurrentX(showCurrentX, x);
	}

	void preset() {
//		resetCurve();
		getDynamicsRadioButton().setSelected(true); // Generated
	}

} // @jve:decl-index=0:visual-constraint="26,7"
