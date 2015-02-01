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

package kieker.gui.common.model.importer.stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.gui.common.domain.AggregatedTrace;
import kieker.gui.common.domain.Trace;

/**
 * This stage aggregates incoming traces into trace equivalence classes.
 * 
 * @author Nils Christian Ehmke
 */
public final class TraceAggregator extends AbstractStage<Trace, AggregatedTrace> {

	private final Map<Trace, List<Trace>> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final Trace trace) {
		if (!this.aggregationMap.containsKey(trace)) {
			final List<Trace> aggregationList = new ArrayList<>();
			this.aggregationMap.put(trace, aggregationList);
		}
		this.aggregationMap.get(trace).add(trace);
	}

	@Override
	public void onTerminating() throws Exception { // NOPMD (the throws clause is forced by the framework)
		for (final List<Trace> aggregationList : this.aggregationMap.values()) {
			final AggregatedTrace aggregatedTrace = new AggregatedTrace(aggregationList);
			super.send(aggregatedTrace);
		}

		super.onTerminating();
	}

}
