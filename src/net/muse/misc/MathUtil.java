package net.muse.misc;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {
	/**
	 * 自然対数の底 e にもっとも近い double 値です。 <br>
	 * == Math.E (2.718281828459045)
	 */
	public static final double E = Math.E;

	/**
	 * 円周とその直径の比 pi にもっとも近い double 値です。<br>
	 * == Math.PI (3.141592653589793)
	 */
	public static final double PI = Math.PI;

	public static boolean PRINT_DEBUG = true;

	public static double abs(double x) {
		return (x < 0) ? -1 * x : x;
	}

	public static int abs(int x) {
		return (x < 0) ? -1 * x : x;
	}

	public static double avr(double[] val) {
		return sum(val) / val.length;
	}

	public static int avr(int[] val) {
		return sum(val) / val.length;
	}

	public static double avr(List<Double> val) {
		return sum(val) / val.size();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] ival = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		double[] dval = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		out(sum(ival));
		out(sum(dval));
		out(avr(ival));
		out(avr(dval));
		out(var(ival));
		out(var(dval));
		out(varp(ival));
		out(varp(dval));
		out(stdev(ival));
		out(stdev(dval));
		out(median(dval));
		out(zscore(dval));
	}

	public static double median(double[] val) {
		final int size = val.length;

		// 奇数個の場合
		if (size % 2 != 0) {
			return val[size / 2];
		}
		// 偶数個の場合
		return (val[size / 2 - 1] + val[size / 2]) / 2;
	}

	public static double median(List<Double> val) {
		final int size = val.size();

		// 奇数個の場合
		if (size % 2 != 0) {
			return val.get(size / 2);
		}
		// 偶数個の場合
		return (val.get(size / 2 - 1) + val.get(size / 2)) / 2;
	}

	/**
	 * 標準偏差（standard deviation）を返します．
	 * 
	 * @param val
	 * @return
	 */
	public static double stdev(double[] val) {
		return Math.sqrt(var(val));
	}

	/**
	 * 標準偏差（standard deviation）を返します．
	 * 
	 * @param val
	 * @return
	 */
	public static double stdev(int[] val) {
		double dval[] = new double[val.length];
		for (int i = 0; i < val.length; i++)
			dval[i] = val[i];
		return stdev(dval);
	}

	public static double sum(double[] val) {
		double ans = 0;
		for (int i = 0; i < val.length; i++) {
			ans += val[i];
		}
		return ans;
	}

	public static int sum(int[] val) {
		int ans = 0;
		for (int i = 0; i < val.length; i++) {
			ans += val[i];
		}
		return ans;
	}

	public static double sum(List<Double> val) {
		double ans = 0;
		for (double v : val) {
			ans += v;
		}
		return ans;
	}

	/**
	 * 標本分散σ<upper>2</upper> を返します．
	 * 
	 * @param dval
	 * @return
	 */
	public static double var(double[] val) {
		double sub[] = new double[val.length];
		double avr = avr(val);
		for (int i = 0; i < val.length; i++)
			sub[i] = Math.pow(avr - val[i], 2.);
		return avr(sub);
	}

	/**
	 * 標本分散σ<upper>2</upper> を返します．
	 * 
	 * @param val
	 * @return
	 */
	public static double var(int[] val) {
		double dval[] = new double[val.length];
		for (int i = 0; i < val.length; i++)
			dval[i] = val[i];
		return var(dval);

	}

	/**
	 * 不偏分散s<upper>2</upper>（母分散σ<upper>2</upper> の推定値）を返します．
	 * 
	 * @param val
	 * @return
	 */
	public static double varp(double[] val) {
		double sub[] = new double[val.length];
		double avr = avr(val);
		for (int i = 0; i < val.length; i++)
			sub[i] = Math.pow(avr - val[i], 2.);
		return sum(sub) / (val.length - 1);
	}

	/**
	 * 不偏分散s<upper>2</upper>（母分散σ<upper>2</upper> の推定値）を返します．
	 * 
	 * @param val
	 * @return
	 */
	public static double varp(int[] val) {
		double dval[] = new double[val.length];
		for (int i = 0; i < val.length; i++)
			dval[i] = val[i];
		return varp(dval);
	}

	/**
	 * 各データの標準化スコア（Zスコア）を返します．
	 * 
	 * @param val
	 *            標本データが格納された配列
	 * @return
	 */
	public static List<Double> zscore(double[] val) {
		double avr = avr(val);
		double stdev = stdev(val);
		List<Double> zscore = new ArrayList<Double>();
		for (int i = 0; i < val.length; i++) {
			zscore.add((val[i] - avr) / stdev);
		}
		return zscore;
	}

	private static void out(Object mesg) {
		if (PRINT_DEBUG)
			System.out.println(mesg);
	}

}
