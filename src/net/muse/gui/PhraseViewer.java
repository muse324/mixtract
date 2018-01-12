package net.muse.gui;

import java.awt.BorderLayout;

import javax.swing.*;

import net.muse.app.Mixtract;
import net.muse.app.MuseApp;
import net.muse.data.Group;

/**
 * @author Mitsuyo Hashida @ CrestMuse Project, JST
 *         <address>http://www.m-use.net/</address>
 *         <address>hashida@kwansei.ac.jp</address>
 * @since 2009/03/23
 */
public class PhraseViewer extends JDialog implements CanvasMouseListener {

	private static final long serialVersionUID = 1L;

	/* 制御データ */
	protected MuseApp main;
	protected MainFrame owner;

	/* 描画モード */
	private boolean isEdited;

	/* グラフィック */
	private JPanel jContentPane = null;

	private Group group;

	/**
	 * @param main.getFrame()
	 * @param group
	 */
	protected PhraseViewer(MuseApp app, Group group) {
		super(app.getFrame());
		this.main = app;
		setOwner(app.getFrame());
		this.group = group;
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.Dialog#setTitle(java.lang.String)
	 */
	@Override
	public void setTitle(String title) {
		super.setTitle(title + " - " + this.getClass().getSimpleName());
	}

	/**
	 * @param gr
	 * @return
	 */
	public boolean contains(Group gr) {
		return gr.equals(group());
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group();
	}

	/**
	 * @return the isEdited
	 */
	boolean isEdited() {
		return isEdited;
	}

	protected void setController(Mixtract main) {}

	/**
	 * @param isEdited the isEdited to set
	 */
	protected void setEdited(boolean isEdited) {
		this.isEdited = isEdited;
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

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	protected void initialize() {
		this.setContentPane(getJContentPane());
		this.setTitle("group name");
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.mixtract.gui.CanvasMouseListener#setShowCurrentX(boolean,
	 * int)
	 */
	public void setShowCurrentX(boolean showCurrentX, int x) {}

	public void preset() {}

	/**
	 * @return main
	 */
	public MuseApp main() {
		return main;
	}

	/**
	 * @param main セットする main
	 */
	public void setMain(Mixtract main) {
		this.main = main;
	}

	/**
	 * @return owner
	 */
	public MainFrame owner() {
		return owner;
	}

	/**
	 * @param owner セットする owner
	 */
	public void setOwner(JFrame owner) {
		this.owner = (MainFrame) owner;
	}

	/**
	 * @return group
	 */
	public Group group() {
		return group;
	}

	/**
	 * @param group セットする group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

} // @jve:decl-index=0:visual-constraint="26,7"
