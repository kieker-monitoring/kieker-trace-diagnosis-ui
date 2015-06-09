package kieker.diagnosis.czi.stages;

import java.util.LinkedList;
import java.util.List;

import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.model.importer.stages.AbstractStage;

/**
 * Merges database calls
 * 
 * @author Christian Zirkelbach
 *
 */
public class DatabaseCallMerger extends
		AbstractStage<List<DatabaseOperationCall>, List<DatabaseOperationCall>> {

	@Override
	protected void execute(List<DatabaseOperationCall> originalCalls) {
		List<DatabaseOperationCall> mergedCalls = this
				.mergeStatementCalls(originalCalls);
		super.send(mergedCalls);
	}

	/**
	 * Stores the results from the analysis & merge before and after events into
	 * one database call
	 * 
	 * @param originalCalls
	 * @return
	 */
	private List<DatabaseOperationCall> mergeStatementCalls(
			List<DatabaseOperationCall> originalCalls) {
		List<DatabaseOperationCall> mergedCalls = new LinkedList<DatabaseOperationCall>();
		for (int i = 0; i < (originalCalls.size() - 1); i += 2) {
			final long duration = originalCalls.get(i + 1).getTimestamp()
					- originalCalls.get(i).getTimestamp();
			final DatabaseOperationCall newMergedCall = new DatabaseOperationCall(
					"", originalCalls.get(i + 1).getComponent(), originalCalls
							.get(i + 1).getOperation(), originalCalls
							.get(i + 1).getStringClassArgs(), originalCalls
							.get(i + 1).getFormattedReturnValue(),
					originalCalls.get(i + 1).getTraceID(), originalCalls.get(i)
							.getTimestamp(), duration);
			mergedCalls.add(newMergedCall);
		}
		return mergedCalls;
	}
}