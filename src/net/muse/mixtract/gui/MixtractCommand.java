package net.muse.mixtract.gui;

import java.io.IOException;

import javax.swing.JFrame;

import net.muse.MuseApp;
import net.muse.gui.*;
import net.muse.gui.GroupingPanel.PrintGroupInfoCommand;
import net.muse.misc.Command;
import net.muse.mixtract.Mixtract;
import net.muse.mixtract.command.GroupAnalyzer;
import net.muse.mixtract.data.Group;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.gui.command.*;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2008/04/21
 */
public class MixtractCommand extends Command implements GroupCommandInterface {

	public static final MixtractCommand ADD_GROUP = new AddGroupCommand(
			"Add group", "グループを追加");
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

	public static final MixtractCommand CLEAR_ALLGROUPS = new GroupingPanel.ClearAllGroupsCommand(
			"Clear all groups", "フレーズ構造全体を削除");

	public static final MixtractCommand DELETE_GROUP = new DeleteGroupCommand(
			"Delete", "グループを削除");

	public static final MixtractCommand DETAIL = new DetailCommand(
			"Show parameters", "Show parameters");

	public static final MixtractCommand EDIT_GROUP = new EditGroupCommand(
			"Edit group", "グループを編集");

	public static final MixtractCommand EXPR_LINE_DISPLAY = new ExprLineCommand(
			"Show/hide expression line");

	public static final MixtractCommand EXPR_VIEW_DISPLAY = new ExprViewCommand(
			"Switch expression view");

	public static final MixtractCommand MAKE_GROUP = new PianoRoll.MakeGroupCommand(
			"Make a group");
	public static final MixtractCommand CHANGE_PART = new PianoRoll.ChangePartCommand(
			"Change part");

	public static final MixtractCommand MOUSE_DISPLAY = new MouseDisplayCommand(
			"Show/Hide mouse pointer", "マウスポインタを表示/非表示");

	public static final MixtractCommand OPEN_RULEPANEL = new OpenRulePanelCommand(
			"Open rulepanel", "ルールパネル");

	public static final MixtractCommand OPEN_MUSICXML = new OpenMusicXMLCommand(
			"Open MusicXML", "MusicXMLを開く");

	public static final MixtractCommand OPEN_RULEMAP = new OpenRuleMapCommand(
			"Open Rulemap", "ルールマップを開く");

	public static final MixtractCommand PRINT_ALL_SIMILAR_GROUPS = new PrintAllSimilarGroupsCommand(
			"Show all similar groups", "Show all similar groups");

	public static final MixtractCommand PRINT_ALLGROUPS = new PrintAllGroupsCommand(
			"Print all groups", "全グループを出力");

	public static final MixtractCommand PRINT_SIMILAR_GROUPS = new PrintSimilarGroupsCommand(
			"Show similar groups");

	public static final MixtractCommand PRINT_SUBGROUPS = new PrintSubgroupsCommand(
			"Print group", "下位グループを出力");

	public static final MixtractCommand REDRAW = new RedrawCommand("Redraw",
			"再描画");

	public static final MixtractCommand REFRESH = new RefreshCommand("Refresh",
			"更新");

	public static final ApplyHierarchicalParamsCommand APPLY_HIERARCHICAL_PARAMS = new ApplyHierarchicalParamsCommand(
			"Apply Parametrs", "階層表現に適用");

	public static final MixtractCommand RESET_PRAMETERS = new ResetParameterCommand(
			"Reset parameters");
	public static final MixtractCommand SELECT_GROUP = new SelectGroupCommand(
			"Select group", "グループを選択");

	public static final MixtractCommand SHOW_SIMILAR_GROUPS = new ShowSimilarGroupsCommand(
			"Show similar groups");

	public static final MixtractCommand SEARCH = new SearchCommand("Search", "検索");

	public static final MixtractCommand RENDER = new RenderCommand("Render", "生成");
	public static final MixtractCommand ANALYZE_GTTM_STRUCTURE = new GTTMAnalysisCommand(
			"GTTMAnalysis");
	protected static MXTuneData _target;

	protected static MuseApp _main;

	protected static MainFrame _mainFrame;
	protected static final SelectedObjects _selectedObjects = new SelectedObjects();

	public static final MixtractCommand OPEN_STRUCTURE_DATA = new OpenStructureDataCommand(
			"Read structure data", "構造データ読込");
	public static final MixtractCommand SET_KEY = new PianoRoll.SetKeyCommand(
			"Change key", "調を変更");
	public static final MixtractCommand SET_KEYMODE = new PianoRoll.SetKeyModeCommand(
			"Change key mode", "長調/短調");
	public static final SetCrescendoCommand SET_TYPE_CRESC = new SetCrescendoCommand(
			"< (cresc.)");
	public static final SetDiminuendoCommand SET_TYPE_DIM = new SetDiminuendoCommand(
			"> (dim.)");
	public static final PrintGroupInfoCommand PRINT_GROUP_INFO = new GroupingPanel.PrintGroupInfoCommand(
			"Print group info.", "グループ情報");

	private static GroupAnalyzer ana;
	private static MixtractCommand commandLists[] = new MixtractCommand[] {
			ADD_GROUP, ANALYZE_STRUCTURE, DELETE_GROUP, DETAIL, EDIT_GROUP,
			MOUSE_DISPLAY, OPEN_RULEPANEL, PRINT_ALL_SIMILAR_GROUPS, PRINT_ALLGROUPS,
			PRINT_SIMILAR_GROUPS, PRINT_SUBGROUPS, OPEN_MUSICXML, OPEN_RULEMAP,
			REDRAW, REFRESH, SELECT_GROUP, SHOW_SIMILAR_GROUPS, APPLY_PULSES_CHOPINS,
			APPLY_PULSES_MOZARTS, APPLY_TOPONOTE, MAKE_GROUP, CHANGE_PART,
			EXPR_LINE_DISPLAY, EXPR_VIEW_DISPLAY, RESET_PRAMETERS, SEARCH, RENDER,
			ANALYZE_GTTM_STRUCTURE, APPLY_HIERARCHICAL_PARAMS, OPEN_STRUCTURE_DATA,
			CLEAR_ALLGROUPS, SET_CHORD, SET_KEY, SET_TYPE_CRESC, SET_TYPE_DIM,
			PRINT_GROUP_INFO };

	/**
	 * @param main
	 * @return
	 */
	public static Command create(String cmd) {
		for (final Command x : commandLists) {
			if (cmd.equals(x.name()))
				return x;
		}
		return Command.create(cmd);
	}

	/**
	 * @return the _mainFrame
	 */
	public static final MainFrame getMainFrame() {
		return _mainFrame;
	}

	/**
	 * @return the _selectedTarget
	 */
	public static final SelectedObjects getSelectedObjects() {
		return _selectedObjects;
	}

	/**
	 * @return
	 */
	public static final MXTuneData getTarget() {
		return _target;
	}

	/**
	 * @return
	 */
	public static boolean hasTarget() {
		return _target != null;
	}

	/**
	 * @param _frame
	 */
	public static void setJFrame(JFrame owner) {
		_mainFrame = (MainFrame) owner;
	}

	/**
	 * @param main2
	 */
	public static void setMainObject(MuseApp main) {
		_main = main;
	}

	public static void setTarget(MXTuneData target) {
		_target = target;
	}

	/**
	 * @deprecated
	 */
	@Deprecated public static void writefileCurveParameters() {
		if (_target == null)
			return;
		try {
			_target.writeTempfileCurveParameters();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// try {
		// _target.writefile();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		// if (_target.getGroupNoteList(0) == null)
		// return;
		// if (isAssertion()) {
		// assert _target != null : "target is null";
		// assert _target.getGroupNoteList(0) != null : "root group is null";
		// }
		// try {
		// PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
		// "paramtest.dat")));
		// out.println(_target.getInputFilename());
		// if (_target.getGroupNoteList(0) != null)
		// writeCurveParam(_target.getGroupList(0), out);
		// out.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
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

	private static final class AddGroupCommand extends MixtractCommand {

		public AddGroupCommand(String... lang) {
			super(lang);
		}

	}
	private static class AnalyzeStructureCommand extends MixtractCommand {

		protected AnalyzeStructureCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
		// _selectedObjects.clearAll();
		// _target.getGroupArrayList();
		// _mainFrame.getPianoroll().repaint();
		// // _mainFrame.getExpressionPanel().clearGroup();
		// ana = GTTMAnalyzer.run(_target, GTTMAnalyzer.doScoreAnalysis(),
		// false);
		// _target.setLatestGroupAnalysis(ana);
		// _mainFrame.notifyReadTuneData(_target);
		}

	}
	private static final class ApplyPuliseMozartsCommand extends MixtractCommand {

		public ApplyPuliseMozartsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
		// ExpressionMaker
		// .transerPulsesTo(getSelectedObjects().getGroup(), "Mozart");
		}

	}
	private static final class ApplyPulseChopinsCommand extends MixtractCommand {

		public ApplyPulseChopinsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			throw new UnsupportedOperationException("chopin's pulse");
		}

	}
	private static final class ApplyTopNoteCommand extends MixtractCommand {

		public ApplyTopNoteCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			_mainFrame.getGroupingPanel().transferExpressionOfMostSimilarGroup();
		}

	}
	private static final class DeleteGroupCommand extends MixtractCommand {

		public DeleteGroupCommand(String... lang) {
			super(lang);
		}/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */

		@Override public void execute() {
			if (_target != null) {
				GroupLabel sel = _mainFrame.getGroupingPanel().getSelectedGroup();
				// _target.deleteGUIGroup(_selectedObjects.getGroupLabel());
				// _selectedObjects.clearAll();
				_main.getData().deleteGroupFromData(sel.getGroup());
				_main.notifyDeleteGroup(sel);
				// getPianorollScroll().repaint();
				// getGroupingPanel().deselectLabel();
				// getExpressionPanel().clearGroup();
				// setTune(target);
			}
		}

	}
	private static final class DetailCommand extends MixtractCommand {

		public DetailCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			_mainFrame.getGroupingPanel().showDetailViewer();
		}

	}
	private static final class EditGroupCommand extends MixtractCommand {

		public EditGroupCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {}

	}
	private static final class ExprLineCommand extends MixtractCommand {

		public ExprLineCommand(String... lang) {
			super(lang);
		}/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */

		@Override public void execute() {
		// _mainFrame.getExpressionPanel().setShowExprLine(
		// !_mainFrame
		// .getExpressionPanel()
		// .isShowExprLine());
		}

	}
	private static final class ExprViewCommand extends MixtractCommand {

		public ExprViewCommand(String... lang) {
			super(lang);
		}/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */

		@Override public void execute() {
		// _mainFrame.getExpressionPanel()
		// .setShowExpression(
		// !_mainFrame.getExpressionPanel()
		// .isShowExpression());
		}

	}
	private static final class GTTMAnalysisCommand extends
			AnalyzeStructureCommand {

		/**
		 * @param string
		 */
		protected GTTMAnalysisCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.components.MixtractCommand.
		 * AnalyseStructureCommand
		 * #execute()
		 */
		@Override public void execute() {
			makeUserGroup();
		}

		/* ユーザ定義のグルーピング */
		private void makeUserGroup() {
		// if (_target != null) {
		// GTTMAnalyzer.run(_target, _mainFrame.getJCheckBoxMenuItem()
		// .isSelected(), false);
		// _main.notifySetTarget(_target);
		// _mainFrame.refreshDatabase();
		// }
		}

	}
	private static final class MouseDisplayCommand extends MixtractCommand {

		public MouseDisplayCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			_mainFrame.getGroupingPanel()
					.setDisplayMousePointer(
																	!_mainFrame.getGroupingPanel()
																			.isDisplayMousePointer());
		}

	}
	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class OpenMusicXMLCommand extends MixtractCommand {

		protected OpenMusicXMLCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
		// setTarget(openFileDialog());
		// _mainFrame.setNewData(true);
		// if (_target != null) {
		// showMusicDataPanel();
		// _mainFrame.notifySetTarget(_target);
		// _mainFrame.notifyReadTuneData(_target);
		// _mainFrame.refreshDatabase();
		// }
		}
		//
		// /**
		// * ダイアログから，表情付けの対象楽曲（XML）を開きます．
		// *
		// * @return fp
		// */
		// public String openFileDialog() {
		// final JFileChooser fc = new
		// JFileChooser(_main.getDefaultDirectory());
		// fc.setAcceptAllFileFilterUsed(true);
		// if (fc.showOpenDialog(_mainFrame) != JFileChooser.APPROVE_OPTION) {
		// GUIUtil.printConsole("File Chooser Cancelled.\n");
		// return null;
		// }
		// final File fp = fc.getSelectedFile();
		// final String fn = fp.getName();
		// final File dir = fp.getParentFile();
		// _main.setDefaultDirectory(dir);
		// //
		// DeviationInstanceWrapper.changeDefaultMusicXMLDirName(dir.getPath());
		// final String pathstr = dir.getPath()
		// .substring(fp.getParent().lastIndexOf("\\") + 1);
		// final String fname = pathstr + "/" + fn;
		// GUIUtil.printConsole("Opening: " + fname);
		// return fname;
		// }

		// /**
		// * @param openFileDialog
		// */
		// private void setTarget(String fname) {
		// if (fname == null)
		// return;
		// try {
		// _target = new GUITuneData(_main.readfile(fname));
		// ana = GTTMAnalyzer.run(_target, _mainFrame
		// .getAnalyzeStructureOnReading().isSelected(), _mainFrame
		// .getJCheckBoxMenuItem1().isSelected());
		// _target.setLatestGroupAnalysis(ana);
		// getSelectedObjects().setData(_target, DataType.GUI);
		// } catch (final IOException e) {
		// _target = null;
		// }
		// }

		// /**
		// *
		// */
		// private void showMusicDataPanel() {
		// // if (!_mainFrame.hasMusicData()) {
		// // JOptionPane.showMessageDialog(_mainFrame, "target is null.");
		// // return;
		// // }
		// _mainFrame.getPhraseStructureDialog().pack();
		// _mainFrame.getPhraseStructureDialog().setVisible(true);
		// _mainFrame.getScoreViewFrame().pack();
		// _mainFrame.getScoreViewFrame().setVisible(true);
		// _mainFrame.getRealtimeViewFrame().pack();
		// _mainFrame.getRealtimeViewFrame().setVisible(true);
		// _mainFrame.getExpressionViewDialog().pack();
		// _mainFrame.getExpressionViewDialog().setVisible(true);
		// _mainFrame.getPlayerDialog().pack();
		// _mainFrame.getPlayerDialog().setVisible(true);
		// _mainFrame.getTempoViewDialog().pack();
		// _mainFrame.getTempoViewDialog().setVisible(true);
		// _mainFrame.getDynamicsViewDialog().pack();
		// _mainFrame.getDynamicsViewDialog().setVisible(true);
		// }
	}

	private static class OpenRuleMapCommand extends OpenMusicXMLCommand {

		protected OpenRuleMapCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MixtractCommand.OpenMusicXMLCommand#execute
		 * ()
		 */
		@Override public void execute() {
		// try {
		// final File fp = new File(openFileDialog());
		// GUIUtil.printConsole(fp.getName() + " is reading...");
		// final Rulemap rulemap = Rulemap.createRulemap(fp);
		// _mainFrame.getParamPanel().setRulemap(rulemap);
		// _mainFrame.getParamPanel().assignRulemapToSliders();
		// GUIUtil.printConsole("done.\n");
		// } catch (final NullPointerException e) {
		// GUIUtil.printConsole("openRuleMapFromDialog cancelled");
		// } catch (final FileNotFoundException e) {
		// e.printStackTrace();
		// } catch (final IOException e) {
		// e.printStackTrace();
		// }
		}

	}
	private static final class OpenRulePanelCommand extends MixtractCommand {

		public OpenRulePanelCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			showParameterPanel();
		}

		public void showParameterPanel() {
		// final JDialog dialog = new JDialog(_mainFrame);
		// dialog.add(_mainFrame.getParamPanel());
		// dialog.pack();
		// dialog.setVisible(true);
		}
	}
	/**
	 * <h1>OpenStructureDataCommand</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/09/01
	 */
	private static class OpenStructureDataCommand extends OpenMusicXMLCommand {

		/**
		 * @param lang
		 */
		public OpenStructureDataCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.gui.MixtractCommand.OpenMusicXMLCommand#execute
		 * ()
		 */
		@Override public void execute() {
		// if (isAssertion()) {
		// assert _target != null : "target is null";
		// assert ana != null : "target has't been analysed structure yet";
		// }
		// String fn = openFileDialog();
		// try {
		// GTTMAnalyzer.setReadFile(true);
		// ana.analyzeGroupStructure(fn);
		// _target.setLatestGroupAnalysis(ana);
		// _mainFrame.setNewData(true);
		// if (_target != null) {
		// _mainFrame.notifySetTarget(_target);
		// _mainFrame.notifyReadTuneData(_target);
		// _mainFrame.refreshDatabase();
		// }
		// // _mainFrame.getGroupingPanel().repaint();
		// // _mainFrame.getTempoPanel().repaint();
		// } catch (TransformerException e) {
		// e.printStackTrace();
		// }
		}

	}
	/**
	 * <h1>PrintAllGroupsCommand</h1>
	 * <p>
	 * 登録されているすべてのグループ情報を出力します。
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/10/29
	 */
	private static final class PrintAllGroupsCommand extends MixtractCommand {

		public PrintAllGroupsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			GUIUtil.printConsole("Hierarchical group list:");
			for (Group g : _target.getRootGroup())
				printGroupList(g);
			GUIUtil.printConsole("Hierarchical group list:");
			for (Group g : _target.getGroupArrayList())
				printGroupList(g);
		}

		/**
		 * 登録されているすべてのグループ情報を出力します。
		 *
		 * @param group 各声部のトップグループ
		 */
		private void printGroupList(Group group) {
			if (group == null)
				return;
			printGroupList(group.getChildFormerGroup());
			printGroupList(group.getChildLatterGroup());
			System.out.println(group);
		}
	}

	private static final class PrintAllSimilarGroupsCommand extends
			MixtractCommand {

		public PrintAllSimilarGroupsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			_main.printAllSimilarList();
		}

	}

	private static final class PrintSimilarGroupsCommand extends MixtractCommand {

		public PrintSimilarGroupsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			_main.printSimilarList();
		}

	}

	private static final class PrintSubgroupsCommand extends MixtractCommand {

		public PrintSubgroupsCommand(String... lang) {
			super(lang);
		}/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */

		@Override public void execute() {
			final GroupLabel gl = getSelectedObjects().getGroupLabel();
			GUIUtil.printConsole(gl.getName() + "'s subgroups:");
			printSubGroups(gl.getGroup());
		}

		/**
		 * @param g
		 */
		private void printSubGroups(Group g) {
		// if (g == null)
		// return;
		// GUIUtil.printConsole("Gr." + g.name() + ", hierarchy=" +
		// g.isHierarchy()
		// + ", level=" + g.getLevel() + ", " + g.getScoreNotelist());
		// printSubGroups(g.getChildFormerGroup());
		// printSubGroups(g.getChildLatterGroup());
		}

	}

	private static final class RedrawCommand extends MixtractCommand {

		public RedrawCommand(String... lang) {
			super(lang);
		}/*
			 * (non-Javadoc)
			 * @see net.muse.misc.Command#execute()
			 */

		@Override public void execute() {}

	}

	private static final class RefreshCommand extends MixtractCommand {

		public RefreshCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
			_mainFrame.refreshDatabase();
		}

	}

	/**
	 * @author Mitsuyo Hashida @ CrestMuse Project, JST
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/03/12
	 */
	private static class RenderCommand extends MixtractCommand {

		protected RenderCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
		// ExpressionMaker.render(_target);
		// _mainFrame.notifySetTarget(_target);
		}
	}

	private static final class ResetParameterCommand extends MixtractCommand {

		public ResetParameterCommand(String... lang) {
			super(lang);
		}

	}

	private static class SearchCommand extends MixtractCommand {

		protected SearchCommand(String... lang) {
			super(lang);
		}

		/*
		 * (非 Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
		// _mainFrame.searchSimilarPhrases();
		// // _mainFrame.getGroupingPanel().searchSimilarityOfThisGroup();
		}
	}

	private static final class SelectGroupCommand extends MixtractCommand {

		public SelectGroupCommand(String... lang) {
			super(lang);
		}

	}

	private static final class ShowSimilarGroupsCommand extends MixtractCommand {

		public ShowSimilarGroupsCommand(String... lang) {
			super(lang);
		}

		/*
		 * (non-Javadoc)
		 * @see net.muse.misc.Command#execute()
		 */
		@Override public void execute() {
		// final SimilarGroupPanel panel = new SimilarGroupPanel(_main, _target,
		// getSelectedObjects().getGroupLabel().getGroup(), _mainFrame
		// .getGroupingPanel().sim);
		// panel.pack();
		// panel.setVisible(true);
		}

	}

	public void setGroup(GroupLabel groupLabel) {}
}
