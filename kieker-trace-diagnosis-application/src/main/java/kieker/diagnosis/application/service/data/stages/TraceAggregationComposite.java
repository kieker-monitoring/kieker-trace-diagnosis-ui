/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.service.data.stages;

import kieker.diagnosis.application.service.data.domain.AggregatedTrace;
import kieker.diagnosis.application.service.data.domain.Trace;

import java.util.List;

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

	public TraceAggregationComposite( final List<AggregatedTrace> aTraces ) {
		ivAggregator = new TraceAggregator( );
		final AggregatedTraceStatisticsDecorator statisticsDecorator = new AggregatedTraceStatisticsDecorator( );
		final CollectorSink<AggregatedTrace> tracesCollector = new CollectorSink<>( aTraces );

		connectPorts( ivAggregator.getOutputPort( ), statisticsDecorator.getInputPort( ) );
		connectPorts( statisticsDecorator.getOutputPort( ), tracesCollector.getInputPort( ) );
	}

	public InputPort<Trace> getInputPort( ) {
		return ivAggregator.getInputPort( );
	}

}
