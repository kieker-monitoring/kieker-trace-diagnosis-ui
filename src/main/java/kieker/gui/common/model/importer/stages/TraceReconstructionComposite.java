package kieker.gui.common.model.importer.stages;

import java.util.List;

import kieker.common.record.flow.IFlowRecord;
import kieker.gui.common.domain.Execution;
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
	private final Distributor<Execution> distributor;
	private final FailedTraceFilter<Execution> failedTraceFilter;
	private final FailureContainingTraceFilter<Execution> failureContainingTraceFilter;

	private final CollectorSink<Execution> tracesCollector;
	private final CollectorSink<Execution> failedTracesCollector;
	private final CollectorSink<Execution> failureContainingTracesCollector;
	private final OutputPort<Execution> outputPort;

	public TraceReconstructionComposite(final List<Execution> traces, final List<Execution> failedTraces, final List<Execution> failureContainingTraces) {
		this.reconstructor = new TraceReconstructor();
		this.distributor = new Distributor<>(new CopyByReferenceStrategy());
		this.failedTraceFilter = new FailedTraceFilter<>();
		this.failureContainingTraceFilter = new FailureContainingTraceFilter<>();

		this.tracesCollector = new CollectorSink<>(traces);
		this.failedTracesCollector = new CollectorSink<>(failedTraces);
		this.failureContainingTracesCollector = new CollectorSink<>(failureContainingTraces);

		this.outputPort = this.distributor.getNewOutputPort();

		final IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(this.reconstructor.getOutputPort(), this.distributor.getInputPort());

		pipeFactory.create(this.distributor.getNewOutputPort(), this.tracesCollector.getInputPort());
		pipeFactory.create(this.distributor.getNewOutputPort(), this.failedTraceFilter.getInputPort());
		pipeFactory.create(this.distributor.getNewOutputPort(), this.failureContainingTraceFilter.getInputPort());

		pipeFactory.create(this.failedTraceFilter.getOutputPort(), this.failedTracesCollector.getInputPort());
		pipeFactory.create(this.failureContainingTraceFilter.getOutputPort(), this.failureContainingTracesCollector.getInputPort());
	}

	@Override
	protected void executeWithPorts() {
		this.reconstructor.executeWithPorts();
	}

	public InputPort<IFlowRecord> getInputPort() {
		return this.reconstructor.getInputPort();
	}

	public OutputPort<Execution> getOutputPort() {
		return this.outputPort;
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		this.distributor.validateOutputPorts(invalidPortConnections);
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
		return this.tracesCollector.isStarted() && this.failedTracesCollector.isStarted() && this.failureContainingTracesCollector.isStarted();
	}

}
