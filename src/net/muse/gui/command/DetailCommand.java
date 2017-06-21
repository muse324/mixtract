package net.muse.gui.command;

import net.muse.mixtract.gui.command.MixtractCommand;

final class DetailCommand extends MixtractCommand {

	public DetailCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		frame().getGroupingPanel().showDetailViewer();
	}

}