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
		PEDBGroup root = (PEDBGroup) rootGroup;
		analyze(root.child());

		PEDBGroup g = null;
		for (Group group : getMiscGroup()) {
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

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.MXTuneData#readStructureData(java.io.File)
	 */
	@Override protected void readStructureData(File file) {
		try {
			getRootGroup().clear();
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str = null;
			List<MXGroup> glist = new ArrayList<MXGroup>();
			while ((str = in.readLine()) != null) {
				String item[] = str.split(";");
				String groupName = item[0]; // group name
				int partNumber = Integer.parseInt(item[1]);
				if (partNumber <= 0)
					partNumber = 1;
				MXNoteData note = null;
				String groupInfo = item[2];
				String topNoteName = item[3];
				MXGroup g = parseGroupInfo(glist, note, groupName, partNumber,
						groupInfo);

				// TODO setTopNote をパースする
			}
			in.close();
			++hierarchicalGroupCount;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.data.MXTuneData#writeGroupStructureData(java.io.
	 * PrintWriter, net.muse.mixtract.data.MXGroup)
	 */
	@Override protected void writeGroupStructureData(PrintWriter out,
			MXGroup group) {
		if (group == null)
			return;
		writeGroupStructureData(out, group.getChildFormerGroup());
		writeGroupStructureData(out, group.getChildLatterGroup());
		out.format("%s;%s\n", group, (group.hasTopNote()) ? group.getTopNote()
				.id() : "null");
	}

}
