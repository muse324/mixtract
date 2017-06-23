package net.muse.mixtract.command;

final class ExprViewCommand extends MixtractCommand {

	public ExprViewCommand(String... lang) {
		super(lang);
	}/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */

	@Override public void execute() {
		// _mainFrame.getExpressionPanel()
		// .setShowExpression(
		// !_mainFrame.getExpressionPanel()
		// .isShowExpression());
	}

}