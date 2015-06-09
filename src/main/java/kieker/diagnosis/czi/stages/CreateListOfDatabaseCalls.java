package kieker.diagnosis.czi.stages;

import java.util.LinkedList;
import java.util.List;

import kieker.diagnosis.domain.DatabaseOperationCall;
import teetime.framework.AbstractConsumerStage;
import teetime.framework.OutputPort;

/**
 * Creates a List of DatabaseOperationCall from single DatabaseOperationCalls
 * 
 * @author Christian Zirkelbach
 *
 */
public final class CreateListOfDatabaseCalls extends
		AbstractConsumerStage<DatabaseOperationCall> {

	private List<DatabaseOperationCall> databaseCalls = new LinkedList<DatabaseOperationCall>();
	private final OutputPort<List<DatabaseOperationCall>> outputPort = createOutputPort();

	@Override
	protected void execute(final DatabaseOperationCall element) {
		databaseCalls.add(element);
	}

	@Override
	public void onTerminating() throws Exception {
		outputPort.send(databaseCalls);
		super.onTerminating();
	}

	public OutputPort<List<DatabaseOperationCall>> getOutputPort() {
		return outputPort;
	}
}
