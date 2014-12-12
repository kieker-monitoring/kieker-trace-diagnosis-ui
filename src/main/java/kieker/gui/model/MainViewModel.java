package kieker.gui.model;

import java.util.Observable;

public final class MainViewModel extends Observable {

	private SubView currentActiveSubView = SubView.NONE;

	public SubView getCurrentActiveSubView() {
		return this.currentActiveSubView;
	}

	public void setCurrentActiveSubView(final SubView currentActiveSubView) {
		this.currentActiveSubView = currentActiveSubView;

		this.setChanged();
		this.notifyObservers();
	}

	public enum SubView {
		RECORDS_SUB_VIEW, TRACES_SUB_VIEW, AGGREGATED_TRACES_SUB_VIEW, NONE
	}

}
