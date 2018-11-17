package net.muse.pedb.data;

import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.app.PEDBStructureEditor;
import net.muse.command.MuseAppCommandType;
import net.muse.data.Concierge;
import net.muse.gui.GroupLabel;
import net.muse.misc.MuseObject;
import net.muse.mixtract.command.MixtractCommandType;
import net.muse.pedb.command.PEDBCommandType;

public class PEDBConcierge extends Concierge {

	public PEDBConcierge(MuseObject obj) {
		super(obj);
	}

	/* (非 Javadoc)
	 * @see net.muse.data.Concierge#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override public void keyPressed(KeyEvent e) {
		c = null;
		assert obj instanceof MuseApp : "MuseApp系のクラスオブジェクトで呼び出してください: obj:"
				+ obj.getClass().getSimpleName();
		MuseApp main = (MuseApp) obj;
		switch (e.getKeyCode()) {
		case KeyEvent.VK_R:
			printConsole("refresh");
		case KeyEvent.VK_G:
			printConsole("make group");
			c = main.searchCommand(PEDBCommandType.PEDBMAKE_GROUP);
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

			// 11/17  藤坂が追加  頂点を動かすために方向キーを使用する。
		case KeyEvent.VK_RIGHT:
			printConsole("move right");
			//PEDBNoteData n = PEDBTopNoteLabel.moveNote(0, n);
			break;
		case KeyEvent.VK_LEFT:
			printConsole("move left");
			//PEDBTopNoteLabel.moveNote(1);
			break;

		default:
			printConsole(e.getSource().getClass().getName()
					+ ": key pressed: ");
		}
		if (c != null) {
			c.setFrame(app().getFrame());
			c.setMain(app());
			c.setTarget(app().data());
			c.run();
		}
	}
	public PEDBNoteData setTopNoteLabel(GroupLabel self) {
		return null;
		// TODO 自動生成されたメソッド・スタブ

	}
	/* (非 Javadoc)
	 * @see net.muse.data.Concierge#app()
	 */
	@Override protected PEDBStructureEditor app() {
		return (PEDBStructureEditor) super.app();
	}

}
