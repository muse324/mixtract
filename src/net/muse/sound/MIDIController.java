package net.muse.sound;

import java.util.ArrayList;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;

import net.muse.misc.MuseObject;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/03/05
 */
public abstract class MIDIController extends MuseObject implements
		MIDIEventListener {

	private static final int MIDI_TIMESTAMP = -1;
	/** デフォルトのMIDIチャンネル(0)に指定するインストゥルメント番号 */
	private static final int DEFAULT_INSTRUMENT = 0;
	/** デフォルトのMIDIデバイス名（Windows） */
	private static final String DEFAULT_DEVICENAME_WINDOWS = "Microsoft GS Wavetable SW Synth";
	/** デフォルトのMIDIデバイス名（Mac） */
	public static final String DEFAULT_DEVICENAME_MACOSX = "Java Sound Synthesizer";
	public static final String DEFAULT_MIDIDEVICE_MACOSX = null;
	/** 内部音源 */
	private MidiDevice device;
	/**
	 * 音源や外部のMIDIデバイスがMIDIメッセージを受け取る窓口．
	 * ここでは内部音源のMIDIメッセージ受け取りに使用します．
	 */
	private Receiver receiver;
	protected ArrayList<MIDIEventListener> midiEventListenerList = new ArrayList<MIDIEventListener>();
	private MidiDevice.Info[] deviceList;
	protected int selectedIndex = -1;

	/**
	 * @return selectedIndex
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * @param selectedIndex セットする selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	public MIDIController() {
		this.deviceList = MidiSystem.getMidiDeviceInfo();
	}

	/**
	 * @param mainFrame
	 */
	public void addMidiEventListener(MIDIEventListener l) {
		getMidiEventListenerList().add(l);
	}

	/**
	 * MIDIデバイスをクローズします．
	 * <p>
	 * プログラム終了時，MIDIデバイスは明示的にクローズされる必要があります．し忘れると，
	 * プログラムが終了してもデバイスがメモリ空間から開放されないため， 他プログラムで使えなくなります．
	 * もしマシンのアドミニストレータ権限を持っていないユーザでMIDIデバイスをクローズし損ねた場合，
	 * 最も手っ取り早い回復方法はオペレーティングシステムを再起動することです．
	 */
	public void close() {
		if (receiver != null)
			receiver.close();
		if (device != null)
			device.close();
	}

	/**
	 * 指定されたノートナンバーを消音します．
	 *
	 * @param noteNumber
	 */
	public void noteOff(int noteNumber) {
		try {
			if (noteNumber != MIDIExpressionDataSet.NOTE_REST) {
				ShortMessage message = new ShortMessage();
				message.setMessage(ShortMessage.NOTE_OFF, noteNumber, 127);
				sendMessage(message);
			}
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 指定されたノートナンバーを指定された強さ(velocity)で発音します．
	 *
	 * @param noteNumber
	 * @param velocity
	 */
	public void noteOn(int noteNumber, int velocity) {
		try {
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.NOTE_ON, noteNumber, velocity);
			sendMessage(message);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param filename
	 */
	public void notifyStartPlaying(String filename) {
		for (MIDIEventListener l : getMidiEventListenerList()) {
			l.startPlaying(filename);
		}
	}

	/**
	 * 演奏が停止されたことを各オブジェクトに通知します．
	 *
	 * @param groupList
	 */
	public void notifyStopPlaying() {
		for (MIDIEventListener l : getMidiEventListenerList()) {
			if (l != null)
				l.stopPlaying();
		}
	}

	/**
	 * MIDIデバイスにMIDIメッセージを送信します．
	 *
	 * @param message
	 */
	public synchronized void sendMessage(MidiMessage message) {
		receiver.send(message, MIDI_TIMESTAMP);
		if (message instanceof ShortMessage) {
			ShortMessage msg = (ShortMessage) message;
			if (isDebug())
				System.out.println(String.format(
						"cmd %d channel %d data1 = %d data2 = %d", msg
								.getCommand(), msg.getChannel(), msg.getData1(),
						msg.getData2()));
		} else if (message instanceof MetaMessage) {
			MetaMessage msg = (MetaMessage) message;
			if (isDebug())
				System.out.println("cmd type = " + msg.getType() + " data1 = "
						+ msg.getData()[0] + " data2 = " + msg.getData()[1]
						+ " data3 = " + msg.getData()[2]);
		}
	}

	/**
	 * @return the midiEventListenerList
	 */
	public ArrayList<MIDIEventListener> getMidiEventListenerList() {
		return midiEventListenerList;
	}

	public void openMidiDevice(String deviceName) {
		if (isDebug())
			checkAllMidiDevices();
		if (device != null)
			close();
		try {
			// ↓ MIDIを鳴らすためのデバイス device を取得。詳しくは関数の実装を見てね。
			this.device = getInternalSoftwareSynthesizer(deviceName);
			if (!device.isOpen()) {
				device.open();
				System.out.println(String.format("[%d] %s is opened.",
						selectedIndex, device.getDeviceInfo().getName()));
			}
		} catch (MidiUnavailableException e) {
			e.printStackTrace();
			device.close();
			return;
		}

		// MIDIメッセージを受け取る窓口をMIDI音源側に作ります。
		try {
			this.receiver = device.getReceiver();
		} catch (MidiUnavailableException e) {
			try {
				this.receiver = MidiSystem.getReceiver();
			} catch (MidiUnavailableException e1) {
				e1.printStackTrace();
				return;
			}
		}
		try {
			// MIDI音源の楽器の番号を指定します。（プログラムチェンジ）
			ShortMessage message = new ShortMessage();
			message.setMessage(ShortMessage.PROGRAM_CHANGE, 0,
					DEFAULT_INSTRUMENT, 0);
			sendMessage(message);
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * Javaから利用できるMIDIデバイスを一覧する．
	 */
	private void checkAllMidiDevices() {
		Info[] dvlist = getDeviceList();
		String str = "Java system found " + dvlist.length + " MIDI devices.\n";
		for (int i = 0; i < dvlist.length; i++) {
			str += "Midi device " + i + "\n";
			str += "  Description:" + dvlist[i].getDescription() + "\n";
			str += "  Name:" + dvlist[i].getName() + "\n";
			str += "  Vendor:" + dvlist[i].getVendor() + "\n";
			try {
				MidiDevice device = MidiSystem.getMidiDevice(dvlist[i]);
				if (device instanceof Sequencer) {
					str += "  *** This is Sequencer.\n";
				}
				if (device instanceof Synthesizer) {
					str += "  *** This is Synthesizer.\n";
				}
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
		}
		System.out.println(str);
		log().println(str);
	}

	/**
	 * 全MIDIデバイスのうち，名前がdeviceName と一致するものを探して そのデバイスを返します。
	 *
	 * @param deviceName
	 * @return
	 * @throws MidiUnavailableException
	 */
	private MidiDevice getInternalSoftwareSynthesizer(String deviceName)
			throws MidiUnavailableException {
		MidiDevice.Info[] info = MidiSystem.getMidiDeviceInfo();
		int defaultIndex = 0;
		for (int i = 0; i < info.length; i++) {
			if (info[i].getName().equals(deviceName)) {
				selectedIndex = i;
				return MidiSystem.getMidiDevice(info[i]);
			} else if (info[i].getName().equals(DEFAULT_DEVICENAME_WINDOWS)) {
				defaultIndex = i;
			} else if (info[i].getName().equals(DEFAULT_DEVICENAME_MACOSX)) {
				defaultIndex = i;
			}
		}
		selectedIndex = defaultIndex;
		return MidiSystem.getMidiDevice(info[defaultIndex]);
	}

	/** アプリで使用できるMIDIデバイスの一覧を取得します。 */
	public MidiDevice.Info[] getDeviceList() {
		if (deviceList == null || !deviceList.equals(MidiSystem
				.getMidiDeviceInfo()))
			deviceList = MidiSystem.getMidiDeviceInfo();
		return deviceList;
	}
}
