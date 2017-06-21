package net.muse.mixtract.gui;

import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.misc.MuseObject;
import net.muse.mixtract.data.curve.PhraseCurve;

@Deprecated public class SelectedObjects extends MuseObject {
	/**
	 *
	 */
	@Deprecated private Group group;
	@Deprecated private GroupLabel groupLabel;

	/**
	 * @return the group
	 */
	@Deprecated
	public Group getGroup() {
		return group;
	}

	/**
	 * 選択中のグループラベルを返します．
	 *
	 * @return groupLabel
	 */
	@Deprecated
	public final GroupLabel getGroupLabel() {
		return groupLabel;
	}

	@Deprecated public void set_curve(PhraseCurve cv) {}
}
