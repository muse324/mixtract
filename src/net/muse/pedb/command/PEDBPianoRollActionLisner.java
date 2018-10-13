package net.muse.pedb.command;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.muse.app.MuseApp;
import net.muse.gui.PianoRollActionListener;
import net.muse.mixtract.command.ChangePartCommand;

public class PEDBPianoRollActionLisner extends PianoRollActionListener {

	public PEDBPianoRollActionLisner(MuseApp main, Container owner) {
		super(main, owner);
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * net.muse.gui.PianoRollActionListener#createCustomPopupMenu(java.awt.event
	 * .MouseEvent)
	 */
	@Override public void createCustomPopupMenu(MouseEvent e) {
		boolean enabled = self().getSelectedNoteLabels().size() > 0;
		getPopup().add(addMenuItem(PEDBCommandType.PEDBMAKE_GROUP, enabled));
		getPopup().addSeparator();

		// change part
		JMenu partSelectMenu = new JMenu("Change part");
		for (int i = 0; i < ChangePartCommand.partSize; i++) {
			JMenuItem item = new JMenuItem(String.valueOf(i + 1));
			item.setActionCommand(PEDBCommandType.PEDBCHANGE_PART.command().name());
			item.addActionListener(self().getMouseActions());
			item.setEnabled(i + 1 != self().getSelectedVoice());
			partSelectMenu.add(item);
		}
		partSelectMenu.setEnabled(enabled);
		getPopup().add(partSelectMenu);
	}

}
