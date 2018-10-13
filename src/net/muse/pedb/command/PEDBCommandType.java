package net.muse.pedb.command;

import net.muse.command.MuseAppCommandAction;
import net.muse.mixtract.command.MixtractCommand;

public enum PEDBCommandType implements MuseAppCommandAction {
	PEDBMAKE_GROUP("Make a group", "グループを作成") {
		@Override public MixtractCommand create(String... lang) {
			return (cmd = new PEDBMakeGroupCommand(lang));
		}
	},
	PEDBCHANGE_PART("Change part", "声部を変更") {
		@Override public MixtractCommand create(String... lang) {
			return (cmd = new PEDBChangePhonyCommand(lang));
		}
	};
	protected MixtractCommand cmd;

	PEDBCommandType(String... lang) {
		create(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.command.MuseAppCommandAction#command()
	 */
	@Override public MixtractCommand command() {
		return cmd;
	}

	public abstract MixtractCommand create(String... lang);

	/*
	 * (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override public void run() {
		cmd.run();
	}

}
