package net.muse.gui;

import java.awt.AWTException;
import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.CurveView;
import net.muse.mixtract.gui.PartSelectorPanel;
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

	private static int pixelperbeat = 30;
	private static final long serialVersionUID = 1L;

	public TuneData data; // @jve:decl-index=0:

	/** JFrameおよびDockのアイコン */
	protected Image icon;

	protected MuseApp main;
	protected PianoRoll pianoroll = null;
	protected JLabel tempoValueLabel = null;
	private JTextField bpmValue = null;
	private JButton dataSetButton = null;
	private CurveView dynamicsView;
	private JMenu fileMenu = null;
	private GroupingPanel groupingPanel = null; // @jve:decl-index=0:visual-constraint="-16,274"
	private JMenuBar menubar = null;
	private JButton pauseButton = null;
	private JButton playButton = null;
	private JMenuItem saveAsMenu;
	private JMenuItem saveMenu = null;
	private int shortcutKey;
	private JButton stopButton = null;
	private final MixtractMIDIController synthe;
	private JPanel tempoSettingPanel = null;
	private JSlider tempoSlider = null;
	private CurveView tempoView;
	private JInternalFrame viewer = null;
	private JDesktopPane desktop;
	private JScrollBar timeScrollBar = null;

	/**
	 * 発音時刻や音長に対する横軸の長さを求めます．
	 *
	 * @param val
	 * @return
	 */
	public static int getXOfNote(final double val) {
		return (int) Math.round(val / pixelperbeat);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Mixtract.main(args);
	}

	/**
	 * This method initializes
	 *
	 * @param app
	 * @throws IOException
	 */
	public MainFrame(MuseApp app) throws IOException {
		super();
		this.main = app;
		synthe = new MixtractMIDIController(main.getMidiDeviceName(), main
				.getTicksPerBeat());
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
	}

	public void actionPerformed(ActionEvent e) {}

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
	 * jp.crestmuse.mixtract.gui.TuneDataListener#changeExpression(jp.crestmuse
	 * .mixtract.data.PhraseProfile.PhraseCurveType)
	 */
	public void changeExpression(PhraseCurveType type) {}

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
	 * This method initializes jPanel1
	 *
	 * @return javax.swing.JPanel
	 */
	public GroupingPanel getGroupingPanel() {
		if (groupingPanel == null) {
			groupingPanel = createGroupingPanel();
			groupingPanel.setController(main);
			main.addTuneDataListener(groupingPanel);
		}
		return groupingPanel;
	}

	/**
	 * This method initializes pianoroll
	 *
	 * @return javax.swing.JPanel
	 */
	public PianoRoll getPianoroll() {
		if (pianoroll == null) {
			pianoroll = createPianoRoll(main);
		}
		return pianoroll;
	}

	protected PianoRoll createPianoRoll(MuseApp main) {
		return new PianoRoll(main);
	}

	/**
	 * @return
	 */
	public final TuneData getTarget() {
		return data;
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

	/*
	 * (非 Javadoc)
	 * @see net.muse.sound.MIDIEventListener#pausePlaying()
	 */
	public void pausePlaying() {
		// do nothing (unsupported in the CEDEC version)
	}

	/** 演奏事例データベースのリストを更新します． */
	public void refreshDatabase() {
		// dbListModel.clear();
		// for (final String fn : Mixtract.db.getDatabaseListName()) {
		// dbListModel.addElement(fn);
		// }
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#selectGroup(javax.swing
	 * .JLabel, boolean)
	 */
	public void selectGroup(GroupLabel g, boolean flg) {}

	public void setTarget(TuneData target) {
		data = target;
		getViewer().setTitle(data.getOutputFile().getName());
		getSaveAsMenu().setEnabled(true);
		getSaveMenu().setEnabled(true);
		getDataSetButton().setEnabled(true);
		getPlayButton().setEnabled(true);

		if (data != null) {
			try {
				getBpmValue().setText(String.valueOf(data.getBPM().get(0)));
				getTempoSlider().setValue(data.getBPM().get(0));
			} catch (IndexOutOfBoundsException e) {
				getBpmValue().setText("120");
				getTempoSlider().setValue(120);
			}

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

	protected GroupingPanel createGroupingPanel() {
		return new GroupingPanel();
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	protected JButton getDataSetButton() {
		if (dataSetButton == null) {
			dataSetButton = new JButton();
			dataSetButton.setText("Set"); // Generated
			dataSetButton.setEnabled(false); // Generated
			dataSetButton.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							data.setNoteScheduleEvent();
						}
					});
		}
		return dataSetButton;
	}

	protected JToolBar getJToolBar() {
		tempoValueLabel = new JLabel();
		tempoValueLabel.setText("   BPM:");
		JToolBar jToolBar = new JToolBar();
		jToolBar.add(getDataSetButton()); // Generated
		jToolBar.add(getPlayButton()); // Generated
		jToolBar.add(getPauseButton()); // Generated
		jToolBar.add(getStopButton()); // Generated
		jToolBar.add(getTempoSettingPanel());
		return jToolBar;
	}

	/**
	 * This method initializes jButton
	 *
	 * @return javax.swing.JButton
	 */
	protected JButton getPauseButton() {
		if (pauseButton == null) {
			pauseButton = new JButton();
			pauseButton.setIcon(new ImageIcon(getClass().getResource(
					"images/Pause16.gif"))); // Generated
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
	protected JButton getPlayButton() {
		if (playButton == null) {
			playButton = new JButton();
			playButton.setIcon(new ImageIcon(getClass().getResource(
					"images/Play16.gif"))); // Generated
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
	protected JButton getStopButton() {
		if (stopButton == null) {
			stopButton = new JButton();
			stopButton.setIcon(new ImageIcon(getClass().getResource(
					"images/Stop16.gif"))); // Generated
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
	 * This method initializes tempoSettingPanel
	 *
	 * @return javax.swing.JPanel
	 */
	protected JPanel getTempoSettingPanel() {
		if (tempoSettingPanel == null) {
			tempoSettingPanel = new JPanel();
			tempoSettingPanel.setLayout(new GridBagLayout());
			tempoSettingPanel.add(tempoValueLabel);
			tempoSettingPanel.add(getBpmValue());
			tempoSettingPanel.add(getTempoSlider());
		}
		return tempoSettingPanel;
	}

	/** ウィンドウ表示の初期設定を行います。 */
	protected void initialize() {
		this.setTitle("Mixtract"); // ウィンドウのタイトル
		this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize()); // ウィンドウサイズ
		this.setContentPane(getDesktop()); // メインの描画領域(詳細)
		this.setJMenuBar(getMenubar()); // メニューバー
	}

	protected void onAbout() {
		JOptionPane.showMessageDialog(this,
				"Mixtract version 1.0.1 -CEDEC2011-", "Version Information",
				JOptionPane.INFORMATION_MESSAGE);
	}

	protected void onPreference() {
		JTextArea textArea = new JTextArea();
		textArea.setText(getSystemProperties(System.getProperty(
				"line.separator")));

		JScrollPane scr = new JScrollPane(textArea);
		scr.setPreferredSize(new Dimension(400, 300));

		JOptionPane.showMessageDialog(this, scr);
	}

	protected void quit() {
		JOptionPane.showMessageDialog(this, "終了します.");
		System.exit(0);
	}

	protected void savefile() {
		try {
			data.setNoteScheduleEvent();
			data.writefile();
			saveScreenShot();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @throws AWTException
	 * @throws IOException
	 */
	protected void saveScreenShot() throws AWTException, IOException {
		Point pos = getViewer().getLocationOnScreen();
		Dimension size = getViewer().getSize();
		size.height -= 15;
		Robot robot = new Robot();
		Image img = robot.createScreenCapture(new Rectangle(pos.x, pos.y,
				size.width, size.height));
		File fp = new File(data.getOutputFile(), "screenshot.png");
		if (!ImageIO.write(createBufferedImage(img), "PNG", fp)) {
			throw new IOException("フォーマットが対象外");
		}
	}

	private BufferedImage createBufferedImage(Image img) {
		BufferedImage bimg = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);

		Graphics g = bimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
	}

	/**
	 * This method initializes desktop
	 *
	 * @return javax.swing.JDesktopPane
	 */
	protected JDesktopPane getDesktop() {
		if (desktop == null) {
			desktop = new JDesktopPane();
			desktop.setLayout(new BorderLayout()); // Generated
			desktop.setBackground(Color.GRAY);
			desktop.add(getViewer(), BorderLayout.CENTER); // Generated
			desktop.add(getToolBarPanel(), BorderLayout.NORTH); // Generated
		}
		return desktop;
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
					getTempoSlider().setValue(Integer.parseInt(bpmValue
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
	 * This method initializes curveSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getCurveSplitPane() {
		JSplitPane p = new JSplitPane();
		p.setOrientation(JSplitPane.VERTICAL_SPLIT); // Generated
		p.setPreferredSize(new Dimension(20, 200)); // Generated
		p.setResizeWeight(0.5D); // Generated
		p.setOneTouchExpandable(true); // Generated
		p.setTopComponent(getDynamicsCurvePane()); // Generated
		p.setBottomComponent(getTempoCurvePane()); // Generated
		p.setDividerSize(10); // Generated
		return p;
	}

	/**
	 * This method initializes jPanel4
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDynamicsCurvePane() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout()); // Generated
		p.add(getDynamicsView(), BorderLayout.CENTER); // Generated
		p.add(getDynamicsHeaderView(), BorderLayout.WEST); // Generated
		return p;
	}

	/**
	 * This method initializes jPanel7
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDynamicsHeaderView() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout()); // Generated
		p.setPreferredSize(new Dimension(KeyBoard.getKeyWidth(), 16)); // Generated
		p.add(new JLabel("dynamics"), BorderLayout.NORTH); // Generated
		return p;
	}

	/**
	 * 「ファイル」メニューを構築します。
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic('F'); // ショートカットキー
			fileMenu.add(getOpenProjectMenu());
			fileMenu.add(getImportXMLMenu());
			fileMenu.add(getSaveMenu());
			fileMenu.add(getSaveAsMenu());
			fileMenu.addSeparator();
			fileMenu.add(getQuitMenu());

			// バージョン情報と終了コマンド
			if (!main.isMac()) {
				fileMenu.addSeparator();
				JMenuItem quitMenu = new JMenuItem("終了(Q)");
				quitMenu.setMnemonic(KeyEvent.VK_Q);
				quitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
						shortcutKey));
				quitMenu.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						quit();
					}
				});
				fileMenu.add(quitMenu);

				getMenubar().add(getHelpMenu());

				JMenuItem menuPreference = new JMenuItem("環境設定(E)");
				menuPreference.setMnemonic(KeyEvent.VK_E);
				menuPreference.setAccelerator(KeyStroke.getKeyStroke(
						KeyEvent.VK_COMMA, shortcutKey));
				menuPreference.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						onPreference();
					}
				});
				getHelpMenu().add(menuPreference);

				JMenuItem menuAbout = new JMenuItem("バージョン情報(V)");
				menuAbout.setMnemonic(KeyEvent.VK_V);
				menuAbout.addActionListener(new AbstractAction() {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						onAbout();
					}
				});
				getHelpMenu().add(menuAbout);
			}
		}
		return fileMenu;
	}

	private JMenu getHelpMenu() {
		JMenu helpMenu = new JMenu("ヘルプ(H)");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		return helpMenu;
	}

	/**
	 * This method initializes importXMLMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getImportXMLMenu() {
		JMenuItem m = new JMenuItem();
		m.setText("Import MusicXML File...");
		m.setMnemonic('M');
		m.setAccelerator(KeyStroke.getKeyStroke('M', shortcutKey));
		m.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					JFileChooser fc = (main != null) ? new JFileChooser(main
							.getMusicXMLDirectory()) : new JFileChooser();
					int res = fc.showOpenDialog(null);
					if (res == JFileChooser.APPROVE_OPTION) {
						main.readfile(fc.getSelectedFile(), new File(main
								.getProjectDirectory(), fc.getSelectedFile()
										.getName() + Mixtract
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
		return m;
	}

	/**
	 * This method initializes akeyboard
	 *
	 * @return javax.swing.JPanel
	 */
	private KeyBoard getKeyboard() {
		KeyBoard keyboard = new KeyBoard(main.getTicksPerBeat());
		main.addTuneDataListener(keyboard);
		return keyboard;
	}

	/**
	 * メニューバーを生成します。
	 */
	private JMenuBar getMenubar() {
		if (menubar == null) {
			menubar = new JMenuBar();
			menubar.add(getFileMenu());
		}
		return menubar;
	}

	/**
	 * This method initializes openProjectMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenProjectMenu() {
		JMenuItem m = new JMenuItem();
		m.setText("Open Project File...");
		m.setMnemonic('O');
		m.setAccelerator(KeyStroke.getKeyStroke('O', shortcutKey));
		m.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					JFileChooser fc = (main != null) ? new JFileChooser(main
							.getProjectDirectory()) : new JFileChooser();
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int res = fc.showOpenDialog(null);
					if (res == JFileChooser.APPROVE_OPTION) {
						main.readfile(fc.getSelectedFile(), main
								.getProjectDirectory());
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InvalidMidiDataException e1) {
					e1.printStackTrace();
				}
			}
		});
		return m;
	}

	/**
	 * This method initializes partSelectorPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPartSelectorPanel() {
		PartSelectorPanel p = new PartSelectorPanel();
		p.setPreferredSize(new Dimension(KeyBoard.getKeyWidth(), 24)); // Generated
		main.addTuneDataListener(p);
		return p;
	}

	/**
	 * This method initializes pianorollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getPianorollPane() {
		JScrollPane p = new JScrollPane();
		p.setRowHeaderView(getKeyboard());
		p.setViewportView(getPianoroll()); // Generated
		p.setHorizontalScrollBar(getTimeScrollBar());
		p.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		return p;
	}

	/**
	 * This method initializes quitMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getQuitMenu() {
		JMenuItem quitMenu = new JMenuItem();
		quitMenu.setMnemonic('Q');
		quitMenu.setText("Quit");
		quitMenu.setAccelerator(KeyStroke.getKeyStroke('Q', shortcutKey));

		quitMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				quit();
			}
		});
		return quitMenu;
	}

	private JMenuItem getSaveAsMenu() {
		if (saveAsMenu == null) {
			saveAsMenu = new JMenuItem();
			saveAsMenu.setText("Save As");
			saveAsMenu.setEnabled(false);
			saveAsMenu.setMnemonic(KeyEvent.VK_S);
			saveAsMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
					shortcutKey + ActionEvent.SHIFT_MASK));
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
			saveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
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
	 * This method initializes structurePane
	 *
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getStructurePane() {
		JScrollPane p = new JScrollPane();
		p.setRowHeaderView(getPartSelectorPanel());
		p.setViewportView(getGroupingPanel());
		p.setHorizontalScrollBar(getTimeScrollBar());
		p.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		return p;
	}

	// システムプロパティをダンプする
	private String getSystemProperties(String lineSep) {
		ArrayList<String> keys = new ArrayList<String>();
		StringBuffer buf = new StringBuffer();
		for (Enumeration<?> enm = System.getProperties().keys(); enm
				.hasMoreElements();) {
			String key = (String) enm.nextElement();
			keys.add(key);
		}
		Collections.sort(keys);
		for (Iterator<String> ite = keys.iterator(); ite.hasNext();) {
			String key = (String) ite.next();
			buf.append(key + "=" + System.getProperty(key) + lineSep);
		}
		buf.append("*EOF*");
		return buf.toString();
	}

	private JPanel getTempoCurvePane() {
		JPanel jPanel5 = new JPanel();
		jPanel5.setLayout(new BorderLayout()); // Generated
		jPanel5.add(getTempoView(), BorderLayout.CENTER); // Generated
		jPanel5.add(getTempoHeaderView(), BorderLayout.WEST); // Generated
		return jPanel5;
	}

	private JPanel getTempoHeaderView() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout()); // Generated
		p.setPreferredSize(new Dimension(KeyBoard.getKeyWidth(), 10)); // Generated
		p.add(new JLabel("tempo"), BorderLayout.NORTH); // Generated
		return p;
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
			tempoSlider.addChangeListener(
					new javax.swing.event.ChangeListener() {
						public void stateChanged(
								javax.swing.event.ChangeEvent e) {
							getBpmValue().setText(String.valueOf(tempoSlider
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
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getToolBarPanel() {
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.VERTICAL; // Generated
		g.gridy = 0; // Generated
		g.weightx = 1.0; // Generated
		g.anchor = GridBagConstraints.NORTHWEST; // Generated
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout()); // Generated
		p.add(getJToolBar(), g); // Generated
		return p;
	}

	/**
	 * This method initializes tuneViewPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getTuneViewPane() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout()); // Generated
		p.add(getStructurePane(), java.awt.BorderLayout.NORTH);
		p.add(getPianorollPane(), BorderLayout.CENTER); // Generated
		p.add(getCurveSplitPane(), BorderLayout.SOUTH); // Generated
//		getStructurePane().getHorizontalScrollBar().setModel(getTimeScrollBar()
//				.getModel());
//		getPianorollPane().getHorizontalScrollBar().setModel(getTimeScrollBar()
//				.getModel());
//		getDynamicsView().getHorizontalScrollBar().setModel(getTimeScrollBar()
//				.getModel());
//		getTempoView().getHorizontalScrollBar().setModel(getTimeScrollBar()
//				.getModel());
		return p;
	}

	private JScrollBar getTimeScrollBar() {
		if (timeScrollBar == null) {
			timeScrollBar = new JScrollBar(Adjustable.HORIZONTAL) {
				@Override public Dimension getPreferredSize() {
					Dimension dim = super.getPreferredSize();
					return new Dimension(dim.width, 0);
				}
			};
		}
		return timeScrollBar;
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
			// viewer.setBounds(new Rectangle(0, 0, 540, 390));
			viewer.setContentPane(getTuneViewPane()); // Generated
			// viewer.setBounds(new Rectangle(8, 8, 238, 155)); // Generated
		}
		return viewer;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
