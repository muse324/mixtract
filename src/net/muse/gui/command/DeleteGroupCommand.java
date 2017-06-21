package net.muse.gui.command;

import net.muse.gui.GroupLabel;

final class DeleteGroupCommand extends MuseAppCommand {

	public DeleteGroupCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		if (_target != null) {
			GroupLabel sel = frame().getGroupingPanel().getSelectedGroup();
			// _target.deleteGUIGroup(_selectedObjects.getGroupLabel());
			// _selectedObjects.clearAll();
			main().getData().deleteGroupFromData(sel.getGroup());
			main().notifyDeleteGroup(sel);
			// getPianorollScroll().repaint();
			// getGroupingPanel().deselectLabel();
			// getExpressionPanel().clearGroup();
			// setTune(target);
		}
	}

}