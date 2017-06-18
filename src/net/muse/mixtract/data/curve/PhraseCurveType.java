package net.muse.mixtract.data.curve;


public enum PhraseCurveType {
	DYNAMICS {

		@Override public PhraseCurve create() {
			return new DynamicsCurve();
		}
	},
	TEMPO {

		@Override public PhraseCurve create() {
			return new TempoCurve();
		}
	},
	ARTICULATION {

		@Override public PhraseCurve create() {
			return new ArticulationCurve();
		}
	};

	public abstract PhraseCurve create();

}