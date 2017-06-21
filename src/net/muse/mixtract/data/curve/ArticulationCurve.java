package net.muse.mixtract.data.curve;


import java.util.LinkedList;

import net.muse.data.NoteData;
import net.muse.mixtract.data.*;

public class ArticulationCurve extends PhraseCurve {

	private LinkedList<Double> articulationList;

	ArticulationCurve() {
		super();
		type = PhraseCurveType.ARTICULATION;
		initializeParamValue(); // 必須
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.data.PhraseCurve#apply(jp.crestmuse.mixtract
	 * .data.TuneData)
	 */
	@Override
	public void apply(MXTuneData target, Group gr) {
		Group g = target.getRootGroup(0);
		double bt = g.getBeginGroupNote().getNote()
				.onsetInMsec(getDefaultBPM());
		double et = g.getEndGroupNote().getNote().offsetInMsec(getDefaultBPM());
		articulationList = target.getArticulationList();
		applyArticulationEvent(g, bt, et);
	}

	@Override
	public double initialValue() {
		return 1.;
	}

	protected long firestGraphicYValue(int i, int axisY, double height) {
		return super.firestGraphicYValue(i, axisY, height);
		// return Math.round(getInitialLogValue() * axisY);
		// return Math.round(getParamlist().get(i) * axisY);
		// return super.firestGraphicYValue(i, axisY, height);
	}

	private void applyArticulationEvent(Group gr, double bt, double et) {
		if (gr == null)
			return;
		if (gr.hasChild()) {
			applyArticulationEvent(gr.getChildFormerGroup(), bt, et);
			applyArticulationEvent(gr.getChildLatterGroup(), bt, et);
		} else {
			// target.applyArticulationEvent(this, gr.getBeginGroupNote(), bt,
			// et);
			applyArticulationEvent(gr.getBeginGroupNote(), bt, et);
		}
	}

	private void applyArticulationEvent(GroupNote gnote, double bt, double et) {
		if (gnote == null)
			return;
		NoteData nd = (NoteData) gnote.getNote();
		double on = nd.onsetInMsec(getDefaultBPM());
		double tv = nd.timeValueInMsec(getDefaultBPM());
		double t = on + tv; // current note の楽譜上のオフセット

		int idx = (int) ((t - bt) / (et - bt) * articulationList.size()) - 1;

		double artc = articulationList
				.get((idx < articulationList.size()) ? idx : idx - 1);
		double newRealOffset = on + tv * artc;
		nd.setRealOffset(newRealOffset);
		applyArticulationEvent(gnote.child(), bt, et);
		applyArticulationEvent(gnote.next(), bt, et);
	}
}
