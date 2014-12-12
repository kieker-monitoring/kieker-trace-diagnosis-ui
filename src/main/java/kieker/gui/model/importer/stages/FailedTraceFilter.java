package kieker.gui.model.importer.stages;

import kieker.gui.model.domain.ExecutionEntry;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

public final class FailedTraceFilter extends AbstractConsumerStage<ExecutionEntry> {

	private final OutputPort<ExecutionEntry> outputPort = super.createOutputPort();

	@Override
	protected void execute(final ExecutionEntry element) {
		if (element.isFailed()) {
			this.outputPort.send(element);
		}
	}

	public OutputPort<ExecutionEntry> getOutputPort() {
		return this.outputPort;
	}

}
