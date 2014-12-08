package kieker.gui.model;

import java.util.ArrayList;
import java.util.List;

public final class AggregatedExecutionEntry {

	private final List<AggregatedExecutionEntry> children = new ArrayList<>();
	private final String failedCause;
	private final String container;
	private final String component;
	private final String operation;
	private int calls;

	public AggregatedExecutionEntry(final ExecutionEntry execEntry) {
		this.container = execEntry.getContainer();
		this.component = execEntry.getComponent();
		this.operation = execEntry.getOperation();
		this.failedCause = execEntry.getFailedCause();

		for (final ExecutionEntry child : execEntry.getChildren()) {
			this.children.add(new AggregatedExecutionEntry(child));
		}
	}

	public List<AggregatedExecutionEntry> getChildren() {
		return this.children;
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

	public String getFailedCause() {
		return this.failedCause;
	}

	public boolean isFailed() {
		return (this.failedCause != null);
	}
}
