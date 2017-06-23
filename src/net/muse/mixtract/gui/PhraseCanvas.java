package net.muse.mixtract.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.LinkedList;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.data.Group;
import net.muse.gui.*;
import net.muse.gui.RoundedCornerButton.RoundButton;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.data.curve.PhraseCurve;
import net.muse.mixtract.gui.command.ApplyHierarchicalParamsCommand;
import net.muse.mixtract.gui.command.MixtractCommand;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/24
 */
class PhraseCanvas extends JPanel implements MouseListener, MouseMotionListener,
		CanvasMouseListener {

	private static final int handlerSize = 10;
	private static int canvasSizeX = 300;
	private static double canvasRate = 0.75;
	private static int canvasSizeY = (int) (canvasSizeX * canvasRate);
	private static final long serialVersionUID = 1L;
	private static int defaultRectangleHeight = 100;

	private static int getDefaultRectangleHeight() {
		return defaultRectangleHeight;
	}

	/* 制御データ */
	protected Mixtract main; // @jve:decl-index=0:
	protected Group group; // @jve:decl-index=0:

	private PhraseCurve cv; // @jve:decl-index=0:
	private int axisX;
	private int axisY;
	private int endX;
	private int minY;
	private int maxY;
	private int apexIndex = 0;
	private LinkedList<Point> freehandStroke = new LinkedList<Point>();
	private LinkedList<Point> preHandStroke = new LinkedList<Point>();

	private final LinkedList<Double> backupParamlist = new LinkedList<Double>();
	/* イベント制御 */
	protected MouseActionListener mouseActions; // @jve:decl-index=0:
	private final ApplyHierarchicalParamsCommand paramApplicator;
	private JButton selectedButton;
	private JButton topButton = null;
	private JButton endButton = null;

	private JButton startButton = null;
	/* グラフィック */
	private JMenuItem approximateCurveMenuItem;
	private JMenuItem alignLinearMenuItem = null;
	private JMenuItem calcRealTempoTimeItem = null;

	private JMenuItem resetStandardValueItem = null;

	/**
	 * @param group
	 * @param phraseCurve
	 */
	PhraseCanvas(final Group group, PhraseCurve phraseCurve) {
		this();
		this.group = group;
		this.cv = phraseCurve;
		paramApplicator.setCurve(group, cv);
		if (cv.getXs() != axisX) {
			final Point st = getStartButton().getLocation();
			final Point tp = getTopButton().getLocation();
			final Point ed = getEndButton().getLocation();
			cv.setOffset(axisX, getAxisY());
			cv.setAxis(st, tp, ed);
		}
		backupParamlist.addAll(cv.getParamlist());
		// reset();
		// repaint();
	}

	private PhraseCanvas() {
		super();
		initialize();
		addMouseListener(this);
		addMouseMotionListener(this);
		paramApplicator = MixtractCommand.APPLY_HIERARCHICAL_PARAMS; // @jve:decl-index=0:

		setAxises();

		int w = (int) Math.round((endX - axisX) / (double) PhraseCurve
				.getDefaultDivision());
		for (int i = 0; i <= PhraseCurve.getDefaultDivision(); i++) {
			freehandStroke.add(new Point(i * w + axisX, axisY));
		}
	}

	public void mouseClicked(MouseEvent e) {}

	public void mouseDragged(MouseEvent e) {
		setMouseLocation(getMousePosition());
		if (selectedButton != null)
			editStroke(e.isShiftDown());
		repaint();
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			repaint();
			return;
		}
		if (e.getSource() instanceof RoundButton) {
			selectedButton = (JButton) e.getSource();
		} else {
			// getStartButton().setVisible(false);
			// getTopButton().setVisible(false);
			// getEndButton().setVisible(false);
		}
		repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			repaint();
			return;
		}
		final Point mouse = getMousePosition();
		setMouseLocation(mouse);
		selectedButton = null;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g);

		// draw axis
		setAxises();
		drawAxis(g2);

		// draw curve(s)
		drawCurves(g2);

		// mouse
		if (cv != null) {
			final Point m = getMousePosition();
			Point p = getCurrentStroke(m, 0, cv.getGraphicData().size());
			int idx = cv.getGraphicData().indexOf(p);
			if (m != null && p != null && idx >= 0) {
				// double l = cv.getLogValueData().get(idx);
				double l = cv.getParamlist().get(idx);
				g2.drawString(String.format("%d, %d (init:%d, val:%d), %f", p.x,
						m.y, axisY, p.y, l), m.x, p.y - 10);
			}
		} else {
			// test
			drawCurve(g2, freehandStroke);
		}
	}

	/*
	 * (非 Javadoc)
	 * @see java.awt.Component#repaint()
	 */
	@Override
	public void repaint() {
		setAxises();
		super.repaint();
	}

	public void reset() {
		// cv.initializeLogValueData();
		// cv.initializeParamValue();
		// cv.initializeGraphicValue(axisX, getAxisY(), getRangeX());
		freehandStroke.clear();
		freehandStroke.addAll(cv.getGraphicData());
		repaint();
	}

	public void setShowCurrentX(boolean showCurrentX, int x) {}

	void setController(Mixtract main) {
		this.main = main;
		this.mouseActions = new CanvasListener(main, this);
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
	}

	/**
	 * @param dynamicsCurve
	 */
	void setCurve(PhraseCurve curve) {
		this.cv = curve;
		paramApplicator.setCurve(group, curve);

		setFreehandStroke(curve);
		repaint();
	}

	/**
	 * 任意の表情カーブを描画します。
	 *
	 * @param g2 描画するGraphic2Dオブジェクト
	 * @param curve 描画する表情カーブ
	 * @param color カーブ線の色
	 * @param stroke カーブ線のストローク
	 */
	protected void drawCurve(Graphics2D g2, PhraseCurve curve, Color color,
			BasicStroke stroke) {
		if (curve == null)
			return;
		g2.setColor(color);
		g2.setStroke((curve == cv) ? new BasicStroke(3.0F) : stroke);

		// 座標修正
		// LinkedList<Point> graphicData = rescaleFreehandXPositions();
		if (curve.getGraphicData().size() <= 0) {
			curve.initializeGraphicValue(axisX, axisY, getRangeX(), getRangeY()
					/ 2.);
			setFreehandStroke(curve);
			//
			// freehandStroke.clear();
			// freehandStroke.addAll(curve.getGraphicData());
		} else {
			// curve.rescale(axisX, axisY, getRangeX(), getRangeY() / 2.);
			// setFreehandStroke(curve);
			// freehandStroke.clear();
			// freehandStroke.addAll(curve.getGraphicData());
		}
		drawCurve(g2, curve.getGraphicData());
	}

	/**
	 * 演奏表情カーブを描画します。
	 * <p>
	 * 2011.09.04 ダミー描画。正しくは {@link CurveViewPanel} へ。
	 *
	 * @param g2 描画するGraphic2Dオブジェクト
	 */
	protected void drawCurves(Graphics2D g2) {
		drawCurve(g2, cv, Color.black, new BasicStroke(3.0F));
	}

	protected int getAxisX() {
		return axisX;
	}

	protected int getAxisY() {
		// return getStartButton().getY() + handlerOffset();
		return axisY;
	}

	/**
	 * @return
	 */
	protected int getRangeX() {
		return endX - axisX;
	}

	/**
	 * @return
	 */
	protected int getRangeY() {
		return minY - maxY;
	}

	/**
	 * @param g2
	 */
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

	/**
	 * @param g2
	 * @param graphicData
	 */
	private void drawCurve(Graphics2D g2, final LinkedList<Point> graphicData) {
		Point pre = null;
		for (Point p : graphicData) {
			if (pre != null) {
				g2.drawLine(pre.x, pre.y, p.x, p.y);
			}
			pre = p;
		}
		if (pre != null)
			g2.drawLine(pre.x, pre.y, endX, pre.y);
	}

	/**
	 * @param shiftDown
	 */
	private void editStroke(boolean shiftDown) {}

	/**
	 * This method initializes alignLinearMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getAlignLinearMenuItem() {
		if (alignLinearMenuItem == null) {
			alignLinearMenuItem = new JMenuItem("Align linear line");
			alignLinearMenuItem.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							cv.setOffset(getStartButton().getWidth(),
									getStartButton().getHeight());
							cv.fit2DCurve(getStartButton().getLocation(),
									getTopButton().getLocation(), getEndButton()
											.getLocation(),
									getDefaultRectangleHeight());
							getApproximateCurveMenuItem().setSelected(true);
							cv.calculate(getHalfRangeY());
							paramApplicator.execute();
							try {
								assert main.getData() instanceof MXTuneData;
								((MXTuneData) main.getData())
										.writeTempfileCurveParameters();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							repaint();
						}
					});
		}
		return alignLinearMenuItem;
	}

	/**
	 * @return the approximateCurveMenuItem
	 */
	private final JMenuItem getApproximateCurveMenuItem() {
		if (approximateCurveMenuItem == null) {
			approximateCurveMenuItem = new JCheckBoxMenuItem(
					"Approximate curve");
			approximateCurveMenuItem.setSelected(true);
			approximateCurveMenuItem.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							System.out.println("actionPerformed()");
						}
					});
		}
		return approximateCurveMenuItem;
	}

	/**
	 * This method initializes calcRealTempoTimeItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCalcRealTempoTimeItem() {
		if (calcRealTempoTimeItem == null) {
			calcRealTempoTimeItem = new JMenuItem();
			calcRealTempoTimeItem.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							cv.calculate(getHalfRangeY());
						}
					});
			if (MixtractCommand.getMainFrame() != null) {
				calcRealTempoTimeItem.addActionListener(MixtractCommand
						.getMainFrame());
				calcRealTempoTimeItem.setActionCommand(paramApplicator.name());
				calcRealTempoTimeItem.setText(paramApplicator.getText());
			}
		}
		return calcRealTempoTimeItem;
	}

	/**
	 * @param mouse
	 * @param i
	 * @param size
	 * @return
	 */
	private Point getCurrentStroke(Point mouse, int i, int size) {
		if (i >= size || mouse == null)
			return freehandStroke.get(getLastIndexOf(freehandStroke));
		Point p = freehandStroke.get(i);
		if (mouse.x < p.x)
			return p;
		return getCurrentStroke(mouse, i + 1, size);
	}

	/**
	 * This method initializes endButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getEndButton() {
		if (endButton == null) {
			endButton = new RoundedCornerButton.RoundButton();
			endButton.setBounds(new Rectangle(getWidth() * 9 / 10 - handlerSize,
					canvasSizeY / 2 - handlerSize, handlerSize, handlerSize));
			endButton.addMouseListener(mouseActions);
			endButton.addMouseMotionListener(mouseActions);
			endButton.addMouseListener(this);
			endButton.addMouseMotionListener(this);
			endButton.setVisible(false);
		}
		return endButton;
	}

	/**
	 * @return
	 */
	private int getHalfRange() {
		return (int) Math.round(getHeight() / 4.0);
	}

	/**
	 * @return
	 */
	private int getHalfRangeY() {
		return getHeight() / 2;
	}

	/**
	 * @param freehandStroke2
	 * @return
	 */
	private int getLastIndexOf(LinkedList<Point> freehandStroke2) {
		return freehandStroke2.size() - 1;
	}

	/**
	 * This method initializes resetStandardValueItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getResetStandardValueItem() {
		if (resetStandardValueItem == null) {
			resetStandardValueItem = new JMenuItem("Initialize values");
			resetStandardValueItem.addActionListener(
					new java.awt.event.ActionListener() {
						public void actionPerformed(
								java.awt.event.ActionEvent e) {
							cv.setOffset(axisX, axisY);
							// cv.setOffset(getStartButton());
							// cv.setFreeCurve(true);
							// getApproximateCurveMenuItem().setSelected(false);
							// cv.resetStandardValue(defaultRectangleHeight);
							reset();
							resetTopButtonLocation();
							cv.calculate(getHeight());
							paramApplicator.execute();
							try {
								assert main.getData() instanceof MXTuneData;
								((MXTuneData) main.getData())
										.writeTempfileCurveParameters();
								((MainFrame) main.getFrame()).getTempoView()
										.repaint();
								((MainFrame) main.getFrame()).getDynamicsView()
										.repaint();
								((MainFrame) main.getFrame()).getPianoroll()
										.makeNoteLabel();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
							repaint();
						}
					});
		}
		return resetStandardValueItem;
	}

	/**
	 * This method initializes startButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getStartButton() {
		if (startButton == null) {
			startButton = new RoundedCornerButton.RoundButton();
			startButton.setBounds(new Rectangle(axisX, axisY, handlerSize,
					handlerSize));
			startButton.addMouseListener(mouseActions);
			startButton.addMouseMotionListener(mouseActions);
			startButton.addMouseListener(this);
			startButton.addMouseMotionListener(this);
			startButton.setVisible(false);
		}
		return startButton;
	}

	/**
	 * This method initializes topButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTopButton() {
		if (topButton == null) {
			topButton = new RoundedCornerButton.RoundButton();
			topButton.setBounds(new Rectangle(getWidth() / 2 - handlerSize,
					canvasSizeY / 2 - handlerSize, handlerSize, handlerSize));
			topButton.addMouseListener(mouseActions);
			topButton.addMouseMotionListener(mouseActions);
			topButton.addMouseListener(this);
			topButton.addMouseMotionListener(this);
			topButton.setVisible(false);
		}
		return topButton;
	}

	/**
	 *
	 */
	private void initialize() {
		setLayout(null);
		setDoubleBuffered(true);
		setBackground(Color.white);
		// setSize(canvasSizeX, canvasSizeY);
		// add(getStartButton(), null);
		// add(getEndButton(), null);
		// add(getTopButton(), null);
		defaultRectangleHeight = getHeight() * 2 / 3;
	}

	/** フリーカーブの頂点にTopButtonを割り当てます． */
	private void resetTopButtonLocation() {
		Point p = null;
		for (final Point r : freehandStroke) {
			if (p == null) {
				p = r.getLocation();
				continue;
			}
			if (r.y < p.y) {
				p = r.getLocation();
			}
		}
		if (p != null) {
			getTopButton().setLocation(p);
			apexIndex = freehandStroke.indexOf(p);
		}
	}

	/**
	 * TODO 2011.09.05
	 * 注：ここを変えたら PianoRollSmall.setAxises() も対応させること！
	 */
	private void setAxises() {
		axisX = (int) Math.round(getWidth() * 0.1);
		endX = (int) Math.round(getWidth() * 0.9);
		axisY = getHeight() / 2;
		minY = axisY + getHalfRange();
		maxY = axisY - getHalfRange();
		defaultRectangleHeight = getHeight() * 2 / 3;
	}

	/**
	 * @param startButton2
	 * @param st
	 */
	private void setButtonLocation(JButton btn, Point p) {
		btn.setLocation(p);
		btn.setVisible(true);
	}

	/**
	 * @param curve
	 */
	private void setFreehandStroke(PhraseCurve curve) {
		preHandStroke.clear();
		preHandStroke.addAll(freehandStroke);
		freehandStroke.clear();
		freehandStroke.addAll(curve.getGraphicData());
	}

	/**
	 * @param mouse
	 */
	private void setMouseLocation(Point mouse) {
		if (selectedButton == getTopButton()) {
			selectedButton.setLocation(mouse);
		} else if (selectedButton == getStartButton()) {
			selectedButton.setLocation(new Point(axisX, mouse.y));
		} else if (selectedButton == getEndButton()) {
			selectedButton.setLocation(new Point(endX, mouse.y));
		} else if (mouse != null) {
			Point cur = getCurrentStroke(mouse, 0, freehandStroke.size());
			cur.y = mouse.y;
			resetTopButtonLocation();
		}
		setButtonLocation(getStartButton(), freehandStroke.get(0));
		setButtonLocation(getEndButton(), freehandStroke.get(getLastIndexOf(
				freehandStroke)));
		getTopButton().setVisible(true);

		if (cv != null) {
			cv.getGraphicData().clear();
			cv.getGraphicData().addAll(freehandStroke);
		}
	}

	public class CanvasListener extends MXMouseActionListener {
		CanvasListener(Mixtract main, Container owner) {
			super(main, owner);
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
		 * MouseEvent
		 * )
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			if (group != null)
				super.mouseDragged(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				cv.calculate(getHeight());
				// cv.calculate(getRangeY() / 2.);
				paramApplicator.setCurve(group, cv);
				paramApplicator.execute();
			}
			repaint();
		}

		/*
		 * (non-Javadoc)
		 * @see
		 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent
		 * )
		 */
		@Override
		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (SwingUtilities.isRightMouseButton(e)) {
				createPopupMenu(e);
				repaint();
				return;
			}
			System.out.println("==== mouse released at PhraseCanvas =====");
			System.out.println("group: " + ((group != null) ? group.name()
					: "null"));
			// System.out.println("graphic.data: " + cv
			// .getGraphicRectangleData());
			// System.out.println("scoretime.data: " + cv.getScoretimeData());
			// System.out.println("logvalue.data: " + cv.getLogValueData());

			cv.calculate(getHeight());
			// cv.calculate(getRangeY() / 2.);
			paramApplicator.execute();
			try {
				assert _main.getData() instanceof MXTuneData;
				((MXTuneData) _main.getData()).writeTempfileCurveParameters();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			repaint();
		}

		/**
		 * @param e
		 */
		@Override
		protected void createPopupMenu(MouseEvent e) {
			JPopupMenu popup = new JPopupMenu();
			popup.add(getResetStandardValueItem()); // Generated
			popup.add(getAlignLinearMenuItem());
			popup.add(getApproximateCurveMenuItem());

			popup.add(getCalcRealTempoTimeItem()); // Generated
			popup.show((Component) e.getSource(), e.getX(), e.getY());
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
