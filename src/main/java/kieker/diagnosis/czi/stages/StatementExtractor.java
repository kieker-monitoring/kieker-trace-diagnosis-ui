package kieker.diagnosis.czi.stages;

import java.util.LinkedList;
import java.util.List;

import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.model.importer.stages.AbstractStage;

/**
 * Extract Statements from database calls and refines them
 * 
 * @author Christian Zirkelbach
 *
 */
public class StatementExtractor extends
		AbstractStage<List<DatabaseOperationCall>, List<DatabaseOperationCall>> {

	@Override
	protected void execute(List<DatabaseOperationCall> mergedCalls) {
		List<DatabaseOperationCall> mergedStatements = this
				.mergeStatementCallsAndChildren(mergedCalls);
		List<DatabaseOperationCall> refinedDatabaseStatementCalls = this
				.refineStatementCalls(mergedStatements);
		super.send(refinedDatabaseStatementCalls);
	}

	/**
	 * Merges Statements (createStatement and executors) into one call,
	 * including children
	 * 
	 * @param mergedCalls
	 * @return
	 */
	private List<DatabaseOperationCall> mergeStatementCallsAndChildren(
			List<DatabaseOperationCall> mergedCalls) {
		List<DatabaseOperationCall> mergedStatements = new LinkedList<DatabaseOperationCall>();
		for (int i = 0; i < (mergedCalls.size()); i += 1) {
			int numOfCalls = mergedStatements.size();
			if (mergedCalls.get(i).getOperation()
					.contains("Statement java.sql.Connection.createStatement")) {
				DatabaseOperationCall call = mergedCalls.get(i);
				DatabaseOperationCall newStatementCall = new DatabaseOperationCall(
						"", call.getComponent(), call.getOperation(),
						call.getStringClassArgs(),
						call.getFormattedReturnValue(), call.getTraceID(),
						call.getTimestamp(), 0);
				mergedStatements.add(newStatementCall);
			} else if (mergedCalls.get(i).getOperation()
					.contains("java.sql.Statement.execute")) {

				if (numOfCalls > 0) {
					DatabaseOperationCall parentCall = mergedStatements
							.get(mergedStatements.size() - 1);
					DatabaseOperationCall child = mergedCalls.get(i);
					parentCall.addChild(child);
					long duration = child.getTimestamp()
							- parentCall.getTimestamp();
					parentCall.setDuration(duration);
				}
			}
		}
		return mergedStatements;
	}

	/**
	 * refines merged statement with focus on the statement -> calculate overall
	 * response time, and keep just the child
	 * 
	 * @param mergedStatements
	 * @return
	 */
	private List<DatabaseOperationCall> refineStatementCalls(
			List<DatabaseOperationCall> mergedStatements) {
		List<DatabaseOperationCall> refinedDatabaseStatementCalls = new LinkedList<DatabaseOperationCall>();
		for (DatabaseOperationCall rootCall : mergedStatements) {
			List<DatabaseOperationCall> children = rootCall.getChildren();
			for (DatabaseOperationCall child : children) {
				final long duration = child.getTimestamp()
						- rootCall.getTimestamp();
				DatabaseOperationCall newRefinedCall = new DatabaseOperationCall(
						"", child.getComponent(), child.getOperation(),
						child.getStringClassArgs(),
						child.getFormattedReturnValue(), child.getTraceID(),
						child.getTimestamp(), duration);
				refinedDatabaseStatementCalls.add(newRefinedCall);
			}
		}
		return refinedDatabaseStatementCalls;
	}
}