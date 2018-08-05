package net.muse.pedb.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;
import net.muse.mixtract.data.MXTuneData;

public class PEDBTuneData extends MXTuneData {

	public PEDBTuneData(File in, File out) throws IOException {
		super(in, out);
	}

	protected void writeGroupStructureData(PrintWriter out, MXGroup group) {
		if (group == null)
			return;
		writeGroupStructureData(out, (PEDBGroup) group.getChildFormerGroup());
		writeGroupStructureData(out, (PEDBGroup) group.getChildLatterGroup());
		out.format("%s;%s\n", group, (group.hasTopNote()) ? group.getTopNote()
				.id() : "null");
	}

	protected void readStructureData(File file) {
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

				//TODO setTopNote をパースする

//				parseCurvePoints(curvePoints, 0, g.getDynamicsCurve());
//				parseCurvePoints(curvePoints, 6, g.getTempoCurve());
//				parseCurvePoints(curvePoints, 12, g.getArticulationCurve());
//				try {
//					parsePhraseProfile(dynCurveInfo, g.getDynamicsCurve());
//					parsePhraseProfile(tmpCurveInfo, g.getTempoCurve());
//					parsePhraseProfile(artCurveInfo, g.getArticulationCurve());
//				} catch (NullPointerException e) {
//					System.err.println("Irregal file format");
//				}
			}
			in.close();
			++hierarchicalGroupCount;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
