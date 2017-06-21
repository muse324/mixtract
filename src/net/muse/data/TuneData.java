package net.muse.data;

import java.util.ArrayList;
import java.util.List;

import jp.crestmuse.cmx.filewrappers.DeviationInstanceWrapper;
import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper;
import net.muse.misc.MuseObject;

public class TuneData extends MuseObject {
	/** 声部ごとのフレーズ構造(二分木) */
	private List<Group> rootGroup = new ArrayList<Group>();
	private static int MAXIMUM_MIDICHANNEL = 16;
	public static boolean segmentGroupnoteLine = false;
	/** MusicXML */
	protected MusicXMLWrapper xml;
	/** テンポ情報 */
	private ArrayList<Integer> bpmlist = new ArrayList<Integer>();
	/** 声部ごとの音符情報 */
	private ArrayList<NoteData> notelist = new ArrayList<NoteData>();
	public int[] midiProgram = new int[MAXIMUM_MIDICHANNEL];

	public double[] volume = new double[MAXIMUM_MIDICHANNEL];
	public List<Group> getRootGroup() {
		return rootGroup;
	}
	public void setGrouplist(int partIndex, Group rootGroup) {
		if (partIndex >= this.getRootGroup().size())
			this.getRootGroup().add(rootGroup);
		else
			this.getRootGroup().set(partIndex, rootGroup);
	}
	public Group getRootGroup(int partIndex) {
		return getRootGroup().get(partIndex);
	}
	/** DeviationInstanceXML */
	protected DeviationInstanceWrapper dev;
	public static void setMaximumMIDIChannel(int num) {
		MAXIMUM_MIDICHANNEL = num;
	}
	/**
	 * @param segmentGroupnoteLine セットする segmentGroupnoteLine
	 */
	public static void setSegmentGroupnoteLine(boolean segmentGroupnoteLine) {
		TuneData.segmentGroupnoteLine = segmentGroupnoteLine;
	}
	public ArrayList<Integer> getBPM() {
		return bpmlist;
	}
	public ArrayList<NoteData> getNotelist() {
		return notelist;
	}

	public NoteData getNoteList(int partIndex) {
		return notelist.get(partIndex);
	}

	public void setNotelist(int partIndex, NoteData root) {
		if (partIndex >= this.notelist.size())
			notelist.add(root);
		else
			notelist.set(partIndex, root);
	}

	protected CMXNoteHandler createCMXNoteHandler() {
		return new CMXNoteHandler(this);
	}

	protected void parseMusicXMLFile() {
		if (xml == null)
			return;
		xml.processNotePartwise(createCMXNoteHandler());
	}
}
