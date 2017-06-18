package net.muse.sound;

/*
 * SMFPlayer.java X.T.
 */
import javax.sound.midi.*;
import java.io.*;

public class SMFPlayer implements MusicPlayer {
	private Sequence sequence;

	private Sequencer sequencer = null;

	private long currnetTickPositon;

	private  int byteLength;

	private  int type;

	private  long microsecondLength;

	public SMFPlayer() throws MidiUnavailableException {
		sequencer = MidiSystem.getSequencer();
		sequencer.open();
		/*
		 * If sequencer and synthesizer is not combied, we have to make connection
		 * between them.
		 */
		if (!(sequencer instanceof Synthesizer)) {
			Synthesizer synthesizer = MidiSystem.getSynthesizer();
			synthesizer.open();
			Receiver receiverSyntheFromSeq = synthesizer.getReceiver();
			Transmitter transmitterSeqToSynthe = sequencer.getTransmitter();
			transmitterSeqToSynthe.setReceiver(receiverSyntheFromSeq);
		}
		System.err.println("(SMFPlayer) Sequencer ---> Synthesizer");

		currnetTickPositon = 0;

		byteLength = 0;
		type = 0;
		microsecondLength = 0;
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#readSMF(java.io.File)
	 */
	public void readSMF(File file) throws InvalidMidiDataException, IOException {

		stopPlay();
		sequence = MidiSystem.getSequence(file);
		sequencer.setSequence(sequence);

		MidiFileFormat midiFileFormat = MidiSystem.getMidiFileFormat(file);
		byteLength = midiFileFormat.getByteLength();
		type = midiFileFormat.getType();
		microsecondLength = sequencer.getMicrosecondLength();
		System.err.println("microsecondLength = " + microsecondLength);
		// midiFileFormat.getMicrosecondLength() doesn't work, I don't know
		// why...
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#play()
	 */
	public void play() {
		if (sequencer.getSequence() == null) {
			System.err.println("(SMFPlayer) sequence in sequencer is empty!");
			return;
		}

		sequencer.start();
		System.err.println("(SMFPlayer) start playing....");
		new BufferedReader(new InputStreamReader(System.in));
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#back()
	 */
	public void back() {
		sequencer.setTickPosition(0);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#stop()
	 */
	public void stopPlay() {
		if (sequencer.isRunning())
			sequencer.stop();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#close()
	 */
	public void close() {
		this.stopPlay();
		if (sequencer.isOpen())
			sequencer.close();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.sound.MusicPlayer#stopPlay(net.muse.sound.MIDIController)
	 */
	public void stopPlay(MIDIController synthe) {
		// do nothing (no necessary)
	}

}
