package net.muse.mixtract.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import net.muse.gui.MainFrame;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class CurvePanel extends JPanel implements MouseInputListener {

	private PhraseCurveType type;
	private boolean isRelationalView = false;
	private double bpmTick = 6.;
	private LinkedList<Double> curvelist;
	private MouseEvent mousePoint;

	/**  */
	private static final long serialVersionUID = 1L;
	private double scoreLength = 0.;
	private ViewerMode viewerMode = ViewerMode.SCORE_VIEW; // @jve:decl-index=0:

	CurvePanel(int tick, PhraseCurveType type) {
		this.bpmTick = tick;
		this.type = type;
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
		mousePoint = e;
		repaint();
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {
		mousePoint = null;
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		mousePoint = e;
		repaint();
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		// Y axis line
		g.drawLine(1, 0, 1, getHeight());
		// memory ticks
		double unit = getHeight() / bpmTick;
		final double base = bpmTick / 2.;
		for (int i = 1; i <= bpmTick; i++) {
			final double y = i * unit;
			g.drawLine(0, (int) y, 5, (int) y);
			String ext = (isRelationalView) ? "%" : "";
			g.drawString(String.valueOf((int) (-6. * y / getHeight() + 3.))
					+ ext, 6, (int) y);
		}
		g.translate(10, 0);
		// base tempo (100%)
		g.setColor(Color.gray);
		final int b = (int) ((isRelationalView) ? unit * base
				: getHeight() / 2);
		g.drawLine(0, b, getWidth(), b);
		g.setColor(Color.blue);
		drawCurve(g);

		// info
		if (mousePoint != null && curvelist != null) {
			int x = mousePoint.getX() / 5;
			if (x > 0 && x < curvelist.size())
				g.drawString("cv:" + String.valueOf(curvelist.get(x)),
						mousePoint.getX(), mousePoint.getY());
			else
				g.drawString("x:" + String.valueOf(x), mousePoint.getX(),
						mousePoint.getY());
		}
	}

	void setCurvelist(MXTuneData target) {
		if (target == null)
			return;
		switch (viewerMode) {
		case REALTIME_VIEW:
			scoreLength = target.getRootGroup(0).duration();
			break;
		default:
			scoreLength = target.getRootGroup(0).getTimeValue();
		}
		switch (type) {
		case TEMPO:
			this.curvelist = target.getTempoList();
			break;
		case DYNAMICS:
			this.curvelist = target.getDynamicsList();
			break;
		default:
			this.curvelist = null;
			break;
		}
		repaint();
	}

	private void drawCurve(Graphics g) {
		if (curvelist == null)
			return;
		if (curvelist.size() < 1)
			return;

		int px = 0, py = 0;
		int delta = (int) (scoreLength / curvelist.size());
		int offset = getHeight() / 2;
		for (int i = 0; i < curvelist.size(); i++) {
			final int x = i * delta;
			// double y = curvelist.get(i);

			// // double y = tempolist.get(i);
			// y = 100. / y;
			// // System.out.print(y);
			// y = Math.log(y) / Math.log(2.);
			// // System.out.println(" -> " + y);
			// y = -30 * y + 100;

			// y = Math.pow(2.0, -1.0 * y) * getHeight() + offset;
			double y = -0.25 * curvelist.get(i) * getHeight() + offset;
			if (i == 0) {
				px = MainFrame.getXOfNote(x);
				py = (int) y;
			}

			int gx1 = MainFrame.getXOfNote(x);
			int gx2 = MainFrame.getXOfNote(x + delta);
			g.drawLine(px, py, gx1, (int) y);
			g.drawLine(gx1, (int) y, gx2, (int) y);
			px = gx2;
			py = (int) y;
		}
	}

	/**
	 * @param mode
	 */
	void setViewerMode(ViewerMode mode) {
		viewerMode = mode;
	}
}
