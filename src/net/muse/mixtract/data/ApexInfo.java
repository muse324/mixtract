package net.muse.mixtract.data;


import java.util.List;

import net.muse.misc.MuseObject;

/**
 * <h1>ApexInfo</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/11/27
 */
abstract class ApexInfo extends MuseObject {

	/**
	 * <h1>AppoggiaturaRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/12/21
	 */
	public static abstract class AppoggiaturaRule extends ApexInfo {

		/**
		 * @param score
		 */
		public AppoggiaturaRule(double score) {
			super(score);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo#apply(jp.crestmuse.mixtract.data
		 * .Group)
		 */
		@Override protected void apply(Group group) {}

		/*
		 * (non-Javadoc)
		 * @see jp.crestmuse.mixtract.data.ApexInfo#apply(java.util.List, int,
		 * int)
		 */
		@Override protected void apply(List<NoteData> notelist, int idx, int size) {
			if (idx + 1 >= size)
				return;
			NoteData n1 = notelist.get(idx);
			AbstractNoteData n2 = notelist.get(idx + 1);
			if (n1.isNonChord() && !n2.isNonChord()) {
				if (getAppoggiaturaScore(n1, n2))
					n1.addApexScore(this);
			}
			apply(notelist, idx + 1, size);
		}

		/**
		 * @param n1
		 * @param n2
		 * @return
		 */
		protected abstract boolean getAppoggiaturaScore(AbstractNoteData n1, AbstractNoteData n2);

	}

	static class BeginNoteRule extends SingleNoteRule {

		private BeginNoteRule(double val) {
			super(val);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo#apply(jp.crestmuse.mixtract.data
		 * .Group)
		 */
		@Override public void apply(Group group) {
			group.getBeginGroupNote().getNote().addApexScore(this);
		}
	}

	public static class CadentI6Rule extends CadentzRule {

		public CadentI6Rule(double b, double c) {
			super(b, c);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo.CadentzIRule#isMatchedCondition
		 * (jp.crestmuse.mixtract.data.Harmony)
		 */
		@Override public boolean isMatchedCondition(Harmony c) {
			return c == Harmony.I6;
		}

	}

	public static class CadentVIRule extends CadentzRule {

		public CadentVIRule(double b, double c) {
			super(b, c);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo.CadentzIRule#isMatchedCondition
		 * (jp.crestmuse.mixtract.data.Harmony)
		 */
		@Override public boolean isMatchedCondition(Harmony c) {
			return c == Harmony.VI;
		}

	}

	public static class CadentzIRule extends CadentzRule {

		public CadentzIRule(double b, double c) {
			super(b, c);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo.CadentzRule#isMatchedCondition
		 * (jp.crestmuse.mixtract.data.Harmony)
		 */
		@Override public boolean isMatchedCondition(Harmony c) {
			return c == Harmony.I;
		}
	}
	public static abstract class CadentzRule extends MountainProgressNoteRule {

		protected CadentzRule(double b, double c) {
			super(2, 0, b, c);
		}

		public void addApexInfoToNotes(NoteData n1, NoteData n2) {
			n1.addApexScore(new ValleyProgressNoteRule(2, score1, score2, score3));
			n2.addApexScore(new ValleyProgressNoteRule(3, score1, score2, score3));
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo.MountainProgressNoteRule#apply
		 * (java.util.List, int, int)
		 */
		@Override public void apply(List<NoteData> notelist, int idx, int size) {
			if (idx + 1 >= size)
				return;
			NoteData n1 = notelist.get(idx);
			if (n1.chord() == Harmony.V || n1.chord() == Harmony.V7) {
				NoteData n2 = notelist.get(idx + 1);
				if (isMatchedCondition(n2.chord())) {
					addApexInfoToNotes(n1, n2);
				}
			}
			apply(notelist, idx + 1, size);
		}

		/**
		 * @param c
		 * @return
		 */
		protected abstract boolean isMatchedCondition(Harmony c);
	}
	public static class ChordChangeRule extends IntervalProgressRule {

		protected ChordChangeRule(double score) {
			super(score);
		}

		@Override protected NoteData getPrimaryNote(NoteData n1, NoteData n2) {
			return n2;
		}

		@Override protected boolean isExceptedCondition(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.chord() == n2.chord();
		}
	}

	static class EndNoteRule extends SingleNoteRule {
		/**
		 * @param val
		 */
		private EndNoteRule(double val) {
			super(val);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo#apply(jp.crestmuse.mixtract.data
		 * .Group)
		 */
		@Override public void apply(Group group) {
			group.getEndGroupNote().getNote().addApexScore(this);
		}

	}

	/**
	 * <h1>HigherNoteRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/11/27
	 */
	static class HigherNoteRule extends IntervalProgressRule {

		/**
		 * @param d
		 */
		private HigherNoteRule(double val) {
			super(val);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo.LongerNoteRule#getPrimaryNote
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected NoteData getPrimaryNote(NoteData n1, NoteData n2) {
			return (n1.noteNumber() > n2.noteNumber()) ? n1 : n2;
		}

		@Override protected boolean isExceptedCondition(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.noteNumber() == n2.noteNumber();
		}

	}

	abstract static class IntervalProgressRule extends ApexInfo {

		protected IntervalProgressRule(double score) {
			super(score);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo#apply(jp.crestmuse.mixtract.data
		 * .Group)
		 */
		@Override public void apply(Group group) {}

		/*
		 * (非 Javadoc)
		 * @see jp.crestmuse.mixtract.data.ApexInfo#apply(java.util.List, int,
		 * int)
		 */
		@Override public void apply(List<NoteData> notelist, int idx, int size) {
			if (idx + 1 >= size)
				return;
			NoteData n1 = notelist.get(idx);
			NoteData n2 = notelist.get(idx + 1);
			if (isExceptedCondition(n1, n2)) {
				apply(notelist, idx + 1, size);
				return;
			}
			NoteData primaryNote = getPrimaryNote(n1, n2);
			primaryNote.addApexScore(this);
			apply(notelist, idx + 1, size);
		}

		protected abstract NoteData getPrimaryNote(NoteData n1, NoteData n2);

		protected abstract boolean isExceptedCondition(AbstractNoteData n1, AbstractNoteData n2);
	}

	/**
	 * <h1>LongerAppoggiaturaRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/12/21
	 */
	public static class LongerAppoggiaturaRule extends AppoggiaturaRule {

		/**
		 * @param score
		 */
		public LongerAppoggiaturaRule(double score) {
			super(score);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.AppoggiaturaRule#getAppoggiaturaScore
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected boolean getAppoggiaturaScore(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.duration() > n2.duration();
		}

	}
	/**
	 * <h1>LongerNoteRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/11/27
	 */
	public static class LongerNoteRule extends IntervalProgressRule {

		/**
		 * @param val
		 */
		private LongerNoteRule(double val) {
			super(val);
		}

		/**
		 * @param n1
		 * @param n2
		 * @return
		 */
		@Override protected NoteData getPrimaryNote(NoteData n1, NoteData n2) {
			return ((n1.timeValue() > n2.timeValue()) ? n1 : n2);
		}

		/**
		 * @param n1
		 * @param n2
		 * @return
		 */
		@Override protected boolean isExceptedCondition(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.timeValue() == n2.timeValue();
		}

	}

	public static class LowerProgressNoteRule extends UpperProgressNoteRule {

		protected LowerProgressNoteRule(double score) {
			super(score);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.UpperProgressNoteRule#matchCondition
		 * (int, int)
		 */
		@Override protected boolean matchCondition(int d1, int d2) {
			return d1 > -4 && d1 < 0 && d2 > -4 && d2 < 0;
		}

		/*
		 * (非 Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.UpperProgressNoteRule#matchCondition
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected boolean matchCondition(AbstractNoteData n1, AbstractNoteData n2) {
			int d = interval(n1, n2);
			return d >= -4 && d < 0;
		}
	}
	public static class LowerStetchNoteRule extends UpperStetchNoteRule {

		protected LowerStetchNoteRule(double score) {
			super(score);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.UpperStetchNoteRule#matchCondition
		 * (int, int)
		 */
		@Override protected boolean matchCondition(int d1, int d2) {
			return d1 > -4 && d1 < 0 && d2 > 0 && d2 < 4;
		}
	}

	static class MountainProgressNoteRule extends ApexInfo {
		protected double score1 = 0.;
		protected double score2 = 0.;
		protected double score3 = 0.;
		private int type;

		protected MountainProgressNoteRule(int type, double score1, double score2,
				double score3) {
			super(score2);
			this.type = type;
			this.score1 = score1;
			this.score2 = score2;
			this.score3 = score3;
		}

		protected void addApexInfoToNotes(NoteData n1, NoteData n2, NoteData n3) {
			n1.addApexScore(new MountainProgressNoteRule(1, score1, score2, score3));
			n2.addApexScore(new MountainProgressNoteRule(2, score1, score2, score3));
			n3.addApexScore(new MountainProgressNoteRule(3, score1, score2, score3));
		}

		@Override public void apply(Group group) {}

		/*
		 * (非 Javadoc)
		 * @see jp.crestmuse.mixtract.data.ApexInfo#apply(java.util.List, int,
		 * int)
		 */
		@Override public void apply(List<NoteData> notelist, int idx, int size) {
			if (idx < 2) {
				apply(notelist, idx + 1, size);
				return;
			}
			if (idx + 1 >= size)
				return;
			AbstractNoteData n1 = notelist.get(idx - 2);
			NoteData n2 = notelist.get(idx - 1);
			NoteData n3 = notelist.get(idx);
			NoteData n4 = notelist.get(idx + 1);
			int interval1 = interval(n1, n2);
			int interval2 = interval(n2, n3);
			int interval3 = interval(n3, n4);
			if (interval1 == REST || interval2 == REST || interval3 == REST)
				apply(notelist, idx + 1, size);
			if (matchCondition(interval1, interval2, interval3)) {
				addApexInfoToNotes(n2, n3, n4);
			}
			apply(notelist, idx + 1, size);
		}

		/*
		 * (非 Javadoc)
		 * @see jp.crestmuse.mixtract.data.ApexInfo#getScore()
		 */
		@Override protected double getScore() {
			switch (type) {
				case 1:
					return score1;
				case 2:
					return score2;
				case 3:
					return score3;
			}
			return score2;
		}

		protected boolean matchCondition(int d1, int d2, int d3) {
			return d1 > 0 && d2 > 0 && d3 < 0;
		}

		public void setType(int type) {
			this.type = type;
		}
	}
	/**
	 * <h1>SameAppoggiaturaRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/12/21
	 */
	public static class SameAppoggiaturaRule extends AppoggiaturaRule {

		/**
		 * @param score
		 */
		public SameAppoggiaturaRule(double score) {
			super(score);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.AppoggiaturaRule#getAppoggiaturaScore
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override public boolean getAppoggiaturaScore(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.duration() == n2.duration();
		}

	}
	/**
	 * <h1>SamePitchNoteRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/11/27
	 */
	static class SamePitchNoteRule extends SameTimeValueNoteRule {

		private SamePitchNoteRule(double val) {
			super(val);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.SameTimeValueNoteRule#matchCondition
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected boolean matchCondition(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.noteNumber() == n2.noteNumber();
		}

	}
	/**
	 * <h1>SameTimeValueNoteRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/11/27
	 */
	static class SameTimeValueNoteRule extends ApexInfo {
		/**
		 * @param score
		 */
		private SameTimeValueNoteRule(double score) {
			super(score);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * jp.crestmuse.mixtract.data.ApexInfo#apply(jp.crestmuse.mixtract.data
		 * .Group)
		 */
		@Override public void apply(Group group) {
			throw new UnsupportedOperationException(); // TODO 実装
		}

		/*
		 * (non-Javadoc)
		 * @see jp.crestmuse.mixtract.data.ApexInfo#apply(java.util.List, int,
		 * int)
		 */
		@Override public void apply(List<NoteData> notelist, int idx, int size) {
			if (idx + 1 >= size)
				return;
			apply(notelist, idx + 1, size);

			NoteData n1 = notelist.get(idx);
			AbstractNoteData n2 = notelist.get(idx + 1);
			if (matchCondition(n1, n2))
				n1.addApexScore(this);
		}

		/**
		 * @param n1
		 * @param n2
		 * @return
		 */
		protected boolean matchCondition(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.timeValue() == n2.timeValue();
		}

	}
	/**
	 * <h1>ShorterAppoggiaturaRule</h1>
	 *
	 * @author Mitsuyo Hashida & Haruhiro Katayose
	 *         <address>CrestMuse Project, JST</address>
	 *         <address>http://www.m-use.net/</address>
	 *         <address>hashida@kwansei.ac.jp</address>
	 * @since 2009/12/21
	 */
	public static class ShorterAppoggiaturaRule extends AppoggiaturaRule {

		/**
		 * @param score
		 */
		public ShorterAppoggiaturaRule(double score) {
			super(score);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.AppoggiaturaRule#getAppoggiaturaScore
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override public boolean getAppoggiaturaScore(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.duration() < n2.duration();
		}

	}
	static class SingleChordRule extends ApexInfo {

		public SingleChordRule(double score) {
			super(score);
		}

		@Override public void apply(Group group) {}

		@Override public void apply(List<NoteData> notelist, int idx, int size) {
			if (idx >= size)
				return;
			NoteData n1 = notelist.get(idx);
			n1.addApexScore(n1.chord().rule());
			apply(notelist, idx + 1, size);
		}

	}
	abstract static class SingleNoteRule extends ApexInfo {

		protected SingleNoteRule(double score) {
			super(score);
		}

		@Override public void apply(List<NoteData> notelist, int idx, int size) {}
	}
	public static class UpperProgressNoteRule extends UpperStetchNoteRule {

		protected UpperProgressNoteRule(double score) {
			super(score);
		}

		/*
		 * (非 Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.UpperStetchNoteRule#matchCondition
		 * (int, int)
		 */
		@Override protected boolean matchCondition(int d1, int d2) {
			return d1 < 4 && d1 > 0 && d2 < 4 && d2 > 0;
		}

		/*
		 * (非 Javadoc)
		 * @see
		 *
		 * jp.crestmuse.mixtract.data.ApexInfo.UpperStetchNoteRule#matchCondition
		 * (jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected boolean matchCondition(AbstractNoteData n1, AbstractNoteData n2) {
			int d = interval(n1, n2);
			return d > 0 && d <= 4;
		}
	}
	public static class UpperStetchNoteRule extends ApexInfo {

		protected UpperStetchNoteRule(double score) {
			super(score);
		}

		@Override public void apply(Group group) {}

		@Override public void apply(List<NoteData> notelist, int idx, int size) {
			if (idx + 2 >= size)
				return;
			AbstractNoteData n1 = notelist.get(idx);
			NoteData n2 = notelist.get(idx + 1);
			AbstractNoteData n3 = notelist.get(idx + 2);
			int d1 = interval(n1, n2);
			int d2 = interval(n2, n3);
			if (matchCondition(n1, n3) && matchCondition(d1, d2))
				n2.addApexScore(this);
			apply(notelist, idx + 1, size);
		}

		protected boolean matchCondition(int d1, int d2) {
			return d1 < 4 && d1 > 0 && d2 < 0 && d2 > -4;
		}

		/**
		 * @param n1
		 * @param n2
		 * @return
		 */
		protected boolean matchCondition(AbstractNoteData n1, AbstractNoteData n2) {
			return n1.noteNumber() == n2.noteNumber();
		}

	}
	static class ValleyProgressNoteRule extends MountainProgressNoteRule {

		protected ValleyProgressNoteRule(int type, double score1, double score2,
				double score3) {
			super(type, score1, score2, score3);
		}

		/*
		 * (非 Javadoc)
		 * @seejp.crestmuse.mixtract.data.ApexInfo.MountainProgressNoteRule#
		 * addApexInfoToNotes(jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected void addApexInfoToNotes(NoteData n1, NoteData n2,
				NoteData n3) {
			n1.addApexScore(new ValleyProgressNoteRule(1, score1, score2, score3));
			n2.addApexScore(new ValleyProgressNoteRule(2, score1, score2, score3));
			n3.addApexScore(new ValleyProgressNoteRule(3, score1, score2, score3));
		}

		/*
		 * (非 Javadoc)
		 * @seejp.crestmuse.mixtract.data.ApexInfo.MountainProgressNoteRule#
		 * matchCondition(int, int, int)
		 */
		@Override protected boolean matchCondition(int d1, int d2, int d3) {
			return d1 < 0 && d2 < 0 && d3 > 0;
		}
	}
	static class ZigZagProgressNoteRule1 extends MountainProgressNoteRule {

		protected ZigZagProgressNoteRule1(int type, double score1, double score2,
				double score3) {
			super(type, score1, score2, score3);
		}

		/*
		 * (非 Javadoc)
		 * @seejp.crestmuse.mixtract.data.ApexInfo.MountainProgressNoteRule#
		 * addApexInfoToNotes(jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData,
		 * jp.crestmuse.mixtract.data.NoteData)
		 */
		@Override protected void addApexInfoToNotes(NoteData n1, NoteData n2,
				NoteData n3) {
			n1.addApexScore(new ZigZagProgressNoteRule1(1, score1, score2, score3));
			n2.addApexScore(new ZigZagProgressNoteRule1(2, score1, score2, score3));
			n3.addApexScore(new ZigZagProgressNoteRule1(3, score1, score2, score3));
		}

		/*
		 * (非 Javadoc)
		 * @seejp.crestmuse.mixtract.data.ApexInfo.MountainProgressNoteRule#
		 * matchCondition(int, int, int)
		 */
		@Override protected boolean matchCondition(int d1, int d2, int d3) {
			return d1 < 0 && d2 > 0 && d3 < 0;
		}
	}
	static class ZigZagProgressNoteRule2 extends MountainProgressNoteRule {

		protected ZigZagProgressNoteRule2(int type, double score1, double score2,
				double score3) {
			super(type, score1, score2, score3);
		}

		@Override protected void addApexInfoToNotes(NoteData n1, NoteData n2,
				NoteData n3) {
			n1.addApexScore(new ZigZagProgressNoteRule2(1, score1, score2, score3));
			n2.addApexScore(new ZigZagProgressNoteRule2(2, score1, score2, score3));
			n3.addApexScore(new ZigZagProgressNoteRule2(3, score1, score2, score3));
		}

		@Override protected boolean matchCondition(int d1, int d2, int d3) {
			return d1 > 0 && d2 < 0 && d3 > 0;
		}
	}

	protected static final int REST = -1000;

	static ApexInfo SAME_TIMEVALUE_NOTE = new SameTimeValueNoteRule(1.);

	static ApexInfo SAME_PITCH_NOTE = new SamePitchNoteRule(1.);

	static ApexInfo LONGER_NOTE = new LongerNoteRule(1.);

	static ApexInfo HIGHER_NOTE = new HigherNoteRule(1.);

	static ApexInfo END_NOTE = new EndNoteRule(-1.);

	static ApexInfo BEGIN_NOTE = new BeginNoteRule(1.);

	static ApexInfo UPPER_STETCH_NOTE = new UpperStetchNoteRule(2.);

	static ApexInfo LOWER_STETCH_NOTE = new LowerStetchNoteRule(1.);

	static ApexInfo UPPER_PROGRESS_NOTE = new UpperProgressNoteRule(2.);

	static ApexInfo LOWER_PROGRESS_NOTE = new LowerProgressNoteRule(1.);

	static ApexInfo MOUNTAIN_PROGRESS = new MountainProgressNoteRule(2, 0., 1.,
			0.);

	static ApexInfo VALLEY_PROGRESS = new ValleyProgressNoteRule(2, 0., 2., 1.);

	static ApexInfo ZIGZAG_PROGRESS1 = new ZigZagProgressNoteRule1(2, 2., 1., 0.);

	static ApexInfo ZIGZAG_PROGRESS2 = new ZigZagProgressNoteRule2(2, 1., 2., 1.);

	static ApexInfo CHORD_CHANGE = new ChordChangeRule(1.);

	static ApexInfo CADENTZ_I = new CadentzIRule(4., 0.);

	static ApexInfo CADENTZ_I6 = new CadentI6Rule(2., 3.);

	static ApexInfo CADENTZ_VI = new CadentVIRule(2., 1.);

	static ApexInfo APPOGGIATURA_LONGER = new LongerAppoggiaturaRule(3.);
	static ApexInfo APPOGGIATURA_SAME = new SameAppoggiaturaRule(2.);
	static ApexInfo APPOGGIATURA_SHORTER = new ShorterAppoggiaturaRule(1.);
	/**
	 * @param eNDNOTE
	 * @param group
	 */
	static void applyRule(ApexInfo type, Group group) {
		type.apply(group);
	}

	/**
	 * @param type
	 * @param nlist
	 * @param i
	 * @param size
	 */
	static void applyRule(ApexInfo type, List<NoteData> notelist, int idx,
			int size) {
		type.apply(notelist, idx, size);
	}
	/** 頂点らしさを表すパラメータ */
	private double score;

	protected ApexInfo(double score) {
		super();
		this.score = score;
	}

	/**
	 * @param group
	 */
	protected abstract void apply(Group group);
	/**
	 * @param type
	 * @param notelist
	 * @param idx
	 * @param size
	 */
	protected abstract void apply(List<NoteData> notelist, int idx, int size);

	/**
	 * @return
	 */
	protected double getScore() {
		return score;
	}

	/**
	 * @param n1
	 * @param n2
	 * @return
	 */
	protected int interval(AbstractNoteData n1, AbstractNoteData n2) {
		if (n2.rest() || n1.rest())
			return REST;
		return n2.noteNumber() - n1.noteNumber();
	}

}
