package net.muse.app;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.muse.data.Group;
import net.muse.data.TuneData;
import net.muse.gui.MainFrame;
import net.muse.mixtract.command.MixtractCommandType;
import net.muse.mixtract.data.MXGroup;
import net.muse.mixtract.data.MXGroupAnalyzer;
import net.muse.mixtract.data.MXTuneData;
import net.muse.pedb.command.PEDBCommandType;
import net.muse.pedb.data.PEDBConcierge;
import net.muse.pedb.data.PEDBTuneData;
import net.muse.pedb.gui.PEDBMainFrame;

public class PEDBStructureEditor extends MuseApp {

	private PEDBStructureEditor(String[] args) throws FileNotFoundException,
			IOException {
		super(args);
	}

	public static void main(String[] args) {
		try {
			final PEDBStructureEditor app = new PEDBStructureEditor(args);
			app.setup();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override public void analyzeStructure(TuneData data, Group group) {
		assert data != null && data instanceof MXTuneData;
		if (group == null)
			return;
		assert group instanceof MXGroup;
		final MXGroupAnalyzer ana = new MXGroupAnalyzer((MXTuneData) data,
				false);
		ana.setRootGroup((MXGroup) group);
		ana.run();
		getAnalyzer().add(ana);
	}

	@Override public PEDBConcierge butler() {
		return (PEDBConcierge) super.butler();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.app.MuseApp#createTuneData(java.io.File, java.io.File)
	 */
	@Override public void createTuneData(File in, File out) throws IOException {
		setData(new PEDBTuneData(in, out));
	}

	@Override public PEDBTuneData data() {
		return (PEDBTuneData) super.data();
	}

	/*
	 * (非 Javadoc)
	 * @see net.muse.gui.MuseGUIObject#getFrame()
	 */
	@Override public PEDBMainFrame getFrame() {
		return (PEDBMainFrame) super.getFrame();
	}

	@Override public MainFrame mainFrame() throws IOException {
		if (getFrame() == null)
			return new PEDBMainFrame(this);
		return getFrame();
	}

	@Override protected PEDBConcierge createConcierge() {
		return new PEDBConcierge(this);
	}

	@Override protected void initialize() {
		setAppImageFile("mixtract-logo.png");
		PROPERTY_FILENAME = "Mixtract.properties";
		projectFileExtension = ".mxt";
	}

	@Override protected void setup() throws Exception {
		if (!isShowGUI()) {
			butler().readfile();
			return;
		}
		// MacOSXでのJava実行環境用のシステムプロパティの設定.
		setupSystemPropertiesForMacOSX();

		// システム標準のL&Fを設定.
		// MacOSXならAqua、WindowsXPならLuna、Vista/Windows7ならばAeroになる.
		// Aeroの場合、メニューに表示されるニーモニックのアンダースコアはALTキーを押さないとでてこない.
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		/* sprash screen */
		createSplashScreen(getAppImageFile());
		EventQueue.invokeLater(() -> showSplashScreen());

		// create mainFrame
		createNewFrame();
		getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		JFrame.setDefaultLookAndFeelDecorated(false);
		getFrame().pack(); // ウィンドウサイズを最適化
		getFrame().setVisible(true); // ウィンドウを表示させる

		// 長い処理のdummy
		try {
			Thread.sleep(2000);
		} catch (final InterruptedException e) {
			butler().printConsole(e.getMessage());
		}
		EventQueue.invokeLater(() -> hideSplash());
	}

	@Override protected void setupCommands() {
		super.setupCommands();
		for (final MixtractCommandType e : MixtractCommandType.values())
			getCommandList().add(e);
		for (final PEDBCommandType e : PEDBCommandType.values())
			getCommandList().add(e);
	}
}
