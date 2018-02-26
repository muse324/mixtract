package net.muse.mixtract.command;

import net.muse.app.Mixtract;
import net.muse.command.MuseAppCommand;
import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXGroupAnalyzer;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.gui.MXGroupLabel;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2008/04/21
 */
public class MixtractCommand extends MuseAppCommand {
	private MXGroupLabel _groupLabel;
	private MXGroup _group;
	protected static final Group _selectedObjects = null;
	private static MXGroupAnalyzer ana;

	public Mixtract main() {
		return (Mixtract) _main;
	}

	protected static final MixtractCommand EDIT_GROUP = new EditGroupCommand(
			"Edit group", "グループを編集");
	protected static final MixtractCommand SELECT_GROUP = new SelectGroupCommand(
			"Select group", "グループを選択");

	public static final MixtractCommand SET_CHORD = new SetChordCommand(
			"Set Chord", "和音を付与");
	public static final MixtractCommand ANALYZE_STRUCTURE = new AnalyzeStructureCommand(
			"Analyze structure", "構造分析");

	public static final MixtractCommand APPLY_PULSES_CHOPINS = new ApplyPulseChopinsCommand(
			"Apply pulses (chopin)");

	public static final MixtractCommand APPLY_PULSES_MOZARTS = new ApplyPuliseMozartsCommand(
			"Apply pulses (mozart)");

	public static final MixtractCommand APPLY_TOPONOTE = new ApplyTopNoteCommand(
			"Apply the most similar expression");

	public static final MixtractCommand CLEAR_ALLGROUPS = new ClearAllGroupsCommand(
			"Clear all groups", "フレーズ構造全体を削除");

	public static final MixtractCommand DELETE_GROUP = new DeleteGroupCommand(
			"Delete", "グループを削除");

	public static final MixtractCommand EXPR_LINE_DISPLAY = new ExprLineCommand(
			"Show/hide expression line");

	public static final MixtractCommand EXPR_VIEW_DISPLAY = new ExprViewCommand(
			"Switch expression view");
	public static final MixtractCommand CHANGE_PART = new ChangePartCommand(
			"Change part");

	public static final MixtractCommand MOUSE_DISPLAY = new MouseDisplayCommand(
			"Show/Hide mouse pointer", "マウスポインタを表示/非表示");

	public static final MixtractCommand OPEN_RULEPANEL = new OpenRulePanelCommand(
			"Open rulepanel", "ルールパネル");

	public static final MixtractCommand OPEN_RULEMAP = new OpenRuleMapCommand(
			"Open Rulemap", "ルールマップを開く");
	public static final MixtractCommand PRINT_ALLGROUPS = new PrintAllGroupsCommand(
			"Print all groups", "全グループを出力");

	public static final MixtractCommand PRINT_ALL_SIMILAR_GROUPS = new PrintAllSimilarGroupsCommand(
			"Show all similar groups", "Show all similar groups");

	public static final MixtractCommand PRINT_SIMILAR_GROUPS = new PrintSimilarGroupsCommand(
			"Show similar groups");

	public static final MixtractCommand PRINT_SUBGROUPS = new PrintSubgroupsCommand(
			"Print group", "下位グループを出力");

	public static final MixtractCommand APPLY_HIERARCHICAL_PARAMS = new ApplyHierarchicalParamsCommand(
			"Apply Parametrs", "階層表現に適用");

	public static final MixtractCommand RESET_PRAMETERS = new ResetParameterCommand(
			"Reset parameters");
	public static final MixtractCommand SHOW_SIMILAR_GROUPS = new ShowSimilarGroupsCommand(
			"Show similar groups");

	public static final MixtractCommand SEARCH = new SearchCommand("Search",
			"検索");

	public static final MixtractCommand ANALYZE_GTTM_STRUCTURE = new GTTMAnalysisCommand(
			"GTTMAnalysis");
	public static final MixtractCommand MAKE_GROUP = new MakeGroupCommand(
			"Make a group", "グループを作成");

	public static final MixtractCommand OPEN_STRUCTURE_DATA = new OpenStructureDataCommand(
			"Read structure data", "構造データ読込");
	public static final MixtractCommand SET_KEY = new SetKeyCommand(
			"Change key", "調を変更");
	public static final MixtractCommand SET_KEYMODE = new SetKeyModeCommand(
			"Change key mode", "長調/短調");
	public static final MixtractCommand SET_TYPE_CRESC = new SetCrescendoCommand(
			"< (cresc.)");
	public static final MixtractCommand SET_TYPE_DIM = new SetDiminuendoCommand(
			"> (dim.)");
	public static final MixtractCommand PRINT_GROUP_INFO = new PrintGroupInfoCommand(
			"Print group info.", "グループ情報");
	private static MixtractCommand commandLists[] = new MixtractCommand[] {
			ANALYZE_STRUCTURE, DELETE_GROUP, EDIT_GROUP, MOUSE_DISPLAY,
			OPEN_RULEPANEL, PRINT_ALL_SIMILAR_GROUPS, PRINT_ALLGROUPS,
			PRINT_SIMILAR_GROUPS, PRINT_SUBGROUPS, OPEN_RULEMAP, SELECT_GROUP,
			SHOW_SIMILAR_GROUPS, APPLY_PULSES_CHOPINS, APPLY_PULSES_MOZARTS,
			APPLY_TOPONOTE, MAKE_GROUP, CHANGE_PART, EXPR_LINE_DISPLAY,
			EXPR_VIEW_DISPLAY, RESET_PRAMETERS, SEARCH, ANALYZE_GTTM_STRUCTURE,
			APPLY_HIERARCHICAL_PARAMS, OPEN_STRUCTURE_DATA, CLEAR_ALLGROUPS,
			SET_CHORD, SET_KEY, SET_TYPE_CRESC, SET_TYPE_DIM,
			PRINT_GROUP_INFO };

	/**
	 * @param mainFrame
	 * @param main
	 * @return
	 */
	public static MuseAppCommand create(String cmd) {
		for (final MuseAppCommand x : commandLists) {
			if (cmd.equals(x.name())) {
				return x;
			}
		}
		return MuseAppCommand.create(cmd);
	}

	/**
	 * @return the _selectedTarget
	 */
	public static final Object getSelectedObjects() {
		return _selectedObjects;
	}

	public final MXTuneData target() {
		return (MXTuneData) _target;
	}

	/**
	 * @return
	 */
	public boolean hasTarget() {
		return _target != null;
	}

	public void setTarget(TuneData target) {
		_target = target;
	}

	protected MixtractCommand(String... lang) {
		super(lang);

	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#toString()
	 */
	@Override public String toString() {
		return super.toString();
	}

	@Override public void setGroup(GroupLabel groupLabel) {
		setGroupLabel(groupLabel);
		assert groupLabel instanceof MXGroupLabel;
		_group = (MXGroup) groupLabel.group();
	}

	/**
	 * @return _groupLabel
	 */
	public GroupLabel getGroupLabel() {
		return _groupLabel;
	}

	/**
	 * @param _groupLabel セットする _groupLabel
	 */
	public void setGroupLabel(GroupLabel _groupLabel) {
		assert _groupLabel instanceof MXGroupLabel;
		this._groupLabel = (MXGroupLabel) _groupLabel;
	}

	/**
	 * @return _group
	 */
	public Group getGroup() {
		return _group;
	}

}
