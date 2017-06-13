package net.muse.mixtract.data;


import java.util.LinkedList;

public class DynamicsCurve extends PhraseCurve {

	private LinkedList<Double> dynamicsList;
	private NoteData lastNote;
	private double[] volume;

	DynamicsCurve() {
		super();
		type = PhraseCurveType.DYNAMICS;
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
		dynamicsList = target.getDynamicsList();
		lastNote = target.getLastNote(0);
		volume = target.getVolume();
		for (Group g : target.getRootGroup()) {
			// target.applyDynamicsEvent(g);
			applyDynamicsEvent(g);
		}
	}

	@Override
	public double initialValue() {
		return 0.;
	}

	private void applyDynamicsEvent(Group group) {
		if (group == null)
			return;
		applyDynamicsEvent(group.getChildFormerGroup());
		applyDynamicsEvent(group.getChildLatterGroup());
		applyDynamicsEvent(group.getBeginGroupNote());
	}

	private void applyDynamicsEvent(GroupNote note) {
		if (note == null)
			return;
		applyDynamicsEvent(note.child());
		applyDynamicsEvent(note.next());
		applyDynamicsEvent(note.getNote());
	}

	private void applyDynamicsEvent(NoteData note) {
		if (note == null)
			return;
		if (!note.rest()) {
			final int size = dynamicsList.size();
			int idx = size * note.onset() / lastNote.offset();
			if (idx >= size)
				idx = size - 1;

			double vel = getDefaultVelocity() * volume[note.partNumber() - 1];
			vel += 20 * dynamicsList.get(idx);
			note.setVelocity((int) vel);
		}
		applyDynamicsEvent(note.child());
		applyDynamicsEvent(note.next());
	}

}
