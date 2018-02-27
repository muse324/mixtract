package net.muse.command;

public enum MuseAppCommandType implements Runnable, MuseAPpCommandAction {
	CLOSE("Close", "閉じる") {
		@Override CloseCommand create(String... lang) {
			return (CloseCommand) (self = new CloseCommand(lang));
		}
	},
	DETAIL("Show parameters", "詳細表示") {
		@Override DetailCommand create(String... lang) {
			return (DetailCommand) (self = new DetailCommand(lang));
		}
	},
	REFRESH("Refresh", "更新") {
		@Override RefreshCommand create(String... lang) {
			return (RefreshCommand) (self = new RefreshCommand(lang));
		}
	},
	RENDER("Render", "生成") {
		@Override RenderCommand create(String... lang) {
			return (RenderCommand) (self = new RenderCommand(lang));
		}
	},
	OPEN_MUSICXML("Open MusicXML...", "MusicXMLを開く...") {
		@Override OpenMusicXMLCommand create(String... lang) {
			return (OpenMusicXMLCommand) (self = new OpenMusicXMLCommand(lang));
		}
	},
	PAUSE("Pause", "一時停止") {
		@Override PauseCommand create(String... lang) {
			return (PauseCommand) (self = new PauseCommand(lang));
		}
	},
	PLAY("Play", "再生") {
		@Override PlayCommand create(String... lang) {
			return (PlayCommand) (self = new PlayCommand(lang));
		}
	},
	QUIT("Quit", "終了") {
		@Override QuitCommand create(String... lang) {
			return (QuitCommand) (self = new QuitCommand(lang));
		}
	},
	SAVE("Save", "保存") {
		@Override SaveCommand create(String... lang) {
			return (SaveCommand) (self = new SaveCommand(lang));
		}
	},
	SAVEAS("Save as", "別名で保存") {
		@Override SaveAsCommand create(String... lang) {
			return (SaveAsCommand) (self = new SaveAsCommand(lang));
		}
	},
	SETENV("Setup", "環境設定") {
		@Override SetEnvCommand create(String... lang) {
			return (SetEnvCommand) (self = new SetEnvCommand(lang));
		}
	},
	SHOW_CONSOLE("Console", "コンソール") {
		@Override ShowConsoleCommand create(String... lang) {
			return (ShowConsoleCommand) (self = new ShowConsoleCommand(lang));
		}
	},
	STOP("Stop", "停止") {
		@Override StopCommand create(String... lang) {
			return (StopCommand) (self = new StopCommand(lang));
		}
	},
	NULL("NULL") {
		@Override NullCommand create(String... lang) {
			return (NullCommand) (self = new NullCommand(lang));
		}
	};

	protected MuseAppCommand self;

	MuseAppCommandType(String... lang) {
		create(lang);
	}

	abstract MuseAppCommand create(String... lang);

	@Override public void run() {
		self.run();
	}

	public MuseAppCommand self() {
		return self;
	}

}