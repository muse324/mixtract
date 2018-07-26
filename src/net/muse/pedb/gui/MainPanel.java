package net.muse.pedb.gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public class MainPanel extends JPanel {
	public MainPanel(JComponent... c) {
		super(new GridLayout(1, 2, 5, 5));
		Box box = Box.createVerticalBox();
		for (JComponent o : c) {
			Box b = Box.createVerticalBox();
			b.add(o);
			b.add(Box.createVerticalStrut(5));
			box.add(b);
		}
		add(new JLayer<>(new JScrollPane(box), new DragScrollLayerUI()));
		setPreferredSize(new Dimension(640, 480));
	}

	public static void main(String... args) {
		EventQueue.invokeLater(new Runnable() {
			@Override public void run() {
				createAndShowGui();
			}
		});
	}

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

}
