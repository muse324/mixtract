package net.muse.pedb.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.gui.GLMouseActionListener;
import net.muse.gui.KeyActionListener;
import net.muse.pedb.data.PEDBNoteData;

public class PEDBTopNoteLabel extends PEDBGroupLabel {

	PEDBNoteData n;

	/* イベント制御 */
	private boolean startEdit;
	private boolean endEdit;
	private RoundRectangle2D d;

	// 追加
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		// RoundRectangle2D rect = new RoundRectangle2D.Double(30, 50, 100, 100,
		// 30, 10);
		// g2.setColor(Color.RED);
		g2.draw((Shape) d);

	}

	public PEDBTopNoteLabel(NoteData topNote, RoundRectangle2D topr) {
		super();
		setBounds(new Rectangle((int) topr.getX(), (int) topr.getY(), (int) topr
				.getWidth(), (int) topr.getHeight()));
		d = topr;
		n = (PEDBNoteData) topNote;

	}

	protected void setEditMode(Point mousePosition) {
		// do nothing
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

			@Override public PEDBTopNoteLabel owner() {
				return (PEDBTopNoteLabel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				super.keyPressedOption(e);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_KP_LEFT:
					// setHigherGroup(owner());
					System.out.println("ababababa");
					break;
				case KeyEvent.VK_KP_RIGHT:
					// setHigherGroup(null);
					System.out.println("cdcdcdcdc");
				}
			}
			/*
			 * protected void setHigherGroup(PEDBGroupLabel owner) {
			 * app().getFrame().getGroupingPanel().setHigherGroup(owner);
			 * }
			 */
		};
	}

	@Override protected GLMouseActionListener createMouseActionListener(
			MuseApp app) {
		return new GLMouseActionListener(app, this) {

			@Override public PEDBTopNoteLabel self() {
				return (PEDBTopNoteLabel) super.self();
			}

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			// 11/17 藤坂が一部追加 クリックした時のグループ(頂点)を選択
			@Override public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				app().butler().notifySelectTopNote(self(), true);
				System.out.print("clicked!");
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * net.muse.gui.GLMouseActionListener#doubleClicked(net.muse.data.
			 * Group)
			 */
			protected void doubleClicked(Group gr) {
				// do nothing
			}
		};
	}

	public void setTopNote(PEDBNoteData i) {
		n = i;
	}

	public PEDBNoteData getScoreNote() {
		return n;
	}

	// 11/17 藤坂が追加 頂点の音を変更するメソッドの作成
	public PEDBNoteData moveNote(int i, PEDBNoteData n) {
		// n = getScoreNote();
		if (i == 0) {
			n = (PEDBNoteData) n.next();
			setTopNote(n);
		} else {
			n = (PEDBNoteData) n.previous();
			setTopNote(n);
		}
		return n;

	}

}
