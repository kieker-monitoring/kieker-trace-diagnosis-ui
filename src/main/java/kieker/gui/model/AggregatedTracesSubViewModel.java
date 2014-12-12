package kieker.gui.model;

import java.util.Observable;

import kieker.gui.model.domain.AggregatedExecutionEntry;

public final class AggregatedTracesSubViewModel extends Observable {

	private AggregatedExecutionEntry currentActiveTrace;

	public AggregatedExecutionEntry getCurrentActiveTrace() {
		return this.currentActiveTrace;
	}

	public void setCurrentActiveTrace(final AggregatedExecutionEntry currentActiveTrace) {
		this.currentActiveTrace = currentActiveTrace;

		this.setChanged();
		this.notifyObservers();
	}

}
