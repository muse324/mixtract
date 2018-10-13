package net.muse.mixtract.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D.Double;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

class CurveHandler extends JPanel implements MouseInputListener {
	private final PhraseCurveEditorPanel owner;
	private Dimension size = new Dimension(10, 10);
	private Point cur;
	private int offset = 5;

	CurveHandler(PhraseCurveEditorPanel phraseCurveEditorPanel) {
		super(null);
		owner = phraseCurveEditorPanel;
		setSize(size);
		setBackground(Color.ORANGE);
		setOpaque(true);
		setVisible(false);
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override public void mouseClicked(MouseEvent e) {}

	@Override public void mouseDragged(MouseEvent e) {
		cur = new Point(getLocation());
		if (equals(owner.tp))
			cur.x += e.getX() - offset;
		cur.y += e.getY() - offset;
		setLocation(cur);
		update();
		repaint();
		getParent().repaint();
	}

	@Override public void mouseEntered(MouseEvent e) {
		setBackground(Color.RED);
		repaint();
	}

	@Override public void mouseExited(MouseEvent e) {
		setBackground(Color.ORANGE);
		repaint();
	}

	@Override public void mouseMoved(MouseEvent e) {}

	@Override public void mousePressed(MouseEvent e) {
		if (e.getSource() instanceof CurveHandler)
			owner.target = (CurveHandler) e.getSource();
	}

	@Override public void mouseReleased(MouseEvent e) {
		update();
	}

	private void update() {
		Double p = owner.graph2param(cur);
		if (equals(owner.st))
			owner.cv.setStart(p);
		else if (equals(owner.tp))
			owner.cv.setTop(p);
		else if (equals(owner.ed))
			owner.cv.setEnd(p);
		owner.notifyCurveUpdate();
		repaint();
	}

}