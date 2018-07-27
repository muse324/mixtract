package net.muse.pedb.gui;

import java.awt.Graphics2D;

import net.muse.app.Mixtract;
import net.muse.data.NoteData;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.gui.MXPianoroll;
import net.muse.mixtract.gui.ViewerMode;

public class PEDBPianoroll extends MXPianoroll {

	@Override
	protected void drawMouseOveredNoteInfo(Graphics2D g2) {
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

	private static final long serialVersionUID = 1L;

	PEDBPianoroll(Mixtract main) {
		super(main);
		setViewMode(ViewerMode.SCORE_VIEW);
	}

	public void selectNote(NoteLabel l, NoteData note, NoteData end) {
		if (note == null)
			return;
		NoteData s = l.getScoreNote();
		l.setSelected(s.onset() >= note.onset() && s.onset() <= end.onset());

		getSelectedNoteLabels().add(l);
	}
}
