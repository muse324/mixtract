package net.muse.misc;

import java.io.File;

import jp.crestmuse.cmx.filewrappers.CMXFileWrapper;
import net.muse.app.MuseApp;
import net.muse.data.Group;
import net.muse.gui.KeyBoard;
import net.muse.mixtract.data.MXTuneData;

public enum OptionType {
	KEYBOARD_WIDTH {
		@Override
		public void exe(MuseApp app, String property) {
			KeyBoard.setKeyWidth(Integer.parseInt(property));
		}
	},
	MAXIMUM_MIDICHANNEL {
		@Override
		public void exe(MuseApp app, String property) {
			MXTuneData.setMaximumMIDIChannel(Integer.parseInt(property));
		}
	},
	INPUT_FILENAME {
		@Override
		public void exe(MuseApp app, String property) {
			app.setInputFileName(property);
		}
	},
	OUTPUT_FILENAME {
		@Override
		public void exe(MuseApp app, String property) {
			app.setOutputFileName(property);
		}
	},
	APPLICATION_LOGO {
		@Override
		public void exe(MuseApp app, String property) {
			app.setAppImageFile(property);
		}
	},
	CMXCATALOG {
		@Override
		public void exe(MuseApp app, String property) {
			CMXFileWrapper.catalogFileName = property;
		}
	},
	MIDIDEVICE {
		@Override
		public void exe(MuseApp app, String property) {
			app.setMidiDeviceName(property);
		}
	},
	MUSICXML_DIR {
		@Override
		public void exe(MuseApp app, String property) {
			app.setMusicXMLDirectory(createDirectory(new File(property)
					.getAbsolutePath()));
		}
	},
	PROJECT_DIR {
		@Override
		public void exe(MuseApp app, String property) {
			app.setProjectDirectory(createDirectory(new File(property)
					.getAbsolutePath()));
		}
	},
	OUTPUT_DIR {
		@Override
		public void exe(MuseApp app, String property) {
			app.setOutputDirectory(createDirectory(new File(property)
					.getAbsolutePath()));
		}
	},
	segmentGroupnoteLine {
		@Override
		public void exe(MuseApp app, String property) {
			MXTuneData.setSegmentGroupnoteLine(Boolean.parseBoolean(property));
		}
	},
	SHOW_GUI {
		@Override
		public void exe(MuseApp app, String property) {
			MuseApp.setShowGUI(Boolean.parseBoolean(property));
		}
	},
	READ_STRDATA_ON_READING {
		@Override
		public void exe(MuseApp app, String property) {
			app.setReadingStructureData(Boolean.parseBoolean(property));
		}

	},
	avoidLastRestsAsGroup {
		@Override
		public void exe(MuseApp app, String property) {
			Group.setAvoidLastRestsFromGroup(Boolean.parseBoolean(property));
		}
	},
	durationOffset {
		@Override
		public void exe(MuseApp app, String property) {
			MXTuneData.setDurationOffset(Integer.parseInt(property));
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
	public abstract void exe(MuseApp app, String property);
}