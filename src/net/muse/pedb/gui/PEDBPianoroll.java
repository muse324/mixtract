package net.muse.pedb.gui;

import java.awt.Graphics2D;
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
	protected void drawOptionalInfo(Graphics2D g2) {
		NoteLabel l = getNotelist();
		while (l.hasNext()) {
			if (l.getScoreNote().hasTiedTo()) {
				drawTiedNotesConnection(g2, l, l.next());
			}
			l = l.next();
		}
	}

	private void drawTiedNotesConnection(Graphics2D g2, NoteLabel from,
			NoteLabel to) {
		if (to == null)
			return;
		if (from.getScoreNote().tiedTo().equals(to.getScoreNote())) {
			// draw connected line
			Rectangle r1 = from.getBounds();
			Rectangle r2 = to.getBounds();
			Path2D.Double p = new Path2D.Double();
			p.moveTo(r1.getCenterX(), r1.getY());
			p.quadTo(r1.getCenterX(), r1.getY() + 10, r2.getCenterX(), r2
					.getY());
			g2.draw(p);
			return;
		}
		drawTiedNotesConnection(g2, from, to.next());
	}

}
