package net.muse.command;

import java.util.LinkedList;

import net.muse.app.Mixtract;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;
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
	@Override public void execute() {
		LinkedList<NoteLabel> notes = frame().getPianoroll()
				.getSelectedNoteLabels();
		NoteLabel begin = notes.get(0);
		NoteLabel end = notes.get(notes.size() - 1);
		Group g1 = createGroup(begin, end, GroupType.USER);
		target().addMiscGroupList(g1);

		Group g0 = null, g2 = null, parent = null;
		// もし前後に未グループの音符があった場合，GroupType.AUTOで自動生成する
		if (!target().getRootGroup().get(0).hasChild()) {
			g0 = createAutoGroupBeforUserGroup(begin);
			g2 = createAutoGroupAfterUserGroup(end);
			// グループを連結する
			// USER が楽曲冒頭に生成された場合
			parent = combineGroups(g0, g1, g2);
			// USER が楽曲の最後に生成された場合
			if (parent == null)
				parent = combineGroups(g2, g0, g1);
			// USER が曲間に生成された場合
			if (parent == null) {
				parent = combineGroups(null, g0, g1);
				parent = combineGroups(null, parent, g2);
			}
		} else {
			// 階層グループが既存の場合、直上の親グループを探す
			for (Group g : target().getRootGroup()) {
				parent = searchGroup((MXGroup) g, g1.getBeginNote(), g1
						.getEndNote());
				if (parent != null)
					break;
			}
			assert parent != null;
			if (g1.getBeginNote().equals(parent.getBeginNote())) {
				g2 = createGroup(g1.getEndNote().next(), parent.getEndNote(),
						GroupType.AUTO);
				g2.setIndex(target().getUniqueGroupIndex());
				parent = combineGroups(null, g1, g2);
			} else if (parent.getEndNote().equals(g1.getEndNote())) {
				g0 = createGroup(parent.getBeginNote(), g1.getBeginNote()
						.previous(), GroupType.AUTO);
				g0.setIndex(target().getUniqueGroupIndex());
				parent = combineGroups(null, g0, g1);
			}
		}
		parent.setIndex(target().getUniqueGroupIndex());
		target().addMiscGroupList(parent);
		// 階層グループとの整合性を取る
		for (Group g : target().getRootGroup()) {
			target().analyze(g);
		}
		main().butler().notifySetTarget(main().data());
	}

	private Group searchGroup(MXGroup g, NoteData beginNote, NoteData endNote) {
		if (g == null)
			return null;

		if (g.getBeginNote().equals(beginNote))
			return g;
		if (g.getEndNote().equals(endNote))
			return g;

		Group f = searchGroup(g.getChildFormerGroup(), beginNote, endNote);
		if (f == null)
			return searchGroup(g.getChildLatterGroup(), beginNote, endNote);
		if (f.getBeginNote().equals(beginNote))
			return f;
		return g;
	}

	private Group createAutoGroupAfterUserGroup(NoteLabel end) {
		Group g = null;
		if (end.next() != null) {
			NoteLabel b = end.next();
			NoteLabel e = b;
			while (e.next() != null)
				e = e.next();
			g = createGroup(b, e, GroupType.AUTO);
			target().addMiscGroupList(g);
			main().notifyAddGroup(g);
		}
		return g;
	}

	private Group createAutoGroupBeforUserGroup(NoteLabel begin) {
		Group g0 = null;
		if (begin.prev() != null) {
			NoteLabel e = begin.prev();
			NoteLabel b = e;
			while (b.prev() != null)
				b = b.prev();
			g0 = createGroup(b, e, GroupType.AUTO);
			target().addMiscGroupList(g0);
			main().notifyAddGroup(g0);
		}
		return g0;
	}

	private Group combineGroups(Group conditionGroup, Group former,
			Group latter) {
		if (conditionGroup != null)
			return null;
		Group p = createGroup(former.getBeginNote(), latter.getEndNote(),
				GroupType.PARENT);
		if (p instanceof MXGroup) {
			((MXGroup) p).setChild((MXGroup) former, (MXGroup) latter);
			target().getMiscGroup().remove(former);
			target().getMiscGroup().remove(latter);
		}
		return p;
	}

	private Group createGroup(NoteData b, NoteData e, GroupType type) {
		if (main() instanceof Mixtract)
			return new MXGroup(b, e, type);
		return new Group(b, e, type);
	}

	/**
	 * ユーザにより範囲選択された音符群に対し，グループを作成します．
	 * <p>
	 * 休符等により音符間が隣接しない場合，複数のグループを作成します．
	 * 意図せずグループが分割された場合は，combineGroups()を用いて接続します．
	 *
	 * @param begin
	 * @param end
	 * @param type
	 * @return
	 * @see combineGroups()
	 */
	private Group createGroup(NoteLabel begin, NoteLabel end, GroupType type) {
		Group g = null;
		// // 巻き戻し
		// while (begin.prev() != null && begin.prev().isSelected())
		// begin = begin.prev();
		// // ユーザグループを構成する音符列を生成する
		// NoteData groupNoteList = createUserGroupNotelist(begin, end, null,
		// null,
		// false);
		// while (groupNoteList.hasParent())
		// groupNoteList = groupNoteList.parent();
		// NoteData endNote = groupNoteList;
		// // 巻き戻し
		// while (groupNoteList.hasPrevious()) {
		// groupNoteList = groupNoteList.previous();
		// if (groupNoteList.equals(begin.getGroupNote()))
		// break;
		// }
		if (begin instanceof MXNoteLabel)
			g = new MXGroup(begin.getScoreNote(), end.getScoreNote(), type);
		else
			g = new Group(begin.getScoreNote(), end.getScoreNote(), type);
		g.setIndex(target().getUniqueGroupIndex());
		return g;
	}

	/**
	 * @param begin
	 * @param end
	 * @param list
	 * @param glist
	 * @param isChild
	 * @return
	 */
	private NoteData createUserGroupNotelist(NoteLabel begin, NoteLabel end,
			NoteData list, NoteData glist, boolean isChild) {
		if (begin == null)
			return list;
		if (glist == null) {
			glist = begin.getScoreNote();
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
		list = createUserGroupNotelist(begin.child(), end, list, glist.child(),
				true);
		if (begin == end)
			return list;
		return createUserGroupNotelist(begin.next(), end, list, glist.next(),
				false);
	}
}