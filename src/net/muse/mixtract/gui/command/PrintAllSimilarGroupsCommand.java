package net.muse.mixtract.gui.command;

final class PrintAllSimilarGroupsCommand extends
		MixtractCommand {

	public PrintAllSimilarGroupsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		main().printAllSimilarList();
	}

}