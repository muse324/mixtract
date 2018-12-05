package net.muse.pedb.command;

import net.muse.command.MuseAppCommand;
import net.muse.command.MuseAppCommandAction;
import net.muse.mixtract.command.DeleteGroupCommand;

public enum PEDBCommandType implements MuseAppCommandAction {
	PEDBMAKE_GROUP("Make a group", "グループを作成") {
		@Override public PEDBMakeGroupCommand create(String... lang) {
			return (PEDBMakeGroupCommand) (cmd = new PEDBMakeGroupCommand(
					lang));
		}
	},
	PEDBCHANGE_PART("Change part", "声部を変更") {
		@Override public PEDBChangePhonyCommand create(String... lang) {
			return (PEDBChangePhonyCommand) (cmd = new PEDBChangePhonyCommand(
					lang));
		}
	},
	PEDBIMPORT_MUSICXML("Import MusicXML...", "MusicXMLを開く...") {
		@Override public PEDBOpenMusicXMLCommand create(String... lang) {
			return (PEDBOpenMusicXMLCommand) (cmd = new PEDBOpenMusicXMLCommand(
					lang));
		}
	},
	DELETE_GROUP {
		@Override public MuseAppCommand create(String... lang) {
			return (DeleteGroupCommand) (cmd = new DeleteGroupCommand(lang));
		}
	};
	protected MuseAppCommand cmd;

	PEDBCommandType(String... lang) {
		create(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.command.MuseAppCommandAction#command()
	 */
	@Override public MuseAppCommand command() {
		return cmd;
	}

	public abstract MuseAppCommand create(String... lang);

	/*
	 * (非 Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override public void run() {
		cmd.run();
	}

}
