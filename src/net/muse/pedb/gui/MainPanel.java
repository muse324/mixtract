package net.muse.pedb.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
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
	private JLayer<JScrollPane> layer;

	public static void createAndShowGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}
		final JFrame frame = new JFrame("@title@");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.getContentPane().add(new MainPanel());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String... args) {
		EventQueue.invokeLater(() -> createAndShowGui());
	}

	public MainPanel(JComponent... c) {
		super(new BorderLayout());

		final JSplitPane header = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		final JSplitPane mainPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		for (final JComponent o : c) {
			if (o instanceof GroupingPanel) {
				mainPane.setTopComponent(o);
			} else if (o instanceof PianoRoll) {
				mainPane.setBottomComponent(o);
			} else if (o instanceof PartSelectorPanel) {
				header.setTopComponent(o);
			} else if (o instanceof KeyBoard) {
				header.setBottomComponent(o);
			}
		}
		final PropertyChangeListener pcl = e -> {
			if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(e
					.getPropertyName())) {
				final JSplitPane source = (JSplitPane) e.getSource();
				final int location = ((Integer) e.getNewValue()).intValue();
				final JSplitPane target = source == header ? mainPane : header;
				if (location != target.getDividerLocation())
					target.setDividerLocation(location);
			}
		};
		header.addPropertyChangeListener(pcl);
		mainPane.addPropertyChangeListener(pcl);

		header.setDividerLocation(150);
		mainPane.setDividerLocation(150);
		add(header, BorderLayout.WEST);
		layer = new JLayer<>(new JScrollPane(mainPane), new DragScrollLayerUI());
		add(layer,
				BorderLayout.CENTER);
		setSize(new Dimension(640, 480));
	}

	public JLayer<JScrollPane> getLayer() {
		return layer;
	}

}
