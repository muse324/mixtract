package net.muse.gui;

import java.awt.BorderLayout;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.PhraseViewer;

public class InfoViewer extends JDialog implements CanvasMouseListener {
	/* 制御データ */
	protected MuseApp main;

	protected Group group;
	protected MainFrame owner;
	private JPanel jContentPane = null;

	public InfoViewer(JFrame frame) {
		super(frame);
	}

	private static final long serialVersionUID = 1L;

	public void preset() {}

	public boolean contains(Group gr) {
		return gr.equals(group());
	}

	/**
	 * @return group
	 */
	public Group group() {
		return group;
	}

	/**
	 * @param owner セットする owner
	 */
	public void setOwner(JFrame owner) {
		this.owner = (MainFrame) owner;
	}

	public InfoViewer(MuseApp app, Group group) {
		this(app.getFrame());
		this.main = app;
		setOwner(app.getFrame());
		this.group = group;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	protected void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("group name");
	}

	public static InfoViewer create(MuseApp app, Group gr) {
		if (app instanceof Mixtract && gr instanceof MXGroup)
			return new PhraseViewer(app, (MXGroup) gr);
		return new InfoViewer(app, gr);
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	protected JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}

	@Override
	public void setShowCurrentX(boolean showCurrentX, int x) {}
}
