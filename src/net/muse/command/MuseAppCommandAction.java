package net.muse.command;

public interface MuseAppCommandAction extends Runnable {
	public MuseAppCommand command();
	public String name();
}
