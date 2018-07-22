package net.muse.command;

public enum MuseAppCommandType implements MuseAppCommandAction {
	CLOSE("Close", "閉じる") {
		@Override public CloseCommand create(String... lang) {
			return (CloseCommand) (cmd = new CloseCommand(lang));
		}
	},
	DETAIL("Show parameters", "詳細表示") {
		@Override public DetailCommand create(String... lang) {
			return (DetailCommand) (cmd = new DetailCommand(lang));
		}
	},
	REFRESH("Refresh", "更新") {
		@Override public RefreshCommand create(String... lang) {
			return (RefreshCommand) (cmd = new RefreshCommand(lang));
		}
	},
	RENDER("Render", "生成") {
		@Override public RenderCommand create(String... lang) {
			return (RenderCommand) (cmd = new RenderCommand(lang));
		}
	},
	OPEN_MUSICXML("Import MusicXML...", "MusicXMLを開く...") {
		@Override public OpenMusicXMLCommand create(String... lang) {
			return (OpenMusicXMLCommand) (cmd = new OpenMusicXMLCommand(lang));
		}
	},
	PAUSE("Pause", "一時停止") {
		@Override public PauseCommand create(String... lang) {
			return (PauseCommand) (cmd = new PauseCommand(lang));
		}
	},
	PLAY("Play", "再生") {
		@Override public PlayCommand create(String... lang) {
			return (PlayCommand) (cmd = new PlayCommand(lang));
		}
	},
	QUIT("Quit", "終了") {
		@Override public QuitCommand create(String... lang) {
			return (QuitCommand) (cmd = new QuitCommand(lang));
		}
	},
	SAVE("Save", "保存") {
		@Override public SaveCommand create(String... lang) {
			return (SaveCommand) (cmd = new SaveCommand(lang));
		}
	},
	SAVEAS("Save as", "別名で保存") {
		@Override public SaveAsCommand create(String... lang) {
			return (SaveAsCommand) (cmd = new SaveAsCommand(lang));
		}
	},
	SETENV("Setup", "環境設定") {
		@Override public SetEnvCommand create(String... lang) {
			return (SetEnvCommand) (cmd = new SetEnvCommand(lang));
		}
	},
	SHOW_CONSOLE("Console", "コンソール") {
		@Override public ShowConsoleCommand create(String... lang) {
			return (ShowConsoleCommand) (cmd = new ShowConsoleCommand(lang));
		}
	},
	STOP("Stop", "停止") {
		@Override public StopCommand create(String... lang) {
			return (StopCommand) (cmd = new StopCommand(lang));
		}
	},
	NULL("NULL") {
		@Override public NullCommand create(String... lang) {
			return (NullCommand) (cmd = new NullCommand(lang));
		}
	};

	protected MuseAppCommand cmd;

	MuseAppCommandType(String... lang) {
		create(lang);
	}

	public abstract MuseAppCommand create(String... lang);

	@Override public void run() {
		cmd.run();
	}

	public MuseAppCommand command() {
		return cmd;
	}

}