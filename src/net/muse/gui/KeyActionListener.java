package net.muse.gui;

import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.misc.MuseObject;

public class KeyActionListener extends KeyAdapter {

	private final MuseObject _main;
	private final Container _owner;

	public KeyActionListener(MuseObject main, Container owner) {
		super();
		_main = main;
		_owner = owner;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override public void keyPressed(KeyEvent e) {
		main().butler().keyPressed(e);
		keyPressedOption(e);
	}

	protected void keyPressedOption(KeyEvent e) {}

	public MuseApp main() {
		return (MuseApp) _main;
	}

	public Container owner() {
		return _owner;
	}
}
