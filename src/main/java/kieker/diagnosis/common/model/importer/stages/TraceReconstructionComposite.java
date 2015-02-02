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

package kieker.diagnosis.common.model.importer.stages;

import java.util.List;

import kieker.common.record.flow.IFlowRecord;
import kieker.diagnosis.common.domain.Trace;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.Stage;
import teetime.framework.TerminationStrategy;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.framework.signal.ISignal;
import teetime.framework.validation.InvalidPortConnection;
import teetime.stage.CollectorSink;
import teetime.stage.basic.distributor.CopyByReferenceStrategy;
import teetime.stage.basic.distributor.Distributor;

public final class TraceReconstructionComposite extends Stage {

	private final TraceReconstructor reconstructor;

	private final CollectorSink<Trace> tracesCollector;
	private final CollectorSink<Trace> failedTracesCollector;
	private final CollectorSink<Trace> failureContainingTracesCollector;
	private final TraceStatisticsDecorator statisticsDecorator;

	private final OutputPort<Trace> outputPort;

	public TraceReconstructionComposite(final List<Trace> traces, final List<Trace> failedTraces, final List<Trace> failureContainingTraces) {
		this.reconstructor = new TraceReconstructor();
		final Distributor<Trace> distributor = new Distributor<>(new CopyByReferenceStrategy());
		final FailedTraceFilter<Trace> failedTraceFilter = new FailedTraceFilter<>();
		final FailureContainingTraceFilter<Trace> failureContainingTraceFilter = new FailureContainingTraceFilter<>();

		this.tracesCollector = new CollectorSink<>(traces);
		this.failedTracesCollector = new CollectorSink<>(failedTraces);
		this.failureContainingTracesCollector = new CollectorSink<>(failureContainingTraces);
		this.statisticsDecorator = new TraceStatisticsDecorator();

		this.outputPort = this.statisticsDecorator.getOutputPort();

		final IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(this.reconstructor.getOutputPort(), distributor.getInputPort());

		pipeFactory.create(distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		pipeFactory.create(distributor.getNewOutputPort(), failedTraceFilter.getInputPort());
		pipeFactory.create(distributor.getNewOutputPort(), failureContainingTraceFilter.getInputPort());
		pipeFactory.create(distributor.getNewOutputPort(), this.statisticsDecorator.getInputPort());

		pipeFactory.create(failedTraceFilter.getOutputPort(), this.failedTracesCollector.getInputPort());
		pipeFactory.create(failureContainingTraceFilter.getOutputPort(), this.failureContainingTracesCollector.getInputPort());
	}

	@Override
	protected void executeWithPorts() {
		this.reconstructor.executeWithPorts();
	}

	public InputPort<IFlowRecord> getInputPort() {
		return this.reconstructor.getInputPort();
	}

	public OutputPort<Trace> getOutputPort() {
		return this.outputPort;
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		this.statisticsDecorator.validateOutputPorts(invalidPortConnections);
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.reconstructor.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return this.reconstructor.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		this.reconstructor.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return this.reconstructor.shouldBeTerminated();
	}

	@Override
	protected InputPort<?>[] getInputPorts() {
		return this.reconstructor.getInputPorts();
	}

	@Override
	protected boolean isStarted() {
		return this.tracesCollector.isStarted() && this.failedTracesCollector.isStarted() && this.failureContainingTracesCollector.isStarted()
				&& this.statisticsDecorator.isStarted();
	}

}
