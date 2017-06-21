package net.muse.mixtract.gui.command;

final class ExprLineCommand extends MixtractCommand {

	public ExprLineCommand(String... lang) {
		super(lang);
	}/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */

	@Override public void execute() {
		// _mainFrame.getExpressionPanel().setShowExprLine(
		// !_mainFrame
		// .getExpressionPanel()
		// .isShowExprLine());
	}

}