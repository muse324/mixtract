package net.muse.gui.command;

import net.muse.data.Group;
import net.muse.mixtract.gui.command.MixtractCommand;

public class ClearAllGroupsCommand extends MixtractCommand {
	public ClearAllGroupsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		target().getGroupArrayList().clear();
		for (Group g : target().getRootGroup())
			target().deleteGroupFromData(g);
		main().notifySetTarget();
	}

}