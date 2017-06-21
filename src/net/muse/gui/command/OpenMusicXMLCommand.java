package net.muse.gui.command;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/12
 */
public class OpenMusicXMLCommand extends MuseAppCommand {

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