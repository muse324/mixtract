package net.muse.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import net.muse.app.MuseApp;

class NLMouseActionListener extends MouseActionListener {

	protected NLMouseActionListener(MuseApp main, Container owner) {
		super(main, owner);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseEntered(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		NoteLabel src = (NoteLabel) e.getSource();
		src.setMouseOver(true);
		((PianoRoll) self().getParent()).setMouseOveredNoteLabel(src);
		self().repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		((NoteLabel) e.getSource()).setMouseOver(false);
		((NoteLabel) e.getSource()).setEditMode(getMousePoint());
		((PianoRoll) self().getParent()).setMouseOveredNoteLabel(null);
		self().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseMoved(java.awt
	 * .event.MouseEvent)
	 */
	@Override public void mouseMoved(MouseEvent e) {
		((PianoRoll) self().getParent()).setMouseOveredNoteLabel(
				(NoteLabel) e.getSource());
	}
}