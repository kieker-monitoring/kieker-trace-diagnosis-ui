/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.gui.model.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simplified representation of an execution within a trace. As an instance of this class can contain other instances, it can be used to represent a trace tree.
 *
 * @author Nils Christian Ehmke
 */
public final class ExecutionEntry {

	private final long traceID;
	private final String container;
	private final String component;
	private final String operation;

	private float percent;
	private long duration;
	private String failedCause;
	private ExecutionEntry parent;
	private final List<ExecutionEntry> children = new ArrayList<>();

	public ExecutionEntry(final long traceID, final String container, final String component, final String operation) {
		this.traceID = traceID;
		this.container = container;
		this.component = component;
		this.operation = operation;
	}

	public int getStackDepth() {
		int stackDepth = this.children.isEmpty() ? 0 : 1;

		int maxChildrenStackDepth = 0;
		for (final ExecutionEntry child : this.children) {
			maxChildrenStackDepth = Math.max(maxChildrenStackDepth, child.getStackDepth());
		}
		stackDepth += maxChildrenStackDepth;

		return stackDepth;
	}

	public long getTraceID() {
		return this.traceID;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
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

	public float getPercent() {
		return this.percent;
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

	public List<ExecutionEntry> getChildren() {
		return this.children;
	}

	public void addExecutionEntry(final ExecutionEntry entry) {
		this.children.add(entry);
		entry.parent = this;
	}

	public ExecutionEntry getParent() {
		return this.parent;
	}

	public void recalculateValues() {
		this.updatePercent();
	}

	private void updatePercent() {
		if (this.parent != null) {
			this.percent = (this.duration * 100.0f) / this.parent.duration;
		} else {
			this.percent = 100.0f;
		}
		for (final ExecutionEntry executionEntry : this.children) {
			executionEntry.updatePercent();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.children == null) ? 0 : this.children.hashCode());
		result = (prime * result) + ((this.component == null) ? 0 : this.component.hashCode());
		result = (prime * result) + ((this.container == null) ? 0 : this.container.hashCode());
		result = (prime * result) + ((this.failedCause == null) ? 0 : this.failedCause.hashCode());
		result = (prime * result) + ((this.operation == null) ? 0 : this.operation.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof ExecutionEntry)) {
			return false;
		}
		final ExecutionEntry otherEntry = (ExecutionEntry) other;
		if (!this.container.equals(otherEntry.container)) {
			return false;
		}
		if (!this.component.equals(otherEntry.component)) {
			return false;
		}
		if (!this.operation.equals(otherEntry.operation)) {
			return false;
		}
		if (this.failedCause == null) {
			if (otherEntry.failedCause != null) {
				return false;
			}
		} else {
			if (!this.failedCause.equals(otherEntry.failedCause)) {
				return false;
			}
		}
		if (this.children.size() != otherEntry.children.size()) {
			return false;
		}

		final Iterator<ExecutionEntry> ownChildrenIterator = this.children.iterator();
		final Iterator<ExecutionEntry> otherChildrenIterator = otherEntry.children.iterator();

		while (ownChildrenIterator.hasNext()) {
			final ExecutionEntry ownChild = ownChildrenIterator.next();
			final ExecutionEntry otherChild = otherChildrenIterator.next();

			if (!ownChild.equals(otherChild)) {
				return false;
			}
		}

		return true;
	}

}
