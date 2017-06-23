package net.muse.mixtract.gui.command;

import net.muse.command.OpenMusicXMLCommand;

/**
 * <h1>OpenStructureDataCommand</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/09/01
 */
class OpenStructureDataCommand extends MixtractCommand {

	/**
	 * @param lang
	 */
	public OpenStructureDataCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MixtractCommand.OpenMusicXMLCommand#execute
	 * ()
	 */
	@Override public void execute() {
		// if (isAssertion()) {
		// assert _target != null : "target is null";
		// assert ana != null : "target has't been analysed structure yet";
		// }
		// String fn = openFileDialog();
		// try {
		// GTTMAnalyzer.setReadFile(true);
		// ana.analyzeGroupStructure(fn);
		// _target.setLatestGroupAnalysis(ana);
		// _mainFrame.setNewData(true);
		// if (_target != null) {
		// _mainFrame.notifySetTarget(_target);
		// _mainFrame.notifyReadTuneData(_target);
		// _mainFrame.refreshDatabase();
		// }
		// // _mainFrame.getGroupingPanel().repaint();
		// // _mainFrame.getTempoPanel().repaint();
		// } catch (TransformerException e) {
		// e.printStackTrace();
		// }
	}

}