package net.muse.sound;

import javax.sound.midi.MidiMessage;

import net.muse.misc.MuseObject;
import net.muse.misc.Util;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 * @since 2008/01/07
 */
public class MIDINoteEvent extends MuseObject {

	private String messageType; // ON or OFF

	private int noteNum;

	private double beat;

	private int measure;

	private double beatOnset;

	private int velocity;

	private MidiMessage msg;

	private double length;

	private long onset;

	public MIDINoteEvent(String type, int measure, double b, int num, int vel,
			double length, int baseBeat) {
		this.measure = measure;
		this.beat = Util.castDouble(b);
		this.noteNum = num;
		this.beatOnset = Util.castDouble(measure * baseBeat + b);
		this.length = length;
		this.velocity = vel;
		setOnset(-1000);
		this.messageType = type;
		System.out.println(this);
	}

	/**
	 * @return beat
	 */
	public double getBeat() {
		return beat;
	}

	/**
	 * @return beatOnset
	 */
	public final double getBeatOnset() {
		return beatOnset;
	}

	/**
	 * @return measure
	 */
	public int getMeasure() {
		return measure;
	}

	/**
	 * @return messageType
	 */
	public final String getMessageType() {
		return messageType;
	}

	public final MidiMessage getMidiMessage() {
		return getMessage();
	}

	/**
	 * @return noteNum
	 */
	public final int getNoteNum() {
		return noteNum;
	}

	/**
	 * @return onset
	 */
	public long getOnset() {
		return onset;
	}

	/**
	 * @return velocity
	 */
	public final int getVelocity() {
		return velocity;
	}

	@Override
	public String toString() {
		String str = getOnset() + " note " + getNoteNum() + ": " + getMessageType()
				+ " v" + velocity + ", beatOnset " + getBeatOnset() + ", measure "
				+ measure + ", beat " + beat + ", length " + getLength();
		return str;
	}

	/**
	 * @return length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param msg
	 *        設定する msg
	 */
	public void setMessage(MidiMessage msg) {
		this.msg = msg;
	}

	/**
	 * @param onset
	 *        設定する onset
	 */
	public void setOnset(long onset) {
		this.onset = onset;
	}

	/**
	 * @return msg
	 */
	private MidiMessage getMessage() {
		return msg;
	}
}
