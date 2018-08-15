package net.muse.mixtract.data.curve;

import java.awt.Color;

public enum PhraseCurveType {
	DYNAMICS {

		@Override public PhraseCurve create() {
			return new DynamicsCurve();
		}

		@Override public Color color() {
			return Color.blue;
		}

	},
	TEMPO {

		@Override public PhraseCurve create() {
			return new TempoCurve();
		}

		@Override public Color color() {
			return Color.green;
		}

	},
	ARTICULATION {

		@Override public PhraseCurve create() {
			return new ArticulationCurve();
		}

		@Override public Color color() {
			return Color.RED;
		}
	};

	public abstract PhraseCurve create();

	public abstract Color color();

}