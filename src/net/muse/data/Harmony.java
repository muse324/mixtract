package net.muse.data;

import java.awt.Color;

public enum Harmony {
	I {
		@Override public Color color() {
			return Color.red;
		}

		@Override public double parameter() {
			return 0;
		}

	},
	I6 {
		@Override public Color color() {
			return Color.red.brighter();
		}

		@Override public double parameter() {
			return 1;
		}

	},
	I46 {
		@Override public Color color() {
			return Color.pink;
		}

		@Override public double parameter() {
			return 3;
		}

	},
	II {
		@Override public Color color() {
			return Color.blue;
		}

		@Override public double parameter() {
			return 6;
		}

	},
	IV {
		@Override public Color color() {
			return Color.green;
		}

		@Override public double parameter() {
			return 1;
		}

	},
	V {
		@Override public Color color() {
			return Color.yellow.darker();
		}

		@Override public double parameter() {
			return 2;
		}

	},
	V7 {
		@Override public Color color() {
			return Color.yellow;
		}

		@Override public double parameter() {
			return 3;
		}

	},
	V9 {
		@Override public Color color() {
			return Color.yellow.darker();
		}

		@Override public double parameter() {
			return 3;
		}
	},
	VI {
		@Override public Color color() {
			return Color.orange;
		}

		@Override public double parameter() {
			return 1;
		}

	},
	DD {
		@Override public Color color() {
			return Color.red;
		}

		@Override public double parameter() {
			return 4;
		}
	};

	public abstract Color color();

	public abstract double parameter();

}
