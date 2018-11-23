package net.muse.pedb.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.mixtract.data.MXNoteData;

public class PEDBNoteData extends MXNoteData {

	public PEDBNoteData(int index) {
		super(index);
	}

	public PEDBNoteData(jp.crestmuse.cmx.filewrappers.SCCXMLWrapper.Note note,
			int partNumber, int idx, int bpm, int beat, int vel) {
		super(note, partNumber, idx, bpm, beat, vel);
	}

	public PEDBNoteData(Note note, int partNumber, int idx, int bpm, int vel) {
		super(note, partNumber, idx, bpm, vel);
		setTimeValue(note.grace() ? getDefaultGraseNoteDuration()
				: note.duration(getTicksPerBeat()));
	}

	@Override public PEDBNoteData child() {
		return (PEDBNoteData) super.child();
	}

	@Override public PEDBNoteData next() {
		return (PEDBNoteData) super.next();
	}

	@Override public PEDBNoteData parent() {
		return (PEDBNoteData) super.parent();
	}

	@Override public PEDBNoteData previous() {
		return (PEDBNoteData) super.previous();
	}

}
