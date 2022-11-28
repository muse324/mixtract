package net.muse.gui;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import net.muse.app.MuseApp;
import net.muse.data.Harmony;
import net.muse.data.KeyMode;
import net.muse.misc.Util;
import net.muse.mixtract.command.ChangePartCommand;
import net.muse.mixtract.command.MixtractCommandType;
import net.muse.mixtract.command.SetChordCommand;
import net.muse.mixtract.command.SetKeyCommand;
import net.muse.mixtract.command.SetKeyModeCommand;

public class PianoRollActionListener extends MouseActionListener {
	private final Point pp = new Point();

	public PianoRollActionListener(MuseApp app, Container owner) {
		super(app, owner);
	}

	@Override public PianoRoll self() {
		return (PianoRoll) super.self();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#actionPerformed
	 * (java .awt.event.ActionEvent)
	 */
	@Override public void actionPerformed(ActionEvent e) {
		JMenuItem src = (JMenuItem) e.getSource();
		String cmd = src.getActionCommand();
		if (cmd.equals(MixtractCommandType.CHANGE_PART.name())) {
			int part = Integer.parseInt(src.getText());
			ChangePartCommand.setChangePartTo(part);
		} else if (cmd.equals(MixtractCommandType.SET_CHORD.name())) {
			SetChordCommand.setSelectedChord(Harmony.valueOf(src.getText()));
		} else if (cmd.equals(MixtractCommandType.SET_KEY.name())) {
			SetKeyCommand.setSelectedKey(src.getText());
		} else if (cmd.equals(MixtractCommandType.SET_KEYMODE.name())) {
			SetKeyModeCommand.setSelectedKeyMode(KeyMode.valueOf(src
					.getText()));
		}
		// 登録済みのコマンドを探して実行する
		super.actionPerformed(e);
		self().repaint();
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * jp.crestmuse.mixtract.gui.MouseActionListener#createPopupMenu
	 * (java .awt.event.MouseEvent)
	 */
	@Override public void createCustomPopupMenu(MouseEvent e) {
		boolean enabled = self().selectedNoteLabels.size() > 0;
		getPopup().add(addMenuItem(MixtractCommandType.MAKE_GROUP, enabled));
		getPopup().addSeparator();

		// annotate chord
		JMenu chordMenu = new JMenu("Harmony chord");
		for (Harmony c : Harmony.values())
			chordMenu.add(createChordMenuItem(c));
		chordMenu.setEnabled(self().selectedNoteLabels.size() > 0);
		getPopup().add(chordMenu);

		// change key
		JMenu keyMenu = new JMenu("Change key");
		for (int i = 0; i < 7; i++) {
			keyMenu.add(createKeyMenuItem(i));
		}
		for (int i = -5; i < 0; i++) {
			keyMenu.add(createKeyMenuItem(i));
		}
		keyMenu.setEnabled(self().selectedNoteLabels.size() > 0);
		getPopup().add(keyMenu);

		// change key mode
		JMenu keyModeMenu = new JMenu("Change key mode");
		keyModeMenu.setEnabled(self().selectedNoteLabels.size() > 0);
		for (int i = 0; i < KeyMode.values().length; i++) {
			keyModeMenu.add(createKeyModeMenuItem(i));
		}
		getPopup().add(keyModeMenu);
		// change part
		JMenu partSelectMenu = new JMenu("Change part");
		for (int i = 0; i < ChangePartCommand.partSize; i++) {
			JMenuItem item = new JMenuItem(String.valueOf(i + 1));
			item.setActionCommand(MixtractCommandType.CHANGE_PART.command()
					.name());
			item.addActionListener(self().getMouseActions());
			item.setEnabled(i + 1 != self().getSelectedVoice());
			partSelectMenu.add(item);
		}
		partSelectMenu.setEnabled(enabled);
		getPopup().add(partSelectMenu);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.
	 * MouseEvent )
	 */
	@Override public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		if (e.isAltDown()) {
			Point cp = e.getPoint();
			final JViewport vport = (JViewport) self().getParent();
			Point vp = vport.getViewPosition();
			vp.translate(pp.x - cp.x, pp.y - cp.y);
			self().scrollRectToVisible(new Rectangle(vp, vport.getSize()));
			pp.setLocation(cp);
		} else {
			self().setMouseEndPoint(e);
			self().encloseNotes();
		}
		self().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.
	 * MouseEvent )
	 */
	@Override public void mouseMoved(MouseEvent e) {
		super.mouseMoved(e);
		self().setMouseOveredNoteLabel(null);
		self().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mousePressed(java.awt.event.
	 * MouseEvent
	 * )
	 */
	@Override public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		if (!SwingUtilities.isLeftMouseButton(e))
			return;
		if (e.isAltDown()) {
			self().setCursor(self().hndCursor);
			pp.setLocation(e.getPoint());
		} else {
			/* ピアノロール上で矩形の左上隅座標を取得する */
			self().getLeftUpperCornerAxis(e);
			self().setSelectedVoice(-1);
		}
		if (!self().getMouseActions().isShiftKeyPressed()) {
			self().selectedNoteLabels.clear();
		}
		self().repaint();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.
	 * MouseEvent
	 * )
	 */
	@Override public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if (e.isAltDown()) {
			self().setCursor(self().defCursor);
		} else {
			self().setMouseEndPoint(e);
			self().setMouseSelectBoxDraw(false);
			self().encloseNotes();
			self().selectNotes();
			if (self().selectedNoteLabels.size() == 0)
				app().butler().notifyDeselectGroup();
			else {
				self().setFocusable(true);
				self().requestFocus();
			}
		}
		self().repaint();
	}

	private JMenuItem createChordMenuItem(Harmony c) {
		JMenuItem item = new JMenuItem(c.name());
		item.setActionCommand(MixtractCommandType.SET_CHORD.name());
		item.addActionListener(self().getMouseActions());
		return item;
	}

	private JMenuItem createKeyMenuItem(int i) {
		JMenuItem item = new JMenuItem(Util.fifthsToString(i));
		item.setActionCommand(MixtractCommandType.SET_KEY.name());
		item.addActionListener(self().getMouseActions());
		item.setEnabled(self().selectedNoteLabels.size() > 0
				&& self().selectedNoteLabels.get(0).getScoreNote()
						.fifths() != i);
		return item;
	}

	private JMenuItem createKeyModeMenuItem(int i) {
		final KeyMode mode = KeyMode.values()[i];
		JMenuItem item = new JMenuItem(mode.name());
		item.setActionCommand(MixtractCommandType.SET_KEYMODE.name());
		item.addActionListener(self().getMouseActions());
		item.setEnabled(self().selectedNoteLabels.size() > 0
				&& self().selectedNoteLabels.get(0).getScoreNote()
						.getKeyMode() != mode);
		return item;
	}
}