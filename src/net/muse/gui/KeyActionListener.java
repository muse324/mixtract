package net.muse.gui;

import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import net.muse.app.MuseApp;
import net.muse.misc.MuseObject;

public class KeyActionListener extends KeyAdapter {

	private final MuseObject _app;
	private final Container _self;

	public KeyActionListener(MuseApp app, Container owner) {
		super();
		_app = app;
		_self = owner;
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
		System.out.println(String.format("%s in KeyActionListener.keyPressedOption: %d", self()
				.getClass().getSimpleName(), e.getKeyCode()));
	}

	public MuseApp app() {
		return (MuseApp) _app;
	}

	public Container self() {
		return _self;
	}

}
