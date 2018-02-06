package net.muse.gui;

import java.awt.BorderLayout;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.PhraseViewer;

public class InfoViewer extends JDialog implements CanvasMouseListener {
	private static final long serialVersionUID = 1L;

	/* 制御データ */
	protected final MuseApp main;
	protected final Group group;
	protected final MainFrame owner;

	public static InfoViewer create(MuseApp app, Group gr) {
		if (app instanceof Mixtract && gr instanceof MXGroup)
			return new PhraseViewer(app, gr);
		return new InfoViewer(app, gr);
	}

	protected InfoViewer(MuseApp app, Group group) {
		super(app.getFrame());
		this.main = app;
		this.owner = (MainFrame) app.getFrame();
		this.group = group;
		initialize();
	}

	/**
	 * @return group
	 */
	public Group group() {
		return group;
	}

	@Override
	public void setShowCurrentX(boolean showCurrentX, int x) {}

	boolean contains(Group gr) {
		return gr.equals(group());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	protected JPanel getJContentPane() {
		JPanel jContentPane = new JPanel(new BorderLayout());
		return jContentPane;
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

	/**
	 * @return main
	 */
	protected MuseApp main() {
		return main;
	}

	protected MainFrame owner() {
		return owner;
	}

	protected void preset() {}
}
