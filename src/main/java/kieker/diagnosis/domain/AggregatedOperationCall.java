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
 * This class represents an aggregated operation call within this application. It adds some properties that are only available due to aggregation, like the average
 * duration of all calls.
 * 
 * @author Nils Christian Ehmke
 */
public final class AggregatedOperationCall extends AbstractOperationCall<AggregatedOperationCall> {

	private long ivTotalDuration;
	private long ivMedianDuration;
	private long ivMinDuration;
	private long ivMaxDuration;
	private long ivMeanDuration;
	private int ivCalls;

	public AggregatedOperationCall(final OperationCall aCall) {
		super(aCall.getContainer(), aCall.getComponent(), aCall.getOperation(), aCall.getFailedCause());

		for (final OperationCall child : aCall.getChildren()) {
			super.addChild(new AggregatedOperationCall(child));
		}

		this.setStackDepth(aCall.getStackDepth());
		this.setStackSize(aCall.getStackSize());
	}

	public AggregatedOperationCall(final String container, final String component, final String operation, // NOPMD (a long parameter list cannot be avoided)
			final String failedCause, final long totalDuration, final long medianDuration, final long minDuration, final long maxDuration, final long meanDuration,
			final int calls) {
		super(container, component, operation);

		this.ivTotalDuration = totalDuration;
		this.ivMedianDuration = medianDuration;
		this.ivMinDuration = minDuration;
		this.ivMaxDuration = maxDuration;
		this.ivMeanDuration = meanDuration;
		this.ivCalls = calls;

		this.setFailedCause(failedCause);
	}

	public long getTotalDuration() {
		return this.ivTotalDuration;
	}

	public void setTotalDuration(final long totalDuration) {
		this.ivTotalDuration = totalDuration;
	}

	public long getMedianDuration() {
		return this.ivMedianDuration;
	}

	public void setMedianDuration(final long meanDuration) {
		this.ivMedianDuration = meanDuration;
	}

	public long getMinDuration() {
		return this.ivMinDuration;
	}

	public void setMinDuration(final long minDuration) {
		this.ivMinDuration = minDuration;
	}

	public long getMaxDuration() {
		return this.ivMaxDuration;
	}

	public void setMaxDuration(final long maxDuration) {
		this.ivMaxDuration = maxDuration;
	}

	public long getMeanDuration() {
		return this.ivMeanDuration;
	}

	public void setMeanDuration(final long avgDuration) {
		this.ivMeanDuration = avgDuration;
	}

	public int getCalls() {
		return this.ivCalls;
	}

	public void setCalls(final int calls) {
		this.ivCalls = calls;
	}

}
