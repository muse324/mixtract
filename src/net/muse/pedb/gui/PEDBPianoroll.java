package net.muse.pedb.gui;

import net.muse.app.Mixtract;
import net.muse.mixtract.gui.MXPianoroll;
import net.muse.mixtract.gui.ViewerMode;

public class PEDBPianoroll extends MXPianoroll {

	private static final long serialVersionUID = 1L;

	PEDBPianoroll(Mixtract main) {
		super(main);
		setViewMode(ViewerMode.SCORE_VIEW);
	}

}
