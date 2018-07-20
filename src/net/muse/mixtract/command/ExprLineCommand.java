package net.muse.mixtract.command;

final class ExprLineCommand extends MixtractCommand {

	public ExprLineCommand(String... lang) {
		super(lang);
	}/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */

	@Override public void run() {
		// _mainFrame.getExpressionPanel().setShowExprLine(
		// !_mainFrame
		// .getExpressionPanel()
		// .isShowExprLine());
	}

}