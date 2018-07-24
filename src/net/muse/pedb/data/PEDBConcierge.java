package net.muse.pedb.data;

import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.command.MuseAppCommandType;
import net.muse.data.Concierge;
import net.muse.gui.MainFrame;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.MixtractCommandType;
import net.muse.pedb.command.PEDBCommandType;

public class PEDBConcierge extends Concierge {

	public PEDBConcierge(MuseObject obj) {
		super(obj);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		MuseApp main = (MuseApp) obj;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_G:
			printConsole("make group");
			c = main.searchCommand(PEDBCommandType.MAKE_GROUP);
			break;
		case KeyEvent.VK_BACK_SPACE:
			printConsole("delete group");
			c = main.searchCommand(MixtractCommandType.DELETE_GROUP);
			break;
		case KeyEvent.VK_SPACE:
			printConsole((!isPlayed()) ? "play" : "stop");
			c = main.searchCommand((!isPlayed()) ? MuseAppCommandType.PLAY
					: MuseAppCommandType.STOP);
			break;
		default:
			printConsole(e.getSource().getClass().getName()
					+ ": key pressed: ");
		}
		if (c != null) {
			c.setFrame((MainFrame) app().getFrame());
			c.setMain(app());
			c.setTarget(app().data());
			c.run();
		}
	}

}
