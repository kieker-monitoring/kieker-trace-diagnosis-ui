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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class OperationCall {

	private final Map<StatisticType, Object> statistics = new EnumMap<>(StatisticType.class);
	private final List<OperationCall> children = new ArrayList<>();

	private final String container;
	private final String component;
	private final String operation;
	private final long traceID;

	private OperationCall parent;
	private String failedCause;
	private long duration;

	public OperationCall(final String container, final String component, final String operation, final long traceID) {
		this(container, component, operation, traceID, null);
	}

	public OperationCall(final String container, final String component, final String operation, final long traceID, final String failedCause) {
		this.container = container;
		this.component = component;
		this.operation = operation;
		this.traceID = traceID;
		this.failedCause = failedCause;
	}

	public void addStatistic(final StatisticType statisticType, final Object value) {
		if (statisticType.getTypeOfValue().isInstance(value)) {
			this.statistics.put(statisticType, value);
		}
	}

	public void addChild(final OperationCall child) {
		this.children.add(child);
		child.parent = this;
	}

	public List<OperationCall> getChildren() {
		return this.children;
	}

	public Object getStatistic(final StatisticType statisticType) {
		return this.statistics.get(statisticType);
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

	public long getTraceID() {
		return this.traceID;
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

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public long getDuration() {
		return this.duration;
	}

	public OperationCall getParent() {
		return this.parent;
	}

	public boolean containsFailure() {
		if (this.isFailed()) {
			return true;
		}
		for (final OperationCall child : this.children) {
			if (child.isFailed()) {
				return true;
			}
		}

		return false;
	}

	public OperationCall copy() {
		final OperationCall copy = new OperationCall(this.container, this.component, this.operation, this.traceID, this.failedCause);

		for (final OperationCall child : this.children) {
			copy.addChild(child.copy());
		}

		copy.statistics.putAll(this.statistics);

		return copy;
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
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final OperationCall other = (OperationCall) obj;
		if (this.children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!this.children.equals(other.children)) {
			return false;
		}
		if (this.component == null) {
			if (other.component != null) {
				return false;
			}
		} else if (!this.component.equals(other.component)) {
			return false;
		}
		if (this.container == null) {
			if (other.container != null) {
				return false;
			}
		} else if (!this.container.equals(other.container)) {
			return false;
		}
		if (this.failedCause == null) {
			if (other.failedCause != null) {
				return false;
			}
		} else if (!this.failedCause.equals(other.failedCause)) {
			return false;
		}
		if (this.operation == null) {
			if (other.operation != null) {
				return false;
			}
		} else if (!this.operation.equals(other.operation)) {
			return false;
		}
		return true;
	}

}
