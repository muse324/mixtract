package net.muse.gui;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

public class RoundedCornerButton extends JButton {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final float arcwidth = 1.0f;
	private static final float archeight = 1.0f;
	protected static final int focusstroke = 2;
	protected final Color fc = new Color(100, 150, 255, 200);
	protected final Color ac = new Color(230, 230, 230);
	protected final Color rc = Color.ORANGE;
	protected Shape shape;
	protected Shape border;
	protected Shape base;

	public RoundedCornerButton() {
		this(null, null);
	}

	public RoundedCornerButton(Action a) {
		this();
		setAction(a);
	}

	public RoundedCornerButton(Icon icon) {
		this(null, icon);
	}

	public RoundedCornerButton(String text) {
		this(text, null);
	}

	public RoundedCornerButton(String text, Icon icon) {
		setModel(new DefaultButtonModel());
		init(text, icon);
		setContentAreaFilled(false);
		setBackground(new Color(250, 250, 250));
		initShape();
	}

	@Override public boolean contains(int x, int y) {
		initShape();
		return shape.contains(x, y);
	}

	protected void initShape() {
		if (!getBounds().equals(base)) {
			base = getBounds();
			shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1,
					arcwidth, archeight);
			border = new RoundRectangle2D.Float(focusstroke, focusstroke, getWidth()
					- 1 - focusstroke * 2, getHeight() - 1 - focusstroke * 2, arcwidth,
					archeight);
		}
	}

	@Override protected void paintBorder(Graphics g) {
		initShape();
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getForeground());
		g2.draw(shape);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	@Override protected void paintComponent(Graphics g) {
		initShape();
		final Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (getModel().isArmed()) {
			g2.setColor(ac);
			g2.fill(shape);
		} else if (isRolloverEnabled() && getModel().isRollover()) {
			paintFocusAndRollover(g2, rc);
		} else if (hasFocus()) {
			paintFocusAndRollover(g2, fc);
		} else {
			g2.setColor(getBackground());
			g2.fill(shape);
		}
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.setColor(getBackground());
		super.paintComponent(g2);
	}

	private void paintFocusAndRollover(Graphics2D g2, Color color) {
		g2.setPaint(new GradientPaint(0, 0, color, getWidth() - 1, getHeight() - 1,
				color.brighter(), true));
		g2.fill(shape);
		g2.setColor(getBackground());
		g2.fill(border);
	}

	public static class RoundButton extends RoundedCornerButton {
		private static final int handlerSize = 12;

		/*
		 * (非 Javadoc)
		 * @see java.awt.Component#setLocation(int, int)
		 */
		@Override public void setLocation(Point point) {
			Point p = new Point(point);
			p.x -= handlerSize / 2;
			p.y -= handlerSize / 2;
			super.setLocation(p);
		}

		private static final long serialVersionUID = 1L;

		public RoundButton() {
			super();
			setSize(handlerSize, handlerSize);
		}

		public RoundButton(String text, Icon icon) {
			setModel(new DefaultButtonModel());
			init(text, icon);
			setFocusPainted(false);
			final Dimension size = getPreferredSize();
			size.width = size.height = Math.max(size.width, size.height);
			setPreferredSize(size);
			initShape();
		}

		@Override protected void initShape() {
			if (!getBounds().equals(base)) {
				base = getBounds();
				shape = new Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1);
				border = new Ellipse2D.Float(focusstroke, focusstroke, getWidth() - 1
						- focusstroke * 2, getHeight() - 1 - focusstroke * 2);
			}
		}
	}
}
