package net.muse.pedb.gui;

import net.muse.app.Mixtract;
import net.muse.mixtract.gui.MXPianoroll;
import net.muse.mixtract.gui.ViewerMode;

public class PEDBPianoroll extends MXPianoroll {

	PEDBPianoroll(Mixtract main) {
		super(main);
		setViewMode(ViewerMode.SCORE_VIEW);
	}

}
