package kieker.diagnosis.model.importer.stages;

import java.util.ArrayList;
import java.util.List;

import teetime.framework.Analysis;
import teetime.framework.AnalysisConfiguration;
import teetime.framework.InputPort;
import teetime.framework.OutputPort;
import teetime.framework.pipe.IPipeFactory;
import teetime.framework.pipe.PipeFactoryRegistry.PipeOrdering;
import teetime.framework.pipe.PipeFactoryRegistry.ThreadCommunication;
import teetime.stage.CollectorSink;
import teetime.stage.IterableProducer;

public final class StageTester {

	public static <I> InputHolder<I> testStageBySending(final Iterable<I> input) {
		return new InputHolder<I>(input);
	}

	public static final class InputHolder<I> {

		private final Iterable<I> input;

		public InputHolder(final Iterable<I> input) {
			this.input = input;
		}

		public InputPortHolder<I> to(final InputPort<I> inputPort) {
			return new InputPortHolder<I>(this.input, inputPort);
		}

	}

	public static final class InputPortHolder<I> {

		private final Iterable<I> input;
		private final InputPort<I> inputPort;

		public InputPortHolder(final Iterable<I> input, final InputPort<I> inputPort) {
			this.input = input;
			this.inputPort = inputPort;
		}

		public <O> List<O> andReceivingFrom(final OutputPort<O> outputPort) {
			final Configuration<I, O> configuration = new Configuration<I, O>(this.input, this.inputPort, outputPort);
			final Analysis analysis = new Analysis(configuration);
			analysis.start();

			return configuration.getOutput();
		}
	}

	private static class Configuration<I, O> extends AnalysisConfiguration {

		private final List<O> collectorList = new ArrayList<>();

		public Configuration(final Iterable<I> input, final InputPort<I> inputPort, final OutputPort<O> outputPort) {
			final IterableProducer<I> producer = new IterableProducer<>(input);
			final CollectorSink<O> collector = new CollectorSink<>(this.collectorList);

			final IPipeFactory pipeFactory = AnalysisConfiguration.PIPE_FACTORY_REGISTRY.getPipeFactory(ThreadCommunication.INTRA, PipeOrdering.ARBITRARY, false);
			pipeFactory.create(producer.getOutputPort(), inputPort);
			pipeFactory.create(outputPort, collector.getInputPort());

			this.addThreadableStage(producer);
		}

		public List<O> getOutput() {
			return this.collectorList;
		}

	}

}
