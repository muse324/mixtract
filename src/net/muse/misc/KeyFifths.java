package net.muse.misc;

/**
 * <h1>KeyFifths</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)</address>
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/12/21
 */
public enum KeyFifths {
	C {
		@Override public int fifths() {
			return 0;
		}
	},
	G {
		@Override public int fifths() {
			return 1;
		}
	},
	D {
		@Override public int fifths() {
			return 2;
		}
	},
	A {
		@Override public int fifths() {
			return 3;
		}
	},
	E {
		@Override public int fifths() {
			return 4;
		}
	},
	B {
		@Override public int fifths() {
			return 5;
		}
	},
	Fis {
		@Override public int fifths() {
			return 6;
		}
	},
	Cis {
		@Override public int fifths() {
			return 7;
		}
	},
	Gis {
		@Override public int fifths() {
			return 8;
		}
	},
	Dis {
		@Override public int fifths() {
			return 9;
		}
	},
	Ais {
		@Override public int fifths() {
			return 10;
		}
	},
	Eis {
		@Override public int fifths() {
			return 11;
		}
	},
	F {
		@Override public int fifths() {
			return -1;
		}
	},
	Bb {
		@Override public int fifths() {
			return -2;
		}
	},
	Eb {
		@Override public int fifths() {
			return -3;
		}
	},
	Ab {
		@Override public int fifths() {
			return -4;
		}
	},
	Db {
		@Override public int fifths() {
			return -5;
		}
	},
	Gb {
		@Override public int fifths() {
			return -6;
		}
	},
	Cb {
		@Override public int fifths() {
			return -7;
		}
	},
	Fb {
		@Override public int fifths() {
			return -8;
		}
	},
	Bbb {
		@Override public int fifths() {
			return -9;
		}
	},
	Ebb {
		@Override public int fifths() {
			return -10;
		}
	},
	Abb {
		@Override public int fifths() {
			return -11;
		}
	};

	/**
	 * @return
	 */
	public abstract int fifths();
}
