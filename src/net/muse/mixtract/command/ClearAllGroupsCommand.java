package net.muse.mixtract.command;

import net.muse.data.Group;

public class ClearAllGroupsCommand extends MixtractCommand {
	public ClearAllGroupsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		target().getMiscGroup().clear();
		for (Group g : target().getRootGroup())
			target().deleteGroupFromData(g);
		main().butler().notifySetTarget(target());
	}

}