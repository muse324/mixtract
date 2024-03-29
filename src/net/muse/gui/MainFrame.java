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

import javax.imageio.ImageIO;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import net.muse.app.MuseApp;
import net.muse.command.MuseAppCommand;
import net.muse.command.MuseAppCommandType;
import net.muse.data.Concierge;
import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.gui.CurveView;
import net.muse.mixtract.gui.PartSelectorPanel;
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

	private static String WINDOW_TITLE = "MuseApp";
	protected static int pixelperbeat = 30;
	private static final long serialVersionUID = 1L;

	public TuneData data; // @jve:decl-index=0:

	/** JFrameおよびDockのアイコン */
	protected Image icon;

	private final MuseApp app;
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
	private final int shortcutKey;
	private JButton stopButton = null;
	private JPanel tempoSettingPanel = null;
	private JSlider tempoSlider = null;
	private CurveView tempoView;
	private JInternalFrame viewer = null;
	private JDesktopPane desktop;
	private JScrollBar timeScrollBar = null;
	private JMenuItem menuAbout;
	private JMenuItem menuPreference;
	private JMenu helpMenu;
	private JMenuItem quitMenu;
	private JMenuItem xmlMenuItem;
	private JSplitPane curveSplitPane;
	private JScrollPane pianorollPanel;
	private JScrollPane structurePane;
	protected JPanel tuneViewPanel;
	protected JPanel toolBarPanel;
	protected JPanel tempoHeaderViewPanel;
	protected JPanel tempoCurvePanel;
	protected PartSelectorPanel partSelectorPanel;
	private KeyBoard keyboard;
	private JPanel dynHeaderView;
	private JPanel dynCurvePanel;
	private JMenu midiMenu;
	private JMenuItem playStopMenu;
	private JToolBar toolBar;

	/**
	 * This method initializes
	 *
	 * @param app
	 * @throws IOException
	 */
	public MainFrame(MuseApp app) throws IOException {
		super();
		this.app = app;
		app.synthe().addMidiEventListener(this);
		butler().addTuneDataListenerList(this);
		butler().addTuneDataListenerList(app.synthe());

		// TODO ウィンドウアイコンの設定
		// ただし、OSXにはウィンドウアイコンはないため表示されない
		// Dockアイコンは、これによって設定することはできない.
		// Leopard以降はApple Java ExtensionsのApplicationクラスで設定可能.
		// this.icon =
		// ImageIO.read(getClass().getResource("window_icon.png"));
		// this.setIconImage(this.icon);

		// システムのデフォルトのコマンド修飾キーを取得する.
		// Windowsならctrl, OSXならばmetaになる.
		final Toolkit tk = Toolkit.getDefaultToolkit();
		shortcutKey = tk.getMenuShortcutKeyMask();

		initialize();
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

	@Override public void actionPerformed(ActionEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#addGroup(jp.crestmuse.
	 * mixtract
	 * .data.Group)
	 */
	@Override public void addGroup(Group g) {
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.TuneDataListener#changeExpression(jp.crestmuse
	 * .mixtract.data.PhraseProfile.PhraseCurveType)
	 */
	@Override public void changeExpression(PhraseCurveType type) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#deleteGroup(javax.swing
	 * .JLabel)
	 */
	@Override public void deleteGroup(GroupLabel g) {
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#deselect(javax.swing.JLabel
	 * )
	 */
	@Override public void deselect(GroupLabel g) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#editGroup(javax.swing.JLabel
	 * )
	 */
	@Override public void editGroup(GroupLabel g) {
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
			butler().addTuneDataListenerList(dynamicsView);
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
			groupingPanel.setController(app());
			butler().addTuneDataListenerList(groupingPanel);
		}
		return groupingPanel;
	}

	/**
	 * This method initializes akeyboard
	 *
	 * @return javax.swing.JPanel
	 */
	public KeyBoard getKeyboard() {
		if (keyboard == null) {
			keyboard = new KeyBoard(app());
			butler().addTuneDataListenerList(keyboard);
		}
		return keyboard;
	}

	/**
	 * This method initializes pianoroll
	 *
	 * @return javax.swing.JPanel
	 */
	public PianoRoll getPianoroll() {
		if (pianoroll == null) {
			pianoroll = createPianoRoll(app());
		}
		return pianoroll;
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
			butler().addTuneDataListenerList(tempoView);
		}
		return tempoView;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.sound.MIDIEventListener#pausePlaying()
	 */
	@Override public void pausePlaying() {
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
	@Override public void selectGroup(GroupLabel g, boolean flg) {}

	@Override public void selectTopNote(NoteData note, boolean b) {
		// TODO 自動生成されたメソッド・スタブ

	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.TuneDataListener#setTarget(net.muse.data.TuneData)
	 */
	@Override public void setTarget(TuneData target) {
		data = target;
		getViewer().setTitle(data.getOutputFile().getName());
		getSaveAsMenu().setEnabled(true);
		getSaveMenu().setEnabled(true);
		getDataSetButton().setEnabled(true);
		getPlayButton().setEnabled(true);
		getPlayStopMenu().setEnabled(true);

		if (data != null) {
			try {
				getBpmValue().setText(String.valueOf(data.getBPM().get(0)));
				getTempoSlider().setValue(data.getBPM().get(0));
			} catch (final IndexOutOfBoundsException e) {
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
			} catch (final PropertyVetoException e) {
				JOptionPane.showMessageDialog(this, e.getLocalizedMessage());
			}
		}
	}

	@Override public void startPlaying(String smfFilename) {
		butler().printConsole("playing...");
		playButton.setEnabled(false);
		stopButton.setEnabled(true);
		setPlaybarMode(MuseAppCommandType.STOP);
		bpmValue.setEnabled(false);
		tempoSlider.setEnabled(false);
	}

	@Override public void stopPlaying() {
		butler().printConsole("Sound stopped.");
		playButton.setEnabled(true);
		stopButton.setEnabled(false);
		setPlaybarMode(MuseAppCommandType.PLAY);
		bpmValue.setEnabled(true);
		tempoSlider.setEnabled(true);
	}

	@Override public void stopPlaying(MIDIController synthe) {
		throw new UnsupportedOperationException();
	}

	protected MuseApp app() {
		return app;
	}

	protected Concierge butler() {
		return app().butler();
	}

	protected GroupingPanel createGroupingPanel() {
		return new GroupingPanel(app());
	}

	protected PianoRoll createPianoRoll(MuseApp app) {
		return new PianoRoll(app);
	}

	/**
	 * This method initializes curveSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	protected JSplitPane getCurveSplitPane() {
		if (curveSplitPane == null) {
			curveSplitPane = new JSplitPane();
			curveSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT); // Generated
			curveSplitPane.setPreferredSize(new Dimension(20, 200)); // Generated
			curveSplitPane.setResizeWeight(0.5D); // Generated
			curveSplitPane.setOneTouchExpandable(true); // Generated
			curveSplitPane.setTopComponent(getDynamicsCurvePane()); // Generated
			curveSplitPane.setBottomComponent(getTempoCurvePane()); // Generated
			curveSplitPane.setDividerSize(10); // Generated
		}
		return curveSplitPane;
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
			dataSetButton.addActionListener(e -> data.setNoteScheduleEvent());
		}
		return dataSetButton;
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
	 * This method initializes partSelectorPanel
	 *
	 * @return javax.swing.JPanel
	 */
	protected JPanel getPartSelectorPanel() {
		if (partSelectorPanel == null) {
			partSelectorPanel = new PartSelectorPanel();
			partSelectorPanel.setPreferredSize(new Dimension(KeyBoard
					.getKeyWidth(), 24)); // Generated
			butler().addTuneDataListenerList(partSelectorPanel);
		}
		return partSelectorPanel;
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
	 * This method initializes pianorollPane
	 *
	 * @return javax.swing.JScrollPane
	 */
	protected JScrollPane getPianorollPane() {
		if (pianorollPanel == null) {
			pianorollPanel = new JScrollPane();
			pianorollPanel.setRowHeaderView(getKeyboard());
			pianorollPanel.setViewportView(getPianoroll()); // Generated
			pianorollPanel.setHorizontalScrollBar(getTimeScrollBar());
			pianorollPanel.setHorizontalScrollBarPolicy(
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			pianorollPanel.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return pianorollPanel;
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
			playButton.setMnemonic(KeyEvent.VK_SPACE);
			final MuseAppCommand cmd = app().searchCommand(
					MuseAppCommandType.PLAY);
			playButton.setActionCommand(cmd.name());
			playButton.setToolTipText("Play");
			playButton.setEnabled(false); // Generated
			playButton.setText("Play");
			playButton.addActionListener(e -> butler().notifyStartPlaying(
					data));
		}
		return playButton;
	}

	protected JMenuItem getPreferenceMenuItem() {
		if (menuPreference == null) {
			menuPreference = new JMenuItem("環境設定(E)");
			menuPreference.setMnemonic(KeyEvent.VK_E);
			menuPreference.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_COMMA, shortcutKey));
			menuPreference.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override public void actionPerformed(ActionEvent e) {
					onPreference();
				}
			});
		}
		return menuPreference;
	}

	protected JMenuItem getQuitMenuItemForMac() {
		if (quitMenu == null) {
			quitMenu = new JMenuItem("終了(Q)");
			quitMenu.setMnemonic(KeyEvent.VK_Q);
			quitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,
					shortcutKey));
			quitMenu.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override public void actionPerformed(ActionEvent e) {
					quit();
				}
			});
		}
		return quitMenu;
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
			final MuseAppCommandType cmd = MuseAppCommandType.STOP;
			stopButton.setActionCommand(cmd.name());
			stopButton.setEnabled(false); // Generated
			stopButton.setToolTipText("Stop");
			stopButton.setText("Stop");
			stopButton.addActionListener(e -> butler().notifyStopPlaying());
		}
		return stopButton;
	}

	/**
	 * This method initializes structurePane
	 *
	 * @return javax.swing.JScrollPane
	 */
	protected JScrollPane getStructurePane() {
		if (structurePane == null) {
			structurePane = new JScrollPane();
			structurePane.setRowHeaderView(getPartSelectorPanel());
			structurePane.setViewportView(getGroupingPanel());
			structurePane.setHorizontalScrollBar(getTimeScrollBar());
			structurePane.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		return structurePane;
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

	protected JToolBar getToolBar() {
		if (toolBar == null) {
			tempoValueLabel = new JLabel();
			tempoValueLabel.setText("   BPM:");
			toolBar = new JToolBar();
			toolBar.add(getDataSetButton()); // Generated
			toolBar.add(getPlayButton()); // Generated
			toolBar.add(getPauseButton()); // Generated
			toolBar.add(getStopButton()); // Generated
			toolBar.add(getTempoSettingPanel());
		}
		return toolBar;
	}

	/**
	 * This method initializes tuneViewPanel
	 *
	 * @return javax.swing.JPanel
	 */
	protected JPanel getTuneViewPanel() {
		if (tuneViewPanel == null) {
			tuneViewPanel = new JPanel();
			tuneViewPanel.setLayout(new BorderLayout()); // Generated
			tuneViewPanel.add(getStructurePane(), BorderLayout.NORTH);
			tuneViewPanel.add(getPianorollPane(), BorderLayout.CENTER); // Generated
			tuneViewPanel.add(getCurveSplitPane(), BorderLayout.SOUTH); // Generated
		}
		return tuneViewPanel;
	}

	protected JMenuItem getVersionMenuItem() {
		if (menuAbout == null) {
			menuAbout = new JMenuItem("バージョン情報(V)");
			menuAbout.setMnemonic(KeyEvent.VK_V);
			menuAbout.addActionListener(new AbstractAction() {
				private static final long serialVersionUID = 1L;

				@Override public void actionPerformed(ActionEvent e) {
					onAbout();
				}
			});
		}
		return menuAbout;
	}

	protected String getWindowTitle() {
		return WINDOW_TITLE;
	}

	/** ウィンドウ表示の初期設定を行います。 */
	protected void initialize() {
		this.setTitle(getWindowTitle()); // ウィンドウのタイトル
		this.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize()); // ウィンドウサイズ
		this.setMinimumSize(new Dimension(640, 480));
		this.setContentPane(getDesktop()); // メインの描画領域(詳細)
		this.setJMenuBar(getMenubar()); // メニューバー
	}

	protected void onAbout() {
		JOptionPane.showMessageDialog(this,
				"Mixtract version 1.0.1 -CEDEC2011-", "Version Information",
				JOptionPane.INFORMATION_MESSAGE);
	}

	protected void onPreference() {
		final JTextArea textArea = new JTextArea();
		textArea.setText(getSystemProperties(System.getProperty(
				"line.separator")));

		final JScrollPane scr = new JScrollPane(textArea);
		scr.setPreferredSize(new Dimension(400, 300));

		JOptionPane.showMessageDialog(this, scr);
	}

	protected void quit() {
		JOptionPane.showMessageDialog(this, "終了します.");
		System.exit(0);
	}

	protected void readfile(File f) throws IOException {
		butler().readfile(f, app().getProjectDirectory());
	}

	protected void savefile() {
		try {
			data.setNoteScheduleEvent();
			data.writefile();
			saveScreenShot();
		} catch (final IOException e1) {
			e1.printStackTrace();
		} catch (final AWTException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * @throws AWTException
	 * @throws IOException
	 */
	protected void saveScreenShot() throws AWTException, IOException {
		final Point pos = getViewer().getLocationOnScreen();
		final Dimension size = getViewer().getSize();
		size.height -= 15;
		final Robot robot = new Robot();
		final Image img = robot.createScreenCapture(new Rectangle(pos.x, pos.y,
				size.width, size.height));
		final File fp = new File(data.getOutputFile(), "screenshot.png");
		if (!ImageIO.write(createBufferedImage(img), "PNG", fp)) {
			throw new IOException("フォーマットが対象外");
		}
	}

	protected void switchPlayMode(ActionEvent e) {
		if (e.getActionCommand().equals(MuseAppCommandType.PLAY.name()))
			butler().notifyStartPlaying(data);
		else if (e.getActionCommand().equals(MuseAppCommandType.STOP.name()))
			butler().notifyStopPlaying();
	}

	private BufferedImage createBufferedImage(Image img) {
		final BufferedImage bimg = new BufferedImage(img.getWidth(null), img
				.getHeight(null), BufferedImage.TYPE_INT_RGB);

		final Graphics g = bimg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bimg;
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
			bpmValue.addActionListener(e -> {
				getTempoSlider().setIgnoreRepaint(true);
				getTempoSlider().setValue(Integer.parseInt(bpmValue.getText()));
				getTempoSlider().setIgnoreRepaint(false);
				System.out.println("actionPerformed()"); // TODO
				if (data != null) {
					data.setBPM(0, Integer.parseInt(bpmValue.getText()));
				}
			});
		}
		return bpmValue;
	}

	/**
	 * This method initializes jPanel4
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDynamicsCurvePane() {
		if (dynCurvePanel == null) {
			dynCurvePanel = new JPanel();
			dynCurvePanel.setLayout(new BorderLayout()); // Generated
			dynCurvePanel.add(getDynamicsView(), BorderLayout.CENTER); // Generated
			dynCurvePanel.add(getDynamicsHeaderView(), BorderLayout.WEST); // Generated
		}
		return dynCurvePanel;
	}

	/**
	 * This method initializes jPanel7
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDynamicsHeaderView() {
		if (dynHeaderView == null) {
			dynHeaderView = new JPanel();
			dynHeaderView.setLayout(new BorderLayout()); // Generated
			dynHeaderView.setPreferredSize(new Dimension(KeyBoard.getKeyWidth(),
					16)); // Generated
			dynHeaderView.add(new JLabel("dynamics"), BorderLayout.NORTH); // Generated
		}
		return dynHeaderView;
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

			// バージョン情報と終了コマンド
			if (!app().isMac()) {
				fileMenu.addSeparator();
				fileMenu.add(getQuitMenuItemForMac());

				getMenubar().add(getHelpMenu());
				getHelpMenu().add(getPreferenceMenuItem());
				getHelpMenu().add(getVersionMenuItem());
			} else {
				fileMenu.addSeparator();
				fileMenu.add(getQuitMenu());
			}
		}
		return fileMenu;
	}

	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu("ヘルプ(H)");
			helpMenu.setMnemonic(KeyEvent.VK_H);
		}
		return helpMenu;
	}

	/**
	 * This method initializes importXMLMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	protected JMenuItem getImportXMLMenu() {
		if (xmlMenuItem == null) {
			final MuseAppCommand cmd = app().searchCommand(
					MuseAppCommandType.OPEN_MUSICXML);
			xmlMenuItem = new JMenuItem();
			xmlMenuItem.setText(cmd.getText());
			xmlMenuItem.setMnemonic('M');
			xmlMenuItem.setAccelerator(KeyStroke.getKeyStroke('M',
					shortcutKey));
			xmlMenuItem.setActionCommand(MuseAppCommandType.OPEN_MUSICXML
					.name());
			xmlMenuItem.addActionListener(new MouseActionListener(app(), this));
		}
		return xmlMenuItem;
	}

	/**
	 * メニューバーを生成します。
	 */
	private JMenuBar getMenubar() {
		if (menubar == null) {
			menubar = new JMenuBar();
			menubar.add(getFileMenu());
			menubar.add(getMIDIMenu());
		}
		return menubar;
	}

	private JMenu getMIDIMenu() {
		if (midiMenu == null) {
			midiMenu = new JMenu();
			midiMenu.setText("MIDI");
			midiMenu.setMnemonic('M'); // ショートカットキー
			midiMenu.add(getPlayStopMenu());
			// midiMenu.add(getStopMenu());
		}
		return midiMenu;
	}

	/**
	 * This method initializes openProjectMenu
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenProjectMenu() {
		final JMenuItem m = new JMenuItem();
		m.setText("Open Project File...");
		m.setMnemonic('O');
		m.setAccelerator(KeyStroke.getKeyStroke('O', shortcutKey));
		m.addActionListener(e -> {
			try {
				final JFileChooser fc = app() != null ? new JFileChooser(app()
						.getProjectDirectory()) : new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				final int res = fc.showOpenDialog(null);
				if (res == JFileChooser.APPROVE_OPTION) {
					readfile(fc.getSelectedFile());
				}
			} catch (final IOException e1) {
				e1.printStackTrace();
			}
		});
		return m;
	}

	private JMenuItem getPlayStopMenu() {
		if (playStopMenu == null) {
			playStopMenu = new JMenuItem();
			setPlaybarMode(MuseAppCommandType.PLAY);
			playStopMenu.setEnabled(false);
			playStopMenu.setMnemonic(KeyEvent.VK_SPACE);
			playStopMenu.setAccelerator(KeyStroke.getKeyStroke(
					KeyEvent.VK_SPACE, shortcutKey));
			playStopMenu.addActionListener(e -> switchPlayMode(e));
		}
		return playStopMenu;
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
			quitMenu.addActionListener(e -> quit());
		}
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
			saveAsMenu.addActionListener(e -> {
				final JFileChooser fc = new JFileChooser(app()
						.getProjectDirectory());
				final int res = fc.showSaveDialog(null);
				if (res == JOptionPane.NO_OPTION) {
					System.out.println("cancelled.");
					return;
				}
				data.createNewOutputFile(fc);
				savefile();
				getViewer().setTitle(data.getOutputFile().getName());
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
			saveMenu.addActionListener(e -> savefile());
		}
		return saveMenu;
	}

	// システムプロパティをダンプする
	private String getSystemProperties(String lineSep) {
		final ArrayList<String> keys = new ArrayList<>();
		final StringBuffer buf = new StringBuffer();
		for (final Enumeration<?> enm = System.getProperties().keys(); enm
				.hasMoreElements();) {
			final String key = (String) enm.nextElement();
			keys.add(key);
		}
		Collections.sort(keys);
		for (final String string : keys) {
			final String key = string;
			buf.append(key + "=" + System.getProperty(key) + lineSep);
		}
		buf.append("*EOF*");
		return buf.toString();
	}

	private JPanel getTempoCurvePane() {
		if (tempoCurvePanel == null) {
			tempoCurvePanel = new JPanel();
			tempoCurvePanel.setLayout(new BorderLayout()); // Generated
			tempoCurvePanel.add(getTempoView(), BorderLayout.CENTER); // Generated
			tempoCurvePanel.add(getTempoHeaderView(), BorderLayout.WEST); // Generated
		}
		return tempoCurvePanel;
	}

	private JPanel getTempoHeaderView() {
		if (tempoHeaderViewPanel == null) {
			tempoHeaderViewPanel = new JPanel();
			tempoHeaderViewPanel.setLayout(new BorderLayout()); // Generated
			tempoHeaderViewPanel.setPreferredSize(new Dimension(KeyBoard
					.getKeyWidth(), 10)); // Generated
			tempoHeaderViewPanel.add(new JLabel("tempo"), BorderLayout.NORTH); // Generated
		}
		return tempoHeaderViewPanel;
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
			tempoSlider.addChangeListener(e -> {
				getBpmValue().setText(String.valueOf(tempoSlider.getValue()));
				System.out.println("stateChanged()"); // TODO
				if (data != null) {
					data.setBPM(0, tempoSlider.getValue());
				}
			});
		}
		return tempoSlider;
	}

	private JScrollBar getTimeScrollBar() {
		if (timeScrollBar == null) {
			timeScrollBar = new JScrollBar(Adjustable.HORIZONTAL) {
				/**
				 *
				 */
				private static final long serialVersionUID = 1L;

				@Override public Dimension getPreferredSize() {
					final Dimension dim = super.getPreferredSize();
					return new Dimension(dim.width, 20);
				}
			};
		}
		return timeScrollBar;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getToolBarPanel() {
		if (toolBarPanel == null) {
			final GridBagConstraints g = new GridBagConstraints();
			g.fill = GridBagConstraints.VERTICAL; // Generated
			g.gridy = 0; // Generated
			g.weightx = 1.0; // Generated
			g.anchor = GridBagConstraints.NORTHWEST; // Generated
			toolBarPanel = new JPanel();
			toolBarPanel.setLayout(new GridBagLayout()); // Generated
			toolBarPanel.add(getToolBar(), g); // Generated
		}
		return toolBarPanel;
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
			viewer.setContentPane(getTuneViewPanel()); // Generated
			// viewer.setBounds(new Rectangle(8, 8, 238, 155)); // Generated
		}
		return viewer;
	}

	private void setPlaybarMode(MuseAppCommandType type) {
		final MuseAppCommand cmd = app().searchCommand(type);
		playStopMenu.setActionCommand(type.name());
		playStopMenu.setText(cmd.getText());
	}

} // @jve:decl-index=0:visual-constraint="10,10"
