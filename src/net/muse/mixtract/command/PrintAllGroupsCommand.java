package net.muse.mixtract.command;

/**
 * <h1>PrintAllGroupsCommand</h1>
 * <p>
 * 登録されているすべてのグループ情報を出力します。
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)</address>
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
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
	@Override public void run() {
		assert data() != null;
		data().printAllGroups();
	}
}