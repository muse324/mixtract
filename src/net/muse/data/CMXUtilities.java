package net.muse.data;
/**
 * mitsuyo 2008/09/08
 */


import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.misc.MuseObject;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2008/09/08
 */
public class CMXUtilities extends MuseObject {
	public static int beamCount;

	/**
	 * @param note
	 * @return
	 */
	public static Note getHighestNoteOfChord(Note note) {
		Note h = null;
		int p = 0;
		if (note.chordNotes() == null)
			return note;
		for (Note n : note.chordNotes()) {
			if (n.notenum() > p) {
				p = n.notenum();
				h = n;
			}
		}
		return h;
	}

	/*
	public static TreeView<Note> checkBeam(MXTuneData data, Note note, int level,
			TreeView<Note> tv) {
		String val = note.getChildText("beam");
		testPrint(val);
		if (val == null) {
			testPrintln("");
			return tv;
		}
		testPrint(">");
		if (val.equals("begin"))
			tv = new TreeView<Note>();
		if (tv != null) {
			tv.add(note, val);
			if (val.equals("end")) {
				Group g = new Group(tv, (-1), GroupType.BEAM, (++beamCount), data
						.getRootNote(0));
				g.setNumber(level);
				g.createLayer(level);
				data.addScoreGroup(g);
			}
		}
		testPrintln("");
		return tv;
	}
	*/
}
