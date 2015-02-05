package kieker.diagnosis.common.model.importer.stages;

import kieker.diagnosis.common.domain.OperationCall;
import kieker.diagnosis.common.domain.Trace;

public final class OperationCallExtractor extends AbstractStage<Trace, OperationCall> {

	@Override
	protected void execute(final Trace element) {
		this.sendAllCalls(element.getRootOperationCall());
	}

	private void sendAllCalls(final OperationCall call) {
		super.send(call);

		for (final OperationCall child : call.getChildren()) {
			this.sendAllCalls(child);
		}
	}

}
