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

package kieker.diagnosis.util.stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.util.Statistics;
import kieker.diagnosis.util.StatisticsUtility;
import teetime.stage.basic.AbstractTransformation;

/**
 * @author Nils Christian Ehmke
 */
public final class OperationCallAggregator extends AbstractTransformation<OperationCall, AggregatedOperationCall> {

	private final Map<String, List<OperationCall>> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final OperationCall call) {
		final String key = call.getContainer() + "," + call.getComponent() + "," + call.getOperation() + ", " + call.getFailedCause();

		if (!this.aggregationMap.containsKey(key)) {
			final List<OperationCall> aggregationList = new ArrayList<>();
			this.aggregationMap.put(key, aggregationList);
		}
		this.aggregationMap.get(key).add(call);
	}

	@Override
	public void onTerminating() throws Exception {
		for (final List<OperationCall> aggregationList : this.aggregationMap.values()) {
			final List<Long> durations = this.extractDurations(aggregationList);
			final Statistics statistics = StatisticsUtility.calculateStatistics(durations);
			super.getOutputPort().send(new AggregatedOperationCall(aggregationList.get(0).getContainer(), aggregationList.get(0).getComponent(), aggregationList.get(0).getOperation(),
					aggregationList.get(0).getFailedCause(), statistics.getTotalDuration(), statistics.getMedianDuration(), statistics.getMinDuration(), statistics.getMaxDuration(),
					statistics.getMeanDuration(), aggregationList.size()));
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

}
