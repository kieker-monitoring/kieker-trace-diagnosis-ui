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

import java.util.Iterator;

/**
 * A simplified representation of an execution within a trace. As an instance of this class can contain other instances, it can be used to represent a trace tree.
 *
 * @author Nils Christian Ehmke
 */
public final class Execution extends AbstractExecution<Execution> {

	private final long traceID;

	private float percent;
	private long duration;

	public Execution(final long traceID, final String container, final String component, final String operation) {
		super(container, component, operation);
		this.traceID = traceID;
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

	public float getPercent() {
		return this.percent;
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
		for (final Execution executionEntry : this.children) {
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
		if (!(other instanceof Execution)) {
			return false;
		}
		final Execution otherEntry = (Execution) other;
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

		final Iterator<Execution> ownChildrenIterator = this.children.iterator();
		final Iterator<Execution> otherChildrenIterator = otherEntry.children.iterator();

		while (ownChildrenIterator.hasNext()) {
			final Execution ownChild = ownChildrenIterator.next();
			final Execution otherChild = otherChildrenIterator.next();

			if (!ownChild.equals(otherChild)) {
				return false;
			}
		}

		return true;
	}

}
