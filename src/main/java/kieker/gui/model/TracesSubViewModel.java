package kieker.gui.model;

import java.util.Observable;

import kieker.gui.model.domain.ExecutionEntry;

public final class TracesSubViewModel extends Observable {

	private ExecutionEntry currentActiveTrace;

	public ExecutionEntry getCurrentActiveTrace() {
		return this.currentActiveTrace;
	}

	public void setCurrentActiveTrace(final ExecutionEntry currentActiveTrace) {
		this.currentActiveTrace = currentActiveTrace;

		this.setChanged();
		this.notifyObservers();
	}

}
