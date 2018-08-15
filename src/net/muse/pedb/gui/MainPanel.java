package net.muse.pedb.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import net.muse.gui.GroupingPanel;
import net.muse.gui.KeyBoard;
import net.muse.gui.PianoRoll;
import net.muse.mixtract.gui.PartSelectorPanel;

public class MainPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public static void createAndShowGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		JFrame frame = new JFrame("@title@");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new MainPanel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String... args) {
		EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				createAndShowGui();
			}
		});
	}

	public MainPanel(JComponent... c) {
		super(new BorderLayout());

		JSplitPane header = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		for (JComponent o : c) {
			if (o instanceof GroupingPanel) {
				main.setTopComponent(o);
			} else if (o instanceof PianoRoll) {
				main.setBottomComponent(o);
			} else if (o instanceof PartSelectorPanel) {
				header.setTopComponent(o);
			} else if (o instanceof KeyBoard) {
				header.setBottomComponent(o);
			}
		}
		PropertyChangeListener pcl = new PropertyChangeListener() {
			@Override public void propertyChange(PropertyChangeEvent e) {
				if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(e
						.getPropertyName())) {
					JSplitPane source = (JSplitPane) e.getSource();
					int location = ((Integer) e.getNewValue()).intValue();
					JSplitPane target = (source == header) ? main : header;
					if (location != target.getDividerLocation())
						target.setDividerLocation(location);
				}
			}
		};
		header.addPropertyChangeListener(pcl);
		main.addPropertyChangeListener(pcl);

		header.setDividerLocation(150);
		main.setDividerLocation(150);
		add(header, BorderLayout.WEST);
		add(new JLayer<>(new JScrollPane(main), new DragScrollLayerUI()),
				BorderLayout.CENTER);
		setPreferredSize(new Dimension(640, 480));
	}

}
