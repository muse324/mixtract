package net.muse.pedb.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import net.muse.data.NoteData;

public class PEDBTopNoteLabel extends PEDBNoteLabel {

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

	}
}
