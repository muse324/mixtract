package net.muse.command;

import net.muse.data.Group;
import net.muse.gui.GroupLabel;

/**
 * <h1>PrintGroupInfoCommand</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2010/02/15
 */
public class PrintGroupInfoCommand extends MuseAppCommand {

	private Group group = null;

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MixtractCommand#setGroup(jp.crestmuse.
	 * mixtract
	 * .gui.GroupLabel)
	 */
	@Override
	public void setGroup(GroupLabel groupLabel) {
		if (groupLabel != null)
			this.group = groupLabel.group();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override
	public void execute() {
		if (group == null) {
			group = frame().getGroupingPanel().getSelectedGroup().group();
		}
		System.out.println(group.printInfo());
	}

	public PrintGroupInfoCommand(String... lang) {
		super(lang);
	}
}