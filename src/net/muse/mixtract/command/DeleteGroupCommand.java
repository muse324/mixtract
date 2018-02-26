package net.muse.mixtract.command;

import net.muse.gui.GroupLabel;

public final class DeleteGroupCommand extends MixtractCommand {

	public DeleteGroupCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		if (target() == null)
			return;

		GroupLabel sel = frame().getGroupingPanel().getSelectedGroup();
		main().data().deleteGroupFromData(sel.group());
		main().notifyDeleteGroup(sel);
	}

}