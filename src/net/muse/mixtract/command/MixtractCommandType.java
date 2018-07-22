package net.muse.mixtract.command;

import net.muse.command.MuseAppCommandAction;

public enum MixtractCommandType implements MuseAppCommandAction {
	ANALYZE_GTTM_STRUCTURE("GTTM Analysis", "GTTM分析") {
		@Override public GTTMAnalysisCommand create(String... lang) {
			return (GTTMAnalysisCommand) (self = new GTTMAnalysisCommand(lang));
		}
	},
	ANALYZE_STRUCTURE("Analyze structure", "構造分析") {
		@Override public AnalyzeStructureCommand create(String... lang) {
			return (AnalyzeStructureCommand) (self = new AnalyzeStructureCommand(
					lang));
		}
	},
	APPLY_HIERARCHICAL_PARAMS("Apply Parametrs", "階層表現に適用") {
		@Override public ApplyHierarchicalParamsCommand create(String... lang) {
			return (ApplyHierarchicalParamsCommand) (self = new ApplyHierarchicalParamsCommand(
					lang));
		}
	},
	APPLY_PULSES_CHOPINS("Apply pulses (chopin)") {
		@Override public ApplyPulseChopinsCommand create(String... lang) {
			return (ApplyPulseChopinsCommand) (self = new ApplyPulseChopinsCommand(
					lang));
		}
	},
	APPLY_PULSES_MOZARTS("Apply pulses (mozart)") {
		@Override public ApplyPuliseMozartsCommand create(String... lang) {
			return (ApplyPuliseMozartsCommand) (self = new ApplyPuliseMozartsCommand(
					lang));
		}
	},
	APPLY_TOPONOTE("Apply the most similar expression") {
		@Override public ApplyTopNoteCommand create(String... lang) {
			return (ApplyTopNoteCommand) (self = new ApplyTopNoteCommand(lang));
		}
	},
	CHANGE_PART("Change part", "声部を変更") {
		@Override public ChangePartCommand create(String... lang) {
			return (ChangePartCommand) (self = new ChangePartCommand(lang));
		}
	},
	CLEAR_ALLGROUPS("Clear all groups", "フレーズ構造全体を削除") {
		@Override public ClearAllGroupsCommand create(String... lang) {
			return (ClearAllGroupsCommand) (self = new ClearAllGroupsCommand(
					lang));
		}
	},
	DELETE_GROUP("Delete", "グループを削除") {
		@Override public DeleteGroupCommand create(String... lang) {
			return (DeleteGroupCommand) (self = new DeleteGroupCommand(lang));
		}
	},
	EDIT_GROUP("Edit group", "グループを編集") {
		@Override public EditGroupCommand create(String... lang) {
			return (EditGroupCommand) (self = new EditGroupCommand(lang));
		}
	},
	EXPR_LINE_DISPLAY("Show/hide expression line") {
		@Override public ExprLineCommand create(String... lang) {
			return (ExprLineCommand) (self = new ExprLineCommand(lang));
		}
	},
	EXPR_VIEW_DISPLAY("Switch expression view") {
		@Override public ExprViewCommand create(String... lang) {
			return (ExprViewCommand) (self = new ExprViewCommand(lang));
		}
	},
	MAKE_GROUP("Make a group", "グループを作成") {
		@Override public MakeGroupCommand create(String... lang) {
			return (MakeGroupCommand) (self = new MakeGroupCommand(lang));
		}
	},
	MOUSE_DISPLAY("Show/Hide mouse pointer", "マウスポインタを表示/非表示") {
		@Override public MouseDisplayCommand create(String... lang) {
			return (MouseDisplayCommand) (self = new MouseDisplayCommand(lang));
		}
	},
	OPEN_RULEMAP("Open Rulemap", "ルールマップを開く") {
		@Override public OpenRuleMapCommand create(String... lang) {
			return (OpenRuleMapCommand) (self = new OpenRuleMapCommand(lang));
		}
	},
	OPEN_RULEPANEL("Open rulepanel", "ルールパネル") {
		@Override public OpenRulePanelCommand create(String... lang) {
			return (OpenRulePanelCommand) (self = new OpenRulePanelCommand(
					lang));
		}
	},
	OPEN_STRUCTURE_DATA("Read structure data", "構造データ読込") {
		@Override public OpenStructureDataCommand create(String... lang) {
			return (OpenStructureDataCommand) (self = new OpenStructureDataCommand(
					lang));
		}
	},
	PRINT_ALL_SIMILAR_GROUPS("Show all similar groups",
			"Show all similar groups") {
		@Override public PrintAllSimilarGroupsCommand create(String... lang) {
			return (PrintAllSimilarGroupsCommand) (self = new PrintAllSimilarGroupsCommand(
					lang));
		}
	},
	PRINT_ALLGROUPS("Print all groups", "全グループを出力") {
		@Override public PrintAllGroupsCommand create(String... lang) {
			return (PrintAllGroupsCommand) (self = new PrintAllGroupsCommand(
					lang));
		}
	},
	PRINT_GROUP_INFO("Print group info.", "グループ情報") {
		@Override public PrintGroupInfoCommand create(String... lang) {
			return (PrintGroupInfoCommand) (self = new PrintGroupInfoCommand(
					lang));
		}
	},
	PRINT_SIMILAR_GROUPS("Show similar groups") {
		@Override public PrintSimilarGroupsCommand create(String... lang) {
			return (PrintSimilarGroupsCommand) (self = new PrintSimilarGroupsCommand(
					lang));
		}
	},
	PRINT_SUBGROUPS("Print group", "下位グループを出力") {
		@Override public PrintSubgroupsCommand create(String... lang) {
			return (PrintSubgroupsCommand) (self = new PrintSubgroupsCommand(
					lang));
		}
	},
	RESET_PRAMETERS("Reset parameters") {
		@Override public ResetParameterCommand create(String... lang) {
			return (ResetParameterCommand) (self = new ResetParameterCommand(
					lang));
		}
	},
	SEARCH("Search", "検索") {
		@Override public SearchCommand create(String... lang) {
			return (SearchCommand) (self = new SearchCommand(lang));
		}
	},
	SELECT_GROUP("Select group", "グループを選択") {
		@Override public SelectGroupCommand create(String... lang) {
			return (SelectGroupCommand) (self = new SelectGroupCommand(lang));
		}
	},
	SET_CHORD("Set Chord", "和音を付与") {
		@Override public SetChordCommand create(String... lang) {
			return (SetChordCommand) (self = new SetChordCommand(lang));
		}
	},
	SET_KEY("Change key", "調を変更") {
		@Override public SetKeyCommand create(String... lang) {
			return (SetKeyCommand) (self = new SetKeyCommand(lang));
		}
	},
	SET_KEYMODE("Change key mode", "長調/短調") {
		@Override public SetKeyModeCommand create(String... lang) {
			return (SetKeyModeCommand) (self = new SetKeyModeCommand(lang));
		}
	},
	SET_TYPE_CRESC("< (cresc.)") {
		@Override public SetCrescendoCommand create(String... lang) {
			return (SetCrescendoCommand) (self = new SetCrescendoCommand(lang));
		}
	},
	SET_TYPE_DIM("> (dim.)") {
		@Override public SetDiminuendoCommand create(String... lang) {
			return (SetDiminuendoCommand) (self = new SetDiminuendoCommand(
					lang));
		}
	},
	SHOW_SIMILAR_GROUPS("Show similar groups") {
		@Override public ShowSimilarGroupsCommand create(String... lang) {
			return (ShowSimilarGroupsCommand) (self = new ShowSimilarGroupsCommand(
					lang));
		}
	};

	protected MixtractCommand self;
	public abstract MixtractCommand create(String... lang);
	MixtractCommandType(String... lang) {
		create(lang);
	}

	@Override public void run() {
		self.run();
	}

	public MixtractCommand command() {
		return self;
	}

}
