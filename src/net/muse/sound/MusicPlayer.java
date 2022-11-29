package net.muse.sound;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2009/03/04
 */
public interface MusicPlayer {

	/** This method reads a standard MIDI file. */
	abstract void readSMF(File file) throws InvalidMidiDataException, IOException;

	/** Play music */
	abstract void play();

	abstract void back();

	/**
	 * Stop playing.<br>
	 * 演奏を停止します．
	 * 
	 * @param synthe {@link MIDIController} オブジェクト
	 */
	abstract void stopPlay(MIDIController synthe);

	abstract void close();

}
