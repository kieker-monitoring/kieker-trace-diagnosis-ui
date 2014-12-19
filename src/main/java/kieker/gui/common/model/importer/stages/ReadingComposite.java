package kieker.gui.common.model.importer.stages;

import java.io.File;
import java.util.List;

import kieker.common.record.IMonitoringRecord;
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
import teetime.stage.InitialElementProducer;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

public final class ReadingComposite extends Stage {

	private final InitialElementProducer<File> producer;
	private final Dir2RecordsFilter reader;

	public ReadingComposite(final File importDirectory) {
		this.producer = new InitialElementProducer<>(importDirectory);
		this.reader = new Dir2RecordsFilter(new ClassNameRegistryRepository());

		final IPipeFactory pipeFactory = PipeFactoryRegistry.INSTANCE.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
		pipeFactory.create(this.producer.getOutputPort(), this.reader.getInputPort());
	}

	@Override
	protected void executeWithPorts() {
		this.producer.executeWithPorts();
	}

	public OutputPort<IMonitoringRecord> getOutputPort() {
		return this.reader.getOutputPort();
	}

	@Override
	public void validateOutputPorts(final List<InvalidPortConnection> invalidPortConnections) {
		this.reader.validateOutputPorts(invalidPortConnections);
	}

	@Override
	protected void onSignal(final ISignal signal, final InputPort<?> inputPort) {
		this.producer.onSignal(signal, inputPort);
	}

	@Override
	protected TerminationStrategy getTerminationStrategy() {
		return this.producer.getTerminationStrategy();
	}

	@Override
	protected void terminate() {
		this.producer.terminate();
	}

	@Override
	protected boolean shouldBeTerminated() {
		return this.producer.shouldBeTerminated();
	}

}
