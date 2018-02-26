package net.muse.command;

import net.muse.app.MuseApp;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.gui.MainFrame;
import net.muse.misc.Language;
import net.muse.misc.MuseObject;
import net.muse.sound.MIDIController;

public class MuseAppCommand extends MuseObject implements Runnable,
		GroupCommandInterface {
	protected MuseApp _main;
	protected TuneData _target;
	protected static String filename;
	protected static MIDIController synthe;
	private static Language _language;

	private static final MuseAppCommand CLOSE = new CloseCommand("Close",
			"閉じる");
	private static final MuseAppCommand NULL = new NullCommand("Null");
	private static final MuseAppCommand PAUSE = new PauseCommand("Pause",
			"一時停止");
	private static final MuseAppCommand PLAY = new PlayCommand("Play", "再生");
	private static final MuseAppCommand QUIT = new QuitCommand("Quit", "終了");
	private static final MuseAppCommand SAVE = new SaveCommand("Save", "保存");
	private static final MuseAppCommand SAVEAS = new SaveAsCommand("Save as",
			"別名で保存");
	private static final MuseAppCommand SETENV = new SetEnvCommand("Setup",
			"環境設定");
	private static final MuseAppCommand SHOW_CONSOLE = new ShowConsoleCommand(
			"Console", "コンソール");
	private static final MuseAppCommand STOP = new StopCommand("Stop", "停止");
	protected static final MuseAppCommand DETAIL = new DetailCommand(
			"Show parameters", "詳細表示");
	protected static final MuseAppCommand OPEN_MUSICXML = new OpenMusicXMLCommand(
			"Open MusicXML...", "MusicXMLを開く...");
	protected static final MuseAppCommand REFRESH = new RefreshCommand(
			"Refresh", "更新");
	protected static final MuseAppCommand RENDER = new RenderCommand("Render",
			"生成");
	private static final MuseAppCommand[] commandlist = new MuseAppCommand[] {
			CLOSE, DETAIL, REFRESH, RENDER, OPEN_MUSICXML, PAUSE, PLAY, QUIT,
			SAVE, SAVEAS, SETENV, SHOW_CONSOLE, STOP, NULL };

	public static void setLanguage(String val) {
		_language = Language.create(val);
	}

	/**
	 * @param _main セットする _main
	 */
	public void setMain(MuseApp main) {
		if (_main == null | _main != main)
			_main = main;
	}

	/**
	 * @return the _language
	 */
	private static Language getLanguage() {
		return _language;
	}

	public void setTarget(TuneData target) {
		_target = target;
	}

	/**
	 * @param cmd
	 * @return
	 */
	protected static MuseAppCommand create(String cmd) {
		for (MuseAppCommand c : commandlist) {
			if (cmd.equals(c.name())) {
				return c;
			}
		}
		return NULL;
	}

	/**
	 * @return _main
	 */
	protected MuseApp main() {
		return _main;
	}

	protected TuneData target() {
		return _target;
	}

	private MainFrame _frame;

	private String[] names;

	private MuseAppCommand() {
		super();
		names = new String[Language.getLanguageList().length];
	}

	protected MuseAppCommand(String... lang) {
		this();
		if (isAssertion())
			assert lang.length <= names.length;
		for (int i = 0; i < names.length; i++) {
			names[i] = (i < lang.length) ? lang[i] : lang[0];
		}
	}

	public final String getText() {
		return names[getLanguage().getIndex()];
	}

	public final String name() {
		return getClass().getSimpleName().replace("MuseAppCommand", "");
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		throw new UnsupportedOperationException(name());
	}

	public void setGroup(GroupLabel groupLabel) {}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return getText();
	}

	private final void setSynthesizer(MIDIController synthe, String filename) {
		this.synthe = synthe;
		this.filename = filename;
	}

	/**
	 * @return _mainFrame
	 */
	protected MainFrame frame() {
		return _frame;
	}

	public void setFrame(MainFrame mainFrame) {
		if (_frame == null || _frame != mainFrame)
			_frame = mainFrame;
	}

}
