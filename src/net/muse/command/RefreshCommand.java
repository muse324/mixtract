package net.muse.command;

final class RefreshCommand extends MuseAppCommand {

	public RefreshCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		frame().refreshDatabase();
	}

}