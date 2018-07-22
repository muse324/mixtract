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
	private static Language _language;
	protected static String filename;
	protected static MIDIController synthe;

	/**
	 * @param cmd
	 * @return
	 */
	public static MuseAppCommand create(String cmd) {
		MuseAppCommandType type = MuseAppCommandType.valueOf(cmd);
		return type.self();
	}

	public static MuseAppCommand create(MuseAppCommandType type) {
		return type.self();
	}

	public static void setLanguage(String val) {
		_language = Language.create(val);
	}

	/**
	 * @return the _language
	 */
	private static Language getLanguage() {
		return _language;
	}

	private MainFrame _frame;

	private TuneData _data;

	private String[] menuText;

	protected MuseApp _main;
	private Object _target;

	protected MuseAppCommand(String... lang) {
		super();
		menuText = new String[Language.getLanguageList().length];
		if (isAssertion())
			assert lang.length <= menuText.length;
		for (int i = 0; i < menuText.length; i++) {
			menuText[i] = (i < lang.length) ? lang[i] : lang[0];
		}
	}

	public final String getText() {
		return menuText[getLanguage().getIndex()];
	}

	public final String name() {
		return getClass().getSimpleName();
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		throw new UnsupportedOperationException(name());
	}

	public void setFrame(MainFrame mainFrame) {
		if (_frame == null || _frame != mainFrame)
			_frame = mainFrame;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.command.GroupCommandInterface#setGroup(net.muse.gui.GroupLabel)
	 */
	public void setGroup(GroupLabel groupLabel) {}

	/**
	 * @param _main セットする _main
	 */
	public void setMain(MuseApp main) {
		if (_main == null | _main != main)
			_main = main;
	}

	public void setTarget(TuneData target) {
		_data = target;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return getText();
	}

	/**
	 * @return _mainFrame
	 */
	protected MainFrame frame() {
		return _frame;
	}

	protected TuneData data() {
		return _data;
	}

	public void setTarget(Object obj) {
		_target = obj;
	}

	protected Object target() {
		return _target;
	}

	public MuseApp app() {
		return _main;
	}

}
