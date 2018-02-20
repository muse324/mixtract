package net.muse.mixtract.gui;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

import net.muse.data.NoteData;
import net.muse.gui.*;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXNoteData;

public class MXNoteLabel extends NoteLabel {

	private static final long serialVersionUID = 1L;
	private MXGroupLabel childFormer;
	private MXGroupLabel childLatter;

	public MXNoteLabel(NoteData note, Rectangle r) {
		super(note, r);
	}

	@Deprecated
	public Color getApexColor() {
		double apex = ((MXNoteData) getGroupNote()).getApexScore();
		final int c = (int) (255 * (1. - apex));
		return new Color(255, c, c);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.GroupLabel#group()
	 */
	@Override
	public MXGroup group() {
		// TODO 自動生成されたメソッド・スタブ
		return (MXGroup) super.group();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.NoteLabel#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean isSelected) {
		super.setSelected(isSelected);
		if (!isSelected)
			return;
		if (!(getParent() instanceof MXPianoroll))
			return;

		MXPianoroll p = (MXPianoroll) getParent();
		if (p.displayApex) {
			MXNoteData n = (MXNoteData) getGroupNote();
			double w = 1. - n.getApexScore();
			int r = PartColor.SELECTED_COLOR.getRed();
			int c = (int) (255 * w);
			setBackground(new Color(r, c, c));
		}
		repaint();
	}

	GroupLabel getChildLatter(ArrayList<GroupLabel> grouplist) {
		if (childLatter == null) {
			for (GroupLabel l : grouplist) {
				assert l instanceof MXGroupLabel;
				if (group().hasChildLatter() && group().getChildLatterGroup()
						.equals(l.group())) {
					childLatter = (MXGroupLabel) l;
					break;
				}
			}
		}
		return childLatter;
	}

	protected GroupLabel getChildFormer(ArrayList<GroupLabel> grouplist) {
		if (childFormer == null) {
			for (GroupLabel l : grouplist) {
				assert l instanceof MXGroupLabel;
				if (group().hasChildFormer() && group().getChildFormerGroup()
						.equals(l.group())) {
					childFormer = (MXGroupLabel) l;
					break;
				}
			}
		}
		return childFormer;
	}

}
