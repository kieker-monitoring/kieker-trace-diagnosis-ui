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

/**
 * This class represents a concrete database call within this application. It
 * adds some properties that are only required for this type of calls, like the
 * trace ID and the duration. It extends the call tree mechanism (inherited from
 * {@link AbstractOperationCall}) by a parent, allowing to navigate in both
 * directions within the tree.
 * 
 * @author Christian Zirkelbach
 */
public final class DatabaseOperationCall extends AbstractOperationCall<DatabaseOperationCall> {

	private final long traceID;

	private DatabaseOperationCall parent;
	private float percent;
	private long duration;
	private long timestamp;
	private String callArguments;
	private String returnValue;

	public DatabaseOperationCall(final String container, final String component,
			final String operation, final String callArguments,
			final String returnValue, final long traceID,
			final long timestamp) {
		super(container, component, operation, null);

		this.callArguments = callArguments;
		this.returnValue = returnValue;
		this.traceID = traceID;
		this.timestamp = timestamp;
	}

	@Override
	public void addChild(final DatabaseOperationCall child) {
		super.addChild(child);
		child.parent = this;
	}

	public DatabaseOperationCall getParent() {
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

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public String getStringClassArgs() {
		return callArguments;
	}

	public void setStringClassArgs(String stringClassArgs) {
		this.callArguments = stringClassArgs;
	}

	public String getFormattedReturnValue() {
		return returnValue;
	}

	public void setFormattedReturnValue(String formattedReturnValue) {
		this.returnValue = formattedReturnValue;
	}

}
