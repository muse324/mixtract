package net.muse.mixtract.gui.command;

final class ApplyTopNoteCommand extends MixtractCommand {

	public ApplyTopNoteCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		frame().getGroupingPanel()
				.transferExpressionOfMostSimilarGroup();
	}

}