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

	protected void createPhraseViewer(MuseApp _main, Group gr) {
		MXPhraseViewer pv = new MXPhraseViewer((Mixtract) _main, gr);
		pv.setTitle(gr.name());
		_main.addPhraseViewerList(pv);
		pv.pack();
		pv.setVisible(true);
		pv.preset();
	}
}
