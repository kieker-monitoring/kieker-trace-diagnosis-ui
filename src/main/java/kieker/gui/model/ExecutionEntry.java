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

package kieker.gui.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A simplified representation of an execution within a trace.
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

		this.updatePercent();
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

		this.updatePercent();
	}

	public ExecutionEntry getParent() {
		return this.parent;
	}

	private void updatePercent() {
		if (this.parent != null) {
			this.percent = (this.duration * 100.0f) / this.parent.duration;
		} else {
			this.percent = 100.0f;
		}
		for (final ExecutionEntry child : this.children) {
			child.updatePercent();
		}
	}

}
