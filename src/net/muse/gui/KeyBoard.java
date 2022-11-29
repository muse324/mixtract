package net.muse.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.mixtract.sound.MixtractMIDIController;
import net.muse.sound.MIDIController;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/04/14
 */
public class KeyBoard extends JPanel implements MouseListener,
		MouseMotionListener, TuneDataListener {

	/** キーボード一音の描画の縦幅[pixel] */
	private int keyHeight = 7;

	/** 最大音域数 */
	static final int maximumKeyRegister = 88;

	/** 白鍵リスト */
	static ArrayList<Integer> whiteMidiKey = createWhiteMidiKey();

	/**
	 * キーボード一音の描画の横幅[pixel]
	 * <p>
	 * Mac OS X: 105 / Windows: 65
	 */
	private static int keyWidth = 105; // Mac OS X 仕様

	private static final long serialVersionUID = 1L;

	/** 最高音 */
	private static int topNoteNumber = 108;

	/** 最低音 */
	private int bottomNoteNumber = 20;

	/** Y座標に対応するノートナンバーのマップ */
	private final TreeMap<Integer, Integer> keyboardMap;

	/** 押されている鍵盤のノート番号リスト（ソート済） */
	private final TreeSet<Integer> noteNumberOfPushedKey;

	private int selectedKey;

	private MIDIController synthe; // @jve:decl-index=0:

	private int ticksperbeat = 480;

	private MuseApp app;

	/**
	 * @return
	 */
	public int getKeyboardHeight() {
		return keyHeight * maximumKeyRegister;
	}

	/**
	 * @return the keyWidth
	 */
	public static int getKeyWidth() {
		return keyWidth;
	}

	/**
	 * @param notenumber
	 * @return
	 */
	public static int getYPositionOfPitch(int notenumber) {
		return topNoteNumber - notenumber + 5;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		final KeyBoard panel = new KeyBoard(480);
		frame.setContentPane(panel);
		frame.setPreferredSize(panel.getPreferredSize());
		frame.pack();
		frame.setVisible(true);
	}

	public static void setKeyWidth(int w) {
		keyWidth = w;
	}

	/**
	 * 白鍵のピッチクラスをインスタンス化します．
	 */
	private static ArrayList<Integer> createWhiteMidiKey() {
		whiteMidiKey = new ArrayList<Integer>();
		whiteMidiKey.add(0);
		whiteMidiKey.add(2);
		whiteMidiKey.add(4);
		whiteMidiKey.add(5);
		whiteMidiKey.add(7);
		whiteMidiKey.add(9);
		whiteMidiKey.add(11);
		return whiteMidiKey;
	}

	public KeyBoard(int tpb) throws NullPointerException {
		super();
		keyboardMap = new TreeMap<Integer, Integer>();
		noteNumberOfPushedKey = new TreeSet<Integer>();
		ticksperbeat = tpb;
		initialize();
	}

	public KeyBoard(MuseApp app) {
		this(app.getTicksPerBeat());
		this.app = app;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#addGroup(jp.crestmuse.
	 * mixtract
	 * .data.Group)
	 */
	public void addGroup(Group g) {}

	public int addSelectedKeyList(int y) {
		final int key = getKeyPosition(y);
		noteNumberOfPushedKey.add(key);
		selectedKey = key;
		repaint();
		return key;
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
	public void deleteGroup(GroupLabel g) {}

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
	public void editGroup(GroupLabel g) {}

	/**
	 * @return the selectedKey
	 */
	public int getSelectedKey() {
		return selectedKey;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent
	 * )
	 */
	public void mouseDragged(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
		pressNoteKey(e);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
		releaseNoteKey();
	}

	// @Deprecated void setKeyRegister(List<SimpleNoteList> simpleNoteList) {
	// if (simpleNoteList == null || simpleNoteList.size() <= 0)
	// return;
	// topNoteNumber = 0;
	// bottomNoteNumber = 1000;
	//
	// for (final SimpleNoteList list : simpleNoteList) {
	// for (final NoteCompatible note : list) {
	// if (note.notenum() > topNoteNumber)
	// topNoteNumber = note.notenum();
	// if (note.notenum() < bottomNoteNumber)
	// bottomNoteNumber = note.notenum();
	// }
	// }
	// keyRegister = topNoteNumber - bottomNoteNumber;
	// }

	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(final Graphics g) {
		/* おまじない */
		final Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		final RenderingHints qualityHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHints(qualityHints);

		/* キーボード表示 */
		drawKeyboard(g2);
		g2.dispose();
	}

	public void removeSelectedKeyList(int key) {
		if (noteNumberOfPushedKey.contains(key)) {
			noteNumberOfPushedKey.remove(key);
		}
		repaint();
	}

	@Deprecated
	public void resetKeyRegister() {
		topNoteNumber = 0;
		bottomNoteNumber = 1000;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#selectGroup(javax.swing
	 * .JLabel, boolean)
	 */
	public void selectGroup(GroupLabel g, boolean flg) {}

	/**
	 * @param synthe the synthe to set
	 */
	public void setSynthe(MIDIController synthe) {
		this.synthe = synthe;
	}

	public void setTarget(TuneData target) {}

	/**
	 * キーボードを薄く描画します．
	 *
	 * @param g
	 * @param width
	 */
	void drawBackgroundKeyboard(final Graphics2D g, int width) {
		int curHeight = 0;
		for (int i = 0; i < maximumKeyRegister; i++) {
			if (whiteMidiKey.contains(getYPositionOfPitch(i) % 12)) {
				// 白鍵
				g.setColor(Color.lightGray);
				g.drawLine(0, curHeight, width, curHeight);
			} else {
				// 黒鍵
				g.setColor(Color.getHSBColor((float) 0.5, (float) 0.,
						(float) 0.9));
				g.fillRect(0, curHeight, width, getKeyHeight());
			}
			curHeight += getKeyHeight();
		}
	}

	/**
	 * @return the keyHeight
	 */
	public int getKeyHeight() {
		return keyHeight;
	}

	/**
	 * キーボード上で押された音高を発音します．
	 * <p>
	 * Sound out a note pressed on the keyboard.
	 *
	 * @param e
	 */
	void pressNoteKey(MouseEvent e) {
		addSelectedKeyList(e.getY());
		System.out.println("mouse on keyboard: " + getSelectedKey());
		if (getSynthe() != null)
			getSynthe().noteOn(getSelectedKey(), Mixtract.getDefaultVelocity());
	}

	/**
	 * キーボード上で押されていた音高を消音します．
	 * <p>
	 * Set a note pressed on the keyboard off.
	 */
	void releaseNoteKey() {
		removeSelectedKeyList(getSelectedKey());
		System.out.println("mouse on keyboard: " + getSelectedKey());
		if (getSynthe() != null)
			getSynthe().noteOff(getSelectedKey());
	}

	/**
	 * キーボードを描画します．
	 */
	private void drawKeyboard(final Graphics2D g) {
		int curHeight = 0;
		keyboardMap.clear();
		for (int i = 0; i < maximumKeyRegister; i++) {
			final int pitch = getYPositionOfPitch(i);
			// 鍵盤座標を取得
			keyboardMap.put(curHeight, pitch);
			if (whiteMidiKey.contains(pitch % 12)) {
				// 白鍵
				g.setColor(Color.black);
				g.draw3DRect(getX(), curHeight, getKeyWidth(), getKeyHeight(),
						true);
			} else {
				// 黒鍵
				g.setColor(Color.black);
				g.fill3DRect(getX(), curHeight, getKeyWidth(), getKeyHeight(),
						true);
			}
			g.setColor(Color.green);
			g.drawString(String.valueOf(pitch), getKeyWidth() - 20, curHeight
					+ getKeyHeight());
			curHeight += getKeyHeight();
		}
		// 今なっている音を色づけ
		g.setColor(Color.yellow);
		for (final int key : noteNumberOfPushedKey) {
			g.fillRect(getX(), getYPositionOfPitch(key) * getKeyHeight(),
					getKeyWidth(), getKeyHeight());
		}
	}

	/** 入力Y座標から鍵盤の位置（音高）を取得します） */
	private int getKeyPosition(int y) {
		for (final int key : keyboardMap.keySet()) {
			if (key > y)
				return keyboardMap.get(key) + 1;
		}
		return -1;
	}

	/**
	 * @return
	 */
	private MIDIController getSynthe() {
		if (synthe == null) {
			synthe = MixtractMIDIController.createMIDIController(
					"Java Sound Synthesizer", ticksperbeat);
		}
		return synthe;
	}

	/**
	 *
	 */
	private void initialize() {
		setLayout(null);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEtchedBorder());
		setDoubleBuffered(true);
		setSize(keyWidth, (keyHeight * maximumKeyRegister));

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	/**
	 * @param note
	 */
	public void setKeyRegister(NoteData note) {
		if (note == null)
			return;
		setKeyRegister(note.child());
		setKeyRegister(note.next());
		if (note.rest())
			return;
		if (note.noteNumber() > topNoteNumber)
			topNoteNumber = note.noteNumber();
		if (note.noteNumber() < bottomNoteNumber)
			bottomNoteNumber = note.noteNumber();
	}

	/**
	 * @param keyHeight セットする keyHeight
	 */
	public void setKeyHeight(int keyHeight) {
		this.keyHeight = keyHeight;
	}

	@Override public void selectTopNote(NoteData note, boolean b) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
