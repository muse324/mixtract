package net.muse.mixtract.command;

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
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/03/12
 */
public class MakeGroupCommand extends MixtractCommand {

	public MakeGroupCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void run() {
		LinkedList<NoteLabel> notes = frame().getPianoroll()
				.getSelectedNoteLabels();
		NoteLabel begin = notes.get(0);
		NoteLabel end = notes.get(notes.size() - 1);
		Group g1 = createGroup(begin, end, GroupType.USER);
		data().addMiscGroupList(g1);

		Group g0 = null, g2 = null, parent = null;
		// もし前後に未グループの音符があった場合，GroupType.AUTOで自動生成する
		if (!data().getRootGroup().get(0).hasChild()) {
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
			for (Group g : data().getRootGroup()) {
				parent = searchGroup((MXGroup) g, g1.getBeginNote(), g1
						.getEndNote());
				if (parent != null)
					break;
			}
			assert parent != null;
			if (g1.getBeginNote().equals(parent.getBeginNote())) {
				g2 = createGroup(g1.getEndNote().next(), parent.getEndNote(),
						GroupType.AUTO);
				parent = combineGroups(null, g1, g2);
			} else if (parent.getEndNote().equals(g1.getEndNote())) {
				g0 = createGroup(parent.getBeginNote(), g1.getBeginNote()
						.previous(), GroupType.AUTO);
				parent = combineGroups(null, g0, g1);
			}
		}
		parent.setIndex(data().getUniqueGroupIndex());
		data().addMiscGroupList(parent);
		// 階層グループとの整合性を取る
		for (Group g : data().getRootGroup()) {
			data().analyze(g);
		}
		app().butler().notifySetTarget(app().data());
	}

	protected Group searchGroup(MXGroup g, NoteData beginNote,
			NoteData endNote) {
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

	protected Group createAutoGroupAfterUserGroup(NoteLabel end) {
		Group g = null;
		if (end.next() != null) {
			NoteLabel b = end.next();
			NoteLabel e = b;
			while (e.next() != null)
				e = e.next();
			g = createGroup(b, e, GroupType.AUTO);
			data().addMiscGroupList(g);
			app().notifyAddGroup(g);
		}
		return g;
	}

	protected Group createAutoGroupBeforUserGroup(NoteLabel begin) {
		Group g0 = null;
		if (begin.prev() != null) {
			NoteLabel e = begin.prev();
			NoteLabel b = e;
			while (b.prev() != null)
				b = b.prev();
			g0 = createGroup(b, e, GroupType.AUTO);
			data().addMiscGroupList(g0);
			app().notifyAddGroup(g0);
		}
		return g0;
	}

	protected Group combineGroups(Group conditionGroup, Group former,
			Group latter) {
		if (conditionGroup != null)
			return null;
		Group p = createGroup(former.getBeginNote(), latter.getEndNote(),
				GroupType.PARENT);
		if (p instanceof MXGroup) {
			((MXGroup) p).setChild((MXGroup) former, (MXGroup) latter);
			data().getMiscGroup().remove(former);
			data().getMiscGroup().remove(latter);
		}
		return p;
	}

	protected Group createGroup(NoteData b, NoteData e, GroupType type) {
		if (app() instanceof Mixtract)
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
	protected Group createGroup(NoteLabel begin, NoteLabel end,
			GroupType type) {
		Group g = null;
		if (begin instanceof MXNoteLabel)
			g = new MXGroup(begin.getScoreNote(), end.getScoreNote(), type);
		else
			g = new Group(begin.getScoreNote(), end.getScoreNote(), type);
		g.setIndex(data().getUniqueGroupIndex());
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