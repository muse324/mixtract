package net.muse.pedb.data;

import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.command.MuseAppCommandType;
import net.muse.data.Concierge;
import net.muse.gui.TuneDataListener;
import net.muse.misc.MuseObject;
import net.muse.pedb.command.PEDBCommandType;
import net.muse.pedb.gui.PEDBTopNoteLabel;

public class PEDBConcierge extends Concierge {

	public PEDBConcierge(MuseObject obj) {
		super(obj);
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Concierge#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override public void keyPressed(KeyEvent e) {
		c = null;
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		final MuseApp app = (MuseApp) obj;
		printConsole(String.format("PEDBConcierge: %d", e.getKeyCode()));
		switch (e.getKeyCode()) {
		case KeyEvent.VK_R:
			printConsole("refresh");
		case KeyEvent.VK_G:
			printConsole("make group");
			c = app.searchCommand(PEDBCommandType.PEDBMAKE_GROUP);
			break;
		case KeyEvent.VK_BACK_SPACE:
			printConsole("delete group");
			c = app.searchCommand(PEDBCommandType.PEDBDELETE_GROUP);
			break;
		case KeyEvent.VK_SPACE:
			printConsole(!isPlayed() ? "play" : "stop");
			c = app.searchCommand(!isPlayed() ? MuseAppCommandType.PLAY
					: MuseAppCommandType.STOP);
			break;

		// 11/17 藤坂が追加 頂点を動かすために方向キーを使用する。
		case KeyEvent.VK_RIGHT:
			printConsole("move right");
			// PEDBNoteData n = PEDBTopNoteLabel.moveNote(0, n);
			break;
		case KeyEvent.VK_LEFT:
			printConsole("move left");
			// PEDBTopNoteLabel.moveNote(1);
			break;
		}
		if (c != null) {
			c.setFrame(app().getFrame());
			c.setApp(app());
			c.setTarget(app().data());
			c.run();
		}
	}

	public void notifySelectTopNote(PEDBTopNoteLabel self, boolean b) {
		for (final TuneDataListener l : getTdListenerList()) {
			l.selectTopNote(self.note(), b);
		}
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.data.Concierge#app()
	 */
	@Override protected PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

}
