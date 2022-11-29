package net.muse.data;

import java.awt.Color;

/**
 * グループのタイプ
 *
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2008/10/15
 */
public enum GroupType {
	/** 連桁 */
	BEAM {
		@Override
		public Color getColor() {
			return Color.gray;
		}
	},
	/** あるグループの子階層として新規作成されたもの */
	CHILD {
		@Override
		public Color getColor() {
			return Color.yellow.darker();
		}
	},
	CHILD_BOUND {
		@Override
		public Color getColor() {
			return Color.magenta;
		}
	},
	DIVIDE {
		@Override
		public Color getColor() {
			return Color.black;
		}
	},
	/** 推測された音符列 */
	NOTE {
		@Override
		public Color getColor() {
			return Color.darkGray;
		}
	},
	/** あるグループの親階層として新規作成されたもの */
	PARENT {
		@Override
		public Color getColor() {
			return Color.magenta.brighter();
		}
	},
	/** MusicXML に記述されたスラー */
	SLUR {
		@Override
		public Color getColor() {
			return Color.blue.darker();
		}
	},
	/** ユーザによる指定 */
	USER {
		@Override
		public Color getColor() {
			return Color.green.darker();
		}
	},
	AUTO {
		@Override
		public Color getColor() {
			return Color.magenta.brighter();
		}
	},
	/** クレシェンド */
	CRESC {
		@Override
		public Color getColor() {
			return Color.black;
		}
	},
	/** 和音 */
	CHORD {
		@Override
		public Color getColor() {
			return Color.CYAN;
		}
	},
	/** ディミヌエンド */
	DIM {
		@Override
		public Color getColor() {
			return Color.black;
		}
	};

	public static GroupType is(char c) {
		switch (c) {
		case 'U':
			return USER;
		case 'N':
			return NOTE;
		case 'P':
			return PARENT;
		case 'A':
			return AUTO;
		case 'S':
			return SLUR;
		case 'B':
			return BEAM;
		case 'C':
			return CHORD;
		default:
			return NOTE;
		}
	}

	public abstract Color getColor();
}