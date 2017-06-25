package net.muse.mixtract.gui;

import java.awt.Rectangle;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;

public class MXGroupLabel extends GroupLabel {

	private static final long serialVersionUID = 1L;

	public MXGroupLabel(Group group, Rectangle r) {
		super(group, r);
	}

	protected MXPhraseViewer createPhraseViewer(MuseApp _main, Group gr) {
		return new MXPhraseViewer((Mixtract) _main, gr);
	}
}
