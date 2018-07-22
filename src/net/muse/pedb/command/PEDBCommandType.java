package net.muse.pedb.command;

import net.muse.command.MuseAppCommandAction;

public enum PEDBCommandType implements MuseAppCommandAction {
	MAKE_GROUP {
		@Override public PEDBEditCommand create(String... lang) {
			return (PEDBEditCommand) (cmd = new PEDBMakeGroupCommand(lang));
		}
	},
	CHANGE_PART {
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
