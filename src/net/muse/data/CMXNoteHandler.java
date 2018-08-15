package net.muse.data;

import java.util.ArrayList;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Attributes;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Direction;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Measure;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.MusicData;
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

	private ArrayList<NoteData> tiedNote = new ArrayList<NoteData>();;

	public CMXNoteHandler(TuneData tuneData) {
		super(tuneData);
	}

	@Override public void beginMeasure(Measure measure,
			MusicXMLWrapper wrapper) {
		super.beginMeasure(measure, wrapper);
		if (currentPartNumber > 1)
			return;
		try {
			currentBPM = measure.tempo();
		} catch (NullPointerException e) {
		} catch (NumberFormatException e) {
		} finally {
			data().getBPM().add(currentBPM);
			if (currentPartNumber == 1 && measure.number() == 1) {
				setDefaultBPM(currentBPM);
			}
			butler().printConsole("-----measure " + measure.number()
					+ ", tempo=" + currentBPM);
		}
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.data.CMXNoteHandler#beginPart(jp.crestmuse
	 * .cmx.filewrappers.MusicXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.MusicXMLWrapper)
	 */
	@Override public void beginPart(MusicXMLWrapper.Part part,
			MusicXMLWrapper wrapper) {
		super.beginPart(part, wrapper);
		int ch = part.midiChannel() - 1;
		data().midiProgram[ch] = part.midiProgram();
		// TODO 声部間velocityの調整 (volume[]) 決めうち。
		data().volume[ch] = (ch == 0) ? 1.0 : 0.7;
		currentDefaultVelocity = (int) (getDefaultVelocity()
				* data().volume[ch]);
		cur = null;
		butler().printConsole(String.format("=====part P%d¥n",
				currentPartNumber));
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.data.CMXNoteHandler#endPart(jp.crestmuse.
	 * cmx.filewrappers.MusicXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.MusicXMLWrapper)
	 */
	@Override public void endPart(MusicXMLWrapper.Part part,
			MusicXMLWrapper wrapper) {
		createGroup();
		super.endPart(part, wrapper);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.AbstractCMXNoteHandler#endPart(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Part,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override public void endPart(SCCXMLWrapper.Part arg0, SCCXMLWrapper arg1) {
		createGroup();
		super.endPart(arg0, arg1);
	}

	@Override public void processMusicData(MusicData md,
			MusicXMLWrapper wrapper) {
		if (md instanceof MusicXMLWrapper.Note)
			readNoteData((MusicXMLWrapper.Note) md);
		else if (md instanceof Attributes) {
			Attributes a = (Attributes) md;
			if (a.mode() != null)
				setKeys(a.mode(), a.fifths());
			data().setBeatInfo(a.measure().number(), a.beats(), a.beatType());
		} else if (md instanceof Direction)
			readDirections((Direction) md);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.AbstractCMXNoteHandler#processNote(jp.crestmuse.cmx.
	 * filewrappers.SCCXMLWrapper.Note,
	 * jp.crestmuse.cmx.filewrappers.SCCXMLWrapper)
	 */
	@Override public void processNote(SCCXMLWrapper.Note note,
			SCCXMLWrapper arg1) {
		data().setTempoListEndtime(note.offset(getTicksPerBeat()), true);
		parseMIDIControlMessage(note, note.getNodeName().equals("control"));
		parseMIDINoteMessage(note, note.getNodeName().equals("note"));
	}

	protected void parseMIDINoteMessage(SCCXMLWrapper.Note note,
			boolean condition) {
		if (!condition)
			return;
		int beat = getBeat(note);
		NoteData nd = createNoteData(note, currentPartNumber, ++idx, data()
				.getBPM().get(0), beat, note.velocity());
		nd.setKeyMode(keyMode, fifths);
		nd.setMeasureNumber(currentMeasureNumber);
		butler().printConsole(nd.toString());
		if (cur == null) {
			// 冒頭音
			cur = nd;
			data().setPartwiseNotelist(partIndex, nd);
		} else {
			cur.setNext(nd);
			cur = nd;
		}
	}

	protected void parseMIDIControlMessage(SCCXMLWrapper.Note note,
			boolean condition) {
		if (!condition)
			return;
		switch (note.notenum()) {
		case 64:
			butler().printConsole(String.format("%d: Sustain %d", note.onset(),
					note.velocity()));
			break;
		default:
			butler().printConsole(String.format("%d: control %d %d", note
					.onset(), note.notenum(), note.velocity()));
		}
		return;
	}

	protected Group createGroup(NoteData n, int i, GroupType type) {
		return new Group(n, i, type);
	}

	protected NoteData createNoteData(MusicXMLWrapper.Note note, int partNumber,
			int idx, Integer bpm, int vel) {
		return new NoteData(note, partNumber, idx, bpm, vel);
	}

	protected NoteData createNoteData(SCCXMLWrapper.Note note, int partNumber,
			int idx, Integer bpm, int beat, int vel) {
		return new NoteData(note, partNumber, idx, bpm, beat, vel);
	}

	protected TuneData data() {
		return data;
	}

	private void createGroup() {
		Group g = createGroup(data().getPartwiseNotelist().get(partIndex),
				partIndex + 1, GroupType.NOTE);
		// data().getPartwiseNotelist().add(g.getBeginNote());
		if (primaryGrouplist == null) {
			primaryGrouplist = g;
			data().setGrouplist(partIndex, g);
		} else if (TuneData.segmentGroupnoteLine) {
			linkToPrimaryGroup(g.getBeginNote(), primaryGrouplist
					.getBeginNote());
		} else {
			data().setGrouplist(partIndex, g);
		}
	}

	private int getBeat(SCCXMLWrapper.Note note) {
		BeatInfo b = data().getBeatInfoList(currentMeasureNumber);
		int beat = note.onset() / getTicksPerBeat() % ((b != null) ? b.beat()
				: 4) + 1;
		currentMeasureNumber = note.onset() / getTicksPerBeat() / ((b != null)
				? b.beat()
				: 4) + 1;
		return beat;
	}

	private void linkToPrimaryGroup(NoteData note,
			NoteData currentPrimaryNote) {
		if (note == null)
			return;
		while (currentPrimaryNote.hasNext() && note
				.onset() >= currentPrimaryNote.next().onset()) {
			currentPrimaryNote = currentPrimaryNote.next();
		}
		if (note.onset() == currentPrimaryNote.onset()) {
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
		data().setTempoListEndtime(note.offset(getTicksPerBeat()), true);
		butler().printConsole(nd.toString());
		if (note.containsTieType("start"))
			tiedNote.add(nd);
		if (cur == null) {
			// 冒頭音
			cur = nd;
			data().setPartwiseNotelist(partIndex, nd);
		} else if (note.chord()) {
			// 和音
			// 最高音へ移動
			while (cur.parent() != null)
				cur = cur.parent();
			cur.setParent(nd);
			nd.setPrevious(cur.previous());
			if (cur.equals(data().getPartwiseNotelist().get(partIndex)))
				data().setPartwiseNotelist(partIndex, nd);
			cur = nd;
		} else if (note.containsTieType("stop")) {
			NoteData tgt = null;
			// タイ
			for (NoteData t : tiedNote) {
				if (t.getXMLNote().tiedTo().equals(note)) {
					tgt = t;
					t.setOffset(nd.offset()); // TODO タイ音符のオフセットの扱い。MIDI再生時に計算させる
					t.setTiedTo(nd);
					break;
				}
			}
			if (tgt != null)
				tiedNote.remove(tgt);
		} else {
			cur.setNext(nd);
			cur = nd;
		}
	}

	private void setChild(NoteData parent, NoteData note) {
		if (!parent.hasChild()) {
			parent.setChild(note);
			return;
		}
		setChild(parent.child(), note);
	}
}
