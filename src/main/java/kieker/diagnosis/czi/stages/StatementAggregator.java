package kieker.diagnosis.czi.stages;

import java.util.LinkedList;
import java.util.List;

import kieker.diagnosis.domain.AggregatedDatabaseOperationCall;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.model.importer.stages.AbstractStage;

/**
 * Aggregates Statements
 * 
 * @author Christian Zirkelbach
 *
 */
public class StatementAggregator
		extends
		AbstractStage<List<DatabaseOperationCall>, List<AggregatedDatabaseOperationCall>> {

	@Override
	protected void execute(
			List<DatabaseOperationCall> refinedDatabaseStatementCalls) {
		List<AggregatedDatabaseOperationCall> aggregatedDatabaseStatementCalls = aggregateStatementCalls(refinedDatabaseStatementCalls);
		super.send(aggregatedDatabaseStatementCalls);
	}

	/**
	 * Aggregates StatementCalls (based on operation and statement (whole
	 * hierarchy - including children)
	 * 
	 * @param refinedDatabaseStatementCalls
	 * @return
	 */
	private List<AggregatedDatabaseOperationCall> aggregateStatementCalls(
			List<DatabaseOperationCall> refinedDatabaseStatementCalls) {
		List<AggregatedDatabaseOperationCall> aggregatedDatabaseStatementCalls = new LinkedList<AggregatedDatabaseOperationCall>();

		boolean handled = false;
		for (DatabaseOperationCall newCall : refinedDatabaseStatementCalls) {

			handled = false;

			// initial case - empty list
			if (aggregatedDatabaseStatementCalls.isEmpty()) {
				final long duration = newCall.getDuration();
				AggregatedDatabaseOperationCall newAggregatedDatabaseStatementCall = new AggregatedDatabaseOperationCall(
						"", newCall.getComponent(), newCall.getOperation(),
						newCall.getStringClassArgs(), duration, duration,
						duration, duration, 1);
				aggregatedDatabaseStatementCalls
						.add(newAggregatedDatabaseStatementCall);
				handled = true;

			} else {
				for (int i = 0; i < (aggregatedDatabaseStatementCalls.size()); i++) {
					AggregatedDatabaseOperationCall existingCall = aggregatedDatabaseStatementCalls
							.get(i);

					// operation and statement arguments match
					if ((existingCall.getOperation().equals(newCall
							.getOperation()))
							&& (existingCall.getStringClassArgs()
									.equals(newCall.getStringClassArgs()))) {

						// previously added aggregated call
						final long existingCallTotalDuration = existingCall
								.getTotalDuration();
						final long existingCallMinDuration = existingCall
								.getMinDuration();
						final long existingCallMaxDuration = existingCall
								.getMaxDuration();

						final long totalDuration = existingCallTotalDuration
								+ newCall.getDuration();
						existingCall.setTotalDuration(totalDuration);

						if (newCall.getDuration() < existingCallMinDuration) {
							final long minDuration = newCall.getDuration();
							existingCall.setMinDuration(minDuration);
						}

						if (newCall.getDuration() > existingCallMaxDuration) {
							final long maxDuration = newCall.getDuration();
							existingCall.setMaxDuration(maxDuration);
						}

						final int calls = existingCall.getCalls() + 1;
						existingCall.setCalls(calls);

						final long averageDuration = existingCallTotalDuration
								/ calls;
						existingCall.setAvgDuration(averageDuration);

						handled = true;
					}
				}
			}

			// list not empty and no matching call existing -> add new
			// aggregatedCall
			if (!handled) {
				long duration = newCall.getDuration();
				AggregatedDatabaseOperationCall newAggregatedDatabaseOperationCall = new AggregatedDatabaseOperationCall(
						"", newCall.getComponent(), newCall.getOperation(),
						newCall.getStringClassArgs(), duration, duration,
						duration, duration, 1);
				aggregatedDatabaseStatementCalls
						.add(newAggregatedDatabaseOperationCall);
				handled = true;
			}
		}
		return aggregatedDatabaseStatementCalls;
	}
}