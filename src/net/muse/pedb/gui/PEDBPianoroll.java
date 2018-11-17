package net.muse.pedb.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.NoteData;
import net.muse.gui.KeyActionListener;
import net.muse.gui.NoteLabel;
import net.muse.gui.PianoRoll;
import net.muse.gui.PianoRollActionListener;
import net.muse.mixtract.gui.ViewerMode;
import net.muse.pedb.command.PEDBPianoRollActionLisner;
import net.muse.pedb.data.PEDBConcierge;

public class PEDBPianoroll extends PianoRoll {
	private static final long serialVersionUID = 1L;

	PEDBPianoroll(MuseApp app) {
		super(app);
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

	/* (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#createKeyActionListener(net.muse.app.MuseApp)
	 */
	protected KeyActionListener createKeyActionListener(MuseApp app) {
		return new KeyActionListener(app, this) {

			@Override public PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

			@Override public PEDBPianoroll owner() {
				return (PEDBPianoroll) super.owner();
			}

		};
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.mixtract.gui.MXPianoroll#createNoteLabel(net.muse.data.NoteData,
	 * java.awt.Rectangle)
	 */
	@Override protected PEDBNoteLabel createNoteLabel(NoteData note,
			Rectangle r) {
		return new PEDBNoteLabel(note, r);
	}

	@Override protected PianoRollActionListener createPianoRollMouseAction(
			MuseApp app) {
		return new PEDBPianoRollActionLisner(app, this) {

			@Override public PEDBPianoroll self() {
				return (PEDBPianoroll) super.self();
			}

			@Override protected PEDBMainFrame frame() {
				return (PEDBMainFrame) super.frame();
			}

			@Override protected PEDBStructureEditor app() {
				return (PEDBStructureEditor) super.app();
			}

		};
	}

	protected String switchViewerMode(final NoteData nd) {
		return String.format("%s (%s): %d-%d", nd.noteName(), nd.chord(), nd
				.onset(), nd.offset());
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#drawOptionalInfo(java.awt.Graphics2D)
	 */
	@Override protected void drawOptionalInfo(Graphics2D g2) {
		PEDBNoteLabel l = notelist();
		while (l.hasNext()) {
			drawTiedNotesConnection(g2, l, l.next());
			l = l.next();
		}
	}

	@Override protected PEDBGroupLabel group() {
		return (PEDBGroupLabel) super.group();
	}

	@Override protected PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.PianoRoll#makeNoteLabel(net.muse.data.NoteData, int,
	 * boolean)
	 */
	@Override protected void makeNoteLabel(final NoteData note, int offset,
			boolean isChild) {
		if (note == null)
			return;

		final Rectangle r = getLabelBounds(note, offset);
		PEDBNoteLabel n = createNoteLabel(note, r);
		n.setController(app());
		n.setSelected(getSelectedNoteLabels().contains(n));
		if (notelist == null) {
			notelist = n;
		} else {
			notelist.setNext(n);
			notelist = notelist.next();
		}
		add(n);

		if (r.x + r.width > getWidth()) {
			Dimension sz = getPreferredSize();
			sz.width = r.x + r.width + 10;
			setPreferredSize(sz);
			repaint();
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.MXPianoroll#notelist()
	 */
	protected PEDBNoteLabel notelist() {
		return (PEDBNoteLabel) super.notelist();
	}

	private void drawTiedNotesConnection(Graphics2D g2, PEDBNoteLabel from,
			PEDBNoteLabel to) {
		if (to == null)
			return;

		NoteData n = from.getScoreNote();
		if (!n.hasTiedTo())
			return;

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
		drawTiedNotesConnection(g2, from, to.next());
	}

	@Override public void selectTopNote(NoteData note, boolean b) {
		// super.selectTopNote(note, b);
		for (Component c : getComponents()) {
			if(c instanceof PEDBNoteLabel) {
				PEDBNoteLabel n = (PEDBNoteLabel)c;
				n.setSelected(n.getScoreNote().equals(note));
			}
		}
	}
}
