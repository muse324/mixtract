package net.muse.pedb.command;

import net.muse.command.MuseAppCommandAction;

public enum PEDBCommandType implements MuseAppCommandAction {
	MAKE_GROUP("Make a group", "グループを作成") {
		@Override public PEDBEditCommand create(String... lang) {
			return (PEDBEditCommand) (cmd = new PEDBMakeGroupCommand(lang));
		}
	},
	CHANGE_PART("Change part", "声部を変更") {
		@Override public PEDBEditCommand create(String... lang) {
			return (PEDBEditCommand) (cmd = new PEDBChangePhonyCommand(lang));
		}
	};
	protected PEDBEditCommand cmd;

	public abstract PEDBEditCommand create(String... lang);

	PEDBCommandType(String... lang) {
		create(lang);
	}

	@Override public void run() {
		cmd.run();
	}

	@Override public PEDBEditCommand command() {
		return cmd;
	}

}
