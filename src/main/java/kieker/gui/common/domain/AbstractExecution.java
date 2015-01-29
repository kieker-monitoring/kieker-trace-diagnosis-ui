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

package kieker.gui.common.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractExecution<T extends AbstractExecution<T>> {

	protected final String container;
	protected final String component;
	protected final String operation;

	protected String failedCause;
	protected T parent;
	protected final List<T> children = new ArrayList<>();

	public AbstractExecution(final String container, final String component, final String operation) {
		this.container = container;
		this.component = component;
		this.operation = operation;
	}

	public final int getTraceDepth() {
		int traceDepth = this.children.isEmpty() ? 0 : 1;

		int maxChildrenTraceDepth = 0;
		for (final T child : this.children) {
			maxChildrenTraceDepth = Math.max(maxChildrenTraceDepth, child.getTraceDepth());
		}
		traceDepth += maxChildrenTraceDepth;

		return traceDepth;
	}

	public final int getTraceSize() {
		int traceSize = 1;

		for (final T child : this.children) {
			traceSize += child.getTraceSize();
		}
		return traceSize;
	}

	public final boolean isFailed() {
		return (this.failedCause != null);
	}

	public final boolean containsFailure() {
		if (this.isFailed()) {
			return true;
		}

		for (final T child : this.children) {
			if (child.containsFailure()) {
				return true;
			}
		}

		return false;
	}

	public final String getFailedCause() {
		return this.failedCause;
	}

	public final void setFailedCause(final String failedCause) {
		this.failedCause = failedCause;
	}

	public final String getContainer() {
		return this.container;
	}

	public final String getComponent() {
		return this.component;
	}

	public final String getOperation() {
		return this.operation;
	}

	public final List<T> getChildren() {
		return this.children;
	}

	public final void addExecutionEntry(final T entry) {
		this.children.add(entry);
		entry.parent = (T) this;
	}

	public final T getParent() {
		return this.parent;
	}

}
