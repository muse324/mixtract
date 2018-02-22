package net.muse.misc;

import net.muse.sound.MIDIController;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2008/04/21
 */
public abstract class Command extends MuseObject implements Runnable {

	public static final Command CLOSE = new CloseCommand("Close", "閉じる");
	public static final Command NULL = new NullCommand("Null");
	public static final Command PAUSE = new PauseCommand("Pause", "一時停止");
	public static final Command PLAY = new PlayCommand("Play", "再生");
	public static final Command QUIT = new QuitCommand("Quit", "終了");
	public static final Command SAVE = new SaveCommand("Save", "保存");
	public static final Command SAVEAS = new SaveAsCommand("Save as", "別名で保存");
	public static final Command SETENV = new SetEnvCommand("Setup", "環境設定");
	public static final Command SHOW_CONSOLE = new ShowConsoleCommand("Console",
			"コンソール");
	public static final Command STOP = new StopCommand("Stop", "停止");
	private static final Command[] commandlist = new Command[] { CLOSE, PAUSE,
			PLAY, QUIT, SAVE, SAVEAS, SETENV, SHOW_CONSOLE, STOP, NULL };
	private static Language _language;
	private String[] names;

	public Command(String... lang) {
		this();
		if (isAssertion())
			assert lang.length <= names.length;
		for (int i = 0; i < names.length; i++) {
			names[i] = (i < lang.length) ? lang[i] : lang[0];
		}
	}

	private Command() {
		super();
		names = new String[Language.getLanguageList().length];

	}

	/**
	 * @param cmd
	 * @return
	 */
	public static Command create(String cmd) {
		for (Command c : commandlist) {
			if (cmd.equals(c.name()))
				return c;
		}
		return NULL;
	}

	/**
	 * @return the _language
	 */
	public static Language getLanguage() {
		return _language;
	}

	public static void setLanguage(String val) {
		_language = Language.create(val);
	}

	/* (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		throw new UnsupportedOperationException(name());
	}

	public final String name() {
		return getClass().getSimpleName().replace("Command", "");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return getText();
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	public static class NullCommand extends Command {

		protected NullCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void run() {
			System.out.println("do nothing");
		}

	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class CloseCommand extends Command {

		protected CloseCommand(String... lang) {
			super(lang);
		}
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	public static class PauseCommand extends Command {

		protected PauseCommand(String... lang) {
			super(lang);
		}
	}

	private static MIDIController synthe;
	private static String filename;

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	public static class PlayCommand extends Command {

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

	public final void setSynthesizer(MIDIController synthe, String filename) {
		Command.synthe = synthe;
		Command.filename = filename;
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class QuitCommand extends Command {

		protected QuitCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void run() {
			System.exit(1);
		}
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class SaveAsCommand extends Command {

		protected SaveAsCommand(String... lang) {
			super(lang);
		}
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class SaveCommand extends Command {

		protected SaveCommand(String... lang) {
			super(lang);
		}
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class SetEnvCommand extends Command {

		protected SetEnvCommand(String... lang) {
			super(lang);
		}
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class ShowConsoleCommand extends Command {

		protected ShowConsoleCommand(String... lang) {
			super(lang);
		}
	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class StopCommand extends Command {

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void run() {
			synthe.notifyStopPlaying();
		}

		protected StopCommand(String... lang) {
			super(lang);
		}
	}

	public final String getText() {
		return names[getLanguage().getIndex()];
	}
}
