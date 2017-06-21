package net.muse.mixtract.gui.command;

import net.muse.data.Group;
import net.muse.gui.GUIUtil;
import net.muse.gui.GroupLabel;

final class PrintSubgroupsCommand extends MixtractCommand {

	public PrintSubgroupsCommand(String... lang) {
		super(lang);
	}/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */

	@Override public void execute() {
		final GroupLabel gl = getSelectedObjects().getGroupLabel();
		GUIUtil.printConsole(gl.getName() + "'s subgroups:");
		printSubGroups(gl.getGroup());
	}

	/**
	 * @param g
	 */
	private void printSubGroups(Group g) {
		// if (g == null)
		// return;
		// GUIUtil.printConsole("Gr." + g.name() + ", hierarchy=" +
		// g.isHierarchy()
		// + ", level=" + g.getLevel() + ", " + g.getScoreNotelist());
		// printSubGroups(g.getChildFormerGroup());
		// printSubGroups(g.getChildLatterGroup());
	}

}