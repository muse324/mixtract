package net.muse.mixtract.data;


import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Measure;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.MusicData;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Part;
import jp.crestmuse.cmx.handlers.NoteHandlerPartwise;

public class CMXNoteHandler implements NoteHandlerPartwise {
	protected int currentPartNumber = 0;
	protected int partIndex = 0;
	protected TuneData data;

	/**
	 * @param tuneData
	 */
	public CMXNoteHandler(TuneData tuneData) {
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
