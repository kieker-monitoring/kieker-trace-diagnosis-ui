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

package kieker.gui.model.importer.filter;

import java.util.HashMap;
import java.util.Map;

import kieker.gui.model.AggregatedExecutionEntry;
import kieker.gui.model.ExecutionEntry;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * @author Nils Christian Ehmke
 */
public final class TraceAggregator extends AbstractConsumerStage<ExecutionEntry> {

	private final OutputPort<AggregatedExecutionEntry> outputPort = super.createOutputPort();
	private final Map<ExecutionEntry, AggregatedExecutionEntry> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final ExecutionEntry execEntry) {
		if (!this.aggregationMap.containsKey(execEntry)) {
			final AggregatedExecutionEntry aggregatedExecutionEntry = new AggregatedExecutionEntry(execEntry.getContainer(), execEntry.getComponent(), execEntry.getOperation());
			this.aggregationMap.put(execEntry, aggregatedExecutionEntry);
		}
		this.aggregationMap.get(execEntry).incrementCalls();
	}

	@Override
	public void onTerminating() throws Exception {
		System.out.println("onTerminating");
		for (final AggregatedExecutionEntry aggregatedExecutionEntry : this.aggregationMap.values()) {
			this.outputPort.send(aggregatedExecutionEntry);
		}
		super.onTerminating();
	}

	public OutputPort<AggregatedExecutionEntry> getOutputPort() {
		return this.outputPort;
	}

}
