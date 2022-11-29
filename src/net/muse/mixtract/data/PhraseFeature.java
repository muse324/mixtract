package net.muse.mixtract.data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.data.AbstractPhraseFeature;
import net.muse.data.NoteData;

/**
 * <h1>PhraseFeature</h1>
 * <p>
 * 旋律の特徴情報を格納するクラスです。
 *
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2008/05/02
 */
public class PhraseFeature extends AbstractPhraseFeature {
	/** subphrase A の音高推移の傾き */
	private double slopeA;

	/** subphrase B の音高推移の傾き */
	private double slopeB;

	/** phrase 中の subphrase A の長さの比率 (length(A) / length(X) ) * */
	private double ratioOfMalodyA;

	/** subphrase A の最後の音(top note)と subphrase B の最初の音の音高差 */
	private double pitchInterval;

	/** phrase X の時間長 */
	private double timeValue;

	private ArrayList<Integer> rhythmVector;// リズムベクトル

	/**
	 * 旋律外形の後半の最初の音符
	 */
	private Note latterFirstNote;

	/**
	 * 旋律外形の前半の最後の音符
	 */
	private Note formerLastNote;

	private NoteData formerLastNoteData;

	private NoteData latterFirstNoteData;

	/**
	 * グループの旋律概形とリズムベクトルを求めます． グループの旋律概形は，前半開始音から頂点音の消音時刻までと，
	 * 後半開始音から終了音の消音時刻までをそれぞれ結ぶ 2本の一次直線であらわされます．
	 *
	 * @param g
	 *            音列グループ
	 */
	public PhraseFeature(MXGroup group) {
		super(group);
		setLineParameters();
		makeRhythmVector();
	}

	/* (非 Javadoc)
	 * @see net.muse.data.AbstractPhraseFeature#group()
	 */
	@Override
	public MXGroup group() {
		return (MXGroup) group;
	}

	public final double getCt1() {
		return slopeA;
	}

	public final double getCt2() {
		return slopeB;
	}

	public final double getCt3() {
		return ratioOfMalodyA;
	}

	public final double getCt4() {
		return pitchInterval;
	}

	public final double getCt5() {
		return timeValue;
	}

	/**
	 * 旋律外形の前半の最後の音符を返します．
	 *
	 * @return formerLastNote
	 */
	public final Note getFormerLastNote() {
		return formerLastNote;
	}

	/**
	 * 後半の最初の音符を返します．
	 *
	 * @return latterFirstNote
	 */
	public final Note getLatterFirstNote() {
		return latterFirstNote;
	}

	/**
	 * @return rhythmVector
	 */
	public final ArrayList<Integer> getRhythmVector() {
		return rhythmVector;
	}

	public List<Double> makeCtArray() {
		List<Double> list = new LinkedList<Double>();
		list.add(slopeA);
		list.add(slopeB);
		list.add(ratioOfMalodyA);
		list.add(pitchInterval);
		list.add(timeValue);
		return list;
	}

	/**
	 * グループのリズムベクトルを求めます．
	 * <p>
	 * Mixtract describes the surface rhythm as a vector. The vector element of
	 * the onset (the unit is the shortest note) is 1 and of the non-onset is 0.
	 * For example, the score of "Kira-Kira-Boshi" (C-C-G-G-A-A-G-rest,
	 * F-F-E-E-D-D-C) is described as {1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1,
	 * 1, 0}.
	 * <p>
	 * Mixtract can evaluate the melodySimilarity of the target melody fragment
	 * and the reference melody fragment even if their lengths are not the same.
	 * When the lengths are different, elements 0 will be added to the front or
	 * end of the shorter vector until the length becomes the same as the longer
	 * one. The cosine distance is exhaustively calculated. The system chooses
	 * the largest cosine-distance obtained by this procedure as the
	 * melodySimilarity of the surface rhythms simr.
	 *
	 * @param target
	 *            音列グループ
	 */
	private void makeRhythmVector() {
		// int unit = getMinimumDuration(group().getBeginGroupNote());
		// int length = (int) group().length();
		//
		// ArrayList<Integer> vec = new ArrayList<Integer>();
		// Note n = group().getBeginningNote(); // 開始音
		// TreeView<Note> tv = group().getNotes();
		// tv.jumpTo(n);// 最初が休符かもしれないから
		// int onset = 0;
		// int val = 0;
		// for (int t = 0; t < length; t += unit) {
		// if (n != null && onset == t) {
		// onset += n.actualDuration(getTicksPerBeat());
		// try {
		// // 和音（重音）を持っている場合
		// val = 1 + n.chordNotes().size();
		// } catch (NullPointerException e) {
		// val = 1;
		// }
		// n = (tv.hasElementsAtNextTime())
		// ? tv.getFirstElementAtNextTime()
		// : null;
		// } else {
		// val = 0;
		// }
		// vec.add(val);
		// }
		// rhythmVector = vec;
		// testPrintln(rhythmVector.toString());
		// noteLevel = group().getMinimumNoteLevel();
		// } catch (UnexpectedException e) {
		// e.printStackTrace();
		// }
	}

	/**
	 * グループの音高推移から，旋律概形のパラメタを取得します．
	 * <p>
	 * There are five basic parameters of melodic contour (slopeA, ... ,
	 * timeValue).
	 * <ul>
	 * <li>slopeA and slopeB are inclinations of two line segments, and
	 * <li>ratioOfMalodyA is the ratio of the length of the former line segment
	 * to the latter.
	 * <li>pitchInterval is the pitch difference between the last note of the
	 * former line segment and the first note the latter line segment.
	 * <li>timeValue is the length of the whole melody fragment.
	 * </ul>
	 * Parameters (slopeA, ... , pitchInterval) are automatically calculated
	 * using least squares fitting. If the user regards the boundary suggested
	 * by the automatic fitting to be unsatisfactory, he/she can manually edit
	 * the position by using the GUI.
	 *
	 * @param g
	 *            音列グループ
	 */
	private synchronized void setLineParameters() {

		/*
		 * 後半最初の音符を取得する。グループがもし頂点音を保有している場合は頂点音を、
		 * そうでなければ時間長の半分の位置にある音符を格納する。
		 */
		latterFirstNoteData = (group().hasTopNote()) ? group().getTopNote()
				 : group().getCenterGroupNote();

		// 前半最後の音符を取得
		formerLastNoteData = latterFirstNoteData.previous();

		double st = group().onsetInTicks();
		double tp = formerLastNoteData.offset();
		double formarLength = (tp - st) / getTicksPerBeat();
		slopeA = (formerLastNoteData.noteNumber() - group().getBeginNote()
				.noteNumber()) / formarLength;
		double ed = group().getEndNote().offset();
		double latterLength = ed - latterFirstNoteData.onset()
				/ getTicksPerBeat();
		slopeB = (group().getEndNote().noteNumber()
				- latterFirstNoteData.noteNumber()) / latterLength;
		timeValue = (ed - st) / getTicksPerBeat();
		ratioOfMalodyA = formarLength / timeValue;
		pitchInterval = latterFirstNoteData.noteNumber() - formerLastNoteData
				.noteNumber();

		butler().printConsole("slopeA=" + slopeA);
		butler().printConsole("slopeB=" + slopeB);
		butler().printConsole("ratioOfMalodyA=" + ratioOfMalodyA);
		butler().printConsole("pitchInterval=" + pitchInterval);
		butler().printConsole("timeValue=" + timeValue);
	}
}
