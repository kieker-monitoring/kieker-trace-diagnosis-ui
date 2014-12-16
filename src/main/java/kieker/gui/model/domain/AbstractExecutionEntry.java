package kieker.gui.model.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExecutionEntry<T extends AbstractExecutionEntry<T>> {

	protected final String container;
	protected final String component;
	protected final String operation;

	protected String failedCause;
	protected T parent;
	protected final List<T> children = new ArrayList<>();

	public AbstractExecutionEntry(final String container, final String component, final String operation) {
		this.container = container;
		this.component = component;
		this.operation = operation;
	}

	public int getTraceDepth() {
		int traceDepth = this.children.isEmpty() ? 0 : 1;

		int maxChildrenTraceDepth = 0;
		for (final T child : this.children) {
			maxChildrenTraceDepth = Math.max(maxChildrenTraceDepth, child.getTraceDepth());
		}
		traceDepth += maxChildrenTraceDepth;

		return traceDepth;
	}

	public int getTraceSize() {
		int traceSize = 1;

		for (final T child : this.children) {
			traceSize += child.getTraceSize();
		}
		return traceSize;
	}

	public boolean isFailed() {
		return (this.failedCause != null);
	}

	public String getFailedCause() {
		return this.failedCause;
	}

	public void setFailedCause(final String failedCause) {
		this.failedCause = failedCause;
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

	public List<T> getChildren() {
		return this.children;
	}

	public void addExecutionEntry(final T entry) {
		this.children.add(entry);
		entry.parent = (T) this;
	}

	public T getParent() {
		return this.parent;
	}

}
