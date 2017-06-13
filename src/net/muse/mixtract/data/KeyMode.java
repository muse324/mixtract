package net.muse.mixtract.data;


/**
 * <h1>KeyMode</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/12/21
 */
public enum KeyMode {
	major {
		@Override public int[] noteIntervals(Harmony c) {
			switch (c) {
				case I:
					return new int[] { 0, 4, 7 };
				case I6:
					return new int[] { 4, 7, 0 };
				case I46:
					return new int[] { 7, 0, 4 };
				case II:
					return new int[] { 2, 5, 9 };
				case IV:
					return new int[] { 5, 9, 0 };
				case V:
					return new int[] { 7, 11, 2 };
				case V7:
					return new int[] { 7, 11, 2, 5 };
				case V9:
					return new int[] { 7, 11, 2, 5, 9 };
				case VI:
					return new int[] { 9, 0, 4 };
				case DD:
					return new int[] { 2, 6, 9 };
			}
			return null;
		}
	},
	minor {
		@Override public int[] noteIntervals(Harmony c) {
			switch (c) {
				case I:
					return new int[] { 0, 3, 7 };
				case I6:
					return new int[] { 3, 7, 0 };
				case I46:
					return new int[] { 7, 0, 3 };
				case II:
					return new int[] { 2, 5, 8 };
				case IV:
					return new int[] { 5, 8, 0 };
				case V:
					return new int[] { 7, 11, 2 };
				case V7:
					return new int[] { 7, 11, 2, 5 };
				case V9:
					return new int[] { 7, 11, 2, 5, 8 };
				case VI:
					return new int[] { 8, 0, 3 };
				case DD:
					return new int[] { 2, 6, 8 };
			}
			return null;
		}
	};

	/**
	 * @param c
	 * @return
	 */
	public abstract int[] noteIntervals(Harmony c);
}
