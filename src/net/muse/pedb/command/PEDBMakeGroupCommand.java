package net.muse.pedb.command;

import java.util.LinkedList;

import net.muse.app.PEDBStructureEditor;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.gui.GroupLabel;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.command.MakeGroupCommand;
import net.muse.pedb.data.PEDBGroup;
import net.muse.pedb.gui.PEDBMainFrame;

public class PEDBMakeGroupCommand extends MakeGroupCommand {
	public PEDBMakeGroupCommand(String... lang) {
		super(lang);
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
		data().addMiscGroupList(g1);

		// 階層グループとの整合性を取る
		for (Group g : data().getRootGroup()) {
			data().analyze(g);
		}
		main().butler().notifySetTarget(main().data());
	}

	@Override protected PEDBGroup createGroup(NoteLabel begin, NoteLabel end,
			GroupType type) {
		return new PEDBGroup(begin.getScoreNote(), end.getScoreNote(), type);
	}

	@Override protected GroupLabel getGroupLabel() {
		// TODO 自動生成されたメソッド・スタブ
		return super.getGroupLabel();
	}

	@Override protected PEDBStructureEditor main() {
		return (PEDBStructureEditor) super.main();
	}

	@Override protected PEDBMainFrame frame() {
		return (PEDBMainFrame) super.frame();
	}

	@Override public PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}
}
