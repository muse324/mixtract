package net.muse.mixtract.data.curve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;
import net.muse.mixtract.data.MXTuneData;

public class TempoCurve extends PhraseCurve {

	private LinkedList<Double> tempolist;
	private MXNoteData lastNote;
	private double musicLengthInRealtimeMsec;

	TempoCurve() {
		super();
		type = PhraseCurveType.TEMPO;
		initializeParamValue(); // 必須
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.data.PhraseCurve#apply(jp.crestmuse.mixtract
	 * .data.TuneData)
	 */
	@Override public void apply(MXTuneData target, MXGroup gr) {
		if (tempolist != target.getTempoList())
			tempolist = target.getTempoList();
		if (lastNote != target.getLastNote(0))
			lastNote = target.getLastNote(0);
		applyTempoEvent(target.getRootGroup(), target.getBPM().get(0));
	}

	@Override public double initialValue() {
		return 0.;
	}

	private void applyTempoEvent(MXGroup group,
			ArrayList<Double> realtimeList) {
		if (group == null)
			return;
		applyTempoEvent(group.getChildFormerGroup(), realtimeList);
		applyTempoEvent(group.getChildLatterGroup(), realtimeList);
		if (!group.hasChild())
			applyTempoEvent(group.getBeginNote(), realtimeList);
	}

	private void applyTempoEvent(NoteData note,
			ArrayList<Double> realtimeList) {
		if (note == null)
			return;
		if (note != null && !note.rest()) {
			final int size = realtimeList.size();
			int idxOn = (int) getCurrentIndex(note, size, note.onset());
			int idxOff = (int) getCurrentIndex(note, size, note.offset());
			if (idxOn >= size)
				idxOn = size - 1;
			note.setRealOnset(realtimeList.get(idxOn));
			if (idxOff >= size)
				idxOff = size - 1;
			note.setRealOffset(realtimeList.get(idxOff));
		}
//		applyTempoEvent(note.child(), realtimeList);
		applyTempoEvent(note.next(), realtimeList);
	}

	/**
	 * @param n
	 * @param size
	 * @return
	 */
	private double getCurrentIndex(NoteData n, final int size, double onset) {
		return Math.round(size * onset / (double) lastNote.offset());
	}

	private void applyTempoEvent(List<Group> rootGroup, int bpm) {
		// 拍占有時間(%?)
		ArrayList<Double> beattimeList = new ArrayList<Double>();
		for (int i = 0; i < tempolist.size(); i++) {
			double beattime = Math.pow(2.0, -1. * tempolist.get(i));
			beattimeList.add(beattime);
		}

		// 実時間（積分）
		ArrayList<Double> realtimeList = new ArrayList<Double>();
		double currentTime = 0.;
		// int bpm = getDefaultBPM();
		double w = lastNote.offsetInMsec(bpm) / (double) beattimeList.size();
		for (int i = 0; i < beattimeList.size(); i++) {
			realtimeList.add(currentTime);
			currentTime += w * beattimeList.get(i);
		}
		musicLengthInRealtimeMsec = currentTime;

		for (Group g : rootGroup) {
			assert g instanceof MXGroup;
			applyTempoEvent((MXGroup) g, realtimeList);
		}
	}
}
