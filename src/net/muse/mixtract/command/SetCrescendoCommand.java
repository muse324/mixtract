package net.muse.mixtract.command;

import net.muse.data.GroupType;
import net.muse.gui.GroupLabel;

public class SetCrescendoCommand extends MixtractCommand {

	private GroupLabel groupLabel;

	public SetCrescendoCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override
	public void execute() {
		groupLabel.group().setType(GroupType.CRESC);
		groupLabel.setTypeShape(GroupType.CRESC);
		groupLabel.repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.command.GroupCommandInterface#setGroup(jp
	 * .crestmuse.mixtract.data.Group)
	 */
	@Override
	public void setGroup(GroupLabel groupLabel) {
		this.groupLabel = groupLabel;
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.command.MixtractCommand#getGroupLabel()
	 */
	public GroupLabel getGroupLabel() {
		return groupLabel;
	}
}
