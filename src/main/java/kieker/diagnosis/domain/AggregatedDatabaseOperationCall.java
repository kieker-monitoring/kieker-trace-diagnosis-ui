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
 * This class represents a concrete aggregated database call within this application. It
 * adds some properties that are only required for this type of calls, like the
 * trace ID and the duration. It extends the call tree mechanism (inherited from
 * {@link AbstractOperationCall}) by a parent, allowing to navigate in both
 * directions within the tree.
 * 
 * @author Christian Zirkelbach
 */
public final class AggregatedDatabaseOperationCall extends AbstractOperationCall<AggregatedDatabaseOperationCall> {

	private AggregatedDatabaseOperationCall parent;
	private long totalDuration;
	private long medianDuration;
	private long minDuration;
	private long maxDuration;
	private long meanDuration;
	private int calls;
	
	private String callArguments;

	public AggregatedDatabaseOperationCall(final String container, final String component,
			final String operation, final String callArguments,
			final long totalDuration, final long medianDuration, final long minDuration, final long maxDuration, final long meanDuration,
			final int calls) {
		super(container, component, operation, null);

		this.callArguments = callArguments;
		this.totalDuration = totalDuration;
		this.medianDuration = medianDuration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.meanDuration = meanDuration;
		this.calls = calls;
	}

	@Override
	public void addChild(final AggregatedDatabaseOperationCall child) {
		super.addChild(child);
		child.parent = this;
	}
	
	public long getTotalDuration() {
		return this.totalDuration;
	}

	public void setTotalDuration(final long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public long getMedianDuration() {
		return this.medianDuration;
	}

	public void setMedianDuration(final long meanDuration) {
		this.medianDuration = meanDuration;
	}

	public long getMinDuration() {
		return this.minDuration;
	}

	public void setMinDuration(final long minDuration) {
		this.minDuration = minDuration;
	}

	public long getMaxDuration() {
		return this.maxDuration;
	}

	public void setMaxDuration(final long maxDuration) {
		this.maxDuration = maxDuration;
	}

	public long getMeanDuration() {
		return this.meanDuration;
	}

	public void setMeanDuration(final long avgDuration) {
		this.meanDuration = avgDuration;
	}

	public int getCalls() {
		return this.calls;
	}

	public void setCalls(final int calls) {
		this.calls = calls;
	}

	public String getStringClassArgs() {
		return callArguments;
	}

	public void setStringClassArgs(String stringClassArgs) {
		this.callArguments = stringClassArgs;
	}
}
