package net.muse.gui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.muse.app.Mixtract;
import net.muse.data.Group;
import net.muse.misc.MuseObject;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.gui.PhraseViewer;

public class InfoViewer extends JDialog implements CanvasMouseListener {
	private static final long serialVersionUID = 1L;

	/* 制御データ */
	protected final MuseObject app;
	protected final Group group;

	public static InfoViewer create(MuseGUIObject<JFrame> app, Group gr) {
		if (app instanceof Mixtract && gr instanceof MXGroup)
			return new PhraseViewer(app, gr);
		return new InfoViewer(app, gr);
	}

	protected InfoViewer(MuseGUIObject<JFrame> app, Group group) {
		super(app.getFrame());
		this.app = app;
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
	 * @return app
	 */
	protected MuseObject app() {
		return app;
	}

	protected MainFrame owner() {
		assert getOwner() instanceof MainFrame;
		return (MainFrame) getOwner();
	}

	protected void preset() {}
}
