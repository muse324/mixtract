package net.muse.gui.command;

import net.muse.MuseApp;
import net.muse.data.TuneData;
import net.muse.gui.*;
import net.muse.misc.Command;
import net.muse.mixtract.gui.command.GroupCommandInterface;

public class MuseAppCommand extends Command implements GroupCommandInterface {

	public static final MuseAppCommand DELETE_GROUP = new DeleteGroupCommand(
			"Delete", "グループを削除");
	public static final MuseAppCommand ADD_GROUP = new AddGroupCommand(
			"Add group", "グループを追加");
	public static final MuseAppCommand EDIT_GROUP = new EditGroupCommand(
			"Edit group", "グループを編集");
	public static final MuseAppCommand MAKE_GROUP = new MakeGroupCommand(
			"Make a group");
	public static final MuseAppCommand OPEN_MUSICXML = new OpenMusicXMLCommand(
			"Open MusicXML", "MusicXMLを開く");
	public static final MuseAppCommand REDRAW = new RedrawCommand("Redraw",
			"再描画");
	public static final MuseAppCommand SELECT_GROUP = new SelectGroupCommand(
			"Select group", "グループを選択");
	public static final MuseAppCommand RENDER = new RenderCommand("Render",
			"生成");
	public static final MuseAppCommand DETAIL = new DetailCommand(
			"Show parameters", "Show parameters");
	public static final MuseAppCommand REFRESH = new RefreshCommand("Refresh",
			"更新");
	protected static TuneData _target;
	protected static MuseApp _main;
	protected static MainFrame _mainFrame;

	public static TuneData target() {
		return _target;
	}

	public MuseAppCommand(String... lang) {
		super(lang);
	}

	@Override public void setGroup(GroupLabel groupLabel) {}

	/**
	 * @return _main
	 */
	public static MuseApp main() {
		return _main;
	}

	/**
	 * @param _main セットする _main
	 */
	protected static void setMain(MuseApp _main) {
		MuseAppCommand._main = _main;
	}

	/**
	 * @return _mainFrame
	 */
	public static MainFrame frame() {
		return _mainFrame;
	}

	/**
	 * @param _mainFrame セットする _mainFrame
	 */
	public static void setMainFrame(MainFrame _mainFrame) {
		MuseAppCommand._mainFrame = _mainFrame;
	}

}
