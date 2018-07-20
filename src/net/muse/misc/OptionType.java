package net.muse.misc;

import java.io.File;

import jp.crestmuse.cmx.filewrappers.CMXFileWrapper;
import net.muse.app.MuseApp;
import net.muse.command.MuseAppCommand;
import net.muse.data.Group;
import net.muse.gui.KeyBoard;
import net.muse.mixtract.data.MXTuneData;

public enum OptionType {
	APPLICATION_LOGO {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setAppImageFile(property);
		}
	},
	ASSERTION {
		@Override
		public void exe(MuseObject app, String property) {
			MuseObject.setAssertion(Boolean.parseBoolean(property));
		}
	},
	avoidLastRestsAsGroup {
		@Override
		public void exe(MuseObject app, String property) {
			Group.setAvoidLastRestsFromGroup(Boolean.parseBoolean(property));
		}
	},
	CMXCATALOG {
		@Override
		public void exe(MuseObject app, String property) {
			CMXFileWrapper.catalogFileName = property;
		}
	},
	DEBUG {
		@Override
		public void exe(MuseObject app, String property) {
			MuseObject.setDebugMode(Boolean.parseBoolean(property));
		}
	},
	DEFAULT_BPM {
		@Override
		public void exe(MuseObject app, String property) {
			MuseObject.setDefaultBPM(Integer.parseInt(property));
		}
	},
	DEFAULT_OFF_VELOCITY {
		@Override
		public void exe(MuseObject app, String property) {
			MuseObject.setDefaultOffVelocity(Integer.parseInt(property));
		}
	},
	DEFAULT_VELOCITY {
		@Override
		public void exe(MuseObject app, String property) {
			MuseObject.setDefaultVelocity(Integer.parseInt(property));
		}
	},
	durationOffset {
		@Override
		public void exe(MuseObject app, String property) {
			MXTuneData.setDurationOffset(Integer.parseInt(property));
		}
	},
	INPUT_FILENAME {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setInputFileName(property);
		}
	},
	KEYBOARD_WIDTH {
		@Override
		public void exe(MuseObject app, String property) {
			KeyBoard.setKeyWidth(Integer.parseInt(property));
		}
	},
	LANGUAGE {
		@Override
		public void exe(MuseObject app, String property) {
			MuseAppCommand.setLanguage(property);
		}
	},
	MAXIMUM_MIDICHANNEL {
		@Override
		public void exe(MuseObject app, String property) {
			MXTuneData.setMaximumMIDIChannel(Integer.parseInt(property));
		}
	},
	MIDIDEVICE {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setMidiDeviceName(property);
		}
	},
	MUSICXML_DIR {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setMusicXMLDirectory(createDirectory(new File(
						property).getAbsolutePath()));
		}
	},
	OUTPUT_DIR {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setOutputDirectory(createDirectory(new File(
						property).getAbsolutePath()));
		}
	},
	OUTPUT_FILENAME {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setOutputFileName(property);
		}
	},
	PROJECT_DIR {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setProjectDirectory(createDirectory(new File(
						property).getAbsolutePath()));
		}
	},
	READ_STRDATA_ON_READING {
		@Override
		public void exe(MuseObject app, String property) {
			if (app instanceof MuseApp)
				((MuseApp) app).setReadingStructureData(Boolean.parseBoolean(
						property));
		}

	},
	segmentGroupnoteLine {
		@Override
		public void exe(MuseObject app, String property) {
			MXTuneData.setSegmentGroupnoteLine(Boolean.parseBoolean(property));
		}
	},
	SHOW_GUI {
		@Override
		public void exe(MuseObject app, String property) {
			MuseApp.setShowGUI(Boolean.parseBoolean(property));
		}
	},
	TICKSPERBEAT {
		@Override
		public void exe(MuseObject app, String property) {
			app.setTicksPerBeat(Integer.parseInt(property));
		}
	};

	private static File createDirectory(String path) {
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		return dir;
	}

	/**
	 * @param app
	 * @param property
	 */
	public abstract void exe(MuseObject app, String property);

}