package net.muse.mixtract.gui;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.muse.data.Group;
import net.muse.data.NoteData;
import net.muse.data.TuneData;
import net.muse.gui.GroupLabel;
import net.muse.gui.TuneDataListener;
import net.muse.mixtract.data.MXTuneData;
import net.muse.mixtract.data.curve.PhraseCurveType;

public class PartSelectorPanel extends JPanel implements TuneDataListener {

	private static final long serialVersionUID = 1L;
	private TuneData data = null;
	private ArrayList<JRadioButton> partCheckBoxList = new ArrayList<JRadioButton>(); // @jve:decl-index=0:
	private ButtonGroup partEditGroup = new ButtonGroup();
	private ArrayList<JRadioButton> voiceCheckBoxList = new ArrayList<JRadioButton>(); // @jve:decl-index=0:
	private ButtonGroup voiceEditGroup = new ButtonGroup();

	public PartSelectorPanel() {
		super();
		initialize();
	}

	/* (非 Javadoc)
	 * @see net.muse.gui.TuneDataListener#setTarget(net.muse.data.TuneData)
	 */
	public void setTarget(TuneData target) {
		// 画面リセット
		removeAll();
		partCheckBoxList.clear();
		voiceCheckBoxList.clear();

		// 楽曲データ代入
		if (data == target || target == null)
			return;

		data = target;
		JPanel partPanel = new JPanel();
		JPanel voicePanel = new JPanel();
		int[] voiceList = new int[MXTuneData.getMaxmimumMIDICchannel()];
		for (int i = 0; i < data.getRootGroup().size(); i++) {
			// 表示リストにパート選択のラジオボタンを追加
			partPanel.add(createPartSelectorBox(i), null);
			// 表示リストにボイス選択のラジオボタンを追加
			parseVoiceAssignedToNote(voiceList, data.getRootGroup(i)
					.getBeginNote());
			int v = 1;
			for (int x = 0; x < voiceList.length; x++) {
				if (voiceList[x] <= 0)
					continue;
				voicePanel.add(createVoiseSelectorBox(i, v), null);
			}
		}
		add(partPanel);
		add(voicePanel);
	}

	private JRadioButton createVoiseSelectorBox(int i, int v) {
		JRadioButton vcbox = new JRadioButton();
		vcbox.setText(String.format("Voice %d", v++));
		vcbox.setSelected(i == 0);
		voiceEditGroup.add(vcbox);
		voiceCheckBoxList.add(vcbox);
		return vcbox;
	}

	private void parseVoiceAssignedToNote(int[] voiceList, NoteData n) {
		if (n == null)
			return;
		voiceList[n.xmlVoice() - 1]++;
		parseVoiceAssignedToNote(voiceList, n.child());
		parseVoiceAssignedToNote(voiceList, n.next());
	}

	private JRadioButton createPartSelectorBox(int i) {
		int partNumber = data.getRootGroup(i).getPartNumber();
		JRadioButton pcbox = new JRadioButton();
		pcbox.setText(String.format("Part %d", partNumber));
		pcbox.setSelected(i == 0);
		partEditGroup.add(pcbox);
		partCheckBoxList.add(pcbox);
		pcbox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				((JRadioButton) e.getItem()).setSelected(true);
				// TODO パートの選択状態を変更して表示を更新する処理
			}
		});
		return pcbox;
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
