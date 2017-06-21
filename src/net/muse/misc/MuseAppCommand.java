package net.muse.misc;

import net.muse.MuseApp;
import net.muse.gui.*;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.gui.MixtractCommand;
import net.muse.mixtract.gui.command.GroupCommandInterface;

public class MuseAppCommand extends Command implements GroupCommandInterface {

	public static final MuseAppCommand DELETE_GROUP = new DeleteGroupCommand(
			"Delete", "グループを削除");
	public static final MuseAppCommand ADD_GROUP = new AddGroupCommand(
			"Add group", "グループを追加");
	public static final MuseAppCommand EDIT_GROUP = new EditGroupCommand(
			"Edit group", "グループを編集");
	public static final MuseAppCommand MAKE_GROUP = new PianoRoll.MakeGroupCommand(
				"Make a group");
	public static final MuseAppCommand OPEN_MUSICXML = new OpenMusicXMLCommand(
				"Open MusicXML", "MusicXMLを開く");
	public static final MuseAppCommand REDRAW = new RedrawCommand("Redraw",
				"再描画");
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

	protected static final class EditGroupCommand extends MixtractCommand {

		public EditGroupCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {}

	}

	/**
		 * @author Mitsuyo Hashida @ CrestMuse Project, JST
		 *         <address>http://www.m-use.net/</address>
		 *         <address>hashida@kwansei.ac.jp</address>
		 * @since 2009/03/12
		 */
		protected static class OpenMusicXMLCommand extends MixtractCommand {

			protected OpenMusicXMLCommand(String... lang) {
				super(lang);
			}

			/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */
			@Override public void execute() {
			// setTarget(openFileDialog());
			// _mainFrame.setNewData(true);
			// if (_target != null) {
			// showMusicDataPanel();
			// _mainFrame.notifySetTarget(_target);
			// _mainFrame.notifyReadTuneData(_target);
			// _mainFrame.refreshDatabase();
			// }
			}
			//
			// /**
			// * ダイアログから，表情付けの対象楽曲（XML）を開きます．
			// *
			// * @return fp
			// */
			// public String openFileDialog() {
			// final JFileChooser fc = new
			// JFileChooser(_main.getDefaultDirectory());
			// fc.setAcceptAllFileFilterUsed(true);
			// if (fc.showOpenDialog(_mainFrame) != JFileChooser.APPROVE_OPTION) {
			// GUIUtil.printConsole("File Chooser Cancelled.\n");
			// return null;
			// }
			// final File fp = fc.getSelectedFile();
			// final String fn = fp.getName();
			// final File dir = fp.getParentFile();
			// _main.setDefaultDirectory(dir);
			// //
			// DeviationInstanceWrapper.changeDefaultMusicXMLDirName(dir.getPath());
			// final String pathstr = dir.getPath()
			// .substring(fp.getParent().lastIndexOf("\\") + 1);
			// final String fname = pathstr + "/" + fn;
			// GUIUtil.printConsole("Opening: " + fname);
			// return fname;
			// }

			// /**
			// * @param openFileDialog
			// */
			// private void setTarget(String fname) {
			// if (fname == null)
			// return;
			// try {
			// _target = new GUITuneData(_main.readfile(fname));
			// ana = GTTMAnalyzer.run(_target, _mainFrame
			// .getAnalyzeStructureOnReading().isSelected(), _mainFrame
			// .getJCheckBoxMenuItem1().isSelected());
			// _target.setLatestGroupAnalysis(ana);
			// getSelectedObjects().setData(_target, DataType.GUI);
			// } catch (final IOException e) {
			// _target = null;
			// }
			// }

			// /**
			// *
			// */
			// private void showMusicDataPanel() {
			// // if (!_mainFrame.hasMusicData()) {
			// // JOptionPane.showMessageDialog(_mainFrame, "target is null.");
			// // return;
			// // }
			// _mainFrame.getPhraseStructureDialog().pack();
			// _mainFrame.getPhraseStructureDialog().setVisible(true);
			// _mainFrame.getScoreViewFrame().pack();
			// _mainFrame.getScoreViewFrame().setVisible(true);
			// _mainFrame.getRealtimeViewFrame().pack();
			// _mainFrame.getRealtimeViewFrame().setVisible(true);
			// _mainFrame.getExpressionViewDialog().pack();
			// _mainFrame.getExpressionViewDialog().setVisible(true);
			// _mainFrame.getPlayerDialog().pack();
			// _mainFrame.getPlayerDialog().setVisible(true);
			// _mainFrame.getTempoViewDialog().pack();
			// _mainFrame.getTempoViewDialog().setVisible(true);
			// _mainFrame.getDynamicsViewDialog().pack();
			// _mainFrame.getDynamicsViewDialog().setVisible(true);
			// }
		}

	protected static final class RedrawCommand extends MixtractCommand {

			public RedrawCommand(String... lang) {
				super(lang);
			}/*
				 * (non-Javadoc)
				 * @see net.muse.misc.Command#execute()
				 */

			@Override public void execute() {}

		}

	public MuseAppCommand(String... lang) {
		super(lang);
	}

	@Override public void setGroup(GroupLabel groupLabel) {}

}
