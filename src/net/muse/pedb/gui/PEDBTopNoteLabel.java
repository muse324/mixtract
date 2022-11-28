package net.muse.pedb.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.gui.GLMouseActionListener;
import net.muse.gui.KeyActionListener;
import net.muse.gui.MainFrame;
import net.muse.gui.PianoRoll;
import net.muse.pedb.data.PEDBNoteData;

public class PEDBTopNoteLabel extends PEDBGroupLabel {
	private static final int _REWIND = 1;
	private static final int _FORWARD = 0;
	private static final long serialVersionUID = 1L;
	private PEDBNoteData note;
	private final RoundRectangle2D d;
	private boolean isNoteChanged;

	public PEDBTopNoteLabel(NoteData topNote, RoundRectangle2D topr,
			Group group) {
		super();
		setOpaque(false);
		setBorder(new LineBorderEx(Color.DARK_GRAY, 3, 240));
		d = topr;
		setBounds(new Rectangle((int) d.getX(), (int) d.getY(), (int) d
				.getWidth(), (int) d.getHeight()));
		setNote((PEDBNoteData) topNote);
		setGroup(group);
	}

	/**
	 * 頂点の音を変更する。
	 *
	 * @author Anan Fujisaka
	 * @since
	 * 		Nov. 17-23, 2018
	 *
	 */
	public void moveTopNote(int i) {
		switch (i) {
		case _FORWARD:
			if (note() != group().getEndNote()) {
				PEDBNoteData nx = note().next();
				while (nx.hasNext() && (nx.xmlVoice() != note().xmlVoice() || nx
						.isGrace()))
					nx = nx.next();
				setNote(nx);
			}
			break;
		case _REWIND:
			if (note() != group().getBeginNote()) {
				PEDBNoteData pre = note().previous();
				while (pre.hasPrevious() && (pre.isGrace() || pre
						.xmlVoice() != note().xmlVoice()))
					pre = pre.previous();
				setNote(pre);
			}
			break;
		}
	}

	/**
	 * 対応する音符データを取得する
	 */
	public PEDBNoteData note() {
		return note;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.pedb.gui.PEDBNoteLabel#createKeyActionListener(net.muse.app.
	 * MuseApp)
	 */
	@Override protected KeyActionListener createKeyActionListener(MuseApp app) {
		return new KeyActionListener(app, this) {

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			@Override public PEDBTopNoteLabel self() {
				return (PEDBTopNoteLabel) super.self();
			}

			// 11/17~23 藤坂が一部追加 方向キーを押した時の処理
			@Override protected void keyPressedOption(KeyEvent e) {
				super.keyPressedOption(e);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT:
					moveTopNote(_FORWARD);
					app().butler().notifySelectTopNote(self(), true);
					break;
				case KeyEvent.VK_LEFT:
					moveTopNote(_REWIND);
					app().butler().notifySelectTopNote(self(), true);
					break;
				}
			}
		};
	}

	@Override protected GLMouseActionListener createMouseActionListener(
			MuseApp app) {
		return new GLMouseActionListener(app, this) {

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			// 11/17~22 藤坂が一部追加 クリックした時のグループ(頂点)を選択
			@Override public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				app().butler().notifySelectTopNote(self(), true);
			}

			@Override public PEDBTopNoteLabel self() {
				return (PEDBTopNoteLabel) super.self();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * net.muse.gui.GLMouseActionListener#doubleClicked(net.muse.data.
			 * Group)
			 */
			@Override protected void doubleClicked(Group gr) {
				// do nothing
			}
		};
	}

	@Override protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (isNoteChanged) {
			final int x = MainFrame.getXOfNote(note().onset()) + PianoRoll
					.getDefaultAxisX();
			final int w = MainFrame.getXOfNote(note().duration());
			setBounds(x, (int) d.getY(), w, (int) d.getHeight());
			((Graphics2D) g).draw(d);
			isNoteChanged = false;
		}

	}

	@Override protected void setEditMode(Point mousePosition) {
		// do nothing
	}

	private void setNote(PEDBNoteData n) {
		this.note = n;
		isNoteChanged = true;
		if (group() != null)
			group().setTopNote(n);
		repaint();
	}
}
