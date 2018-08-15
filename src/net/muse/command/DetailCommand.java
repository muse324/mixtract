package net.muse.command;

final class DetailCommand extends MuseAppCommand {

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