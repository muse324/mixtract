package net.muse.gui.command;

import net.muse.mixtract.gui.command.MixtractCommand;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/12
 */
class RenderCommand extends MixtractCommand {

	protected RenderCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		// ExpressionMaker.render(_target);
		// _mainFrame.notifySetTarget(_target);
	}
}