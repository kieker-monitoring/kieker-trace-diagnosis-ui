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

package kieker.diagnosis.model.importer.stages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.Trace;

/**
 * This stage aggregates incoming traces into trace equivalence classes.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceAggregator extends AbstractStage<Trace, AggregatedTrace> {

	private final Map<TraceWrapper, List<Trace>> aggregationMap = new HashMap<>();

	@Override
	protected void execute(final Trace trace) {
		final TraceWrapper wrapper = new TraceWrapper(trace);
		if (!this.aggregationMap.containsKey(wrapper)) {
			final List<Trace> aggregationList = new ArrayList<>();
			this.aggregationMap.put(wrapper, aggregationList);
		}
		this.aggregationMap.get(wrapper).add(trace);
	}

	@Override
	public void onTerminating() throws Exception { // NOPMD (the throws clause is forced by the framework)
		this.aggregationMap.values().forEach(list -> super.send(new AggregatedTrace(list)));

		super.onTerminating();
	}

	private static class TraceWrapper {

		private final Trace trace;

		public TraceWrapper(final Trace trace) {
			this.trace = trace;
		}

		@Override
		public int hashCode() {
			return this.trace.calculateHashCode();
		}

		@Override
		public boolean equals(final Object obj) {
			if (!(obj instanceof TraceWrapper)) {
				return false;
			}
			return this.trace.isEqualTo(((TraceWrapper) obj).trace);
		}

	}

}
