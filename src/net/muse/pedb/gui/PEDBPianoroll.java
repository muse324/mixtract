package net.muse.pedb.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

import net.muse.app.Mixtract;
import net.muse.data.NoteData;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.gui.MXPianoroll;
import net.muse.mixtract.gui.ViewerMode;
import net.muse.pedb.data.PEDBConcierge;

public class PEDBPianoroll extends MXPianoroll {
	private static final long serialVersionUID = 1L;

	PEDBPianoroll(Mixtract main) {
		super(main);
		setViewMode(ViewerMode.SCORE_VIEW);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#selectNote(net.muse.gui.NoteLabel,
	 * net.muse.data.NoteData, net.muse.data.NoteData)
	 */
	@Override public void selectNote(NoteLabel l, NoteData note, NoteData end) {
		if (note == null)
			return;
		NoteData s = l.getScoreNote();
		l.setSelected(s.onset() >= note.onset() && s.onset() <= end.onset());

		getSelectedNoteLabels().add(l);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.MXPianoroll#createNoteLabel(net.muse.data.NoteData,
	 * java.awt.Rectangle)
	 */
	@Override protected NoteLabel createNoteLabel(NoteData note, Rectangle r) {
		return new PEDBNoteLabel(note, r);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#drawMouseOveredNoteInfo(java.awt.Graphics2D)
	 */
	@Override protected void drawMouseOveredNoteInfo(Graphics2D g2) {
		if (mouseOveredNoteLabel == null)
			return;
		final NoteData nd = mouseOveredNoteLabel.getScoreNote();
		String str = nd.noteName() + "(" + nd.velocity() + ")" + nd.onset()
				+ "-" + nd.offset();
		str = String.format("%s (%s): %d-%d", nd.id(), nd.noteName(), nd
				.onset(), nd.offset());
		System.out.println(str + " at " + getMouseActions().getMousePoint());
		g2.drawString(str, getMouseActions().getMousePoint().x - axisX,
				getMouseActions().getMousePoint().y - main().getFrame()
						.getKeyboard().getKeyHeight());
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#drawOptionalInfo(java.awt.Graphics2D)
	 */
	@Override protected void drawOptionalInfo(Graphics2D g2) {
		NoteLabel l = getNotelist();
		drawTiedNotesConnection(g2, l, l.next());
	}

	private void drawTiedNotesConnection(Graphics2D g2, NoteLabel from,
			NoteLabel to) {
		if (from == null)
			return;
		if (to == null)
			return;
		drawTiedNotesConnection(g2, from.child(), to);
		drawTiedNotesConnection(g2, from.child(), to.child());
		NoteData n = from.getScoreNote();
		if (n.hasTiedTo()) {
			if (n.tiedTo().equals(to.getScoreNote())) {
				// draw connected line
				Rectangle r1 = from.getBounds();
				Rectangle r2 = to.getBounds();
				int sx = (int) (r1.width * 0.8);
				int ex = (int) (r2.width * 0.2);
				Point p1 = new Point(r1.x + sx, r1.y);
				Point p2 = new Point(r2.x + ex, r2.y);
				int cx = (p1.x + p2.x) / 2;
				int cy = p1.y - 15;
				Path2D.Double p = new Path2D.Double();
				p.moveTo(p1.x, p1.y);
				p.quadTo(cx, cy, p2.x, p2.y);
				g2.draw(p);
			}
			drawTiedNotesConnection(g2, from, to.child());
			drawTiedNotesConnection(g2, from, to.next());
		}
		drawTiedNotesConnection(g2, from.next(), to.child());
		drawTiedNotesConnection(g2, from.next(), to.next());
	}

}
