package net.muse.mixtract.command;

import net.muse.command.MuseAppCommand;

final class ApplyTopNoteCommand extends MuseAppCommand {

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