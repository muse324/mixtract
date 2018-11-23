package net.muse.pedb.gui;

import java.awt.Color;
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
import net.muse.gui.MainFrame;
import net.muse.gui.PianoRoll;
import net.muse.pedb.data.PEDBGroup;
import net.muse.pedb.data.PEDBNoteData;

public class PEDBTopNoteLabel extends PEDBGroupLabel {

	PEDBNoteData n;
	PEDBGroup g;



	/* イベント制御 */
	private boolean startEdit;
	private boolean endEdit;
	private RoundRectangle2D d;

	// 追加
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) this.getGraphics();
		 //RoundRectangle2D rect = new RoundRectangle2D.Double(30, 50, 100, 100,
		 //30, 10);
		 //g2.setColor(Color.RED);
		g2.draw((Shape) d);

	}


	public PEDBTopNoteLabel(NoteData topNote, RoundRectangle2D topr, Group group) {
		super();
		setOpaque(false);
		setBorder(new LineBorderEx(Color.DARK_GRAY, 3, 240));
		setBounds(new Rectangle((int) topr.getX(), (int) topr.getY(), (int) topr
				.getWidth(), (int) topr.getHeight()));
		d = topr;
		n = (PEDBNoteData)topNote;
		setGroup(group);

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

			// 11/17~23  藤坂が一部追加  方向キーを押した時の処理
			@Override protected void keyPressedOption(KeyEvent e) {
				super.keyPressedOption(e);
				switch (e.getKeyCode()) {
				case KeyEvent.VK_RIGHT:
					g = group();
					moveNote(0,g);
					break;
					//System.out.println("right →");
				case KeyEvent.VK_LEFT:
					g = group();
					moveNote(1,g);
					break;
					//System.out.println("left ←");
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

			// 11/17~22  藤坂が一部追加  クリックした時のグループ(頂点)を選択
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mousePressed(e);
				app().butler().notifySelectTopNote(self(), true);
				//System.out.print("clicked");;クリック可能確認済み
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

	// 11/17~23  藤坂が追加  頂点の音を変更するメソッドの作成・グラフィックスの頂点を移動させる処理をも行う
	public void moveNote(int i,PEDBGroup g) {
		switch (i) {
		case 0:
			if(g.topNote != g.getEndNote()) {
				//System.out.println("→を押す前　"+g.topNote);
				n = (PEDBNoteData) g.getTopNote().next();
				g.topNote = n;
				//System.out.println("→を押した後　"+g.topNote);
			}
			else {
				//do nothing
			}
			break;
		case 1:
			if(g.topNote != g.getBeginNote()) {
				//System.out.println("←を押す前　"+g.topNote);
				n = (PEDBNoteData) g.getTopNote().previous();
				g.topNote = n;
				//System.out.println("←を押した後　"+g.topNote);
			}
			else {
				//do nothing
			}
			break;
		}
		double x, w;
		x = MainFrame.getXOfNote(g.getTopNote().onset()) + PianoRoll.getDefaultAxisX();
		w = MainFrame.getXOfNote((double) g.getTopNote().duration());
		setBounds(new Rectangle((int) x, (int) d.getY(), (int) w, (int) d.getHeight()));

	}
}
