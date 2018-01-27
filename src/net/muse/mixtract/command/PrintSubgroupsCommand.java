package net.muse.mixtract.command;

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

	@Override
	public void execute() {
		if (getSelectedObjects() instanceof GroupLabel) {
			final GroupLabel gl = getGroupLabel();
			GUIUtil.printConsole(gl.getName() + "'s subgroups:");
			printSubGroups(gl.group());
		}
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