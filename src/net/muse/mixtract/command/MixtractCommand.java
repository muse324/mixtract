package net.muse.mixtract.command;

import net.muse.command.MuseAppCommand;
import net.muse.command.MuseAppCommandType;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.MXTuneData;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         The University of Fukuchiyama (since Apr. 2020)
 *         <address>https://m-use.net/</address>
 *         <address>hashida-mitsuyo@fukuchiyama.ac.jp</address>
 * @since 2008/04/21
 */
public class MixtractCommand extends MuseAppCommand {

	protected static final Group _selectedObjects = null;

	/**
	 * @param mainFrame
	 * @param main
	 * @return
	 */
	public static MuseAppCommand create(MixtractCommandType type) {
		return type.command();
	}

	public static MuseAppCommand create(String cmd) {
		try {
			MixtractCommandType type1 = MixtractCommandType.valueOf(cmd);
			return type1.command();
		} catch (IllegalArgumentException e) {
			MuseAppCommandType type2 = MuseAppCommandType.valueOf(cmd);
			return type2.command();
		}
	}

	/**
	 * @return the _selectedTarget
	 */
	protected static final Object getSelectedObjects() {
		return _selectedObjects;
	}

	private GroupLabel _groupLabel;

	protected MixtractCommand(String... lang) {
		super(lang);
	}

	@Override public void setGroup(GroupLabel groupLabel) {
		setGroupLabel(groupLabel);
		if (groupLabel == null) {
			return;
		}
		groupLabel.group();
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#toString()
	 */
	@Override public String toString() {
		return super.toString();
	}

	/**
	 * @param _groupLabel セットする _groupLabel
	 */
	private void setGroupLabel(GroupLabel _groupLabel) {
		if (_groupLabel == null) {
			this._groupLabel = null;
			return;
		}
		this._groupLabel = _groupLabel;
	}

	/**
	 * @return _groupLabel
	 */
	protected GroupLabel getGroupLabel() {
		return _groupLabel;
	}

	protected MXTuneData data() {
		return (MXTuneData) super.data();
	}

}
