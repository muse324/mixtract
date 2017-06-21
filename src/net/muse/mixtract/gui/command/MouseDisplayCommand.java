package net.muse.mixtract.gui.command;

final class MouseDisplayCommand extends MixtractCommand {

	public MouseDisplayCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		frame().getGroupingPanel().setDisplayMousePointer(!frame()
				.getGroupingPanel().isDisplayMousePointer());
	}

}