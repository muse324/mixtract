package net.muse.command;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/12
 */
class StopCommand extends MuseAppCommand {

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		butler().notifyStopPlaying();
	}

	protected StopCommand(String... lang) {
		super(lang);
	}
}