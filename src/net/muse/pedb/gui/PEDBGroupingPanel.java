package net.muse.pedb.gui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.gui.KeyActionListener;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.MXGroupingPanel;
import net.muse.pedb.data.PEDBTuneData;

public class PEDBGroupingPanel extends MXGroupingPanel {

	private PEDBGroupLabel higherGroup;

	@Override protected PEDBGroupLabel createGroupLabel(Group group,
			Rectangle r) {
		return new PEDBGroupLabel((MXGroup) group, r);
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	public void setHigherGroup(PEDBGroupLabel l) {
		higherGroup = l;
		main().butler().printConsole(String.format("%s is set as higher group",
				l));

	}

	@Override protected KeyActionListener createKeyActionListener(
			MuseApp main) {
		return new KeyActionListener(main, this) {

			@Override public PEDBStructureEditor main() {
				return (PEDBStructureEditor) super.main();
			}

			@Override public PEDBGroupingPanel owner() {
				return (PEDBGroupingPanel) super.owner();
			}

			@Override protected void keyPressedOption(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ESCAPE:
					setHigherGroup(null);
					break;
				}
			}

		};
	}

}
