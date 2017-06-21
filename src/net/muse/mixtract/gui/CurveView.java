package net.muse.mixtract.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.muse.app.Mixtract;
import net.muse.data.Group;
import net.muse.gui.GroupLabel;
import net.muse.gui.TuneDataListener;
import net.muse.mixtract.data.*;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class CurveView extends JScrollPane implements TuneDataListener {
	private static final long serialVersionUID = 1L;
	static final int DEFAULT_HEIGHT = 140;
	private CurvePanel curvePanel = null;
	private MXTuneData data; // @jve:decl-index=0:
	private int bpmTick;
	private PhraseCurveType type;

	/**
	 * This is the default constructor
	 *
	 * @param type
	 * @param min
	 * @param max
	 * @param tick
	 */
	public CurveView(PhraseCurveType type, int max, int min, int tick) {
		super();
		this.type = type;
		setBpmTick(tick);
		initialize();
	}

	private void setBpmTick(int tick) {
		this.bpmTick = tick;
	}

	/**
	 * This method initializes jPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCurvePanel() {
		if (curvePanel == null) {
			curvePanel = new CurvePanel(bpmTick, type);
			curvePanel.setBackground(Color.white);
			curvePanel.setOpaque(true);
			curvePanel.setDoubleBuffered(true);
			curvePanel.setLayout(null);
			curvePanel.setSize(new Dimension(290, 141)); // Generated
		}
		return curvePanel;
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		setViewportView(getCurvePanel()); // Generated
		this.setSize(new Dimension(305, 182)); // Generated
	}

	public void setTarget(MXTuneData target) {
		data = target;
		if (Mixtract.isAssertion())
			assert data != null : "data is null";
		curvePanel.setCurvelist(target);
		repaint();
	}

	public void changeExpression(PhraseCurveType type) {
		repaint();
	}

	public void addGroup(Group g) {
		repaint();
	}

	public void deleteGroup(GroupLabel g) {
		repaint();
	}

	public void deselect(GroupLabel g) {
		repaint();
	}

	public void editGroup(GroupLabel g) {
		repaint();
	}

	public void selectGroup(GroupLabel g, boolean flg) {
		repaint();
	}

	/**
	 * @param scoreView
	 */
	public void setViewMode(ViewerMode mode) {
		curvePanel.setViewerMode(mode);
		curvePanel.setCurvelist(data);
		curvePanel.repaint();
	}

} // @jve:decl-index=0:visual-constraint="21,33"
