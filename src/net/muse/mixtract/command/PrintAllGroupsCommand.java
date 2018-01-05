package net.muse.mixtract.command;

import net.muse.data.Group;
import net.muse.gui.GUIUtil;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXTuneData;

/**
 * <h1>PrintAllGroupsCommand</h1>
 * <p>
 * 登録されているすべてのグループ情報を出力します。
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/10/29
 */
final class PrintAllGroupsCommand extends MixtractCommand {

	public PrintAllGroupsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override
	public void execute() {
		GUIUtil.printConsole("Hierarchical group list:");
		assert _target instanceof MXTuneData;
		MXTuneData t = (MXTuneData) _target;
		for (Group g : t.getRootGroup()) {
			assert g instanceof MXGroup;
			printGroupList((MXGroup) g);
		}
		GUIUtil.printConsole("Hierarchical group list:");
		for (Group g : t.getGroupArrayList()) {
			assert g instanceof MXGroup;
			printGroupList((MXGroup) g);
		}
	}

	/**
	 * 登録されているすべてのグループ情報を出力します。
	 *
	 * @param group 各声部のトップグループ
	 */
	private void printGroupList(MXGroup group) {
		if (group == null)
			return;
		printGroupList(group.getChildFormerGroup());
		printGroupList(group.getChildLatterGroup());
		System.out.println(group);
	}
}