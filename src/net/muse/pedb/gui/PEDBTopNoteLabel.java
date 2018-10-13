package net.muse.pedb.gui;

import java.awt.Rectangle;
import java.awt.event.MouseAdapter;

import javax.swing.Icon;
import javax.swing.JLabel;

import net.muse.app.MuseApp;
import net.muse.data.NoteData;
import net.muse.gui.KeyActionListener;

public class PEDBTopNoteLabel extends JLabel {

//追加
	private NoteData topNote;

	/* イベント制御 */
	private MouseAdapter mouseActions;
	private KeyActionListener keyActions;
	private boolean startEdit;
	private boolean endEdit;

	public PEDBTopNoteLabel() {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//追加

	protected PEDBTopNoteLabel(NoteData topNote, Rectangle r) {
		this();
		this.topNote = topNote;
		//this.setPartNumber(topNote.getBeginNote().musePhony());
		setLocation(r.x, r.y);
		setBounds(r);
		//setTypeShape(topNote.getType());
		setOpaque(true);
	}



	public PEDBTopNoteLabel(String text) {
		super(text);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PEDBTopNoteLabel(Icon image) {
		super(image);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PEDBTopNoteLabel(String text, int horizontalAlignment) {
		super(text, horizontalAlignment);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PEDBTopNoteLabel(Icon image, int horizontalAlignment) {
		super(image, horizontalAlignment);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PEDBTopNoteLabel(String text, Icon icon, int horizontalAlignment) {
		super(text, icon, horizontalAlignment);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	//追加
	public void setController(MuseApp main) {
		//mouseActions = createMouseActionListener(main);
		addMouseListener(mouseActions);
		addMouseMotionListener(mouseActions);
		//keyActions = createKeyActionListener(main);
		addKeyListener(keyActions);
	}

}
