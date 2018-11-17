package net.muse.pedb.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
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
import net.muse.pedb.data.PEDBConcierge;
import net.muse.pedb.data.PEDBNoteData;

public class PEDBTopNoteLabel extends PEDBNoteLabel {

	PEDBNoteData n;

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

	public PEDBTopNoteLabel(NoteData topNote, RoundRectangle2D topr) {

		super(topNote, new Rectangle((int) topr.getX(), (int) topr.getY(),
				(int) topr.getWidth(), (int) topr.getHeight()));
		d = topr;
		n = (PEDBNoteData)topNote;

	}

	@Override protected KeyActionListener createKeyActionListener(
			MuseApp main) {
		return new KeyActionListener(main, this) {

			@Override public PEDBStructureEditor main() {
				return (PEDBStructureEditor) super.main();
			}

			@Override public PEDBTopNoteLabel owner() {
				return (PEDBTopNoteLabel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_KP_LEFT:
					//setHigherGroup(owner());
					System.out.println("ababababa");
					break;
				case KeyEvent.VK_KP_RIGHT:
					//setHigherGroup(null);
					System.out.println("cdcdcdcdc");
				}
			}
/*
			protected void setHigherGroup(PEDBGroupLabel owner) {
				main().getFrame().getGroupingPanel().setHigherGroup(owner);
			}
*/
		};
	}

	@Override protected GLMouseActionListener createMouseActionListener(
			MuseApp main) {
		return new GLMouseActionListener(main, this) {

			@Override public PEDBStructureEditor main() {
				return (PEDBStructureEditor) super.main();
			}

			// 11/17  藤坂が一部追加  クリックした時のグループ(頂点)を選択
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ
				super.mousePressed(e);
				((PEDBConcierge)(main().butler())).setTopNoteLabel(self());
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

	@Override
	public PEDBNoteData getScoreNote() {
		// TODO 自動生成されたメソッド・スタブ
		return n;
	}

	// 11/17  藤坂が追加  頂点の音を変更するメソッドの作成
	public PEDBNoteData moveNote(int i,PEDBNoteData n) {
		//n = getScoreNote();
		if(i == 0)
		{
			n = (PEDBNoteData) n.next();
			setTopNote(n);
		}
		else
		{
			n = (PEDBNoteData) n.previous();
			setTopNote(n);
		}
		return n;

	}



}
