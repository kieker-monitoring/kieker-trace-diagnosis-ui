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

package kieker.diagnosis.common.model.importer.stages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.diagnosis.common.domain.AggregatedOperationCall;
import kieker.diagnosis.common.domain.OperationCall;

public final class OperationCallAggregator extends AbstractStage<OperationCall, AggregatedOperationCall> {

	private final Map<String, List<OperationCall>> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final OperationCall call) {
		// TODO we should use a better approach here than this "key"
		final String key = call.getContainer() + "," + call.getComponent() + "," + call.getOperation() + ", " + call.getFailedCause();

		if (!this.aggregationMap.containsKey(key)) {
			final List<OperationCall> aggregationList = new ArrayList<>();
			this.aggregationMap.put(key, aggregationList);
		}
		this.aggregationMap.get(key).add(call);
	}

	@Override
	public void onTerminating() throws Exception { // NOPMD (the throws clause is forced by the framework)
		for (final List<OperationCall> aggregationList : this.aggregationMap.values()) {
			// TODO the statistics calculation is the same as in AggregatedTraceStatisticsDecorator
			final Statistics statistics = this.calculateStatistics(this.extractDurations(aggregationList));
			super.send(new AggregatedOperationCall(aggregationList.get(0).getContainer(), aggregationList.get(0).getComponent(), aggregationList.get(0).getOperation(),
					aggregationList.get(0).getFailedCause(), statistics.getTotalDuration(), statistics.getMedianDuration(), statistics.getMinDuration(),
					statistics.getMaxDuration(), statistics.getMeanDuration(), aggregationList.size()));
		}

		super.onTerminating();
	}

	private List<Long> extractDurations(final List<OperationCall> callList) {
		final List<Long> result = new ArrayList<>();

		for (final OperationCall call : callList) {
			result.add(call.getDuration());
		}

		return result;
	}

	private Statistics calculateStatistics(final List<Long> durations) {
		Collections.sort(durations);

		long totalDuration = 0;
		for (final Long duration : durations) {
			totalDuration += duration;
		}

		final long minDuration = durations.get(0);
		final long maxDuration = durations.get(durations.size() - 1);
		final long meanDuration = totalDuration / durations.size();
		final long medianDuration = durations.get(durations.size() / 2);

		return new Statistics(totalDuration, meanDuration, medianDuration, minDuration, maxDuration);
	}

	private static class Statistics {

		private final long totalDuration;
		private final long meanDuration;
		private final long medianDuration;
		private final long minDuration;
		private final long maxDuration;

		public Statistics(final long totalDuration, final long meanDuration, final long medianDuration, final long minDuration, final long maxDuration) {
			this.totalDuration = totalDuration;
			this.meanDuration = meanDuration;
			this.medianDuration = medianDuration;
			this.minDuration = minDuration;
			this.maxDuration = maxDuration;
		}

		public long getTotalDuration() {
			return this.totalDuration;
		}

		public long getMeanDuration() {
			return this.meanDuration;
		}

		public long getMedianDuration() {
			return this.medianDuration;
		}

		public long getMinDuration() {
			return this.minDuration;
		}

		public long getMaxDuration() {
			return this.maxDuration;
		}

	}

}
