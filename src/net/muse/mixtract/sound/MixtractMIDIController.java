package net.muse.mixtract.sound;

import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.gui.TuneDataListener;
import net.muse.mixtract.data.curve.PhraseCurveType;
import net.muse.sound.MIDIController;
import net.muse.sound.ThreadPlayer;

public class MixtractMIDIController extends MIDIController implements
		TuneDataListener {

	private ThreadPlayer smfplayer;
	private TuneData data;

	public static MIDIController createMIDIController(String deviceName,
			int ticksPerBeat) {
		return new MixtractMIDIController(deviceName, ticksPerBeat);
	}

	public MixtractMIDIController(String deviceName, int ticksPerBeat) {
		openMidiDevice(deviceName);
		addMidiEventListener(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#addGroup(jp.crestmuse.
	 * mixtract
	 * .data.Group)
	 */
	public void addGroup(Group g) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.TuneDataListener#changeExpression(jp.crestmuse
	 * .mixtract.data.PhraseProfile.PhraseCurveType)
	 */
	public void changeExpression(PhraseCurveType type) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#deleteGroup(javax.swing
	 * .JLabel)
	 */
	public void deleteGroup(GroupLabel g) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#deselect(javax.swing.JLabel
	 * )
	 */
	public void deselect(GroupLabel g) {}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#editGroup(javax.swing.JLabel
	 * )
	 */
	public void editGroup(GroupLabel g) {
		throw new UnsupportedOperationException(); // TODO 実装
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.sound.MIDIEventListener#pausePlaying()
	 */
	public void pausePlaying() {
		// do nothing (unsupported in the CEDEC version)
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.GroupEditListener#selectGroup(javax.swing
	 * .JLabel, boolean)
	 */
	public void selectGroup(GroupLabel g, boolean flg) {}

	public void setTarget(TuneData target) {
		data = target;
	}

	public void startPlaying(String smfFilename) {
		smfplayer = new ThreadPlayer(data.getNoteScheduleEventList(), this);
		smfplayer.setMIDIProgram(data.getMIDIPrograms(), data.getVolume());
		smfplayer.play();
	}

	public void stopPlaying() {
		smfplayer.stopPlay(this);
	}

	public void stopPlaying(MIDIController synthe) {}

	@Override public void selectTopNote(NoteData note, boolean b) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
