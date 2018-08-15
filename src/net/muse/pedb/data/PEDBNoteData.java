package net.muse.pedb.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.mixtract.data.MXNoteData;

public class PEDBNoteData extends MXNoteData {

	public PEDBNoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		super(note, partNumber, idx, bpm, vel);
		setTimeValue((note.grace()) ? getDefaultGraseNoteDuration()
				: note.duration(getTicksPerBeat()));
	}

	public PEDBNoteData(jp.crestmuse.cmx.filewrappers.SCCXMLWrapper.Note note,
			int partNumber, int idx, int bpm, int beat, int vel) {
		super(note, partNumber, idx, bpm, beat, vel);
	}

	public PEDBNoteData(int index) {
		super(index);
	}

}
