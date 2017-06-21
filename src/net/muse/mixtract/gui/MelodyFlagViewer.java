package net.muse.mixtract.gui;

import java.awt.*;

import javax.swing.*;

import jp.crestmuse.cmx.filewrappers.MusicXMLWrapper.Note;
import net.muse.data.NoteData;
import net.muse.mixtract.data.*;

public class MelodyFlagViewer extends JPanel {

	private static final long serialVersionUID = 1L;

	private final Group group;

	private final PhraseFeature flag;

	public MelodyFlagViewer(Group group) {
		this.group = group;
		flag = group.getMelodyFlagment();
		JPanel graphPanel = new GroupGraph();
		JPanel textPanel = new JPanel();
		textPanel.add(new JLabel("Start note = " + group.getBeginGroupNote()));
		textPanel.add(new JLabel("Top note   = " + group.getTopGroupNote()));
		// textPanel.add(new JLabel("Center note   = "
		// + group.getCenterNote(Shunji_System.getTicksPerBeat())));
		textPanel.add(new JLabel("End note   = " + group.getEndGroupNote()));
		textPanel.add(new JLabel("Reduction Level = " + group.getLevel()));
		textPanel.add(new JLabel("Former group = " + group.getChildFormerGroup()));
		textPanel.add(new JLabel("Latter group = " + group.getChildLatterGroup()));
		textPanel.add(new JLabel("c1 (slope of the fomer)  = " + flag.getCt1()));
		textPanel.add(new JLabel("c2 (slope of the latter) = " + flag.getCt2()));
		textPanel.add(new JLabel("c3 (ratio of the fomer length) = "
				+ flag.getCt3()));
		textPanel.add(new JLabel("c4 (interval from the top note = "
				+ flag.getCt4()));
		textPanel.add(new JLabel("c5 (length of the target) = " + flag.getCt5()));
		textPanel.add(new JLabel("Rhythm vector: " + flag.getRhythmVector()));
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.PAGE_AXIS));
		add(graphPanel);
		add(textPanel);
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

	}

	public static MelodyFlagViewer createNewViewer(Group group) {
		return new MelodyFlagViewer(group);
	}

	public class GroupGraph extends JPanel {

		private static final long serialVersionUID = 1L;

		private static final int offset = 10;

		private static final int height = 100;

		private static final int width = 200;

		private int maxNoteNumber;

		private int minNoteNumber;

		GroupGraph() {
			setLayout(null);
			setFocusable(false);
			setBackground(Color.white);
			setPreferredSize(new Dimension(width, height));

			// 旋律外形の音域を求める．
			// 開始音，頂点音（もしくは中央音），その次の音，終了音の音高を比較する．
			NoteData bg = group.getBeginGroupNote().getNote();
			Note n1 = flag.getFormerLastNote();
			Note n2 = flag.getLatterFirstNote();
			NoteData ed = group.getEndGroupNote().getNote();

			if (bg.noteNumber() < n1.notenum()) {
				minNoteNumber = bg.noteNumber();
				maxNoteNumber = n1.notenum();
			} else {
				minNoteNumber = n1.notenum();
				maxNoteNumber = bg.noteNumber();
			}
			if (n2.notenum() < minNoteNumber)
				minNoteNumber = n2.notenum();
			if (n2.notenum() > maxNoteNumber)
				maxNoteNumber = n2.notenum();

			if (ed.noteNumber() < minNoteNumber)
				minNoteNumber = ed.noteNumber();
			if (ed.noteNumber() > maxNoteNumber)
				maxNoteNumber = ed.noteNumber();
			System.out.println("min = " + minNoteNumber);
			System.out.println("max = " + maxNoteNumber);
		}

		/*
		 * (non-Javadoc)
		 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
		 */
		@Override protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.black);
			g2.translate(offset, offset);
			int h = height - offset * 2;
			// 軸線
			g2.drawOval(0, 0, 0, h);
			g2.drawOval(width, 0, 0, h);
			g2.drawOval(0, h, width, 0);

			// 旋律概形
			double ylen = maxNoteNumber - minNoteNumber;
			int y0 = (int) (h - (group.getBeginGroupNote().getNote().noteNumber() - minNoteNumber)
					/ ylen * h);
			int y1 = (int) (h - (flag.getFormerLastNote().notenum() - minNoteNumber)
					/ ylen * h);
			int y2 = (int) (h - (flag.getLatterFirstNote().notenum() - minNoteNumber)
					/ ylen * h);
			int y3 = (int) (h - (group.getEndGroupNote().getNote().noteNumber() - minNoteNumber)
					/ ylen * h);
			int formerLength = (int) (width * flag.getCt3());
			g2.setColor(Color.red);
			g2.drawLine(0, y0, formerLength, y1);
			g2.drawLine(formerLength, y2, width, y3);
			g2.setColor(Color.gray);
			g2.drawLine(formerLength, y1, formerLength, y2);
		}
	}

}
