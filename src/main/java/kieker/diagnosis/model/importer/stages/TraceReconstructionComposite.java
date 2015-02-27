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

package kieker.diagnosis.model.importer.stages;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.diagnosis.domain.Trace;
import teetime.framework.CompositeStage;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.stage.CollectorSink;
import teetime.stage.MultipleInstanceOfFilter;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;
import teetime.stage.basic.merger.Merger;

/**
 * This class is a composite {@code TeeTime} stage, which reconstruct traces based on the incoming records, adds statistical data and stores the traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceReconstructionComposite extends CompositeStage {

	private final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter;
	private final CollectorSink<Trace> tracesCollector;
	private final CollectorSink<Trace> failedTracesCollector;
	private final CollectorSink<Trace> failureContainingTracesCollector;
	private final TraceStatisticsDecorator statisticsDecorator;

	private final OutputPort<Trace> outputPort;

	public TraceReconstructionComposite(final List<Trace> traces, final List<Trace> failedTraces, final List<Trace> failureContainingTraces) {
		final TraceReconstructor reconstructor = new TraceReconstructor();
		final LegacyTraceReconstructor legacyReconstructor = new LegacyTraceReconstructor();
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final FailedTraceFilter<Trace> failedTraceFilter = new FailedTraceFilter<>();
		final Merger<Trace> merger = new Merger<>();
		final FailureContainingTraceFilter<Trace> failureContainingTraceFilter = new FailureContainingTraceFilter<>();

		this.typeFilter = new MultipleInstanceOfFilter<>();
		this.tracesCollector = new CollectorSink<>(traces);
		this.failedTracesCollector = new CollectorSink<>(failedTraces);
		this.failureContainingTracesCollector = new CollectorSink<>(failureContainingTraces);
		this.statisticsDecorator = new TraceStatisticsDecorator();

		this.outputPort = this.statisticsDecorator.getOutputPort();

		super.connectStages(this.typeFilter.getOutputPortForType(IFlowRecord.class), reconstructor.getInputPort());
		super.connectStages(this.typeFilter.getOutputPortForType(OperationExecutionRecord.class), legacyReconstructor.getInputPort());
		super.connectStages(reconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectStages(legacyReconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectStages(merger.getOutputPort(), distributor.getInputPort());
		super.connectStages(distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		super.connectStages(distributor.getNewOutputPort(), failedTraceFilter.getInputPort());
		super.connectStages(distributor.getNewOutputPort(), failureContainingTraceFilter.getInputPort());
		super.connectStages(distributor.getNewOutputPort(), this.statisticsDecorator.getInputPort());
		super.connectStages(failedTraceFilter.getOutputPort(), this.failedTracesCollector.getInputPort());
		super.connectStages(failureContainingTraceFilter.getOutputPort(), this.failureContainingTracesCollector.getInputPort());
	}

	public InputPort<IMonitoringRecord> getInputPort() {
		return this.typeFilter.getInputPort();
	}

	public OutputPort<Trace> getOutputPort() {
		return this.outputPort;
	}

	@Override
	protected Stage getFirstStage() {
		return this.typeFilter;
	}

	@Override
	protected Collection<? extends Stage> getLastStages() {
		return Arrays.asList(this.tracesCollector, this.failedTracesCollector, this.failureContainingTracesCollector, this.statisticsDecorator);
	}

}
