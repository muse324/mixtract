package net.muse.data;

import net.muse.misc.MuseObject;

public class BeatInfo extends MuseObject {
	private int measure;
	private int beats;
	private int beatType;

	public BeatInfo(int measure, int beats, int beatType) {
		this.setMeasure(measure);
		this.setBeats(beats);
		this.setBeatType(beatType);
	}

	public int beat() {
		return beats;
	}

	public int beatType() {
		return beatType;
	}

	public int measure() {
		return measure;
	}

	public void setBeats(int beats) {
		this.beats = beats;
	}

	public void setBeatType(int beatType) {
		this.beatType = beatType;
	}

	public void setMeasure(int measure) {
		this.measure = measure;
	}

}
