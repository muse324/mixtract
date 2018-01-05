package net.muse.command;

import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.MXGroup;

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
	@Override public void setGroup(GroupLabel groupLabel) {
		this.group = groupLabel.group();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		if (group == null) {
			group = frame().getGroupingPanel().getSelectedGroup()
					.group();
		}
		if (group instanceof MXGroup) {
			MXGroup g = (MXGroup) group;
			System.out.println(String.format("Group %s\n\t%s\n\t%s\n\t%s\n",
					group.name(), g.getDynamicsCurve(), g.getTempoCurve(), g
							.getArticulationCurve()));
		} else {
			System.out.println(String.format("Group %s\n", group.name()));
		}
	}

	public PrintGroupInfoCommand(String... lang) {
		super(lang);
	}
}