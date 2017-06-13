package net.muse.mixtract.gui.command;

import net.muse.mixtract.data.Group.GroupType;

public class SetDiminuendoCommand extends SetCrescendoCommand {

	public SetDiminuendoCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.command.SetCrescendoCommand#execute()
	 */
	@Override public void execute() {
		getGroupLabel().getGroup().setType(GroupType.DIM);
		getGroupLabel().setTypeShape(GroupType.DIM);
		getGroupLabel().repaint();
	}

}
