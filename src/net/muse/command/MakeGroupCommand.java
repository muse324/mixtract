package net.muse.command;

import java.util.LinkedList;

import net.muse.data.*;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.MXNoteLabel;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/12
 */
public class MakeGroupCommand extends MuseAppCommand {

	public MakeGroupCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override
	public void execute() {
		LinkedList<NoteLabel> notes = frame().getPianoroll()
				.getSelectedNoteLabels();
		Group g = createUserGroup(notes.get(0), notes.get(notes.size() - 1));
		g.setIndex(target().getUniqueGroupIndex());
		target().addGroupArrayList(g);
		main().analyzeStructure(target(), null);
		main().notifyAddGroup(g);
		main().notifySetTarget();
		// // ArrayList<Group> newlist = createUserGroup();
		// _target.addUserGroup(g);
		// _target.getGroupList(true);
		// // _target.setLatestGroupAnalysis(GTTMAnalyzer.run(_target,
		// // GTTMAnalyzer
		// // .doScoreAnalysis(), false));
		// _mainFrame.setTargetTuneData(_target);
		// _mainFrame.notifyAddGroup(g);
		// // getGroupingPanel().setTune(data);
		// GUIUtil.printConsole("new group:" + g);
	}

	/**
	 * ユーザにより範囲選択された音符群に対し，グループを作成します．
	 * <p>
	 * 休符等により音符間が隣接しない場合，複数のグループを作成します．
	 * 意図せずグループが分割された場合は，combineGroups()を用いて接続します．
	 *
	 * @param begin
	 * @param end
	 * @return
	 * @see combineGroups()
	 */
	private Group createUserGroup(NoteLabel begin, NoteLabel end) {
		// 巻き戻し
		while (begin.prev() != null && begin.prev().isSelected())
			begin = begin.prev();
		// ユーザグループを構成する音符列を生成する
		NoteData groupNoteList = createUserGroupNotelist(begin, end, null, null,
				false);
		while (groupNoteList.hasParent())
			groupNoteList = groupNoteList.parent();
		NoteData endNote = groupNoteList;
		// 巻き戻し
		while (groupNoteList.hasPrevious()) {
			groupNoteList = groupNoteList.previous();
			if (groupNoteList.equals(begin.getGroupNote()))
				break;
		}
		if (begin instanceof MXNoteLabel)
			return new MXGroup(groupNoteList, endNote, GroupType.USER);
		return new Group(groupNoteList, endNote, GroupType.USER);
	}

	/**
	 * @param l
	 * @param le
	 * @param list
	 * @param glist
	 * @param isChild
	 * @return
	 */
	private NoteData createUserGroupNotelist(NoteLabel l, NoteLabel le,
			NoteData list, NoteData glist, boolean isChild) {
		if (l == null)
			return list;
		if (glist == null) {
			glist = l.getScoreNote();
		}
		if (list != null) {
			if (isChild) {
				list.setChild(glist);
				glist.setParent(list);
				list = list.child();
			} else {
				while (list.hasParent()) {
					if (glist.hasPrevious() && glist.previous().equals(list))
						break;
					list = list.parent();
				}
				list.setNext(glist);
				glist.setParent(list.parent(), false);
				glist.setPrevious(list);
			}
		}
		list = glist;
		list = createUserGroupNotelist(l.child(), le, list, glist.child(),
				true);
		if (l == le)
			return list;
		return createUserGroupNotelist(l.next(), le, list, glist.next(), false);
	}
}