package net.muse.pedb.command;

import java.util.LinkedList;

import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.gui.NoteLabel;
import net.muse.mixtract.command.MakeGroupCommand;

public class PEDBMakeGroupCommand extends MakeGroupCommand {
	public PEDBMakeGroupCommand(String... lang) {
		super(lang);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.command.MuseAppCommand#run()
	 */
	@Override
	public void run() {
		LinkedList<NoteLabel> notes = frame().getPianoroll()
				.getSelectedNoteLabels();
		NoteLabel begin = notes.get(0);
		NoteLabel end = notes.get(notes.size() - 1);
		Group g1 = super.createGroup(begin, end, GroupType.USER);
		data().addMiscGroupList(g1);

		// 階層グループとの整合性を取る
		for (Group g : data().getRootGroup()) {
			data().analyze(g);
		}
		main().butler().notifySetTarget(main().data());
	}
}
