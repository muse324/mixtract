package net.muse.mixtract.gui.command;

final class GTTMAnalysisCommand extends
		AnalyzeStructureCommand {

	/**
	 * @param string
	 */
	protected GTTMAnalysisCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.components.MixtractCommand.
	 * AnalyseStructureCommand
	 * #execute()
	 */
	@Override public void execute() {
		makeUserGroup();
	}

	/* ユーザ定義のグルーピング */
	private void makeUserGroup() {
		// if (_target != null) {
		// GTTMAnalyzer.run(_target, _mainFrame.getJCheckBoxMenuItem()
		// .isSelected(), false);
		// _main.notifySetTarget(_target);
		// _mainFrame.refreshDatabase();
		// }
	}

}