package net.muse.gui.command;

import net.muse.mixtract.gui.command.MixtractCommand;

final class RefreshCommand extends MixtractCommand {

	public RefreshCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		frame().refreshDatabase();
	}

}