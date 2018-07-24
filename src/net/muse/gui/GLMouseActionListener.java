package net.muse.gui;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseEvent;

import net.muse.app.MuseApp;
import net.muse.data.Group;

public class GLMouseActionListener extends MouseActionListener {

	public GLMouseActionListener(MuseApp main, Container owner) {
		super(main, owner);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseClicked(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseClicked(MouseEvent e) {
		super.mouseClicked(e);
		Group gr = self().group();
		if (gr == null) {
			self().repaint();
			return;
		}
		if (e.getClickCount() == 2) {
			doubleClicked(gr);
		}
		self().repaint();
	}

	protected void doubleClicked(Group gr) {
		for (InfoViewer r : main().butler().getInfoViewList()) {
			if (r.contains(gr)) {
				r.setVisible(true);
				return;
			}
		}
		self().showInfoViewer(main(), gr);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseDragged(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (!frame().getGroupingPanel().isGroupEditable()) {
			self().moveLabelVertical(e, getMousePoint(), self().getBounds(),
					isShiftKeyPressed(), isMousePressed());
		} else
			self().moveLabel(e, getMousePoint(), isMousePressed());
		frame().getGroupingPanel().repaint();
		self().repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseEntered(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseEntered(MouseEvent e) {
		super.mouseEntered(e);
		self().setMouseOver(true);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseExited(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseExited(MouseEvent e) {
		super.mouseExited(e);
		self().setMouseOver(false);
		self().setEditMode(getMousePoint());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseMoved(java.awt
	 * .event.MouseEvent)
	 */
	@Override public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		self().setEditMode(getMousePoint());
		self().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mousePressed(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		main().butler().notifySelectGroup(self(), true);
		if (self().getCursor().getType() == Cursor.W_RESIZE_CURSOR) {
			frame().getGroupingPanel().setGroupEditable(true);
		}
		self().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#mouseReleased(java
	 * .awt.event.MouseEvent)
	 */
	@Override public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		frame().getGroupingPanel().setGroupEditable(false);
		frame().getGroupingPanel().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		frame().getPianoroll().repaint();
		self().repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MouseActionListener#owner()
	 */
	@Override public GroupLabel self() {
		return (GroupLabel) super.self();
	}
}