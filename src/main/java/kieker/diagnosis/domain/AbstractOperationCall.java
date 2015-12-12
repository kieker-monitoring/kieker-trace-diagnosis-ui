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
 
	private final List<T> ivChildren = new ArrayList<>();

	private final String ivContainer;
	private final String ivComponent;
	private final String ivOperation;

	private int ivStackDepth;
	private int ivStackSize;
	private String ivFailedCause;

	public AbstractOperationCall(final String aContainer, final String aComponent, final String aOperation) {
		this(aContainer, aComponent, aOperation, null);
	}

	public AbstractOperationCall(final String aContainer, final String aComponent, final String aOperation, final String aFailedCause) {
		this.ivContainer = aContainer.intern();
		this.ivComponent = aComponent.intern();
		this.ivOperation = aOperation.intern();
		this.ivFailedCause = (aFailedCause != null) ? aFailedCause.intern() : null;
	}

	public void addChild(final T aChild) {
		this.ivChildren.add(aChild);
	}

	public final List<T> getChildren() {
		return this.ivChildren;
	}

	public final String getContainer() {
		return this.ivContainer;
	}

	public final String getComponent() {
		return this.ivComponent;
	}

	public final String getOperation() {
		return this.ivOperation;
	}

	public final int getStackDepth() {
		return this.ivStackDepth;
	}

	public final void setStackDepth(final int aStackDepth) {
		this.ivStackDepth = aStackDepth;
	}

	public final int getStackSize() {
		return this.ivStackSize;
	}

	public final void setStackSize(final int aStackSize) {
		this.ivStackSize = aStackSize;
	}

	public final boolean isFailed() {
		return (this.ivFailedCause != null);
	}

	public final String getFailedCause() {
		return this.ivFailedCause;
	}

	public final void setFailedCause(final String aFailedCause) {
		this.ivFailedCause = (aFailedCause != null) ? aFailedCause.intern() : null;
	}

	public final boolean containsFailure() {
		return this.isFailed() || this.ivChildren.parallelStream().anyMatch(T::containsFailure);
	}

	public final int calculateHashCode() {
		final int prime = 31;
		int result = 1;

		result = (prime * result) + ((this.ivChildren == null) ? 0 : this.calculateHashCodeForChildren());
		result = (prime * result) + ((this.ivComponent == null) ? 0 : this.ivComponent.hashCode());
		result = (prime * result) + ((this.ivContainer == null) ? 0 : this.ivContainer.hashCode());
		result = (prime * result) + ((this.ivFailedCause == null) ? 0 : this.ivFailedCause.hashCode());
		result = (prime * result) + ((this.ivOperation == null) ? 0 : this.ivOperation.hashCode());

		return result;
	}

	private final int calculateHashCodeForChildren() {
		int hashCode = 1;
		for (final T child : this.ivChildren) {
			hashCode = (31 * hashCode) + (child == null ? 0 : child.calculateHashCode());
		}
		return hashCode;
	}

	@SuppressWarnings("unchecked")
	public final boolean isEqualTo(final Object aObj) { // NOPMD (this method violates some metrics)
		if (this == aObj) {
			return true;
		}
		if (aObj == null) {
			return false;
		}
		if (this.getClass() != aObj.getClass()) {
			return false;
		}
		final T other = (T) aObj;
		if (this.ivChildren == null) {
			if (other.getChildren() != null) {
				return false;
			}
		} else {
			final int length1 = this.ivChildren.size();
			final int length2 = other.getChildren().size();
			if (length1 != length2) {
				return false;
			}
			for (int i = 0; i < length1; i++) {
				if (!this.ivChildren.get(i).isEqualTo(other.getChildren().get(i))) {
					return false;
				}
			}
		}
		if (this.ivComponent == null) {
			if (other.getComponent() != null) {
				return false;
			}
		} else if (!this.ivComponent.equals(other.getComponent())) {
			return false;
		}
		if (this.ivContainer == null) {
			if (other.getContainer() != null) {
				return false;
			}
		} else if (!this.ivContainer.equals(other.getContainer())) {
			return false;
		}
		if (this.ivFailedCause == null) {
			if (other.getFailedCause() != null) {
				return false;
			}
		} else if (!this.ivFailedCause.equals(other.getFailedCause())) {
			return false;
		}
		if (this.ivOperation == null) {
			if (other.getOperation() != null) {
				return false;
			}
		} else if (!this.ivOperation.equals(other.getOperation())) {
			return false;
		}
		return true;
	}

}
