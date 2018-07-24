package net.muse.pedb.command;

import net.muse.command.MuseAppCommandAction;
import net.muse.mixtract.command.MixtractCommand;

public enum PEDBCommandType implements MuseAppCommandAction {
	MAKE_GROUP("Make a group", "グループを作成") {
		@Override
		public MixtractCommand create(String... lang) {
			return (cmd = new PEDBMakeGroupCommand(lang));
		}
	},
	CHANGE_PART("Change part", "声部を変更") {
		@Override
		public MixtractCommand create(String... lang) {
			return (cmd = new PEDBChangePhonyCommand(lang));
		}
	};
	protected MixtractCommand cmd;

	public abstract MixtractCommand create(String... lang);

	PEDBCommandType(String... lang) {
		create(lang);
	}

	@Override
	public void run() {
		cmd.run();
	}

	@Override
	public MixtractCommand command() {
		return cmd;
	}

}
