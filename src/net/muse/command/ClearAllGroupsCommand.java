package net.muse.command;

import net.muse.data.Group;

public class ClearAllGroupsCommand extends MuseAppCommand {
	public ClearAllGroupsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		target().getMiscGroup().clear();
		for (Group g : target().getRootGroup())
			target().deleteGroupFromData(g);
		main().notifySetTarget();
	}

}