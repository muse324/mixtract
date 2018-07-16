package net.muse.mixtract.command;

import net.muse.app.Mixtract;
import net.muse.command.MuseAppCommand;
import net.muse.command.MuseAppCommandType;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.gui.MXGroupLabel;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
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
		return type.self();
	}

	public static MuseAppCommand create(String cmd) {
		try {
			MixtractCommandType type1 = MixtractCommandType.valueOf(cmd);
			return type1.self();
		} catch (IllegalArgumentException e) {
			MuseAppCommandType type2 = MuseAppCommandType.valueOf(cmd);
			return type2.self();
		}
	}

	/**
	 * @return the _selectedTarget
	 */
	protected static final Object getSelectedObjects() {
		return _selectedObjects;
	}

	private MXGroup _group;

	private MXGroupLabel _groupLabel;

	protected MixtractCommand(String... lang) {
		super(lang);
	}

	@Override public void setGroup(GroupLabel groupLabel) {
		setGroupLabel(groupLabel);
		if (groupLabel == null) {
			_group = null;
			return;
		}
		assert groupLabel instanceof MXGroupLabel;
		_group = (MXGroup) groupLabel.group();
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
		assert _groupLabel instanceof MXGroupLabel;
		this._groupLabel = (MXGroupLabel) _groupLabel;
	}

	/**
	 * @return _groupLabel
	 */
	protected GroupLabel getGroupLabel() {
		return _groupLabel;
	}

	protected Mixtract main() {
		return (Mixtract) _main;
	}

	protected final MXTuneData data() {
		return (MXTuneData) super.data();
	}

}
