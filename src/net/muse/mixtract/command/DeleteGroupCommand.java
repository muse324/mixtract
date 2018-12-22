package net.muse.mixtract.command;

import net.muse.gui.GroupLabel;

public class DeleteGroupCommand extends MixtractCommand {

	public DeleteGroupCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		if (data() == null)
			return;

		final GroupLabel sel = frame().getGroupingPanel().getSelectedGroup();
		app().data().deleteGroupFromData(sel.group());
		app().notifyDeleteGroup(sel);
	}

}