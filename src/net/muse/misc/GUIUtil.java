package net.muse.misc;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.Document;

/**
 * よく使う各種コンポーネントの記述テンプレートを集めたユーティリティクラスです．<br>
 * 
 * @author Mitsuyo Hashida @ CrestMuse Project
 * @since ver2.0 - 2007.12.24
 *        <ul>
 *        <li>static クラスへ変更
 *        </ul>
 * @since ver1.0 - 2007.9.1
 */
public class GUIUtil {
	private static JTextArea console;
	static {
		console = new JTextArea(5, 30);
		console.setEditable(false);
	}

	/**
	 * @return console
	 */
	public static final JTextArea getConsole() {
		return console;
	}

	/**
	 * 新しいボタンを作成します．
	 * 
	 * @param toolBar
	 * @param label ボタンに表示させるラベル
	 * @param actionCommand アクションコマンド名
	 * @param editable 使用可否（初期値）
	 * @param btn 作成するボタン
	 */
	static public void createButton(final JComponent obj, final String label,
			final String actionCommand, final boolean editable,
			final ActionListener target) {
		final JButton btn = new JButton(label);
		btn.setVerticalTextPosition(SwingConstants.CENTER);
		btn.setHorizontalTextPosition(SwingConstants.LEADING); // aka LEFT, for
		btn.setEnabled(editable);
		btn.addActionListener(target);
		btn.setActionCommand(actionCommand);
		obj.add(btn);
	}

	@Deprecated static public JMenuItem createMenuItem(final ActionListener l,
			final JMenu menu, final String name, final Enum action, final int key) {
		return createMenuItem(l, menu, name, action.toString(), key,
													ActionEvent.CTRL_MASK);
	}

	@Deprecated static public JMenuItem createMenuItem(final ActionListener l,
			final JMenu menu, final String name, final Enum action, final int key,
			final int opt) {
		return createMenuItem(l, menu, name, action.toString(), key, opt);
	}

	@Deprecated static public JMenuItem createMenuItem(final ActionListener l,
			final JMenu menu, final String name, final String action, final int key) {
		return createMenuItem(l, menu, name, action, key, ActionEvent.CTRL_MASK);
	}

	@Deprecated static public JMenuItem createMenuItem(final ActionListener l,
			final JMenu menu, final String name, final String action, final int key,
			final int opt) {
		JMenuItem menuItem;
		menuItem = new JMenuItem(name);
		menuItem.setMnemonic(key);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(key, opt));
		menuItem.setActionCommand(action);
		menuItem.addActionListener(l);
		menu.add(menuItem);
		return menuItem;
	}

	/**
	 * コンソール(JTextAreaオブジェクト）にテキストを表示します．
	 * 
	 * @param e
	 */
	static public void printConsole(final String str) {
		console.append(str + "\n");
		Document doc = console.getDocument();
		console.setCaretPosition(doc.getLength());
		System.out.println(str);
	}

	/**
	 * @param l
	 * @param cmd
	 * @param key
	 */
	public static final JMenuItem createMenuItem(ActionListener l, Command cmd, int key) {
		return createMenuItem(l, cmd, key, ActionEvent.CTRL_MASK);
	}

	/**
	 * @param l
	 * @param cmd
	 * @param key
	 * @param ctrlMask
	 * @return
	 */
	public static final JMenuItem createMenuItem(ActionListener l, Command cmd,
			int key, int opt) {
		JMenuItem menuItem;
		menuItem = new JMenuItem();
		menuItem.setText(cmd.getText());
		menuItem.setMnemonic(key);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(key, opt));
		menuItem.setActionCommand(cmd.name());
		menuItem.addActionListener(l);
		return menuItem;
	}
}
