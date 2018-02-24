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

	public static final MuseAppCommand CLOSE = new CloseCommand("Close", "閉じる");
	public static final MuseAppCommand DETAIL = new DetailCommand(
			"Show parameters", "詳細表示");
	public static final MuseAppCommand EDIT_GROUP = new EditGroupCommand(
			"Edit group", "グループを編集");
	public static final MuseAppCommand MAKE_GROUP = new MakeGroupCommand(
			"Make a group", "グループを作成");
	public static final MuseAppCommand NULL = new NullCommand("Null");
	public static final MuseAppCommand OPEN_MUSICXML = new OpenMusicXMLCommand(
			"Open MusicXML...", "MusicXMLを開く...");
	public static final MuseAppCommand PAUSE = new PauseCommand("Pause",
			"一時停止");
	public static final MuseAppCommand PLAY = new PlayCommand("Play", "再生");
	public static final MuseAppCommand PRINT_ALLGROUPS = new PrintAllGroupsCommand(
			"Print all groups", "全グループを出力");
	public static final MuseAppCommand QUIT = new QuitCommand("Quit", "終了");
	public static final MuseAppCommand REDRAW = new RedrawCommand("Redraw",
			"再描画");
	public static final MuseAppCommand REFRESH = new RefreshCommand("Refresh",
			"更新");
	public static final MuseAppCommand RENDER = new RenderCommand("Render",
			"生成");
	public static final MuseAppCommand SAVE = new SaveCommand("Save", "保存");
	public static final MuseAppCommand SAVEAS = new SaveAsCommand("Save as",
			"別名で保存");
	public static final MuseAppCommand SELECT_GROUP = new SelectGroupCommand(
			"Select group", "グループを選択");
	public static final MuseAppCommand SETENV = new SetEnvCommand("Setup",
			"環境設定");
	public static final MuseAppCommand SHOW_CONSOLE = new ShowConsoleCommand(
			"Console", "コンソール");
	public static final MuseAppCommand STOP = new StopCommand("Stop", "停止");
	private static Language _language;
	private static final MuseAppCommand[] commandlist = new MuseAppCommand[] {
			CLOSE, PAUSE, PLAY, QUIT, SAVE, SAVEAS, SETENV, SHOW_CONSOLE, STOP,
			NULL };
	@Deprecated protected static MuseApp _main;

	protected MainFrame _frame;

	@Deprecated protected static TuneData _target;

	static String filename;

	static MIDIController synthe;

	/**
	 * @param cmd
	 * @return
	 */
	public static MuseAppCommand create(MainFrame mainFrame, String cmd) {
		for (MuseAppCommand c : commandlist) {
			if (cmd.equals(c.name())) {
				c.setFrame(mainFrame);
				return c;
			}
		}
		return NULL;
	}

	private void setFrame(MainFrame mainFrame) {
		if (_frame == null || _frame != mainFrame)
			_frame = mainFrame;
	}

	/**
	 * @return _mainFrame
	 */
	public MainFrame frame() {
		return _frame;
	}

	/**
	 * @return the _language
	 */
	public static Language getLanguage() {
		return _language;
	}

	/**
	 * @return _main
	 */
	@Deprecated public static MuseApp main() {
		return _main;
	}

	public static void setLanguage(String val) {
		_language = Language.create(val);
	}

	/**
	 * @param _main セットする _main
	 */
	public static void setMain(MuseApp main) {
		_main = main;
	}

	@Deprecated public static TuneData target() {
		return _target;
	}

	protected static void setTarget(TuneData _target) {
		MuseAppCommand._target = _target;
	}

	private String[] names;

	public MuseAppCommand(String... lang) {
		this();
		if (isAssertion())
			assert lang.length <= names.length;
		for (int i = 0; i < names.length; i++) {
			names[i] = (i < lang.length) ? lang[i] : lang[0];
		}
	}

	private MuseAppCommand() {
		super();
		names = new String[Language.getLanguageList().length];
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

	public final void setSynthesizer(MIDIController synthe, String filename) {
		this.synthe = synthe;
		this.filename = filename;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return getText();
	}

}
