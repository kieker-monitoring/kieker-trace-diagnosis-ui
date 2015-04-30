package kieker.diagnosis.czi;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.database.DatabaseAfterEventRecord;
import kieker.common.record.io.database.DatabaseBeforeEventRecord;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.model.importer.stages.AbstractStage;

public class DatabaseRecordTransformator extends
		AbstractStage<IMonitoringRecord, DatabaseOperationCall> {

	@Override
	protected void execute(IMonitoringRecord input) {

		System.out.println("executing DatabaseRecordReconstructor");
		System.out.println(input.toString());
		System.out.println(input.getClass().getName());
		
		if (input instanceof DatabaseBeforeEventRecord) {
			System.out.println("matched DatabaseBeforeEventRecord");
			this.handleDatabaseBeforeEventRecord((DatabaseBeforeEventRecord) input);
		} else if (input instanceof DatabaseAfterEventRecord) {
			System.out.println("matched DatabaseAfterEventRecord");
			this.handleDatabaseAfterEventRecord((DatabaseAfterEventRecord) input);
		}
	}

	private void handleDatabaseBeforeEventRecord(
			final DatabaseBeforeEventRecord record) {
		final DatabaseOperationCall newCall = new DatabaseOperationCall(null,
				record.getClassSignature(), record.getOperationSignature(),
				record.getCallArgs(), record.getReturnValue(), 0,
				record.getLoggingTimestamp());
		super.send(newCall);
	}

	private void handleDatabaseAfterEventRecord(
			final DatabaseAfterEventRecord record) {
		final DatabaseOperationCall newCall = new DatabaseOperationCall(null,
				record.getClassSignature(), record.getOperationSignature(),
				record.getCallArgs(), record.getReturnValue(), 0,
				record.getLoggingTimestamp());
		super.send(newCall);
	}
}
