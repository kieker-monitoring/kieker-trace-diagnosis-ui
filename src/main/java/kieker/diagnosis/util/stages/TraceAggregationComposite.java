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

import java.util.List;

import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.Trace;
import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.stage.CollectorSink;

/**
 * This is a composite {@code TeeTime} stage which aggregates incoming traces, adds statistical data and stores the aggregated traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceAggregationComposite extends AbstractCompositeStage {

	private final TraceAggregator ivAggregator;
	private final CollectorSink<AggregatedTrace> ivTracesCollector;
	private final AggregatedTraceStatisticsDecorator ivStatisticsDecorator;

	public TraceAggregationComposite(final List<AggregatedTrace> aTraces) {
		this.ivAggregator = new TraceAggregator();
		this.ivStatisticsDecorator = new AggregatedTraceStatisticsDecorator();
		this.ivTracesCollector = new CollectorSink<>(aTraces);

		super.connectPorts(this.ivAggregator.getOutputPort(), this.ivStatisticsDecorator.getInputPort());
		super.connectPorts(this.ivStatisticsDecorator.getOutputPort(), this.ivTracesCollector.getInputPort());
	}

	public InputPort<Trace> getInputPort() {
		return this.ivAggregator.getInputPort();
	}

}
