package kieker.diagnosis.czi.stages;

import java.util.concurrent.atomic.AtomicInteger;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.io.database.DatabaseAfterEventRecord;
import kieker.common.record.io.database.DatabaseBeforeEventRecord;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.model.importer.stages.AbstractStage;

/**
 * Transforms DatabaseEventRecords into DatabaseOperationCalls
 * 
 * @author Christian Zirkelbach
 *
 */
public class DatabaseRecordTransformator extends
		AbstractStage<IMonitoringRecord, DatabaseOperationCall> {

	private static AtomicInteger recordID = new AtomicInteger(0);

	// TODO check whether one of the records is missing, error handling
	@Override
	protected void execute(IMonitoringRecord input) {
		if (input instanceof DatabaseBeforeEventRecord) {
			this.handleDatabaseBeforeEventRecord((DatabaseBeforeEventRecord) input);
		} else if (input instanceof DatabaseAfterEventRecord) {
			this.handleDatabaseAfterEventRecord((DatabaseAfterEventRecord) input);
		}
	}

	private void handleDatabaseBeforeEventRecord(
			final DatabaseBeforeEventRecord record) {
		final DatabaseOperationCall newCall = new DatabaseOperationCall("",
				record.getClassSignature(), record.getOperationSignature(),
				record.getCallArgs(), record.getReturnValue(), recordID.get(),
				record.getLoggingTimestamp(), 0);
		super.send(newCall);
	}

	private void handleDatabaseAfterEventRecord(
			final DatabaseAfterEventRecord record) {
		final DatabaseOperationCall newCall = new DatabaseOperationCall("",
				record.getClassSignature(), record.getOperationSignature(),
				record.getCallArgs(), record.getReturnValue(),
				recordID.getAndIncrement(), record.getLoggingTimestamp(), 0);
		super.send(newCall);
	}
}