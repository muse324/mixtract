package net.muse.pedb.command;

import java.util.LinkedList;

import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.gui.GroupLabel;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.command.MakeGroupCommand;
import net.muse.mixtract.data.MXGroup;
import net.muse.pedb.data.PEDBGroup;
import net.muse.pedb.data.PEDBTuneData;
import net.muse.pedb.gui.PEDBMainFrame;

public class PEDBMakeGroupCommand extends MakeGroupCommand {

	public PEDBMakeGroupCommand(String... lang) {
		super(lang);
	}

	@Override public PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

	@Override protected PEDBGroup combineGroups(Group conditionGroup,
			Group former, Group latter) {
		return (PEDBGroup) super.combineGroups(conditionGroup, former, latter);
	}

	@Override protected PEDBGroup createAutoGroupAfterUserGroup(NoteLabel end) {
		return (PEDBGroup) super.createAutoGroupAfterUserGroup(end);
	}

	@Override protected PEDBGroup createAutoGroupBeforUserGroup(
			NoteLabel begin) {
		return (PEDBGroup) super.createAutoGroupBeforUserGroup(begin);
	}

	@Override protected PEDBGroup createGroup(NoteData b, NoteData e,
			GroupType type) {
		return new PEDBGroup(b, e, type);
	}

	@Override protected PEDBGroup createGroup(NoteLabel begin, NoteLabel end,
			GroupType type) {
		return new PEDBGroup(begin.getScoreNote(), end.getScoreNote(), type);
	}

	@Override protected PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	@Override protected PEDBMainFrame frame() {
		return (PEDBMainFrame) super.frame();
	}

	@Override protected GroupLabel getGroupLabel() {
		// TODO 自動生成されたメソッド・スタブ
		return super.getGroupLabel();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.command.MuseAppCommand#run()
	 */
	@Override public void run() {
		LinkedList<NoteLabel> notes = frame().getPianoroll()
				.getSelectedNoteLabels();
		NoteLabel begin = notes.get(0);
		NoteLabel end = notes.get(notes.size() - 1);
		PEDBGroup g1 = createGroup(begin, end, GroupType.USER);

		// グループ作成が初回で、かつ、もし前後に未グループの音符があった場合，
		// GroupType.AUTOで前のグループを自動生成する
		// （連結はしない）
		if (data().getMiscGroup().size() == 0 && begin.hasPrevious()) {
			PEDBGroup g0 = createAutoGroupBeforUserGroup(begin);
			if (g0 != null) {
				// g0.setNext(g1);
				g0.setIndex(data().getUniqueGroupIndex());
			}
		}
		data().addMiscGroupList(g1);
		g1.setIndex(data().getUniqueGroupIndex());

		app().butler().notifySetTarget(app().data());
	}

	@Override protected PEDBGroup searchGroup(MXGroup g, NoteData beginNote,
			NoteData endNote) {
		return (PEDBGroup) super.searchGroup(g, beginNote, endNote);
	}
}
