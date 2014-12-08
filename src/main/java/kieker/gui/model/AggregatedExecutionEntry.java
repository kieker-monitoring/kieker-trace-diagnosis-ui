package kieker.gui.model;

public final class AggregatedExecutionEntry {

	private final String container;
	private final String component;
	private final String operation;
	private int calls;

	public AggregatedExecutionEntry(final String container, final String component, final String operation) {
		this.container = container;
		this.component = component;
		this.operation = operation;
	}

	public String getContainer() {
		return this.container;
	}

	public String getComponent() {
		return this.component;
	}

	public String getOperation() {
		return this.operation;
	}

	public void incrementCalls() {
		this.calls++;
	}

	public int getCalls() {
		return this.calls;
	}

}
