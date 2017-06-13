package net.muse.mixtract.gui;

import net.muse.misc.MuseObject;
import net.muse.mixtract.data.Group;
import net.muse.mixtract.data.PhraseCurve;

@Deprecated public class SelectedObjects extends MuseObject {
	/**
	 *
	 */
	@Deprecated private Group group;
	@Deprecated private GroupLabel groupLabel;

	/**
	 * @return the group
	 */
	@Deprecated Group getGroup() {
		return group;
	}

	/**
	 * 選択中のグループラベルを返します．
	 *
	 * @return groupLabel
	 */
	@Deprecated final GroupLabel getGroupLabel() {
		return groupLabel;
	}

	@Deprecated public void set_curve(PhraseCurve cv) {}
}
