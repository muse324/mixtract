package net.muse.command;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
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