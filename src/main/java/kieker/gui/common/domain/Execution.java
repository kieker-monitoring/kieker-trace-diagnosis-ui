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
		if (this.getParent() != null) {
			this.percent = (this.duration * 100.0f) / this.getParent().duration;
		} else {
			this.percent = 100.0f;
		}
		for (final Execution executionEntry : this.getChildren()) {
			executionEntry.updatePercent();
		}
	}

	@Override
	public int hashCode() { // NOPMD (This method violates some metrics
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.getChildren() == null) ? 0 : this.getChildren().hashCode());
		result = (prime * result) + ((this.getComponent() == null) ? 0 : this.getComponent().hashCode());
		result = (prime * result) + ((this.getContainer() == null) ? 0 : this.getContainer().hashCode());
		result = (prime * result) + ((this.getFailedCause() == null) ? 0 : this.getFailedCause().hashCode());
		result = (prime * result) + ((this.getOperation() == null) ? 0 : this.getOperation().hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object other) { // NOCS NOPMD (This method violates some metrics)
		if (this == other) {
			return true;
		}
		if (!(other instanceof Execution)) {
			return false;
		}
		final Execution otherEntry = (Execution) other;
		if (!this.getContainer().equals(otherEntry.getContainer())) {
			return false;
		}
		if (!this.getComponent().equals(otherEntry.getComponent())) {
			return false;
		}
		if (!this.getOperation().equals(otherEntry.getOperation())) {
			return false;
		}
		if (this.getFailedCause() == null) {
			if (otherEntry.getFailedCause() != null) {
				return false;
			}
		} else {
			if (!this.getFailedCause().equals(otherEntry.getFailedCause())) {
				return false;
			}
		}
		if (this.getChildren().size() != otherEntry.getChildren().size()) {
			return false;
		}

		final Iterator<Execution> ownChildrenIterator = this.getChildren().iterator();
		final Iterator<Execution> otherChildrenIterator = otherEntry.getChildren().iterator();

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
