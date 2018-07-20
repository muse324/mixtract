package net.muse.mixtract.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import net.muse.app.MuseApp;
import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.MXGroup;

public class MXGroupLabel extends GroupLabel {

	private static final long serialVersionUID = 1L;
	private MXGroupLabel childFormer;
	private MXGroupLabel childLatter;

	public MXGroupLabel(MXGroup group, Rectangle r) {
		super(group, r);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.GroupLabel#group()
	 */
	@Override public MXGroup group() {
		return (MXGroup) super.group();
	}

	GroupLabel getChildLatter(ArrayList<GroupLabel> grouplist) {
		if (childLatter == null) {
			for (GroupLabel l : grouplist) {
				if (group().hasChildLatter() && group().getChildLatterGroup()
						.equals(l.group())) {
					childLatter = (MXGroupLabel) l;
					break;
				}
			}
		}
		return childLatter;
	}

	protected GroupLabel child(ArrayList<GroupLabel> grouplist) {
		throw new NoClassDefFoundError("MXGroupLabelでは使用できません．");
	}

	/* (非 Javadoc)
	 * @see net.muse.gui.GroupLabel#createMouseActionListener(net.muse.app.MuseApp)
	 */
	@Override protected GLMouseActionListener createMouseActionListener(
			MuseApp main) {
		return new GLMouseActionListener(main, this){

			/* (非 Javadoc)
			 * @see net.muse.gui.GroupLabel.GLMouseActionListener#self()
			 */
			@Override public MXGroupLabel self() {
				return (MXGroupLabel) super.self();
			}
		};
	}

	protected GroupLabel getChildFormer(ArrayList<GroupLabel> grouplist) {
		if (childFormer == null) {
			for (GroupLabel l : grouplist) {
				if (group().hasChildFormer() && group().getChildFormerGroup()
						.equals(l.group())) {
					childFormer = (MXGroupLabel) l;
					break;
				}
			}
		}
		return childFormer;
	}

	protected void moveChildLabel(MouseEvent e, boolean mousePressed) {
		if (!mousePressed)
			return;
		Point pc;
		MXGroupLabel c = null;
		if (hasChildFormer()) {
			c = getChildFormer();
			c.setStartEdit(true);
			pc = c.getLocation();
			pc.translate(e.getX(), e.getY());
			moveLabel(e, pc, mousePressed);
		}
		if (hasChildLatter()) {
			c = getChildLatter();
			c.setStartEdit(true);
			pc = c.getLocation();
			pc.translate(e.getX(), e.getY());
			moveLabel(e, pc, mousePressed);
		}
	}

	protected void moveLabelVertical(MouseEvent e, boolean mousePressed) {
		Point pc;
		if (hasChildFormer()) {
			pc = getChildFormer().getLocation();
			pc.translate(e.getX(), e.getY());
			getChildFormer().moveLabel(e, pc, mousePressed);
		}
		if (hasChildLatter()) {
			pc = getChildLatter().getLocation();
			pc.translate(e.getX(), e.getY());
			getChildLatter().moveLabel(e, pc, mousePressed);
		}
	}

	protected void moveLabelVertical(MouseEvent e, Point p, Rectangle r,
			boolean shiftKeyPressed, boolean mousePressed) {
		r.y = p.y;
		setBounds(r);
		if (shiftKeyPressed) {
			Point pc;
			if (hasChildFormer()) {
				pc = getChildFormer().getLocation();
				pc.translate(e.getX(), e.getY());
				getChildFormer().moveLabel(e, pc, mousePressed);
			}
			if (hasChildLatter()) {
				pc = getChildLatter().getLocation();
				pc.translate(e.getX(), e.getY());
				getChildLatter().moveLabel(e, pc, mousePressed);
			}
		}
		repaint();
	}

	private MXGroupLabel getChildFormer() {
		return childFormer;
	}

	private MXGroupLabel getChildLatter() {
		return childLatter;
	}

	/**
	 * @return
	 */
	private boolean hasChildFormer() {
		return childFormer != null;
	}

	/**
	 * @return
	 */
	private boolean hasChildLatter() {
		return childLatter != null;
	}

}
