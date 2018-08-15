package net.muse.sound;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.sound.midi.*;

import net.muse.data.NoteScheduleEvent;
import net.muse.mixtract.sound.MixtractMIDIController;

/**
 * currentTime 以前のMIDIメッセージをひとつ取得し， MIDIデバイスに送信します．
 * MIDIイベントリストは時刻順にソートされていることが
 * 前提条件です．
 *
 * @author Mitsuyo Hashida
 */
public class ThreadPlayer extends Thread implements MusicPlayer {
	/** ループを回す時間間隔（ミリ秒） */
	private static final int RUNNING_TIME_INTERVAL = 3;

	private MIDIController controller;

	/** runメソッドのループを継続する判定値 */
	private boolean isRunning = false;

	private LinkedList<NoteScheduleEvent> midiEventList;
	private int[] midiPrograms = null;

	/**
	 * 発音中であるかどうかを記憶するイベントバッファ
	 */
	private Boolean soundNotesStatus[];

	private double[] volume = null;

	/**
	 * @param list
	 * @param midiProgram
	 * @param controller TODO
	 */
	public ThreadPlayer(LinkedList<NoteScheduleEvent> list,
			MIDIController controller) {
		this.controller = controller;
		midiEventList = list;
		soundNotesStatus = new Boolean[127];
		for (int i = 0; i < 127; i++)
			soundNotesStatus[i] = false;
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#back()
	 */
	public void back() {}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#close()
	 */
	public void close() {}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#play()
	 */
	public void play() {
		start();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#readSMF(java.io.File)
	 */
	public void readSMF(File file) throws InvalidMidiDataException, IOException {}

	@Override
	public void run() {
		isRunning = true;

		// ループ直前の現在時刻を取得しておく
		double offsetTime = System.currentTimeMillis();
		if (midiEventList.size() > 0)
			offsetTime -= midiEventList.getFirst().onset();

		System.out.println("===== start playing =====");
		if (MixtractMIDIController.isDebug()) {
			for (NoteScheduleEvent ev : midiEventList) {
				System.out.println(ev);
			}
		}
		setProgramChanges();
		while (true) {
			try {
				Thread.sleep(RUNNING_TIME_INTERVAL); // 実行間隔を空ける
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}

			/* 現在時刻を取得 */
			double currentTime = System.currentTimeMillis() - offsetTime;

			/* ループ終了条件 */
			if (!isRunning) {
				// 停止命令が出た
				break;
			}
			if (midiEventList.size() <= 0) {
				// 最後まで再生したら1.5秒後に終了
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("midimessage finished.");
				this.controller.notifyStopPlaying();
				break;
			}
			if (MixtractMIDIController.isDebug())
				System.out.println("#midi current time = " + currentTime);

			/*
			 * 現在時刻より前にあるMIDIイベントリストの最初のイベントを取得します．<br>
			 * 取得したイベントはMIDIイベントリストから削除されます．
			 */
			ShortMessage msg = (ShortMessage) getMidiMessage(currentTime);
			if (msg == null) {
				if (MixtractMIDIController.isDebug())
					System.err.println("msg musicdata is null at time "
							+ currentTime);
			} else {
				int notenum = msg.getData1();
				if (msg.getStatus() == ShortMessage.NOTE_ON) {
					soundNotesStatus[notenum] = true;
					// velocity をパート別の調整する
//					int vel = (int) (msg.getData2() * volume[msg.getChannel()]);
//					try {
//						msg.setMessage(msg.getStatus(), msg.getChannel(), msg
//								.getData1(), vel);
//					} catch (InvalidMidiDataException e) {
//						e.printStackTrace();
//					}
				} else if (msg.getStatus() == ShortMessage.NOTE_OFF) {
					// if (!soundNotesStatus[msg.getData1()])
					// continue;
					soundNotesStatus[notenum] = false;
				}
				this.controller.sendMessage(msg);
				msg = null;
			}
		}
	}

	public void setMIDIProgram(int[] midiPrograms, double[] dynamics) {
		this.midiPrograms = new int[midiPrograms.length];
		this.volume = new double[midiPrograms.length];
		for (int i = 0; i < midiPrograms.length; i++) {
			this.midiPrograms[i] = midiPrograms[i];
			this.volume[i] = dynamics[i] = (i == 0 ? 1.5 : 0.6);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#stopPlay(net.muse.sound.MIDIController)
	 */
	public void stopPlay(MIDIController synthe) {
		isRunning = false;
		sendAllSoundOff(synthe);
	}

	/**
	 * 現在時刻より前にあるMIDIイベントリストの最初のイベントを取得します．<br>
	 * 取得したイベントはMIDIイベントリストから削除されます．
	 *
	 * @param currentTime
	 * @return
	 */
	final MidiMessage getMidiMessage(double currentTime) {
		if (midiEventList.size() > 0
				&& midiEventList.peekFirst().onset() < currentTime)
			return midiEventList.pollFirst().getMidiMessage();
		return null;
	}

	void resetReceiver() throws InterruptedException {
		System.out.println("synthesizer reset...");
		join(1000);
	}

	/**
	 * 鳴っているすべての音を消音します．
	 */
	void sendAllSoundOff(MIDIController synthe) {
		System.out.println("all sound off");
		ShortMessage msg = new ShortMessage();
		try {
			// コントロールチェンジの All Sound Off (120) を使用
			msg.setMessage(ShortMessage.CONTROL_CHANGE, 120, 0);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
		// 全ての音を消します。
		for (int i = 0; i < 127; i++) {
			try {
				msg.setMessage(ShortMessage.NOTE_OFF, i, 127);
			} catch (InvalidMidiDataException e) {
				e.printStackTrace();
			}
			synthe.sendMessage(msg);
		}
	}

	private void setProgramChanges() {
		for (int i = 0; i < midiPrograms.length; i++) {
			ShortMessage msg = new ShortMessage();
			try {
				msg.setMessage(ShortMessage.PROGRAM_CHANGE, i, midiPrograms[i],
						0);
				controller.sendMessage(msg);
			} catch (InvalidMidiDataException e) {}
		}
	}
}
