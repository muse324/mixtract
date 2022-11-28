package net.muse.mixtract.command;

final class ApplyPuliseMozartsCommand extends
		MixtractCommand {

	public ApplyPuliseMozartsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		// ExpressionMaker
		// .transerPulsesTo(getSelectedObjects().getGroup(), "Mozart");
	}

}