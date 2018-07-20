package net.muse.data;

import java.security.InvalidParameterException;
import javax.sound.midi.*;

/**
 * <h1>NoteScheduleEvent</h1>
 *
 * @author Mitsuyo Hashida & Haruhiro Katayose
 *         <address>CrestMuse Project, JST</address>
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/08/17
 */
public class NoteScheduleEvent {

	private static boolean CLIP_OVER_VELOCITY = true;

	public static boolean isClipOverVelocity() {
		return CLIP_OVER_VELOCITY;
	}

	public static void setClipOverVelociy(boolean cLIP_OVER_VELOCITY) {
		CLIP_OVER_VELOCITY = cLIP_OVER_VELOCITY;
	}

	/** 参照元の {@link NoteData} */
	private final NoteData parent;

	/** MIDI メッセージ */
	private ShortMessage message;

	/** 発音時刻 (ミリ秒) */
	private long onset;

	/** MIDI メッセージタイプ(ShortMessage.NOTE_ON, NOTE_OFF, ...) */
	private NoteType type;

	public NoteScheduleEvent(NoteData note, double onset, int shortMessageType,
			int velocity) throws InvalidMidiDataException {
		this.parent = note;
		this.onset = (long) onset;
		this.type = NoteType.is(shortMessageType);
		message = new ShortMessage();
		message.setMessage(shortMessageType, parent.xmlPartNumber() - 1, (!parent
				.rest()) ? parent.noteNumber() : 0, velocity);
	}

	public MidiMessage getMidiMessage() {
		return message;
	}

	/**
	 * @return onset
	 */
	public long onset() {
		return onset;
	}

	/*
	 * (非 Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return onset + " " + type + " " + parent.noteName() + " (v" + velocity()
				+ ")";
	}

	/**
	 * @param onset セットする onset
	 */
	public void setOnset(long onset) {
		this.onset = onset;
	}

	/**
	 * MIDIメッセージに送るvelocity値を整数で代入します．
	 * velocity値は，<code>0<=velocity<=127</code>を満たさなければなりません．
	 * 代入値に違反が起きた場合，CLIP_OVER_VELOCITY の真偽値に従って，以下のいずれかの処理が返されます．
	 * <ul>
	 * <li><code>0<=velocity<=127</code>に収まるようクリップ
	 * <li>InvalidParameterExceptionをthrowする
	 * </ul>
	 *
	 * @param velocity 代入するヴェロシティ値
	 * @throws InvalidMidiDataException
	 */
	public void setVelocity(int velocity) throws InvalidMidiDataException {
		message.setMessage(message.getCommand(), message.getChannel(), message
				.getData1(), confirmAppropriateVelocity(velocity));
	}

	public int velocity() {
		return message.getData2();
	}

	/**
	 * @param velocity
	 * @return
	 */
	private int confirmAppropriateVelocity(int velocity) {
		if (velocity >= 0 && velocity <= 127)
			return velocity;
		if (CLIP_OVER_VELOCITY) {
			if (velocity < 0)
				return 0;
			if (velocity > 127)
				return 127;
		}
		throw new InvalidParameterException("input velocity must be 0 to 127 ("
				+ velocity + ").");
	}

	enum NoteType {
		ON, OFF, UNKNOWN;

		private static NoteType is(int shortMessageType) {
			switch (shortMessageType) {
			case ShortMessage.NOTE_OFF:
				return OFF;
			case ShortMessage.NOTE_ON:
				return ON;
			default:
				return UNKNOWN;
			}
		}
	}
}
