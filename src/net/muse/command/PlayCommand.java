package net.muse.command;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/12
 */
public class PlayCommand extends MuseAppCommand {

	protected PlayCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		synthe.notifyStartPlaying(filename);
	}

}