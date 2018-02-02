package net.muse.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.*;
import jp.crestmuse.cmx.filewrappers.SCCXMLWrapper;

public class CMXNoteHandler extends AbstractCMXNoteHandler {

	private NoteData cur = null;

	private Group primaryGrouplist = null;

	private int idx = 0;
	/**
	 * TODO この定数値(120)はMusicXMLWrapperのデフォルト値。
	 * <sound>タグが存在しないとこの値が返ってくる
	 */
	private int currentBPM = 120;
	private int currentDefaultVelocity;

	public CMXNoteHandler(TuneData tuneData) {
		super(tuneData);
	}

	@Override
	public void beginMeasure(Measure measure, MusicXMLWrapper wrapper) {
		super.beginMeasure(measure, wrapper);
		if (currentPartNumber == 1) {
			try {
				currentBPM = (currentBPM == measure.tempo()) ? currentBPM
						: measure.tempo();
				data().getBPM().add(currentBPM);
				if (currentPartNumber == 1 && measure.number() == 1) {
					setDefaultBPM(currentBPM);
				}
				testPrintln("-----measure " + measure.number() + ", tempo="
						+ currentBPM);
			} catch (NullPointerException e) {
			}
		}
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.data.CMXNoteHandler#beginPart(jp.crestmuse
	 * .cmx.filewrappers.MusicXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.MusicXMLWrapper)
	 */
	@Override
	public void beginPart(MusicXMLWrapper.Part part, MusicXMLWrapper wrapper) {
		super.beginPart(part, wrapper);
		int ch = part.midiChannel() - 1;
		data().midiProgram[ch] = part.midiProgram();
		// TODO 声部間velocityの調整 (volume[]) 決めうち。
		data().volume[ch] = (ch == 0) ? 1.0 : 0.7;
		currentDefaultVelocity = (int) (getDefaultVelocity()
				* data().volume[ch]);
		cur = null;
		testPrintln("=====part " + currentPartNumber);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.data.CMXNoteHandler#endPart(jp.crestmuse.
	 * cmx.filewrappers.MusicXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.MusicXMLWrapper)
	 */
	@Override
	public void endPart(MusicXMLWrapper.Part part, MusicXMLWrapper wrapper) {
		createGroup();
		super.endPart(part, wrapper);
	}

	private void createGroup() {
		Group g = createGroup(data().getNoteList(partIndex), partIndex + 1,
				GroupType.NOTE);

		if (primaryGrouplist == null) {
			primaryGrouplist = g;
			data().setGrouplist(partIndex, g);
		} else if (TuneData.segmentGroupnoteLine) {
			linkToPrimaryGroup(g.getBeginGroupNote(), primaryGrouplist
					.getBeginGroupNote());
		} else {
			data().setGrouplist(partIndex, g);
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.AbstractCMXNoteHandler#endPart(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void endPart(SCCXMLWrapper.Part arg0, SCCXMLWrapper arg1) {
		createGroup();
		super.endPart(arg0, arg1);
	}

	@Override
	public void processMusicData(MusicData md, MusicXMLWrapper wrapper) {
		if (md instanceof MusicXMLWrapper.Note)
			readNoteData((MusicXMLWrapper.Note) md);
		else if (md instanceof Attributes) {
			Attributes a = (Attributes) md;
			setKeys(a.mode(), a.fifths());
		} else if (md instanceof Direction)
			readDirections((Direction) md);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.AbstractCMXNoteHandler#processNote(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Note,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override
	public void processNote(SCCXMLWrapper.Note note, SCCXMLWrapper arg1) {
		// readNoteData((MusicXMLWrapper.Note) note.getMusicXMLWrapperNote());
		NoteData nd = createNoteData(note, currentPartNumber, ++idx, data()
				.getBPM().get(0), currentDefaultVelocity);
		nd.setKeyMode(keyMode, fifths);
		testPrintln(nd.toString());
		if (cur == null) {
			// 冒頭音
			cur = nd;
			data().setNotelist(partIndex, nd);
		} else {
			cur.setNext(nd);
			cur = nd;
		}
	}

	protected Group createGroup(NoteData n, int i, GroupType type) {
		return new Group(n, i, type);
	}

	protected NoteData createNoteData(MusicXMLWrapper.Note note, int partNumber,
			int idx, Integer bpm, int vel) {
		return new NoteData(note, partNumber, idx, bpm, vel);
	}

	protected NoteData createNoteData(SCCXMLWrapper.Note note, int partNumber,
			int idx, Integer bpm, int vel) {
		return new NoteData(note, partNumber, idx, bpm, vel);
	}

	protected TuneData data() {
		return data;
	}

	private void linkToPrimaryGroup(GroupNote note,
			GroupNote currentPrimaryNote) {
		if (note == null)
			return;
		while (currentPrimaryNote.hasNext() && note.getNote()
				.onset() >= currentPrimaryNote.next().getNote().onset()) {
			currentPrimaryNote = currentPrimaryNote.next();
		}
		if (note.getNote().onset() == currentPrimaryNote.getNote().onset()) {
			setChild(currentPrimaryNote, note);
			if (TuneData.segmentGroupnoteLine) {
				if (note.hasPrevious())
					note.previous().setNext(null);
				note.setPrevious(null);
			}
		}
		linkToPrimaryGroup(note.next(), currentPrimaryNote);
	}

	private void readDirections(Direction md) {
		try {
			data().getBPM().add((int) md.tempo());
		} catch (NullPointerException e) {
		}
		// TODO 他の Direction も実装する
	}

	/**
	 * @param md
	 */
	private void readNoteData(MusicXMLWrapper.Note note) {
		NoteData nd = createNoteData(note, currentPartNumber, ++idx, data()
				.getBPM().get(0), currentDefaultVelocity);
		nd.setKeyMode(keyMode, fifths);
		testPrintln(nd.toString());
		if (cur == null) {
			// 冒頭音
			cur = nd;
			data().setNotelist(partIndex, nd);
		} else if (note.chord()) {
			// 和音
			// 最高音へ移動
			while (cur.parent() != null)
				cur = cur.parent();
			cur.setParent(nd);
			nd.setPrevious(cur.previous());
			if (cur.equals(data().getNoteList(partIndex)))
				data().setNotelist(partIndex, nd);
			cur = nd;
		} else if (note.containsTieType("stop")) {
			// タイ
			cur.setOffset(nd.offset());
		} else {
			cur.setNext(nd);
			cur = nd;
		}
	}

	private void setChild(GroupNote parent, GroupNote note) {
		if (!parent.hasChild()) {
			parent.setChild(note);
			return;
		}
		setChild(parent.child(), note);
	}
}
