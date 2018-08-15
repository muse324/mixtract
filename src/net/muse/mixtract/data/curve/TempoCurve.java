package net.muse.mixtract.data.curve;


import java.util.*;

import net.muse.mixtract.data.*;

public class TempoCurve extends PhraseCurve {

	private LinkedList<Double> tempolist;
	private NoteData lastNote;
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
	@Override
	public void apply(TuneData target, Group gr) {
		if (tempolist != target.getTempoList())
			tempolist = target.getTempoList();
		if (lastNote != target.getLastNote(0))
			lastNote = target.getLastNote(0);
		applyTempoEvent(target.getRootGroup(), target.getBPM().get(0));
	}

	@Override
	public double initialValue() {
		return 0.;
	}

	private void applyTempoEvent(Group group, ArrayList<Double> realtimeList) {
		if (group == null)
			return;
		applyTempoEvent(group.getChildFormerGroup(), realtimeList);
		applyTempoEvent(group.getChildLatterGroup(), realtimeList);
		applyTempoEvent(group.getBeginGroupNote(), realtimeList);
	}

	private void applyTempoEvent(GroupNote note, ArrayList<Double> realtimeList) {
		if (note == null)
			return;
		NoteData n = note.getNote();
		if (n != null && !n.rest()) {
			final int size = realtimeList.size();
			int idxOn = (int) getCurrentIndex(n, size, n.onset());
			int idxOff = (int) getCurrentIndex(n, size, n.offset());
			if (idxOn >= size)
				idxOn = size - 1;
			n.setRealOnset(realtimeList.get(idxOn));
			if (idxOff >= size)
				idxOff = size - 1;
			n.setRealOffset(realtimeList.get(idxOff));
		}
		applyTempoEvent(note.child(), realtimeList);
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
			applyTempoEvent(g, realtimeList);
		}
	}
}
