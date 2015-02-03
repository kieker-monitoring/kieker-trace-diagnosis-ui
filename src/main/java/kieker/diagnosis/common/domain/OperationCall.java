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

package kieker.diagnosis.common.domain;

/**
 * This class represents an operation call (or an execution) within this application. As it can has multiple children, an instance of this class can represent a whole call tree.
 * This class implements the both methods {@link OperationCall#equals(Object)} and {@link OperationCall#hashCode()}, allowing to easily check whether two traces are equal (aside
 * from some varying properties like the duration) and should be in the same equivalence class.
 *
 * @author Nils Christian Ehmke
 */
public final class OperationCall extends AbstractOperationCall<OperationCall> {

	private final long traceID;

	private OperationCall parent;
	private float percent;
	private long duration;

	public OperationCall(final String container, final String component, final String operation, final long traceID) {
		this(container, component, operation, traceID, null);
	}

	public OperationCall(final String container, final String component, final String operation, final long traceID, final String failedCause) {
		super(container, component, operation, failedCause);

		this.traceID = traceID;
	}

	@Override
	public void addChild(final OperationCall child) {
		super.addChild(child);
		child.parent = this;
	}

	public OperationCall getParent() {
		return this.parent;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(final float percent) {
		this.percent = percent;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public long getTraceID() {
		return this.traceID;
	}

}
