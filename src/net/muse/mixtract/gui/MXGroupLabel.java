package net.muse.mixtract.gui;

import java.awt.Rectangle;

import net.muse.gui.GroupLabel;
import net.muse.gui.PhraseViewer;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.data.Group;

public class MXGroupLabel extends GroupLabel {

	private static final long serialVersionUID = 1L;

	public MXGroupLabel(Group group, Rectangle r) {
		super(group, r);
	}

	protected void createPhraseViewer(Mixtract _main, Group gr) {
		PhraseViewer pv = new MXPhraseViewer(_main, gr);
		pv.setTitle(gr.name());
		_main.addPhraseViewerList(pv);
		pv.pack();
		pv.setVisible(true);
		pv.preset();
	}
}
