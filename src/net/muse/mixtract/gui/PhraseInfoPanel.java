package net.muse.mixtract.gui;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.gui.TuneDataListener;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class PhraseInfoPanel extends JPanel implements TuneDataListener {

	private static final String DEFAULT_TEXT = "Group: -----";
	private Group group;
	private final JLabel groupNameLabel = new JLabel("----");
	private TitledBorder border = BorderFactory.createTitledBorder(
			DEFAULT_TEXT);

	public PhraseInfoPanel() {
		super();
		setLayout(new GridLayout(5, 2));
		setBorder(border);
	}

	@Override public void addGroup(Group g) {}

	@Override public void deleteGroup(GroupLabel g) {}

	@Override public void editGroup(GroupLabel g) {}

	@Override public void selectGroup(GroupLabel g, boolean flg) {
		setGroup(g.group());
	}

	private void setGroup(Group g) {
		group = g;
		border.setTitle("Group: " + group.name());
		repaint();
	}

	@Override public void deselect(GroupLabel g) {
		group = null;
		border.setTitle(DEFAULT_TEXT);
		repaint();
	}

	@Override public void setTarget(TuneData data) {
		setGroup(data.getRootGroup().get(0));
	}

	@Override public void changeExpression(PhraseCurveType type) {}

	public JLabel getGroupNameLabel() {
		return groupNameLabel;
	}

	@Override public void selectTopNote(NoteData note, boolean b) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
