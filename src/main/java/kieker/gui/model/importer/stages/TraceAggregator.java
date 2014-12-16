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

import kieker.gui.model.domain.AggregatedExecution;
import kieker.gui.model.domain.Execution;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Aggregates incoming traces into trace equivalence classes.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceAggregator extends AbstractConsumerStage<Execution> {

	private final OutputPort<AggregatedExecution> outputPort = super.createOutputPort();
	private final Map<Execution, AggregatedExecution> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final Execution executionEntry) {
		if (!this.aggregationMap.containsKey(executionEntry)) {
			final AggregatedExecution aggregatedExecutionEntry = new AggregatedExecution(executionEntry);
			this.aggregationMap.put(executionEntry, aggregatedExecutionEntry);
		}
		this.aggregationMap.get(executionEntry).incrementCalls(executionEntry);
	}

	@Override
	public void onTerminating() throws Exception {
		for (final AggregatedExecution aggregatedExecutionEntry : this.aggregationMap.values()) {
			aggregatedExecutionEntry.recalculateValues();
			this.outputPort.send(aggregatedExecutionEntry);
		}
		super.onTerminating();
	}

	public OutputPort<AggregatedExecution> getOutputPort() {
		return this.outputPort;
	}

}
