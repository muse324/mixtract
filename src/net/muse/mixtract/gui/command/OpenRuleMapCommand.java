package net.muse.mixtract.gui.command;

import net.muse.gui.command.MuseAppCommand.OpenMusicXMLCommand;

class OpenRuleMapCommand extends OpenMusicXMLCommand {

	protected OpenRuleMapCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MixtractCommand.OpenMusicXMLCommand#execute
	 * ()
	 */
	@Override public void execute() {
		// try {
		// final File fp = new File(openFileDialog());
		// GUIUtil.printConsole(fp.getName() + " is reading...");
		// final Rulemap rulemap = Rulemap.createRulemap(fp);
		// _mainFrame.getParamPanel().setRulemap(rulemap);
		// _mainFrame.getParamPanel().assignRulemapToSliders();
		// GUIUtil.printConsole("done.\n");
		// } catch (final NullPointerException e) {
		// GUIUtil.printConsole("openRuleMapFromDialog cancelled");
		// } catch (final FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (final IOException e) {
		// e.printStackTrace();
		// }
	}

}