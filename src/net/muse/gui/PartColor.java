package net.muse.gui;

import java.awt.Color;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/02/18
 */
public class PartColor {

	public final static Color MOUSE_OVER_COLOR = Color.pink;
	public final static Color SELECTED_COLOR = Color.red;
	private Color[] _list = { Color.blue, Color.gray, Color.gray, Color.gray,
			Color.gray.darker(), Color.darkGray, Color.darkGray.darker() };
	/** voice number */
	private int _voice;

	public PartColor() {
		super();
	}

	/** create new part color */
	public PartColor(int voice) {
		assert voice > 0 : "invalid voice " + voice;
		assert voice < _list.length : "PartColor provides only " + _list.length
				+ " parts. Please add the voice " + voice;
		this._voice = voice;
	}

	public Color getColor() {
		return _list[getVoice() - 1];
	}

	/**
	 * @return the voice
	 */
	public int getVoice() {
		return _voice;
	}

}
