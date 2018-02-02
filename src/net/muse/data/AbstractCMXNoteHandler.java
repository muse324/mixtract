package net.muse.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.*;
import jp.crestmuse.cmx.filewrappers.SCC.HeaderElement;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper.Note;
import jp.crestmuse.cmx.handlers.NoteHandlerPartwise;
import jp.crestmuse.cmx.handlers.SCCHandler;
import net.muse.misc.MuseObject;

public abstract class AbstractCMXNoteHandler extends MuseObject implements
		NoteHandlerPartwise, SCCHandler {
	protected int currentPartNumber = 0;
	protected int partIndex = 0;
	protected TuneData data;
	protected KeyMode keyMode;
	protected int fifths;

	/**
	 * @param tuneData
	 */
	public AbstractCMXNoteHandler(TuneData tuneData) {
		this.data = tuneData;
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.cmx.handlers.SCCHandler#beginHeader(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void beginHeader(SCCXMLWrapper arg0) {
		for (SCCXMLWrapper.HeaderElement h : arg0.getHeaderElementList()) {
			if (h.name().equals("TEMPO"))
				data().getBPM().add(Integer.valueOf(h.content()));
		}
		HeaderElement key = arg0.getFirstKey();
		if (key != null) {
			String[] str = key.content().split(" ");
			setKeys(str[1], Integer.valueOf(str[0]));
		} else
			setKeys(KeyMode.major.name(), 0);
	}

	public void beginMeasure(Measure measure, MusicXMLWrapper wrapper) {}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.cmx.handlers.SCCHandler#beginPart(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void beginPart(jp.crestmuse.cmx.filewrappers.SCCXMLWrapper.Part arg0,
			SCCXMLWrapper arg1) {
		countUpCurrentPartNumber();
		System.out.println(arg1.getFirstKey());
	}

	public void beginPart(Part part, MusicXMLWrapper wrapper) {
		countUpCurrentPartNumber();
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.cmx.handlers.SCCHandler#endHeader(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void endHeader(SCCXMLWrapper arg0) {}

	public void endMeasure(Measure measure, MusicXMLWrapper wrapper) {}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.cmx.handlers.SCCHandler#endPart(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void endPart(jp.crestmuse.cmx.filewrappers.SCCXMLWrapper.Part arg0,
			SCCXMLWrapper arg1) {
		countUpPartIndex();
	}

	public void endPart(Part part, MusicXMLWrapper wrapper) {
		countUpPartIndex();
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.cmx.handlers.SCCHandler#processHeaderElement(int,
	 * java.lang.String, java.lang.String,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void processHeaderElement(int arg0, String arg1, String arg2,
			SCCXMLWrapper arg3) {}

	public void processMusicData(MusicData md, MusicXMLWrapper wrapper) {}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.cmx.handlers.SCCHandler#processNote(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Note,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void processNote(Note arg0, SCCXMLWrapper arg1) {}

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

	private void countUpCurrentPartNumber() {
		currentPartNumber++;
	}

	private void countUpPartIndex() {
		partIndex++;
	}

	protected void setKeys(String mode, int f) {
		keyMode = KeyMode.valueOf(mode);
		fifths = f;
	}

}
