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

package kieker.gui.common.domain;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents an aggregated trace (or a trace equivalence class) within this application.
 * 
 * @author Nils Christian Ehmke
 */
public final class AggregatedTrace extends AbstractTrace {

	private final Map<StatisticType, Object> statistics = new EnumMap<>(StatisticType.class);
	private final List<Trace> traces;

	public AggregatedTrace(final List<Trace> traces) {
		super(traces.get(0).getRootOperationCall().copy());

		this.traces = traces;
	}

	public List<Trace> getTraces() {
		return this.traces;
	}

	public void addStatistic(final StatisticType statisticType, final Object value) {
		if (statisticType.getTypeOfValue().isInstance(value)) {
			this.statistics.put(statisticType, value);
		}
	}

	public Object getStatistic(final StatisticType statisticType) {
		return this.statistics.get(statisticType);
	}

}
