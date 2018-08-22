package net.muse.mixtract.command;

final class PrintSimilarGroupsCommand extends
		MixtractCommand {

	public PrintSimilarGroupsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		app().printSimilarList();
	}

}