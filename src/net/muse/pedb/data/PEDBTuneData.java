package net.muse.pedb.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;
import net.muse.data.CMXNoteHandler;
import net.muse.data.Group;
import net.muse.data.GroupType;
import net.muse.data.NoteData;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;
import net.muse.mixtract.data.MXTuneData;

public class PEDBTuneData extends MXTuneData {
	/**
	 * @param in - File
	 * @param out - File
	 * @throws IOException
	 */
	public PEDBTuneData(File in, File out) throws IOException {
		super(in, out);
	}

	@Override public void analyze(Group rootGroup) {
		if (rootGroup == null)
			return;
		assert rootGroup instanceof PEDBGroup;
		final PEDBGroup root = (PEDBGroup) rootGroup;
		analyze(root.child());
		analyze((Group) root.next());

		PEDBGroup g = null;
		for (final Group group : getMiscGroup()) {
			if (root.nearlyEquals(group)) {
				g = (PEDBGroup) group;
				break;
			}
		}
		if (g != null)
			root.setChild(g.child());
		getMiscGroup().remove(g);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	@Override public PEDBNoteData getLastNote(int partIndex) {
		return (PEDBNoteData) super.getLastNote(partIndex);
	}

	@Override public PEDBGroup getRootGroup(int partIndex) {
		return (PEDBGroup) super.getRootGroup(partIndex);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.MXTuneData#createCMXNoteHandler()
	 */
	@Override protected CMXNoteHandler createCMXNoteHandler() {
		return new CMXNoteHandler(this) {
			@Override protected PEDBGroup createGroup(NoteData n, int i,
					GroupType type) {
				return new PEDBGroup(n, i, type);
			}

			@Override protected NoteData createNoteData(
					MusicXMLWrapper.Note note, int partNumber, int idx,
					Integer bpm, int vel) {
				return new PEDBNoteData(note, partNumber, idx, bpm, vel);
			}

			@Override protected NoteData createNoteData(SCCXMLWrapper.Note note,
					int partNumber, int idx, Integer bpm, int beat, int vel) {
				return new PEDBNoteData(note, partNumber, idx, bpm, beat, vel);
			}

			@Override protected PEDBTuneData data() {
				return (PEDBTuneData) data;
			}
		};
	}

	@Override protected PEDBConcierge createConcierge() {
		return new PEDBConcierge(this);
	}

	@Override protected void deleteHierarchicalGroup(Group root, Group target) {
		if (root == null)
			return;

		deleteHierarchicalGroup(root.child(), target);
		deleteHierarchicalGroup((Group) root.next(), target);
		if (root.equals(target)) {
			// 子グループをすべて削除
			deleteGroup(target);
			target.setType(GroupType.USER);
			// 親グループの再分析
			// analyzeStructure(target);
			return;
		}
	}

	@Override protected void getUniqueGroupIndex(Group glist,
			ArrayList<Integer> idxlist) {
		if (glist == null)
			return;
		assert glist instanceof PEDBGroup;
		final PEDBGroup g = (PEDBGroup) glist;
		getUniqueGroupIndex(g.child(), idxlist);
		if (!idxlist.contains(g.index()))
			idxlist.add(g.index());
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.MXTuneData#readStructureData(java.io.File)
	 */
	@Override protected void readStructureData(File file) {
		try {
			getRootGroup().clear();
			final BufferedReader in = new BufferedReader(new FileReader(file));
			String str = null;
			final List<MXGroup> glist = new ArrayList<>();
			while ((str = in.readLine()) != null) {
				final String item[] = str.split(";");
				final String groupName = item[0]; // group name
				int partNumber = Integer.parseInt(item[1]);
				if (partNumber <= 0)
					partNumber = 1;
				final MXNoteData note = null;
				final String groupInfo = item[2];
				parseGroupInfo(glist, note, groupName, partNumber, groupInfo);

				// TODO setTopNote をパースする
			}
			in.close();
			++hierarchicalGroupCount;
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	@Override protected void setNoteScheduleEvent(final Group group) {
		if (group == null)
			return;
		assert group instanceof PEDBGroup;
		final PEDBGroup g = (PEDBGroup) group;
		setNoteScheduleEvent(g.child());
		setNoteScheduleEvent(g.getBeginNote(), g.getBeginNote().onset(), g
				.getEndNote().offset());
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.MXTuneData#writeGroupStructureData(java.io.
	 * PrintWriter, net.muse.mixtract.data.MXGroup)
	 */
	@Override protected void writeGroupStructureData(PrintWriter out,
			Group group) {
		if (group == null)
			return;
		writeGroupStructureData(out, group.child());
		out.format("%s;%s\n", group, group.hasTopNote() ? group.getTopNote()
				.id() : "null");
		writeGroupStructureData(out, (Group) group.next());
	}

	@Override protected void writeNoteData(PrintWriter out, Group g) {
		if (g == null)
			return;
		if (!g.hasChild())
			writeNoteData(out, g.getBeginNote());
		writeNoteData(out, g.child());
	}

}
