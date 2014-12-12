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
import java.util.List;

public final class AggregatedExecutionEntry {

	private final List<AggregatedExecutionEntry> children = new ArrayList<>();
	private final String failedCause;
	private final String container;
	private final String component;
	private final String operation;
	private long minDuration;
	private long maxDuration;
	private long avgDuration;
	private int calls;

	public AggregatedExecutionEntry(final ExecutionEntry execEntry) {
		this.container = execEntry.getContainer();
		this.component = execEntry.getComponent();
		this.operation = execEntry.getOperation();
		this.failedCause = execEntry.getFailedCause();
		this.minDuration = execEntry.getDuration();
		this.maxDuration = execEntry.getDuration();

		for (final ExecutionEntry child : execEntry.getChildren()) {
			this.children.add(new AggregatedExecutionEntry(child));
		}
	}

	public int getTraceDepth() {
		int traceDepth = this.children.isEmpty() ? 0 : 1;

		int maxChildrenTraceDepth = 0;
		for (final AggregatedExecutionEntry child : this.children) {
			maxChildrenTraceDepth = Math.max(maxChildrenTraceDepth, child.getTraceDepth());
		}
		traceDepth += maxChildrenTraceDepth;

		return traceDepth;
	}

	public int getTraceSize() {
		int traceSize = 1;

		for (final AggregatedExecutionEntry child : this.children) {
			traceSize += child.getTraceSize();
		}
		return traceSize;
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

	public void incrementCalls(final ExecutionEntry executionEntry) {
		this.calls++;
		this.minDuration = Math.min(this.minDuration, executionEntry.getDuration());
		this.maxDuration = Math.max(this.maxDuration, executionEntry.getDuration());
		this.avgDuration += executionEntry.getDuration();
	}

	public void recalculateValues() {
		this.avgDuration /= this.calls;
	}

	public long getMinDuration() {
		return this.minDuration;
	}

	public long getMaxDuration() {
		return this.maxDuration;
	}

	public long getAvgDuration() {
		return this.avgDuration;
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
