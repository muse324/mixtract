package net.muse.pedb.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.mixtract.data.MXNoteData;

public class PEDBNoteData extends MXNoteData {

	private int idxInMeasure;

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

	public String id() {
		return String.format("P%d-%d-%d", xmlPartNumber(), measureNumber(),
				idxInMeasure);
	}

	@Override public String toString() {
		return String.format(
				"idx=%d, on=%d, n=%s, beat=%1f, tval=%d, p=%d, m=%d, voice=%d, phony=%s/off=%d, vel=%d, rest=%b, chd=%b, grc=%b, tiedFrom=%s, fifths=%d, harmony=%s",
				index(), onset(), noteName(), beat(), timeValue(),
				xmlPartNumber(), measureNumber(), xmlVoice(), musePhony(),
				offset(), velocity(), rest(), child() != null, isGrace(),
				(hasTiedFrom()) ? tiedFrom().index() : NO_TIED, fifths(),
				chord());
	}

	public void setIndexInMeasure(int i) {
		idxInMeasure = i;
	}

}
