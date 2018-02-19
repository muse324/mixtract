package net.muse.mixtract.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.gui.MouseActionListener;
import net.muse.gui.TuneDataListener;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.curve.PhraseCurve;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class PhraseCurveEditorPanel extends JPanel implements TuneDataListener {

	public enum CurveHandlerType {
		START {
			@Override public Point graphicBase() {
				return null;
			}
		},
		TOP {
			@Override public Point graphicBase() {
				return null;
			}
		},
		END {
			@Override public Point graphicBase() {
				return null;
			}
		};

		public abstract Point graphicBase();
	}

	private PhraseCurveType type;
	private MXGroupLabel targetLabel;
	private MXGroup group;
	private PhraseCurve cv;
	private CurveHandler st;
	private CurveHandler tp;
	private CurveHandler ed;
	private int axisX;
	private int axisY;
	private int endX;
	private int minY;
	private int maxY;
	private MouseActionListener mouseActions;
	public CurveHandler target;
	private int offset = 5;

	public PhraseCurveEditorPanel(MuseApp main, PhraseCurveType type) {
		super();
		setLayout(null);
		setDoubleBuffered(true);
		setBackground(Color.white);
		setBorder(BorderFactory.createRaisedBevelBorder());
		this.type = type;
		st = new CurveHandler(CurveHandlerType.START);
		tp = new CurveHandler(CurveHandlerType.TOP);
		ed = new CurveHandler(CurveHandlerType.END);
		add(st);
		add(tp);
		add(ed);
		setAxises();
		setController(main);
	}

	private void setController(MuseApp main) {
		mouseActions = new MouseActionListener(main, this) {

			@Override public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
			}

			@Override public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				repaint();
			}
		};
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
	}

	protected int getAxisX() {
		return axisX;
	}

	protected int getAxisY() {
		// return getStartButton().getY() + handlerOffset();
		return axisY;
	}

	protected int getHalfRange() {
		return (int) Math.round(getHeight() / 2.5);
	}

	private class CurveHandler extends JPanel implements MouseInputListener {
		Dimension size = new Dimension(10, 10);
		private CurveHandlerType START, TOP, END;
		private final CurveHandlerType type;

		public CurveHandler(CurveHandlerType type) {
			super(null);
			this.type = type;
			setSize(size);
			setBackground(Color.ORANGE);
			setOpaque(true);
			setVisible(false);
			addMouseListener(this);
			addMouseMotionListener(this);
		}

		@Override public void mousePressed(MouseEvent e) {
			if (e.getSource() instanceof CurveHandler)
				target = (CurveHandler) e.getSource();
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

		@Override public void mouseClicked(MouseEvent e) {}

		@Override public void mouseReleased(MouseEvent e) {}

		@Override public void mouseDragged(MouseEvent e) {
			if (target == null)
				return;
			Point p = new Point(target.getLocation());
			if (target.equals(tp))
				p.x += e.getX() - offset;
			p.y += e.getY() - offset;
			target.setLocation(p);
			target.repaint();
			getParent().repaint();
		}

	}

	private void drawAxis(Graphics2D g2) {
		g2.setColor(Color.gray);
		g2.drawLine(axisX, 0, axisX, getHeight()); // x-axis (x=0)
		g2.drawLine(endX, 0, endX, getHeight()); // x-axis (x=endX)
		g2.drawString("0.0", axisX - 20, axisY);
		g2.drawLine(axisX, axisY, endX, axisY); // y-axis (y=0)
		g2.drawLine(axisX, minY, endX, minY);
		g2.drawString("-1.0", axisX - 23, minY);
		g2.drawLine(axisX, maxY, endX, maxY);
		g2.drawString("1.0", axisX - 20, maxY + 5);
	}

	public double sizeX() {
		return endX - axisX;
	}

	public double sizeY() {
		return maxY - minY;
	}

	@Override public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g);

		// draw axis
		setAxises();
		drawAxis(g2);

		// draw curve(s)
		drawCurve(g2);

		// mouse
		// if (cv != null) {
		// final Point m = getMousePosition();
		// Point p = getCurrentStroke(m, 0, cv.getGraphicData().size());
		// int idx = cv.getGraphicData().indexOf(p);
		// if (m != null && p != null && idx >= 0) {
		// // double l = cv.getLogValueData().get(idx);
		// double l = cv.getParamlist().get(idx);
		// g2.drawString(String.format("%d, %d (init:%d, val:%d), %f", p.x,
		// m.y, axisY, p.y, l), m.x, p.y - 10);
		// }
		// } else {
		// // test
		// drawCurve(g2, freehandStroke);
		// }
	}

	private void drawCurve(Graphics2D g2) {
		if (!tp.isVisible())
			return;
		g2.setStroke(new BasicStroke(3.0F));
		g2.setColor(type.color());
		drawCurve(g2, st, tp);
		drawCurve(g2, tp, ed);
	}

	private void drawCurve(Graphics2D g2, CurveHandler p1, CurveHandler p2) {
		Dimension sz = p1.getSize();
		sz.width /= 2;
		sz.height /= 2;
		g2.drawLine(p1.getX() + sz.width, p1.getY() + sz.height, p2.getX()
				+ sz.width, p2.getY() + sz.height);
	}

	void initialHandlerLocations() {
		st.setLocation(getAxisX() - offset, getAxisY() - offset);
		tp.setLocation(getWidth() / 2 - offset, getAxisY() - offset);
		ed.setLocation(endX - offset, getAxisY() - offset);
	}

	private void setAxises() {
		axisX = (int) Math.round(getWidth() * 0.1);
		endX = (int) Math.round(getWidth() * 0.9);
		axisY = getHeight() / 2;
		minY = axisY + getHalfRange();
		maxY = axisY - getHalfRange();
	}

	@Override public void addGroup(Group g) {}

	@Override public void deleteGroup(GroupLabel g) {}

	@Override public void editGroup(GroupLabel g) {}

	@Override public void selectGroup(GroupLabel g, boolean flg) {
		targetLabel = (MXGroupLabel) g;
		selectGroup((MXGroup) g.group());
	}

	@Override public void deselect(GroupLabel g) {}

	@Override public void setTarget(TuneData data) {
		selectGroup((MXGroup) data.getRootGroup().get(0));
	}

	private void selectGroup(MXGroup g) {
		group = g;
		switch (type) {
		case DYNAMICS:
			cv = group.getDynamicsCurve();
			break;
		case TEMPO:
			cv = group.getTempoCurve();
			break;
		case ARTICULATION:
			cv = group.getArticulationCurve();
			break;
		}
		initialHandlerLocations();
		st.setVisible(true);
		tp.setVisible(true);
		ed.setVisible(true);
		repaint();
	}

	@Override public void changeExpression(PhraseCurveType type) {}

}
