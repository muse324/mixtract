package net.muse.mixtract.gui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;

import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.gui.TuneDataListener;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class PartSelectorPanel extends JPanel implements TuneDataListener {

	private static final long serialVersionUID = 1L;
	private TuneData data = null;
	private ArrayList<JRadioButton> checkBoxList = new ArrayList<JRadioButton>(); // @jve:decl-index=0:
	private ButtonGroup editGroup = new ButtonGroup();

	public PartSelectorPanel() {
		super();
		initialize();
	}

	public void setTarget(TuneData target) {
		// 画面リセット
		removeAll();
		checkBoxList.clear();

		// 楽曲データ代入
		if (data == target || data == null)
			return;

		data = target;
		for (int i = 0; i < data.getRootGroup().size(); i++) {
			// 表示リストにラジオボタンを追加
			int partNumber = data.getRootGroup(i).getPartNumber();
			JRadioButton cbox = new JRadioButton();
			cbox.setText(String.format("Part %d", partNumber));
			cbox.setSelected(i == 0);
			editGroup.add(cbox);
			checkBoxList.add(cbox);
			add(cbox, null);
			cbox.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					((JRadioButton) e.getItem()).setSelected(true);
					// TODO パートの選択状態を変更して表示を更新する処理
				}
			});
		}
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setSize(new Dimension(58, 89));
	}

	@Override
	public void addGroup(Group g) {}

	@Override
	public void deleteGroup(GroupLabel g) {}

	@Override
	public void editGroup(GroupLabel g) {}

	@Override
	public void selectGroup(GroupLabel g, boolean flg) {}

	@Override
	public void deselect(GroupLabel g) {}

	@Override
	public void changeExpression(PhraseCurveType type) {}

} // @jve:decl-index=0:visual-constraint="10,10"
