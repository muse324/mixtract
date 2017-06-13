package net.muse.mixtract.gui;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;

import net.muse.mixtract.data.*;

public class PartSelectorPanel extends JPanel implements TuneDataListener {

	private static final long serialVersionUID = 1L;
	private TuneData data = null;
	private ArrayList<JRadioButton> checkBoxList = new ArrayList<JRadioButton>(); // @jve:decl-index=0:
	private ButtonGroup editGroup = new ButtonGroup();

	public PartSelectorPanel() {
		super();
		initialize();
	}

	public PartSelectorPanel(LayoutManager layout) {
		super(layout);
		initialize();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PartSelectorPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		initialize();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public PartSelectorPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		initialize();
		// TODO 自動生成されたコンストラクター・スタブ
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setSize(new Dimension(58, 89));
	}

	public void addGroup(Group g) {
		// TODO 自動生成されたメソッド・スタブ
	}

	public void deleteGroup(GroupLabel g) {
		// TODO 自動生成されたメソッド・スタブ
	}

	public void editGroup(GroupLabel g) {
		// TODO 自動生成されたメソッド・スタブ
	}

	public void selectGroup(GroupLabel g, boolean flg) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void deselect(GroupLabel g) {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void setTarget(TuneData target) {
		if (data != target) {
			data = target;
			removeAll();
			checkBoxList.clear();
			if (data == null)
				return;
			for (int i = 0; i < data.getRootGroup().size(); i++) {
				// 表示リストに追加
				JRadioButton cbox = new JRadioButton();
				cbox.setText(String.format("P%d", i + 1));
				cbox.setSelected(true);
				editGroup.add(cbox);
				checkBoxList.add(cbox);
				add(cbox, null);
				cbox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						int idx = checkBoxList.indexOf(e.getItem());
						// TODO 選択パートを変更する →同期処理が要る
					}
				});
			}
		}
	}

	public void changeExpression(PhraseCurveType type) {
		// TODO 自動生成されたメソッド・スタブ

	}

} // @jve:decl-index=0:visual-constraint="10,10"
