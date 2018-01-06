package net.muse.gui;

import java.awt.Container;
import java.awt.event.KeyAdapter;

import net.muse.app.MuseApp;

public class KeyActionListener extends KeyAdapter {

	private MuseApp _main;
	private Container _owner;

	public KeyActionListener(MuseApp main, Container owner) {
		super();
		_main = main;
		_owner = owner;
	}

}
