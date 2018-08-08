package net.muse.pedb.data;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.muse.mixtract.data.MXGroup;
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

	/*
	 * (非 Javadoc)
	 * @see net.muse.misc.MuseObject#butler()
	 */
	@Override
	public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

}
