package net.muse.mixtract.command;

final class GTTMAnalysisCommand extends AnalyzeStructureCommand {

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
	@Override public void run() {
		makeUserGroup();
	}

	/* ユーザ定義のグルーピング */
	private void makeUserGroup() {}

}