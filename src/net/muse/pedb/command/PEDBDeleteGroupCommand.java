package net.muse.pedb.command;

import net.muse.app.PEDBStructureEditor;
import net.muse.mixtract.command.DeleteGroupCommand;
import net.muse.pedb.data.PEDBConcierge;
import net.muse.pedb.data.PEDBTuneData;
import net.muse.pedb.gui.PEDBGroupLabel;
import net.muse.pedb.gui.PEDBMainFrame;

public class PEDBDeleteGroupCommand extends DeleteGroupCommand {

	@Override public PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.command.DeleteGroupCommand#run()
	 */
	@Override public void run() {
		if (data() == null)
			return;

		final PEDBGroupLabel sel = frame().getGroupingPanel()
				.getSelectedGroup();
		app().data().deleteGroupFromData(sel.group());
		app().notifyDeleteGroup(sel);
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override protected PEDBMainFrame frame() {
		return (PEDBMainFrame) super.frame();
	}

	@Override protected PEDBGroupLabel getGroupLabel() {
		return (PEDBGroupLabel) super.getGroupLabel();
	}

	@Override protected PEDBTuneData target() {
		return (PEDBTuneData) super.target();
	}

}
