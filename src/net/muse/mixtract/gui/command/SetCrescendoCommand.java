package net.muse.mixtract.gui.command;

import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.GroupType;
import net.muse.mixtract.gui.MixtractCommand;

public class SetCrescendoCommand extends MixtractCommand {

	private GroupLabel groupLabel;

	public SetCrescendoCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		groupLabel.getGroup().setType(GroupType.CRESC);
		groupLabel.setTypeShape(GroupType.CRESC);
		groupLabel.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.command.GroupCommandInterface#setGroup(jp
	 * .crestmuse.mixtract.data.Group)
	 */
	@Override public void setGroup(GroupLabel groupLabel) {
		this.groupLabel = groupLabel;
	}

	/**
	 * @return groupLabel
	 */
	protected GroupLabel getGroupLabel() {
		return groupLabel;
	}
}
