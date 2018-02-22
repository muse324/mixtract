package net.muse.mixtract.command;

class SearchCommand extends MixtractCommand {

	protected SearchCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		// _mainFrame.searchSimilarPhrases();
		// // _mainFrame.getGroupingPanel().searchSimilarityOfThisGroup();
	}
}