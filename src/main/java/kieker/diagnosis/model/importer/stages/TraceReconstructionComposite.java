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

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.diagnosis.domain.Trace;
import teetime.framework.AbstractCompositeStage;
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
public final class TraceReconstructionComposite extends AbstractCompositeStage {

	private final MultipleInstanceOfFilter<IMonitoringRecord> typeFilter;
	private final CollectorSink<Trace> tracesCollector;
	private final CollectorSink<Trace> failedTracesCollector;
	private final CollectorSink<Trace> failureContainingTracesCollector;
	private final TraceStatisticsDecorator statisticsDecorator;
	private final OutputPort<Trace> outputPort;
	private final LegacyTraceReconstructor legacyReconstructor;
	private final TraceReconstructor reconstructor;

	public TraceReconstructionComposite(final List<Trace> traces, final List<Trace> failedTraces, final List<Trace> failureContainingTraces) {
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final Filter<Trace> failedTraceFilter = new Filter<>(trace -> trace.getRootOperationCall().isFailed());
		final Merger<Trace> merger = new Merger<>();
		final Filter<Trace> failureContainingTraceFilter = new Filter<>(trace -> trace.getRootOperationCall().containsFailure());

		this.typeFilter = new MultipleInstanceOfFilter<>();
		this.tracesCollector = new CollectorSink<>(traces);
		this.failedTracesCollector = new CollectorSink<>(failedTraces);
		this.failureContainingTracesCollector = new CollectorSink<>(failureContainingTraces);
		this.statisticsDecorator = new TraceStatisticsDecorator();
		this.reconstructor = new TraceReconstructor();
		this.legacyReconstructor = new LegacyTraceReconstructor();

		this.outputPort = this.statisticsDecorator.getOutputPort();

		super.connectPorts(this.typeFilter.getOutputPortForType(IFlowRecord.class), this.reconstructor.getInputPort());
		super.connectPorts(this.typeFilter.getOutputPortForType(OperationExecutionRecord.class), this.legacyReconstructor.getInputPort());
		super.connectPorts(this.reconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectPorts(this.legacyReconstructor.getOutputPort(), merger.getNewInputPort());
		super.connectPorts(merger.getOutputPort(), distributor.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), failedTraceFilter.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), failureContainingTraceFilter.getInputPort());
		super.connectPorts(distributor.getNewOutputPort(), this.statisticsDecorator.getInputPort());
		super.connectPorts(failedTraceFilter.getOutputPort(), this.failedTracesCollector.getInputPort());
		super.connectPorts(failureContainingTraceFilter.getOutputPort(), this.failureContainingTracesCollector.getInputPort());
	}

	public int countIncompleteTraces() {
		return this.reconstructor.countIncompleteTraces() + this.legacyReconstructor.countIncompleteTraces();
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

}
