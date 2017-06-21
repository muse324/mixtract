package net.muse.data;

import jp.crestmuse.cmx.filewrappers.DeviationInstanceWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import net.muse.misc.MuseObject;
import net.muse.mixtract.data.MXGroup;

public class TuneData extends MuseObject {
	/** MusicXML */
	protected MusicXMLWrapper xml;

	/** DeviationInstanceXML */
	protected DeviationInstanceWrapper dev;

	protected void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(createCMXNoteHandler());
	}

	protected CMXNoteHandler createCMXNoteHandler() {
		return new CMXNoteHandler(this) {
			protected MXGroup createGroup(NoteData n, int i, GroupType type) {
				return new MXGroup(n, i, type);
			}

		};
	}

}
