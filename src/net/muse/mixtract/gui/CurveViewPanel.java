package net.muse.mixtract.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import net.muse.app.Mixtract;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.curve.ArticulationCurve;
import net.muse.mixtract.data.curve.DynamicsCurve;
import net.muse.mixtract.data.curve.TempoCurve;

public class CurveViewPanel extends PhraseCanvas {

	private static final long serialVersionUID = 1L;
	private static float[] dashLineList = { 3.0f, 3.0f, 3.0f, 3.0f };
	private static final BasicStroke stroke = new BasicStroke(1.0f,
			BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dashLineList,
			0.0f);

	/* 制御データ */
	private DynamicsCurve cvDyn;
	private TempoCurve cvTmp;
	private ArticulationCurve cvArt;
	private int currentMousePositionX;

	/* 描画モード */
	private boolean showDyn = true;
	private boolean showTmp = true;
	private boolean showArt = true;
	private boolean showCurrentX;

	public CurveViewPanel(MXGroup group) {
		super(group, group.getDynamicsCurve());
		this.group = group;
		this.cvDyn = group.getDynamicsCurve();
		this.cvTmp = group.getTempoCurve();
		this.cvArt = group.getArticulationCurve();
		// reset();
		// repaint();
	}

	public CurveViewPanel() {
		super();
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.PhraseCanvas#reset()
	 */
	@Override public void reset() {
		if (cvDyn != null)
			cvDyn.initializeGraphicValue(getAxisX(), getAxisY(), getRangeX(),
					getRangeY() / 2.);
		if (cvTmp != null)
			cvTmp.initializeGraphicValue(getAxisX(), getAxisY(), getRangeX(),
					getRangeY() / 2.);
		if (cvArt != null) {
			cvArt.initializeGraphicValue(getAxisX(), getAxisY(), getRangeX(),
					getRangeY() / 2.);
		}
		super.reset();
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see jp.crestmuse.mixtract.gui.PhraseCanvas#setShowCurrentX(boolean,
	 * int, java.awt.Point)
	 */
	@Override public void setShowCurrentX(boolean showCurrentX, int x) {
		this.showCurrentX = showCurrentX;
		this.currentMousePositionX = x;
		repaint();
	}

	public void showArticulationCurve(boolean selected) {
		this.showArt = selected;
		repaint();
	}

	public void showDynamicsCurve(boolean selected) {
		this.showDyn = selected;
		repaint();
	}

	public void showTempoCurve(boolean selected) {
		this.showTmp = selected;
		repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PhraseCanvas#setController(jp.crestmuse.
	 * mixtract
	 * .Mixtract)
	 */
	@Override void setController(Mixtract main) {
		this.main = main;
		this.mouseActions = new CanvasListener(main, this) {

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.PhraseCanvas.CanvasListener#
			 * mouseDragged
			 * (java.awt.event.MouseEvent)
			 */
			@Override public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				if (SwingUtilities.isLeftMouseButton(e))
					main().notifyShowCurrentX(true, getMousePosition().x);
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.MouseActionListener#mousePressed(java
			 * .awt.event.MouseEvent)
			 */
			@Override public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				if (SwingUtilities.isLeftMouseButton(e))
					main().notifyShowCurrentX(true, getMousePosition().x);
				repaint();
			}

			/*
			 * (非 Javadoc)
			 * @see
			 * jp.crestmuse.mixtract.gui.PhraseCanvas.CanvasListener#
			 * mouseReleased
			 * (java.awt.event.MouseEvent)
			 */
			@Override public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				if (SwingUtilities.isLeftMouseButton(e))
					main().notifyShowCurrentX(false, getMousePosition().x);
				repaint();
			}

		};
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
	}

	/*
	 * (non-Javadoc)
	 * @see jp.crestmuse.mixtract.gui.PhraseCanvas#drawCurves(null)
	 */
	@Override protected void drawCurves(Graphics2D g2) {
		if (showArt)
			drawCurve(g2, cvArt, Color.red, stroke);
		if (showTmp)
			drawCurve(g2, cvTmp, Color.green, stroke);
		if (showDyn)
			drawCurve(g2, cvDyn, Color.blue, stroke);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.PhraseCanvas#paintComponent(java.awt.Graphics
	 * )
	 */
	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (showCurrentX) {
			g.setColor(Color.magenta);
			((Graphics2D) g).setStroke(new BasicStroke(0.5f));
			g.drawLine(currentMousePositionX, 0, currentMousePositionX,
					getHeight());
		}
	}

}
