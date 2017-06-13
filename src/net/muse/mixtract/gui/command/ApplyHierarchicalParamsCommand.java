package net.muse.mixtract.gui.command;

import net.muse.mixtract.data.Group;
import net.muse.mixtract.data.PhraseCurve;
import net.muse.mixtract.gui.MixtractCommand;

public final class ApplyHierarchicalParamsCommand extends MixtractCommand {

	private PhraseCurve cv;
	private Group group;

	public ApplyHierarchicalParamsCommand(String... lang) {
		super(lang);
	}

	/*
	 * (non-Javadoc)
	 * @see net.muse.misc.Command#execute()
	 */
	@Override public void execute() {
		_target.calculateHierarchicalParameters();
		if (cv != null) {
			cv.apply(_target,group);
		}
		_main.notifyChangeHierarchicalParameters(cv.getType());
		_mainFrame.getTempoView().repaint();
		_mainFrame.getDynamicsView().repaint();
	}

	/**
	 * @param group
	 * @param cv
	 */
	public void setCurve(Group group, PhraseCurve cv) {
		this.group = group;
		this.cv = cv;
	}

	/**
	 * @return
	 */
	public PhraseCurve getCurve() {
		return cv;
	}

}
