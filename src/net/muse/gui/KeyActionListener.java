package net.muse.gui;

import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.misc.MuseObject;

public class KeyActionListener extends KeyAdapter {

	private final MuseObject _app;
	private final Container _owner;

	public KeyActionListener(MuseObject app, Container owner) {
		super();
		_app = app;
		_owner = owner;
	}

	/*
	 * (非 Javadoc)
	 * @see
	 * java.awt.event.KeyAdapter#keyPressed(java.awt.event.KeyEvent)
	 */
	@Override public void keyPressed(KeyEvent e) {
		System.out.println(String.format("KeyActionListener: %d", e
				.getKeyCode()));
		app().butler().keyPressed(e);
		keyPressedOption(e);
	}

	protected void keyPressedOption(KeyEvent e) {
		System.out.println(String.format("keyPressedOption in %s: %d", owner()
				.getClass().getSimpleName(), e.getKeyCode()));
	}

	public MuseApp app() {
		return (MuseApp) _app;
	}

	public Container owner() {
		return _owner;
	}
}
