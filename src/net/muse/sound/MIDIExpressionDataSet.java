package net.muse.sound;

import java.util.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import net.muse.misc.ConfigEditListener;
import net.muse.misc.MuseObject;
import net.muse.misc.Util;

public class MIDIExpressionDataSet extends MuseObject implements
		ConfigEditListener {

	/** Note が休符の場合の`音高'(-100) */
	protected static final int NOTE_REST = -100;

	/** NOTE ONの直後にOFFが来るのを避ける時間範囲 (ミリ秒) */
	private static int ON_OFF_RESOLUTION_TIME = 3; // milliseconds

	/** MIDIイベントが密集するのを避ける時間範囲（ミリ秒) */
	private static int EVENT_RESOLUTION_TIME = 3; // milliseconds

	/** ある時刻上に存在するMIDIイベントリスト */
	private final LinkedList<MIDINoteEvent> midiEventList;

	/** ある時刻上に存在する音符リスト */
	private final TimeEventMap timeEventMap;

	/** 基準拍数（1小節内の拍子） */
	private int baseBeat = 4;

	/** 基準テンポ (beat per minute) */
	private double baseBPM = 120;

	/** 基準テンポ (beat time) */
	private long baseBeatTime = 500;

	/** 楽曲データの全長(時間) */
	protected double maxLength = 0;

	/** ある時刻上に存在するテンポリスト */
	private final TreeMap<Double, List<Long>> tempoEventMap;

	/** 発音されているかどうかを追跡するboolean配列 */
	protected final boolean[] soundNotesStatus;

	/** 演奏開始時刻 */
	protected double selectTimeFrom = 0.;
	/** 演奏終了時刻 */
	protected double selectTimeEnd = 0.;

	/** 演奏データの総演奏時間長 */
	private double performanceLength = 0.;

	public static MIDIExpressionDataSet createMIDIExpressionDataSet(int tpb) {
		return new MIDIExpressionDataSet(tpb);
	}

	protected MIDIExpressionDataSet(int tpb) {
		super();
		setTicksPerBeat(tpb);
		timeEventMap = new TimeEventMap();
		tempoEventMap = new TreeMap<Double, List<Long>>();
		midiEventList = new LinkedList<MIDINoteEvent>();
		soundNotesStatus = new boolean[127];
		for (int i = 0; i < 127; i++)
			soundNotesStatus[i] = false;
	}

	/**
	 * サンプルの音列を生成します．
	 *
	 * @param console
	 */
	public void createSampleNotes() {
		/*
		 * =====================================================================
		 * =
		 * =
		 * テスト音符作成
		 * =====================================================================
		 * =
		 * =
		 */
		timeEventMap.clear();
		tempoEventMap.clear();
		addNoteToTimeEventMap("ON", 1, 1.0, 60, 50, 0.5); // 小節，拍，MIDIノートナンバー，音長（1拍1.0
		// ）
		addNoteToTimeEventMap("ON", 1, 1.6, 62, 60, 0.5);
		addNoteToTimeEventMap("ON", 1, 2.0, 64, 70, 0.5);
		addNoteToTimeEventMap("ON", 1, 2.5, 65, 80, 0.5);
		addNoteToTimeEventMap("ON", 1, 3.0, 67, 90, 1.0);
		addNoteToTimeEventMap("ON", 1, 4.0, 74, 64, 1.0);
		addNoteToTimeEventMap("ON", 2, 1.0, 72, 64, 1.0);
		addTempoEventMap(1, 1.0, 400);
		addTempoEventMap(1, 2.0, 600);
		addTempoEventMap(1, 3.0, 400);
		addTempoEventMap(1, 4.0, 600);
		addTempoEventMap(1, 5.0, 400);
		addNoteOffEvents();
		System.out.println(timeEventMap.toString());
		System.out.println("Time event map: (maxlength: " + maxLength + ")");
		setDeltaTimes();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.jpope.ConfigEditListener#editConfig(java.lang.String,
	 * java.lang.String)
	 */
	public void editConfig(String key, String value) {

		if (key.equals("DEBUG")) {
			DEBUG = Boolean.parseBoolean(value);
			// } else if (key.equals("TICKSPERBEAT")) {
			// TICKS_PER_BEAT = Integer.parseInt(value);
		} else if (key.equals("OFF_VELOCITY")) {
			setDefaultOffVelocity(Integer.parseInt(value));
		} else if (key.equals("EVENT_RESOLUTION_TIME")) {
			EVENT_RESOLUTION_TIME = Integer.parseInt(value);
		} else if (key.equals("ON_OFF_RESOLUTION_TIME")) {
			ON_OFF_RESOLUTION_TIME = Integer.parseInt(value);
		} else {
			return;
		}

		System.out.println(key + " is set to " + value);

	}

	public final List<MIDINoteEvent> getMidiEventList() {
		return midiEventList;
	}

	/**
	 * 時刻順にソートされたノートイベントをMIDIイベントリストとしてセットします． <br>
	 */
	public synchronized void setMIDIEventList() {
		midiEventList.clear();
		if (DEBUG)
			System.err.println("==== Set MIDI Event list ====");
		for (double t : timeEventMap.keySet()) {
			if (t < selectTimeFrom || t > selectTimeEnd)
				continue;
			for (MIDINoteEvent n : timeEventMap.get(t)) {
				// MIDIイベントの作成
				butler().printConsole(n.toString());
				// if (!(n instanceof MIDINoteEvent))
				// continue;
				int status = ShortMessage.SYSTEM_RESET;
				final int noteNum = n.getNoteNum();
				if (n.getMessageType().equals("ON")) {
					status = ShortMessage.NOTE_ON;
					if (noteNum != NOTE_REST)
						soundNotesStatus[noteNum] = true;

				} else if (n.getMessageType().equals("OFF")) {
					status = ShortMessage.NOTE_OFF;
					if (noteNum != NOTE_REST && soundNotesStatus[noteNum]) {
						soundNotesStatus[noteNum] = false;
					}
				} else {
					System.err.println("??? status=" + status);
				}

				ShortMessage msg = new ShortMessage();
				try {
					if (noteNum != NOTE_REST)
						msg.setMessage(status, noteNum, n.getVelocity());
				} catch (InvalidMidiDataException e) {
					e.printStackTrace();
				}

				// 時刻ソートされたイベント配列へ登録
				n.setMessage(msg);
				midiEventList.add(n);

				if (DEBUG)
					System.err.println("   ontime " + n.getOnset() + " note "
							+ msg.getData1() + " " + n.getMessageType()
							+ " length " + n.getLength());
			}
		}
		expandMIDIEvents();
		if (DEBUG)
			System.err.println("==== (end) MIDI Event list ====");
	}

	protected synchronized void addNoteOffEvents() {
		TimeEventMap tmp = new TimeEventMap();
		for (double key : timeEventMap.keySet()) {
			for (MIDINoteEvent n : timeEventMap.get(key)) {
				if (n.getMessageType().equals("OFF"))
					continue;
				double offsetTime = Util.castDouble(n.getBeatOnset() + n
						.getLength());
				double b = offsetTime - n.getMeasure();
				if (timeEventMap.containsKey(offsetTime)) {
					LinkedList<MIDINoteEvent> list = timeEventMap.get(
							offsetTime);
					list.add(0, new MIDINoteEvent("OFF", n.getMeasure(), b, n
							.getNoteNum(), getDefaultOffVelocity(), 0.,
							getBaseBeat()));
				} else if (tmp.containsKey(offsetTime)) {
					List<MIDINoteEvent> list = tmp.get(offsetTime);
					list.add(0, new MIDINoteEvent("OFF", n.getMeasure(), b, n
							.getNoteNum(), getDefaultOffVelocity(), 0.,
							getBaseBeat()));
				} else {
					LinkedList<MIDINoteEvent> list = new LinkedList<MIDINoteEvent>();
					list.add(0, new MIDINoteEvent("OFF", n.getMeasure(), b, n
							.getNoteNum(), getDefaultOffVelocity(), 0.,
							getBaseBeat()));
					tmp.put(offsetTime, list);
				}
				if (maxLength < offsetTime)
					maxLength = offsetTime;
			}
		}
		timeEventMap.putAll(tmp);
	}

	protected synchronized void addNoteToTimeEventMap(String status,
			int measure, double beat, int notenum, int vel, double length) {
		int m = measure - 1; // ゼロ始まりにする
		double b = Util.castDouble(beat - 1); // ゼロ始まりにする
		double onset = m * baseBeat + b;
		LinkedList<MIDINoteEvent> list = (timeEventMap.containsKey(onset))
				? timeEventMap.get(onset) : new LinkedList<MIDINoteEvent>();
		list.add(new MIDINoteEvent(status, m, b, notenum, vel, length,
				getBaseBeat()));
		timeEventMap.put(onset, list);
		if (maxLength < onset + length) {
			maxLength = onset + length;
		}
	}

	protected final int getBaseBeat() {
		return baseBeat;
	}

	// private final double getSelectTimeEnd() {
	// return selectTimeEnd;
	// }

	protected final double getMaxLength() {
		return maxLength;
	}

	/**
	 * 楽曲の演奏時間長を取得します。
	 *
	 * @return performanceLength
	 */
	protected double getPerformanceLength() {
		return performanceLength;
	}

	protected TimeEventMap getTimeEventMap() {
		return timeEventMap;
	}

	/**
	 * 楽曲のテンポマップを設定します．
	 */
	protected void setDefaultTempo() {
		tempoEventMap.clear();
		int len = (int) maxLength;
		for (int i = 0; i < len; i++) {
			List<Long> list = new ArrayList<Long>();
			list.add(baseBeatTime);
			tempoEventMap.put((double) i, list);
		}
	}

	protected synchronized void setDeltaTimes() {
		int beatLength = (int) maxLength;
		int beatsum = 0;
		// selectTimeFrom = selectTimeEnd = 0;// 現在の累積時刻
		if (DEBUG)
			System.out.println("======== setDeltaTimes ============");
		for (int currentBeat = timeEventMap.firstKey()
				.intValue(); currentBeat < beatLength; currentBeat++) {
			int tempo = (int) getBaseBeatTime(); // TODO tempoは常に曲冒頭の値
			for (double t : timeEventMap.keySet()) {
				if (t < currentBeat)
					continue;
				if (t >= currentBeat + 1)
					break;
				for (MIDINoteEvent ev : timeEventMap.get(t)) {
					if (ev.getOnset() >= 0)
						continue;

					ev.setOnset((int) (beatsum + tempo * (t - currentBeat)));
					// onset が積分値
					// tempo をいずれ次の(予測)拍に変える

					if (DEBUG)
						System.out.println("mstime=" + ev.getOnset() + " note "
								+ ev.getNoteNum() + " " + ev.getMessageType()
								+ " onset=" + ev.getOnset() + " length=" + ev
										.getLength());
				}
			}
			setPerformanceLength(selectTimeEnd = beatsum += tempo);
		}
		/*
		 * 最後のOFFイベント TODO 上のループのこぴぺ
		 */
		if (timeEventMap.containsKey((double) beatLength)) {
			for (MIDINoteEvent ev : timeEventMap.get((double) beatLength)) {
				if (ev.getOnset() >= 0)
					continue;
				ev.setOnset(beatsum);
				// onset が積分値
				// tempomap[i] が次の(予測)拍に変わる
				if (DEBUG)
					System.out.println("time=" + ev.getOnset() + " note " + ev
							.getNoteNum() + " " + ev.getMessageType()
							+ " onset=" + ev.getOnset() + " length=" + ev
									.getLength());
			}
		}
		if (DEBUG)
			System.out.println("======== (end) setDeltaTimes ============");

	}

	/**
	 * @param maxLength the maxLength to set
	 */
	protected void setMaxLength(double maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @param selectTimeEnd the selectTimeEnd to set
	 */
	protected void setSelectTimeEnd(double selectTimeEnd) {
		this.selectTimeEnd = selectTimeEnd;
	}

	/**
	 * @param selectTimeFrom the selectTimeFrom to set
	 */
	protected void setSelectTimeFrom(double selectTimeFrom) {
		this.selectTimeFrom = selectTimeFrom;
	}

	protected synchronized void switchOFFEvents() {
		if (DEBUG)
			System.out.println("======== switchOFFEvents ============");
		for (double t : timeEventMap.keySet()) {
			if (t < selectTimeFrom || t > selectTimeEnd)
				continue;
			for (MIDINoteEvent ev : timeEventMap.get(t)) {
				if (!ev.getMessageType().equals("ON"))
					continue;
				while (true) {
					MIDINoteEvent futureOffEvent = getNoteOffEvent(ev, t,
							ON_OFF_RESOLUTION_TIME);
					if (futureOffEvent == null)
						break;

					futureOffEvent.setOnset(ev.getOnset()
							- ON_OFF_RESOLUTION_TIME);
				}
			}
		}
		if (DEBUG) {
			for (double t : timeEventMap.keySet()) {
				for (MIDINoteEvent ev : timeEventMap.get(t)) {
					System.out.println("time=" + ev.getOnset() + " note " + ev
							.getNoteNum() + " " + ev.getMessageType()
							+ " onset=" + ev.getOnset() + " length=" + ev
									.getLength());
				}
			}
			System.out.println("======== (end) switchOFFEvents ============");
		}
	}

	// private void sortEventsByTime(Object musicdata) {}

	private void addTempoEventMap(int measure, double beat, long tempo) {
		int m = measure - 1;
		double b = (long) ((beat - 1.) * 1000);
		double onset = (m * baseBeat + b);
		List<Long> list = (tempoEventMap.containsKey(onset)) ? tempoEventMap
				.get(onset) : new ArrayList<Long>();
		list.add(tempo);
		tempoEventMap.put(onset, list);
	}

	/**
	 * MIDIイベントの発行時刻が密集していた場合，TIME_REVOLUTION分の間隔をあける
	 */
	protected synchronized void expandMIDIEvents() {
		if (DEBUG)
			System.out.println("======== expandMIDIEvents ============");
		MIDINoteEvent pre = null;
		Iterator<MIDINoteEvent> it = midiEventList.iterator();
		while (it.hasNext()) {
			MIDINoteEvent ev = it.next();
			if (pre != null && ev.getOnset() - pre
					.getOnset() < EVENT_RESOLUTION_TIME) {
				ev.setOnset(pre.getOnset() + EVENT_RESOLUTION_TIME);
			}
			if (DEBUG)
				System.out.println("time=" + ev.getOnset() + " note " + ev
						.getNoteNum() + " " + ev.getMessageType() + " onset="
						+ ev.getOnset() + " length=" + ev.getLength());
			pre = ev;
		}
		if (DEBUG)
			System.out.println("======== (end) expandMIDIEvents ============");

	}

	protected final double getBaseBeatTime() {
		return baseBeatTime;
	}

	private final double getBPM() {
		return baseBPM;
	}

	/**
	 * @param preTime
	 * @param currentTime
	 * @return
	 */
	@SuppressWarnings("unused") private final List<MidiMessage> getMidiMessageList(
			double preTime, double currentTime) {
		List<MidiMessage> list = new LinkedList<MidiMessage>();
		for (MIDINoteEvent ev : midiEventList) {
			if (ev.getOnset() >= currentTime)
				break;
			if (ev.getOnset() < preTime)
				continue;
			list.add(ev.getMidiMessage());
		}

		return list;
	}

	private MIDINoteEvent getNoteOffEvent(MIDINoteEvent onEvent, double from,
			int res) {
		for (double t : timeEventMap.keySet()) {
			if (t < from)
				continue;
			if (t > from + Util.millisecondToBeatLength(res, getBPM()))
				break;
			for (MIDINoteEvent n : timeEventMap.get(t)) {
				if (n.getMessageType().equals("ON"))
					continue;

				long diff = n.getOnset() - onEvent.getOnset();
				if (n.getNoteNum() == onEvent.getNoteNum() && diff > -res
						&& diff < res) {
					return n;
				}
			}
		}
		return null;
	}

	/**
	 * @param onsetFrom
	 * @param onsetTo
	 * @return
	 */
	private TimeEventMap getTimeEventMapIn(double onsetFrom, double onsetTo) {
		SortedMap<Double, LinkedList<MIDINoteEvent>> map = timeEventMap.subMap(
				onsetFrom, onsetTo);
		TimeEventMap tmap = new TimeEventMap();
		for (double onset : map.keySet()) {
			tmap.put(onset - onsetFrom, map.get(onset));
		}
		return tmap;
	}

	public void setBaseBeat(int baseBeat, int beatType) {
		this.baseBeat = (int) (baseBeat / (beatType / 4.));
	}

	/**
	 * 基準テンポ(beat time)を設定します．このメソッドを実行すると，基準テンポ(BPM)も同時に計算されます．
	 * TODO 使ってない 2011.8.31
	 *
	 * @param beatTime 設定する beat time
	 */
	public void setBaseBeatTime(double beatTime) {
		this.baseBeatTime = (int) beatTime;
		baseBPM = Util.beatTimeToBPM(beatTime);
	}

	/**
	 * 基準テンポ(beat per minute)を設定します．このメソッドを実行すると，基準テンポ(beat time)も同時に計算されます．
	 *
	 * @param bpm 設定する BPM
	 */
	private void setBPM(double bpm) {
		this.baseBPM = bpm;
		baseBeatTime = (int) Util.bpmToBeatTime(bpm);
	}

	/**
	 * @param performanceLength 設定する performanceLength
	 */
	protected void setPerformanceLength(double performanceLength) {
		this.performanceLength = performanceLength;
	}

	/**
	 * @param eVENT_RESOLUTION_TIME セットする eVENT_RESOLUTION_TIME
	 */
	public static void setEventResolutionTime(int eVENT_RESOLUTION_TIME) {
		EVENT_RESOLUTION_TIME = eVENT_RESOLUTION_TIME;
	}

	/**
	 * @return eVENT_RESOLUTION_TIME
	 */
	public static int getEventResolutionTime() {
		return EVENT_RESOLUTION_TIME;
	}

}
