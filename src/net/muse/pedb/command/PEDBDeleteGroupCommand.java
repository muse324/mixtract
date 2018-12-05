package net.muse.pedb.command;

import net.muse.app.PEDBStructureEditor;
import net.muse.mixtract.command.DeleteGroupCommand;
import net.muse.pedb.data.PEDBConcierge;
import net.muse.pedb.data.PEDBTuneData;
import net.muse.pedb.gui.PEDBGroupLabel;
import net.muse.pedb.gui.PEDBMainFrame;

public class PEDBDeleteGroupCommand extends DeleteGroupCommand {

	@Override protected PEDBGroupLabel getGroupLabel() {
		return (PEDBGroupLabel) super.getGroupLabel();
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override public PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	@Override protected PEDBMainFrame frame() {
		return (PEDBMainFrame) super.frame();
	}

	@Override protected PEDBTuneData target() {
		return (PEDBTuneData) super.target();
	}

}
