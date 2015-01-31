package kieker.gui.common.model.importer.stages;

import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public abstract class AbstractStage<I, O> extends AbstractConsumerStage<I> {

	private final OutputPort<O> outputPort = super.createOutputPort();

	protected final void send(final O element) {
		this.outputPort.send(element);
	}

	public final OutputPort<O> getOutputPort() {
		return this.outputPort;
	}

}
