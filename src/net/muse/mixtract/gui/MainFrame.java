package net.muse.mixtract.gui;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.swing.*;

import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.*;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.sound.MixtractMIDIController;
import net.muse.sound.MIDIController;
import net.muse.sound.MIDIEventListener;

/**
 * <h1>MainFrame</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose <address>CrestMuse Project,
 *         JST</address> <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/10/24
 */
public class MainFrame extends JFrame implements TuneDataListener,
		MIDIEventListener, ActionListener {

	/**  */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_WIDTH = 1260;
	private final MixtractMIDIController synthe;
	private JMenuBar menubar = null;
	private JMenu fileMenu = null;
	private JMenuItem openProjectMenu = null;
	private JMenuItem importXMLMenu = null;
	private JMenuItem quitMenu = null;
	private Mixtract main;
	private TuneData data; // @jve:decl-index=0:
	private JDesktopPane desktop = null;
	private JInternalFrame viewer = null;
	private JPanel jContentPane = null;
	private PianoRoll pianoroll = null;
	private KeyBoard keyboard = null; // @jve:decl-index=0:visual-constraint="15,847"
	private JScrollPane pianorollPane = null;
	private JButton playButton = null;
	private JButton stopButton = null;
	private JButton pauseButton = null;
	private JButton dataSetButton = null;
	private JPanel mainPanel = null;
	private GroupingPanel groupingPanel = null; // @jve:decl-index=0:visual-constraint="-16,274"
	private CurveView tempoView;
	private CurveView dynamicsView;
	private PartSelectorPanel partSelectorPanel = null; // @jve:decl-index=0:visual-constraint="13,752"
	private JCheckBox p1selector = null;
	private JSplitPane curveSplitPane = null;
	private JPanel jPanel4 = null;
	private JPanel jPanel5 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JPanel jPanel6 = null;
	private JPanel jPanel7 = null;
	private JRadioButton scoreViewButton = null;
	private JRadioButton realtimeViewButton = null;
	private ButtonGroup pianorollViewerGroup;
	private JMenuItem saveMenu = null;
	private JButton analyzeButton = null;
	private JPanel phraseView = null; // @jve:decl-index=0:visual-constraint="303,697"
	private JPanel jPanel = null;
	private JToolBar jJToolBarBar = null;
	private JTextField bpmValue = null;
	private JLabel jLabel2 = null;
	static int pixelperbeat = 30;
	private JScrollPane structurePane = null;
	private JSlider tempoSlider = null;
	private JLabel tempoValueLabel = null;
	private JPanel tempoSettingPanel = null;
	// JFrameおよびDockのアイコン
	protected Image icon;
	private int shortcutKey;
	private JMenuItem saveAsMenu;

	/**
	 * This method initializes
	 *
	 * @param mixtract
	 * @throws IOException
	 */
	public MainFrame(Mixtract mixtract) throws IOException {
		super();
		this.main = mixtract;
		pianorollViewerGroup = new ButtonGroup();
		synthe = new MixtractMIDIController(main.getMidiDeviceName(),
				Mixtract.getTicksPerBeat());
		synthe.addMidiEventListener(this);
		this.main.addTuneDataListener(this);
		this.main.addTuneDataListener(synthe);

		// TODO ウィンドウアイコンの設定
		// ただし、OSXにはウィンドウアイコンはないため表示されない
		// Dockアイコンは、これによって設定することはできない.
		// Leopard以降はApple Java ExtensionsのApplicationクラスで設定可能.
		// this.icon =
		// ImageIO.read(getClass().getResource("window_icon.png"));
		// this.setIconImage(this.icon);

		// システムのデフォルトのコマンド修飾キーを取得する.
		// Windowsならctrl, OSXならばmetaになる.
		Toolkit tk = Toolkit.getDefaultToolkit();
		shortcutKey = tk.getMenuShortcutKeyMask();

		initialize();
		getScoreViewButton().setSelected(true);
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setContentPane(getDesktop()); // Generated
		this.setTitle("Mixtract"); // Generated
		this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
		this.setJMenuBar(getMenubar());

	}

	/**
	 * This method initializes menubar
	 *
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getMenubar() {
		if (menubar == null) {
			menubar = new JMenuBar();
			menubar.add(getFileMenu());
		}
		return menubar;
	}

	/**
	 * This method initializes fileMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic('F');
			fileMenu.add(getOpenProjectMenu());
			fileMenu.add(getImportXMLMenu());
			fileMenu.add(getSaveMenu());
			fileMenu.add(getSaveAsMenu());
			fileMenu.addSeparator();
			fileMenu.add(getQuitMenu());

			// バージョン情報と終了コマンド
			// "apple.laf.useScreenMenuBar"が定義されている場合はシステムで用意するので不要.
			// if (System.getProperty("apple.laf.useScreenMenuBar") == null)
			// {
			if (!main.isMac()) {
				fileMenu.addSeparator();
				JMenuItem quitMenu = new JMenuItem("終了(Q)");
				quitMenu.setMnemonic(KeyEvent.VK_Q);
				quitMenu.setAccelerator(KeyStroke.getKeyStroke(	KeyEvent.VK_Q,
																shortcutKey));
				quitMenu.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						quit();
					}
				});
				fileMenu.add(quitMenu);

				JMenu helpMenu = new JMenu("ヘルプ(H)");
				helpMenu.setMnemonic(KeyEvent.VK_H);
				getMenubar().add(helpMenu);

				JMenuItem menuPreference = new JMenuItem("環境設定(E)");
				menuPreference.setMnemonic(KeyEvent.VK_E);
				menuPreference.setAccelerator(KeyStroke
						.getKeyStroke(KeyEvent.VK_COMMA, shortcutKey));
				menuPreference.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						onPreference();
					}
				});
				helpMenu.add(menuPreference);

				JMenuItem menuAbout = new JMenuItem("バージョン情報(V)");
				menuAbout.setMnemonic(KeyEvent.VK_V);
				menuAbout.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						onAbout();
					}
				});
				helpMenu.add(menuAbout);
			}

		}
		return fileMenu;
	}

	private JMenuItem getSaveAsMenu() {
		if (saveAsMenu == null) {
			saveAsMenu = new JMenuItem();
			saveAsMenu.setText("Save As");
			saveAsMenu.setEnabled(false);
			saveAsMenu.setMnemonic(KeyEvent.VK_S);
			saveAsMenu.setAccelerator(KeyStroke
					.getKeyStroke(KeyEvent.VK_S, shortcutKey
							+ ActionEvent.SHIFT_MASK));
			saveAsMenu.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = new JFileChooser(main
							.getProjectDirectory());
					int res = fc.showSaveDialog(null);
					if (res == JOptionPane.NO_OPTION) {
						System.out.println("cancelled.");
						return;
					}
					data.createNewOutputFile(fc);
					savefile();
					getViewer().setTitle(data.getOutputFile().getName());
				}
			});
		}
		return saveAsMenu;
	}

	/**
	 * This method initializes tempoPanel
	 *
	 * @return javax.swing.JPanel
	 */
	public CurveView getTempoView() {
		if (tempoView == null) {
			tempoView = new CurveView(PhraseCurveType.TEMPO, 280, 10, 10);
			main.addTuneDataListener(tempoView);
		}
		return tempoView;
	}

	/**
	 * This method initializes tempoPanel
	 *
	 * @return javax.swing.JPanel
	 */
	public CurveView getDynamicsView() {
		if (dynamicsView == null) {
			dynamicsView = new CurveView(PhraseCurveType.DYNAMICS, 127, 0, 10);
			main.addTuneDataListener(dynamicsView);
		}
		return dynamicsView;
	}

	/**
	 * This method initializes openProjectMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenProjectMenu() {
		if (openProjectMenu == null) {
			openProjectMenu = new JMenuItem();
			openProjectMenu.setText("Open Project File...");
			openProjectMenu.setMnemonic('O');
			openProjectMenu.setAccelerator(KeyStroke
					.getKeyStroke('O', shortcutKey));
			openProjectMenu
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								JFileChooser fc = (main != null)
										? new JFileChooser(main
												.getProjectDirectory())
										: new JFileChooser();
								fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
								int res = fc.showOpenDialog(null);
								if (res == JFileChooser.APPROVE_OPTION) {
									main.readfile(	fc.getSelectedFile(),
													main.getProjectDirectory());
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (InvalidMidiDataException e1) {
								e1.printStackTrace();
							}
						}
					});
		}
		return openProjectMenu;
	}

	/**
	 * This method initializes importXMLMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getImportXMLMenu() {
		if (importXMLMenu == null) {
			importXMLMenu = new JMenuItem();
			importXMLMenu.setText("Import MusicXML File...");
			importXMLMenu.setMnemonic('M');
			importXMLMenu.setAccelerator(KeyStroke.getKeyStroke('M',
																shortcutKey));
			importXMLMenu
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								JFileChooser fc = (main != null)
										? new JFileChooser(main
												.getMusicXMLDirectory())
										: new JFileChooser();
								int res = fc.showOpenDialog(null);
								if (res == JFileChooser.APPROVE_OPTION) {
									main.readfile(	fc.getSelectedFile(),
													new File(
															main.getProjectDirectory(),
															fc.getSelectedFile()
																	.getName()
																	+ Mixtract
																			.getProjectFileExtension()));
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (InvalidMidiDataException e1) {
								// TODO 自動生成された catch ブロック
								e1.printStackTrace();
							}
						}
					});
		}
		return importXMLMenu;
	}

	/**
	 * This method initializes quitMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getQuitMenu() {
		if (quitMenu == null) {
			quitMenu = new JMenuItem();
			quitMenu.setMnemonic('Q');
			quitMenu.setText("Quit");
			quitMenu.setAccelerator(KeyStroke.getKeyStroke('Q', shortcutKey));

			quitMenu.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					quit();
				}
			});
		}
		return quitMenu;
	}

	protected void quit() {
		JOptionPane.showMessageDialog(this, "終了します.");
		System.exit(0);
	}

	protected void onPreference() {
		JTextArea textArea = new JTextArea();
		textArea.setText(getSystemProperties(System
				.getProperty("line.separator")));

		JScrollPane scr = new JScrollPane(textArea);
		scr.setPreferredSize(new Dimension(400, 300));

		JOptionPane.showMessageDialog(this, scr);
	}

	// システムプロパティをダンプする
	private String getSystemProperties(String lineSep) {
		ArrayList keys = new ArrayList();
		StringBuffer buf = new StringBuffer();
		for (Enumeration<?> enm = System.getProperties().keys(); enm
				.hasMoreElements();) {
			String key = (String) enm.nextElement();
			keys.add(key);
		}
		Collections.sort(keys);
		for (Iterator ite = keys.iterator(); ite.hasNext();) {
			String key = (String) ite.next();
			buf.append(key + "=" + System.getProperty(key) + lineSep);
		}
		buf.append("*EOF*");
		return buf.toString();
	}

	/**
	 * This method initializes desktop
	 *
	 * @return javax.swing.JDesktopPane
	 */
	private JDesktopPane getDesktop() {
		if (desktop == null) {
			desktop = new JDesktopPane();
			desktop.setLayout(new BorderLayout()); // Generated
			desktop.setPreferredSize(new Dimension(1024, 600)); // Generated
			desktop.add(getMainPanel(), BorderLayout.CENTER); // Generated
			desktop.add(getJPanel(), BorderLayout.NORTH); // Generated
		}
		return desktop;
	}

	/**
	 * This method initializes viewer
	 *
	 * @return javax.swing.JInternalFrame
	 */
	private JInternalFrame getViewer() {
		if (viewer == null) {
			viewer = new JInternalFrame();
			viewer.setClosable(true);
			viewer.setResizable(true);
			viewer.setMaximizable(true);
			viewer.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			viewer.setBounds(new Rectangle(0, 0, 540, 390));
			viewer.setContentPane(getJContentPane()); // Generated
			// viewer.setBounds(new Rectangle(8, 8, 238, 155)); // Generated
		}
		return viewer;
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout()); // Generated
			jContentPane.add(getStructurePane(), java.awt.BorderLayout.NORTH);
			jContentPane.add(getPianorollPane(), BorderLayout.CENTER); // Generated
			jContentPane.add(getCurveSplitPane(), BorderLayout.SOUTH); // Generated
		}
		return jContentPane;
	}

	/**
	 * This method initializes pianoroll
	 *
	 * @return javax.swing.JPanel
	 */
	public PianoRoll getPianoroll() {
		if (pianoroll == null) {
			pianoroll = new PianoRoll();
			pianoroll.setController(main);
			pianoroll.setPreferredSize(new Dimension(DEFAULT_WIDTH, KeyBoard
					.getKeyboardHeight() /3*2));
		}
		return pianoroll;
	}

	/**
	 * This method initializes akeyboard
	 *
	 * @return javax.swing.JPanel
	 */
	private KeyBoard getKeyboard() {
		if (keyboard == null) {
			keyboard = new KeyBoard();
			main.addTuneDataListener(keyboard);
		}
		return keyboard;
	}

	/**
	 * This method initializes pianorollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getPianorollPane() {
		if (pianorollPane == null) {
			pianorollPane = new JScrollPane();
			pianorollPane.setRowHeaderView(getKeyboard());
			pianorollPane.setViewportView(getPianoroll()); // Generated
			pianorollPane
					.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			pianorollPane
					.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return pianorollPane;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton();
			playButton.setIcon(new ImageIcon(getClass()
					.getResource("images/Play16.gif"))); // Generated
			playButton.setActionCommand("Play");
			playButton.setToolTipText("Play");
			playButton.setEnabled(false); // Generated
			playButton.setText("Play");
			playButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (data == null)
						return;
					if (!data.getRootGroup(0).hasChild())
						data.initializeNoteEvents();
					data.setNoteScheduleEvent();
					synthe.notifyStartPlaying(data.getInputFilename());
				}
			});
		}
		return playButton;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setIcon(new ImageIcon(getClass()
					.getResource("images/Stop16.gif"))); // Generated
			stopButton.setActionCommand("Stop");
			stopButton.setEnabled(false); // Generated
			stopButton.setToolTipText("Stop");
			stopButton.setText("Stop");
			stopButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// data.setNoteScheduleEvent();
					synthe.notifyStopPlaying();
				}
			});
		}
		return stopButton;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JButton();
			pauseButton.setIcon(new ImageIcon(getClass()
					.getResource("images/Pause16.gif"))); // Generated
			pauseButton.setActionCommand("Pause");
			pauseButton.setEnabled(false); // Generated
			pauseButton.setToolTipText("Pause");
			pauseButton.setText("Pause");
		}
		return pauseButton;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getDataSetButton() {
		if (dataSetButton == null) {
			dataSetButton = new JButton();
			dataSetButton.setText("Set"); // Generated
			dataSetButton.setEnabled(false); // Generated
			dataSetButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							data.setNoteScheduleEvent();
						}
					});
		}
		return dataSetButton;
	}

	/**
	 * This method initializes mainPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			mainPanel.setLayout(null); // Generated
			mainPanel.add(getViewer(), null); // Generated
		}
		return mainPanel;
	}

	/**
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	GroupingPanel getGroupingPanel() {
		if (groupingPanel == null) {
			groupingPanel = new GroupingPanel();
			groupingPanel.setController(main);
			main.addTuneDataListener(groupingPanel);
		}
		return groupingPanel;
	}

	/** 演奏事例データベースのリストを更新します． */
	public void refreshDatabase() {
		// dbListModel.clear();
		// for (final String fn : Mixtract.db.getDatabaseListName()) {
		// dbListModel.addElement(fn);
		// }
	}

	/**
	 * This method initializes partSelectorPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPartSelectorPanel() {
		if (partSelectorPanel == null) {
			partSelectorPanel = new PartSelectorPanel();
			partSelectorPanel.setPreferredSize(new Dimension(KeyBoard
					.getKeyWidth(), 24)); // Generated
			main.addTuneDataListener(partSelectorPanel);
		}
		return partSelectorPanel;
	}

	/**
	 * This method initializes curveSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getCurveSplitPane() {
		if (curveSplitPane == null) {
			curveSplitPane = new JSplitPane();
			curveSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT); // Generated
			curveSplitPane.setPreferredSize(new Dimension(20, 200)); // Generated
			curveSplitPane.setResizeWeight(0.5D); // Generated
			curveSplitPane.setOneTouchExpandable(true); // Generated
			curveSplitPane.setTopComponent(getJPanel4()); // Generated
			curveSplitPane.setBottomComponent(getJPanel5()); // Generated
			curveSplitPane.setDividerSize(10); // Generated
		}
		return curveSplitPane;
	}

	/**
	 * This method initializes jPanel4
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel4() {
		if (jPanel4 == null) {
			jLabel = new JLabel();
			jLabel.setText("dynamics"); // Generated
			jPanel4 = new JPanel();
			jPanel4.setLayout(new BorderLayout()); // Generated
			jPanel4.add(getDynamicsView(), BorderLayout.CENTER); // Generated
			jPanel4.add(getJPanel7(), BorderLayout.WEST); // Generated
		}
		return jPanel4;
	}

	/**
	 * This method initializes jPanel5
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel5() {
		if (jPanel5 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("tempo"); // Generated
			jPanel5 = new JPanel();
			jPanel5.setLayout(new BorderLayout()); // Generated
			jPanel5.add(getTempoView(), BorderLayout.CENTER); // Generated
			jPanel5.add(getJPanel6(), BorderLayout.WEST); // Generated
		}
		return jPanel5;
	}

	/**
	 * This method initializes jPanel6
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel6() {
		if (jPanel6 == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.gridx = -1; // Generated
			gridBagConstraints3.ipady = 80; // Generated
			gridBagConstraints3.gridy = -1; // Generated
			jPanel6 = new JPanel();
			jPanel6.setLayout(new BorderLayout()); // Generated
			jPanel6.setPreferredSize(new Dimension(KeyBoard.getKeyWidth(), 10)); // Generated
			jPanel6.add(jLabel1, BorderLayout.NORTH); // Generated
		}
		return jPanel6;
	}

	/**
	 * This method initializes jPanel7
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel7() {
		if (jPanel7 == null) {
			jPanel7 = new JPanel();
			jPanel7.setLayout(new BorderLayout()); // Generated
			jPanel7.setPreferredSize(new Dimension(KeyBoard.getKeyWidth(), 16)); // Generated
			jPanel7.add(jLabel, BorderLayout.NORTH); // Generated
		}
		return jPanel7;
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
			pianorollViewerGroup.add(scoreViewButton);
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
			pianorollViewerGroup.add(realtimeViewButton);
			realtimeViewButton
					.addItemListener(new java.awt.event.ItemListener() {
						public void itemStateChanged(java.awt.event.ItemEvent e) {
							if (((JRadioButton) e.getItem()).isSelected()) {
								getPianoroll()
										.setViewMode(ViewerMode.REALTIME_VIEW);
								getGroupingPanel()
										.setViewMode(ViewerMode.REALTIME_VIEW);
								getDynamicsView()
										.setViewMode(ViewerMode.REALTIME_VIEW);
								getTempoView()
										.setViewMode(ViewerMode.REALTIME_VIEW);
							}
						}
					});
		}
		return realtimeViewButton;
	}

	protected void onAbout() {
		JOptionPane.showMessageDialog(	this,
										"Mixtract version 1.0.1 -CEDEC2011-",
										"Version Information",
										JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * This method initializes saveMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getSaveMenu() {
		if (saveMenu == null) {
			saveMenu = new JMenuItem();
			saveMenu.setText("Save");
			saveMenu.setEnabled(false);
			saveMenu.setMnemonic(KeyEvent.VK_S);
			saveMenu.setAccelerator(KeyStroke.getKeyStroke(	KeyEvent.VK_S,
															shortcutKey));
			saveMenu.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					savefile();
				}

			});
		}
		return saveMenu;
	}

	/**
	 * @throws AWTException
	 * @throws IOException
	 */
	protected void saveScreenShot() throws AWTException, IOException {
		Point pos = getViewer().getLocationOnScreen();
		Dimension size = getViewer().getPreferredSize();
		size.height -= 15;
		data.writeScreenShot(pos, size);
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
			analyzeButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							main.analyzeStructure(data, null);
							main.notifySetTarget();
						}
					});
		}
		return analyzeButton;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
			gridBagConstraints21.fill = GridBagConstraints.VERTICAL; // Generated
			gridBagConstraints21.gridy = 0; // Generated
			gridBagConstraints21.weightx = 1.0; // Generated
			gridBagConstraints21.anchor = GridBagConstraints.WEST; // Generated
			gridBagConstraints21.gridx = 4; // Generated
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout()); // Generated
			jPanel.add(getJJToolBarBar(), gridBagConstraints21); // Generated
		}
		return jPanel;
	}

	/**
	 * This method initializes jJToolBarBar
	 *
	 * @return javax.swing.JToolBar
	 */
	private JToolBar getJJToolBarBar() {
		if (jJToolBarBar == null) {
			tempoValueLabel = new JLabel();
			tempoValueLabel.setText("   BPM:");
			jJToolBarBar = new JToolBar();
			jJToolBarBar.add(getDataSetButton()); // Generated
			jJToolBarBar.add(getPlayButton()); // Generated
			jJToolBarBar.add(getPauseButton()); // Generated
			jJToolBarBar.add(getStopButton()); // Generated
			jJToolBarBar.add(getScoreViewButton()); // Generated
			jJToolBarBar.add(getRealtimeViewButton()); // Generated
			jJToolBarBar.add(getAnalyzeButton()); // Generated
			jJToolBarBar.add(getTempoSettingPanel());
		}
		return jJToolBarBar;
	}

	/**
	 * This method initializes bpmValue
	 *
	 * @return javax.swing.JTextField
	 */
	private JTextField getBpmValue() {
		if (bpmValue == null) {
			bpmValue = new JTextField();
			bpmValue.setText(" 100"); // Generated
			bpmValue.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getTempoSlider().setIgnoreRepaint(true);
					getTempoSlider().setValue(	Integer.parseInt(bpmValue
														.getText()));
					getTempoSlider().setIgnoreRepaint(false);
					System.out.println("actionPerformed()"); // TODO
					if (data != null) {
						data.setBPM(0, Integer.parseInt(bpmValue.getText()));
					}
				}
			});
		}
		return bpmValue;
	}

	/**
	 * This method initializes structurePane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getStructurePane() {
		if (structurePane == null) {
			structurePane = new JScrollPane();
			structurePane.setRowHeaderView(getPartSelectorPanel());
			structurePane.setViewportView(getGroupingPanel());
			structurePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}
		return structurePane;
	}

	/**
	 * This method initializes tempoSlider
	 *
	 * @return javax.swing.JSlider
	 */
	private JSlider getTempoSlider() {
		if (tempoSlider == null) {
			tempoSlider = new JSlider();
			tempoSlider.setPaintLabels(true);
			tempoSlider.setMinimum(30);
			tempoSlider.setMaximum(218);
			tempoSlider.setValue(130);
			tempoSlider.setMajorTickSpacing(50);
			tempoSlider.setMinorTickSpacing(10);
			tempoSlider.setPaintTicks(true);
			tempoSlider
					.addChangeListener(new javax.swing.event.ChangeListener() {
						public void stateChanged(javax.swing.event.ChangeEvent e) {
							getBpmValue().setText(	String.valueOf(tempoSlider
															.getValue()));
							System.out.println("stateChanged()"); // TODO
							if (data != null) {
								data.setBPM(0, tempoSlider.getValue());
							}
						}
					});
		}
		return tempoSlider;
	}

	/**
	 * This method initializes tempoSettingPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getTempoSettingPanel() {
		if (tempoSettingPanel == null) {
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridy = 0;
			gridBagConstraints2.gridx = 2;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.gridy = 0;
			gridBagConstraints.gridx = 0;
			tempoSettingPanel = new JPanel();
			tempoSettingPanel.setLayout(new GridBagLayout());
			tempoSettingPanel.add(tempoValueLabel, gridBagConstraints);
			tempoSettingPanel.add(getBpmValue(), gridBagConstraints1);
			tempoSettingPanel.add(getTempoSlider(), gridBagConstraints2);
		}
		return tempoSettingPanel;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Mixtract.main(args);
	}

	public void setTarget(TuneData target) {
		data = target;
		getViewer().setTitle(data.getOutputFile().getName());
		getSaveAsMenu().setEnabled(true);
		getSaveMenu().setEnabled(true);
		getDataSetButton().setEnabled(true);
		getPlayButton().setEnabled(true);
		getAnalyzeButton().setEnabled(true);
		if (data != null) {
			getBpmValue().setText(String.valueOf(data.getBPM().get(0)));
			getTempoSlider().setValue(data.getBPM().get(0));
		}
		if (!getViewer().isVisible()) {
			try {
				getViewer().setMaximum(true);
				getViewer().validate();
				getViewer().pack();
				getViewer().setVisible(true);
			} catch (PropertyVetoException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage());
			}
		}
	}

	public void startPlaying(String smfFilename) {
		Mixtract.log.println("playing...");
		playButton.setEnabled(false);
		stopButton.setEnabled(true);
		bpmValue.setEnabled(false);
		tempoSlider.setEnabled(false);
	}

	public void stopPlaying() {
		Mixtract.log.println("Sound stopped.");
		playButton.setEnabled(true);
		stopButton.setEnabled(false);
		bpmValue.setEnabled(true);
		tempoSlider.setEnabled(true);
	}

	public void stopPlaying(MIDIController synthe) {
		throw new UnsupportedOperationException();
	}

	public void actionPerformed(ActionEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.TuneDataListener#changeExpression(jp.crestmuse
	 * .mixtract.data.PhraseProfile.PhraseCurveType)
	 */
	public void changeExpression(PhraseCurveType type) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#addGroup(jp.crestmuse.
	 * mixtract
	 * .data.Group)
	 */
	public void addGroup(Group g) {
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#deleteGroup(javax.swing
	 * .JLabel)
	 */
	public void deleteGroup(GroupLabel g) {
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#deselect(javax.swing.JLabel
	 * )
	 */
	public void deselect(GroupLabel g) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#editGroup(javax.swing.JLabel
	 * )
	 */
	public void editGroup(GroupLabel g) {
		throw new UnsupportedOperationException(); // TODO 実装
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#selectGroup(javax.swing
	 * .JLabel, boolean)
	 */
	public void selectGroup(GroupLabel g, boolean flg) {}

	/**
	 * @return
	 */
	final TuneData getTarget() {
		return data;
	}

	/**
	 * 発音時刻や音長に対する横軸の長さを求めます．
	 *
	 * @param val
	 * @return
	 */
	public static int getXOfNote(final double val) {
		return (int) Math.round(val / pixelperbeat);
	}

	protected void savefile() {
		try {
			data.setNoteScheduleEvent();
			data.writefile();
			// save screen shot
			saveScreenShot();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (AWTException e1) {
			e1.printStackTrace();
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/* (非 Javadoc)
	 * @see net.muse.sound.MIDIEventListener#pausePlaying()
	 */
	public void pausePlaying() {
		// do nothing (unsupported in the CEDEC version)
	}
} // @jve:decl-index=0:visual-constraint="10,10"
