package net.muse.data;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.*;
import net.muse.mixtract.data.*;

public class CMXNoteHandler extends AbstractCMXNoteHandler {

	public CMXNoteHandler(MXTuneData tuneData) {
		super(tuneData);
	}

	private MXNoteData cur = null;
	private Group primaryGrouplist = null;
	private int idx = 0;
	private KeyMode keyMode;
	private int fifths;
	/**
	 * TODO この定数値(120)はMusicXMLWrapperのデフォルト値。
	 * <sound>タグが存在しないとこの値が返ってくる
	 */
	private int currentBPM = 120;
	private int currentDefaultVelocity;

	@Override public void beginMeasure(Measure measure,
			MusicXMLWrapper wrapper) {
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
	@Override public void beginPart(Part part, MusicXMLWrapper wrapper) {
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
	@Override public void endPart(Part part, MusicXMLWrapper wrapper) {
		Group g = new MXGroup(data().getNoteList(partIndex), partIndex + 1,
				GroupType.NOTE);

		if (primaryGrouplist == null) {
			primaryGrouplist = g;
			data().setGrouplist(partIndex, g);
		} else if (MXTuneData.segmentGroupnoteLine) {
			linkToPrimaryGroup(g.getBeginGroupNote(), primaryGrouplist
					.getBeginGroupNote());
		} else {
			data().setGrouplist(partIndex, g);
		}
		super.endPart(part, wrapper);
	}

	@Override public void processMusicData(MusicData md,
			MusicXMLWrapper wrapper) {
		if (md instanceof Note)
			readNoteData((Note) md);
		else if (md instanceof Attributes)
			readAttributes((Attributes) md);
		else if (md instanceof Direction)
			readDirections((Direction) md);
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
			if (MXTuneData.segmentGroupnoteLine) {
				if (note.hasPrevious())
					note.previous().setNext(null);
				note.setPrevious(null);
			}
		}
		linkToPrimaryGroup(note.next(), currentPrimaryNote);
	}

	private void readAttributes(Attributes attr) {
		keyMode = KeyMode.valueOf(attr.mode());
		fifths = attr.fifths();
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
	private void readNoteData(Note note) {
		MXNoteData nd = new MXNoteData(note, currentPartNumber, ++idx, data()
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
