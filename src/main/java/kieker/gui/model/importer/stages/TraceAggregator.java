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

package kieker.gui.model.importer.stages;

import java.util.HashMap;
import java.util.Map;

import kieker.gui.model.domain.AggregatedExecutionEntry;
import kieker.gui.model.domain.ExecutionEntry;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Aggregates incoming traces into trace equivalence classes.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceAggregator extends AbstractConsumerStage<ExecutionEntry> {

	private final OutputPort<AggregatedExecutionEntry> outputPort = super.createOutputPort();
	private final Map<ExecutionEntry, AggregatedExecutionEntry> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final ExecutionEntry executionEntry) {
		if (!this.aggregationMap.containsKey(executionEntry)) {
			final AggregatedExecutionEntry aggregatedExecutionEntry = new AggregatedExecutionEntry(executionEntry);
			this.aggregationMap.put(executionEntry, aggregatedExecutionEntry);
		}
		this.aggregationMap.get(executionEntry).incrementCalls(executionEntry);
	}

	@Override
	public void onTerminating() throws Exception {
		for (final AggregatedExecutionEntry aggregatedExecutionEntry : this.aggregationMap.values()) {
			aggregatedExecutionEntry.recalculateValues();
			this.outputPort.send(aggregatedExecutionEntry);
		}
		super.onTerminating();
	}

	public OutputPort<AggregatedExecutionEntry> getOutputPort() {
		return this.outputPort;
	}

}
