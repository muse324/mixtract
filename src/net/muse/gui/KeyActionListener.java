package net.muse.gui;

import java.awt.Container;
import java.awt.event.KeyAdapter;

import net.muse.app.MuseApp;

public class KeyActionListener extends KeyAdapter {

	private final MuseApp _main;
	private final Container _owner;

	public KeyActionListener(MuseApp main, Container owner) {
		super();
		_main = main;
		_owner = owner;
	}

	public MuseApp main() {
		return _main;
	}

	public Container owner() {
		return _owner;
	}

}
