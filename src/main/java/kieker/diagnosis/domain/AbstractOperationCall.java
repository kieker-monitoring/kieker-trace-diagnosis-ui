/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an abstract base for classes representing operation calls (also called executions) within this application. As it can has multiple children, an instance of this class
 * can represent a whole call tree. This class implements the both methods {@link OperationCall#equals(Object)} and {@link OperationCall#hashCode()}, allowing to easily check
 * whether two traces are equal and should be in the same equivalence class.
 *
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The precise type of the children. This should usually be the implementing class itself.
 */
public abstract class AbstractOperationCall<T extends AbstractOperationCall<T>> {

	private final List<T> children = new ArrayList<>();

	private final String container;
	private final String component;
	private final String operation;

	private int stackDepth;
	private int stackSize;
	private String failedCause;

	public AbstractOperationCall(final String container, final String component, final String operation) {
		this(container, component, operation, null);
	}

	public AbstractOperationCall(final String container, final String component, final String operation, final String failedCause) {
		this.container = container.intern();
		this.component = component.intern();
		this.operation = operation.intern();
		if (failedCause != null) {
		  this.failedCause = failedCause.intern();
		}
	}

	public void addChild(final T child) {
		this.children.add(child);
	}

	public final List<T> getChildren() {
		return this.children;
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

	public final int getStackDepth() {
		return this.stackDepth;
	}

	public final void setStackDepth(final int stackDepth) {
		this.stackDepth = stackDepth;
	}

	public final int getStackSize() {
		return this.stackSize;
	}

	public final void setStackSize(final int stackSize) {
		this.stackSize = stackSize;
	}

	public final boolean isFailed() {
		return (this.failedCause != null);
	}

	public final String getFailedCause() {
		return this.failedCause;
	}

	public final void setFailedCause(final String failedCause) {
	    this.failedCause = failedCause;
	    if (this.failedCause != null) {
	      this.failedCause =   this.failedCause.intern();
	    }
	}

	public final boolean containsFailure() {
		if (this.isFailed()) {
			return true;
		}
		for (final T child : this.children) {
			if (child.isFailed()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public final int hashCode() { // NOPMD (this method violates some metrics)
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
	@SuppressWarnings("unchecked")
	public final boolean equals(final Object obj) { // NOPMD (this method violates some metrics)
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final T other = (T) obj;
		if (this.children == null) {
			if (other.getChildren() != null) {
				return false;
			}
		} else if (!this.children.equals(other.getChildren())) {
			return false;
		}
		if (this.component == null) {
			if (other.getComponent() != null) {
				return false;
			}
		} else if (!this.component.equals(other.getComponent())) {
			return false;
		}
		if (this.container == null) {
			if (other.getContainer() != null) {
				return false;
			}
		} else if (!this.container.equals(other.getContainer())) {
			return false;
		}
		if (this.failedCause == null) {
			if (other.getFailedCause() != null) {
				return false;
			}
		} else if (!this.failedCause.equals(other.getFailedCause())) {
			return false;
		}
		if (this.operation == null) {
			if (other.getOperation() != null) {
				return false;
			}
		} else if (!this.operation.equals(other.getOperation())) {
			return false;
		}
		return true;
	}

	

}
