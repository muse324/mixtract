package net.muse.pedb.gui;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLayer;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.plaf.LayerUI;

public class DragScrollLayerUI extends LayerUI<JScrollPane> {
	private static final long serialVersionUID = 1L;
	private final Point pp = new Point();

	@Override public void installUI(JComponent c) {
		super.installUI(c);
		if (c instanceof JLayer) {
			((JLayer) c).setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK
					| AWTEvent.MOUSE_MOTION_EVENT_MASK);
		}
	}

	@Override public void uninstallUI(JComponent c) {
		if (c instanceof JLayer) {
			((JLayer) c).setLayerEventMask(0);
		}
		super.uninstallUI(c);
	}

	@Override protected void processMouseEvent(MouseEvent e,
			JLayer<? extends JScrollPane> l) {
		Component c = e.getComponent();
		if (c instanceof JScrollBar || c instanceof JSlider) {
			return;
		}
		if (e.getID() == MouseEvent.MOUSE_PRESSED) {
			JViewport vport = l.getView().getViewport();
			Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
			pp.setLocation(cp);
		}
	}

	@Override protected void processMouseMotionEvent(MouseEvent e,
			JLayer<? extends JScrollPane> l) {
		Component c = e.getComponent();
		if (c instanceof JScrollBar || c instanceof JSlider) {
			return;
		}
		if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
			JViewport vport = l.getView().getViewport();
			JComponent cmp = (JComponent) vport.getView();
			Point cp = SwingUtilities.convertPoint(c, e.getPoint(), vport);
			Point vp = vport.getViewPosition();
			vp.translate(pp.x - cp.x, pp.y - cp.y);
			cmp.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
			pp.setLocation(cp);
		}
	}
}
