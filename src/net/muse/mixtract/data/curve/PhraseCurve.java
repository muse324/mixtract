package net.muse.mixtract.data.curve;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.LinkedList;

import net.muse.app.Mixtract;
import net.muse.misc.MuseObject;
import net.muse.misc.Util;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXTuneData;

public abstract class PhraseCurve extends MuseObject {

	private static final int minimumDivision = 20;

	/** フレーズの標準分割数．1以上の整数で表す． */
	private static final int defaultDivision = 40;

	/** フレーズ分割数の最大値．1以上の整数で表す． */
	private static final int defaultMaximumDivision = 480;

	public static PhraseCurve createPhraseProfile(PhraseCurveType type) {
		return type.create();
	}

	/**
	 * @return the defaultresolution
	 */
	public static int getDefaultDivision() {
		return defaultDivision;
	}

	/**
	 * @return the defaultMaximumResolution
	 */
	private static int getDefaultMaximumDivision() {
		return defaultMaximumDivision;
	}

	// private static PhraseCurveType GUI, DYNAMICS, TEMPO;
	protected PhraseCurveType type;
	/**
	 * フレーズ線を直線にするか曲線にするかを0%～100%の重みで表します．0%で直線，100%で曲線です．
	 */
	private int curveWeight = 0;
	private int density;
	/** フレーズの分割数 */
	private int division;
	/** 音量カーブへ適用する線形テンプレートの傾き(jPop-E) */
	private double dynamicSlope;
	/**
	 * 画面上のテンポマップ
	 * <ul>
	 * <li>x: canvas 上のX座標(pixel)
	 * <li>y: canvas 上のY座標(pixel)
	 * <li>width: (getXe()-getXs()) / division
	 * <li>height: canvas 上の高さ(pixel)
	 * </ul>
	 */
	@Deprecated private final LinkedList<Rectangle> graphicRectangleData;

	/**
	 * 画面上のエディットカーブ座標
	 ** <ul>
	 * <li>x: canvas 上のX座標(pixel)
	 * <li>y: canvas 上のY座標(pixel)
	 * </ul>
	 */
	private final LinkedList<Point> graphicData;

	private double unitLength;
	/** 開始点のx座標 */
	private int xs;
	/** 頂点のx座標 */
	private int xa;
	/** 終点のx座標 */
	private int xe;
	/** 開始点のy座標 */
	private int ys;
	/** 頂点のy座標 */
	private int ya;
	/** 終点のy座標 */
	private int ye;

	private int xoffset;
	private int yoffset;

	private final ArrayList<Double> paramlist;
	private Point2D.Double paramST = null;
	private Point2D.Double paramTP = null;
	private Point2D.Double paramED = null;

	protected PhraseCurve() {
		super();
		graphicRectangleData = new LinkedList<Rectangle>();
		graphicData = new LinkedList<Point>();
		new LinkedList<Rectangle>();
		paramlist = new ArrayList<Double>();
		getDefaultMaximumDivision();
		setDivision(defaultDivision);
	}

	/**
	 * @param target
	 * @param gr
	 */
	public abstract void apply(MXTuneData target, MXGroup gr);

	public void calculate(double height) {
		System.out.println("paramlist:");
		paramlist.clear();
		for (Point p : graphicData) {
			double y = Util.castDouble(2.0 - 4.0 * p.y / height);
			paramlist.add(y);

			System.out.println(y);
		}
		System.out.println("==================");
	}

	/**
	 * TODO 再実装 | 2011.09.07 コーディング休止
	 *
	 * @param st
	 * @param tp
	 * @param ed
	 * @param height
	 */
	public synchronized void fit2DCurve(Point st, Point tp, Point ed,
			int height) {
		graphicRectangleData.clear();
		unitLength = Math.round(unitLength(st, ed));
		/* 始点・頂点・終点の座標を取得する */
		setAxis(st, tp, ed);

		dynamicSlope = slope(xs, ys, xa, ya, ys - ya);
		// int den = (100 - d) / (xs - xa) + d - 100;
		double densitySlope = slope(xs, 100, ya, density, 100 - density);
		System.out.println("densitySlope:" + densitySlope);
		/* st-top, top-edをそれぞれ通過する直線 py1 と 二次曲線 py2 を求める */
		// y = a * Math.pow(x - ta, 2) + q;
		// ps = a * Math.pow(ts - ta, 2) + q;
		// pa = a * Math.pow(ta - ta, 2) + q;
		// pe = a * Math.pow(te - ta, 2) + q;
		// (1) pa - ps= a *( Math.pow(ta - ta, 2) - Math.pow(ts - ta, 2) );
		// (2) pe - pa= a *( Math.pow(te - ta, 2) - Math.pow(ta - ta, 2) );
		double pow1 = Math.pow(xa - xa, 2) - Math.pow(xs - xa, 2);
		double a1 = (ya - ys) / pow1;
		double q1 = ys - a1 * Math.pow(xs - xa, 2);

		double pow2 = Math.pow(xe - xa, 2) - Math.pow(xa - xa, 2);
		double a2 = (ye - ya) / pow2;
		double q2 = ye - a2 * Math.pow(xe - xa, 2);

		for (int x = xs; x + unitLength <= xe; x += unitLength) {

			int begin = (x <= xa) ? xs : xe;
			int py1 = (int) linear(dynamicSlope, x, begin, xa); // n=1
			int py2 = quadraticCurve(a1, q1, a2, q2, x);
			// int accentHeight = (x == xs) ? -ys * (100 - getAccent()) / 100 :
			// 0;

			double py = curveWeight(py1, py2);
			// final int y = (int) (((ys + ye) / 2) - py - accentHeight);
			final int y = (int) (((ys + ye) / 2) - py);
			// phraseRectangleData.add(new Rectangle(x, y, unitLength, (int) py
			// +
			// accentHeight));
			graphicRectangleData.add(new Rectangle(x, y, (int) unitLength,
					(int) py + height));

		}
		System.out.println("len:" + getGraphicLength(st, ed) + ", val:"
				+ division + ", res:" + division + ", div:" + division + ", w:"
				+ unitLength + ", dynamicSlope:" + dynamicSlope);
	}

	/**
	 * フレーズの分割数を返します．
	 *
	 * @return the resolution
	 */
	public final int getDivision() {
		return division;
	}

	/**
	 * @return graphicData
	 */
	public LinkedList<Point> getGraphicData() {
		return graphicData;
	}

	/**
	 * @return paramlist
	 */
	public final ArrayList<Double> getParamlist() {
		return paramlist;
	}

	/**
	 * @return type
	 */
	public final PhraseCurveType getType() {
		return type;
	}

	public int getXs() {
		return xs;
	}

	public void initializeGraphicValue(int axisX, int axisY, double length,
			double height) {
		graphicData.clear();
		setDivision(paramlist.size());
		final double w = length / division;
		for (int i = 0; i < division; i++) {
			// createGraphicData(i, axisX, axisY, w);
			long x = Math.round(i * w + axisX);
			long y = firestGraphicYValue(i, axisY, height);
			graphicData.add(new Point((int) x, (int) y));
		}
	}

	/**
	 * パラメータの初期値を返します。値はLogで。
	 *
	 * @return パラメータ初期値（double）
	 */
	public abstract double initialValue();

	/**
	 * 始点・頂点・終点の座標を取得します．
	 *
	 * @param st 始点座標
	 * @param tp 頂点座標
	 * @param ed 終点座標
	 */
	public void setAxis(Point st, Point tp, Point ed) {
		xs = st.x + xoffset;
		xa = tp.x + xoffset;
		xe = ed.x + xoffset;
		ys = st.y + yoffset;
		ya = tp.y + yoffset;
		ye = ed.y + yoffset;
	}

	public void setDivision(int value) {
		division = (value > minimumDivision) ? value : minimumDivision;
	}

	/**
	 * @param width
	 * @param height
	 */
	public void setOffset(int width, int height) {
		xoffset = width / 2;
		yoffset = height / 2;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override public String toString() {
		return paramlist + ", " + graphicData;
	}

	/**
	 * @param axisY
	 * @param height
	 * @return
	 */
	protected long firestGraphicYValue(int i, int axisY, double height) {
		// return axisY;
		return axisY - Math.round(paramlist.get(i) * height);
	}

	protected void initializeParamValue() {
		paramlist.clear();
		for (int i = 0; i < division; i++) {
			paramlist.add(initialValue());
		}
	}

	/**
	 * @param location
	 * @param location2
	 * @param location3
	 */
	private synchronized void changeCurve(Point st, Point tp, Point ed) {
		// TODO ちょっとお休み．マウスハンドラのドラッグによるカーブ変形処理．

		/* 始点・頂点・終点の座標を取得する */
		// setAxis(st, tp, ed);
		// int topIndex = getTopPhraseDataIndex();
		//
		// final int size = graphicRectangleData.size();
		// for (int i = 0; i < size; i++) {
		// Rectangle r = graphicRectangleData.get(i);
		// double unit = (i < topIndex)
		// ? (getXa() - getXs()) / topIndex
		// : (getXe() - getXa()) / (size - topIndex);
		//
		// r.x = (int) (unit * i + getXs());
		// r.width = (int) unit;
		// }
		// // 再計算
		// int w = 0;
		// for (Rectangle r : graphicRectangleData) {
		// if (w == 0) {
		// w = r.x;
		// } else {
		// r.x = w;
		// }
		// w += r.width;
		// }
	}

	/**
	 * @param py1
	 * @param py2
	 * @return
	 */
	private int curveWeight(int py1, int py2) {
		return (py1 * (100 - curveWeight) + py2 * curveWeight) / 100;
	}

	private synchronized void fitResolution() {
		final Point st = new Point(xs, ys);
		final Point ed = new Point(xe, ye);
		final long unit = Math.round(unitLength(st, ed));
		System.out.print(" unit=" + graphicRectangleData.get(0).width + "->"
				+ unit + ", div=" + division);

		if (unit == graphicRectangleData.get(0).width) {
			System.out.println("\n\t>>skipped.");
			return;
		}
		System.out.println("\n\t>> x starts from " + xs);
		LinkedList<Rectangle> newdata = new LinkedList<Rectangle>();

		for (double x = xs; x + unit <= xe; x += unit) {
			int h = getPhraseValue(x);
			// Rectangle[] old = getPhraseDataInXAxis(x, unit);
			System.out.println("\tx=" + x + ", h=" + h);
			// System.out.println("\t " + printRectangle(old[0]));
			// System.out.println("\t " + printRectangle(old[1]));
			// double slope = slope(old[0].x, old[0].y, old[1].x, old[1].y,
			// old[1].height - old[0].height);
			// double y = linear(slope, (int) x, old[0].x, old[1].x); // n=1

			Rectangle integral = new Rectangle((int) x, ys - h, (int) unit, h);
			newdata.add(integral);

		}
		System.out.println(" (" + newdata.size() + ")");

		graphicRectangleData.clear();
		graphicRectangleData.addAll(newdata);
	}

	/**
	 * @param begin
	 * @param end
	 * @return
	 */
	private double getGraphicLength(Point begin, Point end) {
		return end.x - begin.x;
	}

	private int getPhraseValue(double x) {
		for (int i = 0; i + 1 < graphicRectangleData.size(); i++) {
			Rectangle r1 = graphicRectangleData.get(i);
			Rectangle r2 = graphicRectangleData.get(i + 1);
			if (x >= r1.x && x < r2.x)
				return r1.height;
		}
		return -10000;// ここにはこないはず
	}

	/**
	 * @param slope
	 * @param x
	 * @param x1
	 * @param x2 TODO
	 * @return
	 */
	private double linear(double slope, int x, int x1, int x2) {
		if (Mixtract.isAssertion()) {
			assert x1 != x2 : "x1==x2(" + x1 + "," + x2 + ")";
		}
		return ((x - x1) * slope + (x2 - x)) / (x2 - x1);
	}

	/**
	 * @param a
	 * @param q
	 * @param x
	 * @param xOfApex TODO
	 * @param offset TODO
	 * @return
	 */
	private int quad(double a, double q, int x, int xOfApex, int offset) {
		return offset - (int) (a * Math.pow(x - xOfApex, 2) + q);
	}

	/**
	 * @param a1
	 * @param q1
	 * @param a2
	 * @param q2
	 * @param x
	 * @return
	 */
	private int quadraticCurve(double a1, double q1, double a2, double q2,
			int x) {
		return (x <= xa) ? quad(a1, q1, x, xa, ys) : quad(a2, q2, x, xa, ys);
	}

	/**
	 * @param x1 TODO
	 * @param y1 TODO
	 * @param x2 TODO
	 * @param y2 TODO
	 * @return
	 */
	private double slope(int x1, int y1, int x2, int y2, int offset) {
		if (Mixtract.isAssertion()) {
			assert x2 != x1 : "x1 == x2 (" + x1 + "," + x2 + ")";
		}
		return (double) (y2 - y1) / (x2 - x1) + offset;
	}

	private double unitLength(Point st, Point ed) {
		return getGraphicLength(st, ed) / division;
	}

	public Point2D.Double start() {
		if (paramST == null)
			paramST = new Point2D.Double(0., 0.);
		return paramST;
	}

	public Point2D.Double top() {
		if (paramTP == null)
			paramTP = new Point2D.Double(0.5, 0.);
		return paramTP;
	}

	public Point2D.Double end() {
		if (paramED == null)
			paramED = new Point2D.Double(1., 0.);
		return paramED;
	}

	public void setStart(Point2D.Double p) {
		paramST = p;
	}

	public void setTop(Point2D.Double p) {
		paramTP = p;
	}

	public void setEnd(Point2D.Double p) {
		paramED = p;
	}

	public void rasterize() {
		int size = paramlist.size();
		for (int i = 0; i < size; i++) {
			double t = (double) i / size;
			double value = 0.;
			if (t <= top().x)
				value = t * (paramTP.y - paramST.y);
			else if (t <= end().x)
				value = t * (paramED.y - paramTP.y) + paramTP.y;
			paramlist.set(i, value);
		}
	}

}
