package net.muse.mixtract.command;

import net.muse.data.GroupType;

public class SetDiminuendoCommand extends SetCrescendoCommand {

	public SetDiminuendoCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.command.SetCrescendoCommand#execute()
	 */
	@Override public void execute() {
		getGroupLabel().group().setType(GroupType.DIM);
		getGroupLabel().setTypeShape(GroupType.DIM);
		getGroupLabel().repaint();
	}

}
