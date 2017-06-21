package net.muse.misc;

import net.muse.MuseApp;
import net.muse.gui.GroupLabel;
import net.muse.gui.MainFrame;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.gui.MixtractCommand;
import net.muse.mixtract.gui.command.GroupCommandInterface;

public class MuseAppCommand extends Command implements GroupCommandInterface {

	public static final MuseAppCommand DELETE_GROUP = new DeleteGroupCommand(
			"Delete", "グループを削除");
	public static final MixtractCommand ADD_GROUP = new AddGroupCommand(
			"Add group", "グループを追加");
	protected static MXTuneData _target;
	protected static MuseApp _main;
	protected static MainFrame _mainFrame;

	protected static final class DeleteGroupCommand extends MixtractCommand {

		public DeleteGroupCommand(String... lang) {
			super(lang);
		}/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */

		@Override public void execute() {
			if (_target != null) {
				GroupLabel sel = _mainFrame.getGroupingPanel()
						.getSelectedGroup();
				// _target.deleteGUIGroup(_selectedObjects.getGroupLabel());
				// _selectedObjects.clearAll();
				_main.getData().deleteGroupFromData(sel.getGroup());
				_main.notifyDeleteGroup(sel);
				// getPianorollScroll().repaint();
				// getGroupingPanel().deselectLabel();
				// getExpressionPanel().clearGroup();
				// setTune(target);
			}
		}

	}

	protected static final class AddGroupCommand extends MixtractCommand {

		public AddGroupCommand(String... lang) {
			super(lang);
		}

	}

	public MuseAppCommand(String... lang) {
		super(lang);
	}

	@Override public void setGroup(GroupLabel groupLabel) {}

}
