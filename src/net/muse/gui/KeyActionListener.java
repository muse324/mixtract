package net.muse.gui;

import java.awt.Container;
import java.awt.event.KeyAdapter;

import net.muse.misc.MuseObject;

public class KeyActionListener extends KeyAdapter {

	private final MuseObject _main;
	private final Container _owner;

	public KeyActionListener(MuseObject main, Container owner) {
		super();
		_main = main;
		_owner = owner;
	}

	public MuseObject main() {
		return _main;
	}

	public Container owner() {
		return _owner;
	}

}
