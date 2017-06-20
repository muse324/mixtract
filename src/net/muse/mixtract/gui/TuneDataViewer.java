package net.muse.mixtract.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.*;

import net.muse.gui.GroupLabel;
import net.muse.mixtract.data.*;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class TuneDataViewer extends JInternalFrame implements TuneDataListener,
		ActionListener {

	/**  */
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;

	public void changeExpression(PhraseCurveType type) {
	// TODO 自動生成されたメソッド・スタブ

	}

	public void setTarget(TuneData target) {
	// TODO 自動生成されたメソッド・スタブ

	}

	public void addGroup(Group g) {
	// TODO 自動生成されたメソッド・スタブ

	}

	public void deleteGroup(GroupLabel g) {}

	public void deselect(GroupLabel g) {
	// TODO 自動生成されたメソッド・スタブ

	}

	public void editGroup(GroupLabel g) {
	// TODO 自動生成されたメソッド・スタブ

	}

	public void selectGroup(GroupLabel g, boolean flg) {
	// TODO 自動生成されたメソッド・スタブ

	}

	public void actionPerformed(ActionEvent e) {
	// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * This is the xxx default constructor
	 *
	 * @throws PropertyVetoException
	 */
	public TuneDataViewer() throws PropertyVetoException {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 * @throws PropertyVetoException
	 */
	private void initialize() throws PropertyVetoException {
		this.setSize(700, 450);
		this.setMaximum(true);
		this.setMaximizable(true);
		this.setResizable(true);
		this.setClosable(true);
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.setContentPane(getJContentPane());
	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
		}
		return jContentPane;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
