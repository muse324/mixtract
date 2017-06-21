package net.muse.data;


import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Measure;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.MusicData;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Part;
import jp.crestmuse.cmx.handlers.NoteHandlerPartwise;
import net.muse.mixtract.data.MXTuneData;

public class CMXNoteHandler implements NoteHandlerPartwise {
	protected int currentPartNumber = 0;
	protected int partIndex = 0;
	protected MXTuneData data;

	/**
	 * @param tuneData
	 */
	public CMXNoteHandler(MXTuneData tuneData) {
		this.data = tuneData;
	}

	public void beginMeasure(Measure measure, MusicXMLWrapper wrapper) {}

	public void beginPart(Part part, MusicXMLWrapper wrapper) {
		currentPartNumber++;
	}

	public void endMeasure(Measure measure, MusicXMLWrapper wrapper) {}

	public void endPart(Part part, MusicXMLWrapper wrapper) {
		partIndex++;
	}

	public void processMusicData(MusicData md, MusicXMLWrapper wrapper) {}

}
