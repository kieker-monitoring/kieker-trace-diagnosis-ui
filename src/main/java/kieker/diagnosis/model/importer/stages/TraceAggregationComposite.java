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

import java.util.List;

import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.Trace;
import teetime.framework.AbstractCompositeStage;
import teetime.framework.InputPort;
import teetime.framework.Stage;
import teetime.stage.CollectorSink;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

/**
 * This is a composite {@code TeeTime} stage which aggregates incoming traces, adds statistical data and stores the aggregated traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceAggregationComposite extends AbstractCompositeStage {

	private final TraceAggregator aggregator;

	private final CollectorSink<AggregatedTrace> tracesCollector;
	private final CollectorSink<AggregatedTrace> failedTracesCollector;
	private final CollectorSink<AggregatedTrace> failureContainingTracesCollector;
	private final AggregatedTraceStatisticsDecorator statisticsDecorator;

	public TraceAggregationComposite(final List<AggregatedTrace> traces, final List<AggregatedTrace> failedTraces,
			final List<AggregatedTrace> failureContainingTraces) {
		this.aggregator = new TraceAggregator();
		this.statisticsDecorator = new AggregatedTraceStatisticsDecorator();

		final Distributor<AggregatedTrace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final Filter<AggregatedTrace> failedTraceFilter = new Filter<>(trace -> trace.getRootOperationCall().isFailed());
		final Filter<AggregatedTrace> failureContainingTraceFilter = new Filter<>(trace -> trace.getRootOperationCall().containsFailure());
		this.tracesCollector = new CollectorSink<>(traces);
		this.failedTracesCollector = new CollectorSink<>(failedTraces);
		this.failureContainingTracesCollector = new CollectorSink<>(failureContainingTraces);

		super.connectStages(this.aggregator.getOutputPort(), this.statisticsDecorator.getInputPort());
		super.connectStages(this.statisticsDecorator.getOutputPort(), distributor.getInputPort());

		super.connectStages(distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		super.connectStages(distributor.getNewOutputPort(), failedTraceFilter.getInputPort());
		super.connectStages(distributor.getNewOutputPort(), failureContainingTraceFilter.getInputPort());

		super.connectStages(failedTraceFilter.getOutputPort(), this.failedTracesCollector.getInputPort());
		super.connectStages(failureContainingTraceFilter.getOutputPort(), this.failureContainingTracesCollector.getInputPort());
	}

	public InputPort<Trace> getInputPort() {
		return this.aggregator.getInputPort();
	}

	@Override
	protected Stage getFirstStage() {
		return this.aggregator;
	}

}
