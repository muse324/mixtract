package net.muse.mixtract.gui.command;

final class OpenRulePanelCommand extends MixtractCommand {

	public OpenRulePanelCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		showParameterPanel();
	}

	public void showParameterPanel() {
		// final JDialog dialog = new JDialog(_mainFrame);
		// dialog.add(_mainFrame.getParamPanel());
		// dialog.pack();
		// dialog.setVisible(true);
	}
}