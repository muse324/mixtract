package net.muse.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.*;
import jp.crestmuse.cmx.handlers.NoteHandlerPartwise;
import net.muse.misc.MuseObject;

public abstract class AbstractCMXNoteHandler extends MuseObject implements
		NoteHandlerPartwise {
	protected int currentPartNumber = 0;
	protected int partIndex = 0;
	protected TuneData data;

	/**
	 * @param tuneData
	 */
	public AbstractCMXNoteHandler(TuneData tuneData) {
		this.data = tuneData;
	}

	/**
	 * @return data
	 */
	protected TuneData data() {
		return data;
	}

	/**
	 * @param data セットする data
	 */
	protected void setData(TuneData data) {
		this.data = data;
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
