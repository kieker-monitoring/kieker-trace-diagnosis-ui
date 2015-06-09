package kieker.diagnosis.czi.stages;

import java.util.LinkedList;
import java.util.List;

import kieker.diagnosis.czi.Utils;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.domain.PreparedStatementCall;
import kieker.diagnosis.model.importer.stages.AbstractStage;

/**
 * Extracts and aggregates PreparedStatements
 * 
 * @author Christian Zirkelbach
 *
 */
public class PreparedStatementExtactor extends
		AbstractStage<List<DatabaseOperationCall>, List<PreparedStatementCall>> {

	@Override
	protected void execute(List<DatabaseOperationCall> mergedCalls) {
		List<DatabaseOperationCall> mergedPreparedStatementCalls = mergedPreparedStatements(mergedCalls);
		List<PreparedStatementCall> refinedPreparedStatementCalls = refinePreparedStatements(mergedPreparedStatementCalls);
		List<PreparedStatementCall> groupedRefinedPreparedStatementCalls = groupRefinedPreparedStatements(refinedPreparedStatementCalls);
		System.out.println("groupedRefinedPreparedStatementCalls.size(): " + groupedRefinedPreparedStatementCalls.size());
		super.send(groupedRefinedPreparedStatementCalls);
	}

	/**
	 * Merges PreparedStatements (prepareStatement, setter and executors) into
	 * one call including children
	 * 
	 * @param refinedDatabaseStatementCalls
	 * @return
	 */
	private List<DatabaseOperationCall> mergedPreparedStatements(
			List<DatabaseOperationCall> mergedCalls) {
		List<DatabaseOperationCall> mergedPreparedStatementCalls = new LinkedList<DatabaseOperationCall>();

		for (int i = 0; i < (mergedCalls.size()); i += 1) {

			int numOfPrepCalls = mergedPreparedStatementCalls.size();
			if (mergedCalls
					.get(i)
					.getOperation()
					.equals("PreparedStatement java.sql.Connection.prepareStatement(String)")) {

				DatabaseOperationCall call = mergedCalls.get(i);

				DatabaseOperationCall newPreparedStatementCall = new DatabaseOperationCall(
						"", call.getComponent(), call.getOperation(),
						call.getStringClassArgs(),
						call.getFormattedReturnValue(), call.getTraceID(),
						call.getTimestamp(), 0);

				mergedPreparedStatementCalls.add(newPreparedStatementCall);

			}

			else if (mergedCalls.get(i).getOperation()
					.contains("java.sql.PreparedStatement.set")) {
				if (numOfPrepCalls > 0) {
					DatabaseOperationCall parentPreparedCall = mergedPreparedStatementCalls
							.get(mergedPreparedStatementCalls.size() - 1);

					DatabaseOperationCall child = mergedCalls.get(i);
					parentPreparedCall.addChild(child);
				}
			}

			else if (mergedCalls.get(i).getOperation()
					.contains("java.sql.PreparedStatement.execute")) {

				if (numOfPrepCalls > 0) {
					DatabaseOperationCall parentPreparedCall = mergedPreparedStatementCalls
							.get(mergedPreparedStatementCalls.size() - 1);

					DatabaseOperationCall child = mergedCalls.get(i);
					parentPreparedCall.addChild(child);
					long duration = child.getTimestamp()
							- parentPreparedCall.getTimestamp();
					parentPreparedCall.setDuration(duration);
				}
			}
		}
		return mergedPreparedStatementCalls;
	}

	/**
	 * Refines PreparedStatemens based on their statement
	 * 
	 * @param mergedPreparedStatementCalls
	 * @return
	 */
	private List<PreparedStatementCall> refinePreparedStatements(
			List<DatabaseOperationCall> mergedPreparedStatementCalls) {
		List<PreparedStatementCall> refinedPreparedStatementCalls = new LinkedList<PreparedStatementCall>();

		// convert into PreparedStatementCalls
		for (DatabaseOperationCall mergedPrepCall : mergedPreparedStatementCalls) {
			PreparedStatementCall newCall = new PreparedStatementCall(
					mergedPrepCall.getContainer(),
					mergedPrepCall.getComponent(),
					mergedPrepCall.getOperation(),
					mergedPrepCall.getFormattedReturnValue(),
					mergedPrepCall.getTraceID(), mergedPrepCall.getTimestamp(),
					mergedPrepCall.getDuration());

			// merge into root of call hierarchy
			// customizes the SQL-Statement for visualization purposes
			String abstractStatement = mergedPrepCall.getStringClassArgs()
					.toUpperCase();

			// insert Values into PreparedStatement to display concrete
			// Statement
			String concreteStatement = Utils
					.insertParametersIntoPreparedStatment(mergedPrepCall);

			// duration including the response time of the children
			long totalDuration = mergedPrepCall.getDuration();
			List<DatabaseOperationCall> children = mergedPrepCall.getChildren();
			for (DatabaseOperationCall child : children) {
				totalDuration += child.getDuration();
			}

			newCall.setDuration(totalDuration);
			newCall.setAbstractStatement(abstractStatement);
			newCall.setConcreteStatement(concreteStatement);

			refinedPreparedStatementCalls.add(newCall);
		}
		return refinedPreparedStatementCalls;
	}

	/**
	 * Group based on the abstract statement includes creating a new root node
	 * 
	 * @param refinedPreparedStatementCalls
	 * @return
	 */
	private List<PreparedStatementCall> groupRefinedPreparedStatements(
			List<PreparedStatementCall> refinedPreparedStatementCalls) {
		List<PreparedStatementCall> groupedRefinedPreparedStatementCalls = new LinkedList<PreparedStatementCall>();
		boolean handled = false;

		for (PreparedStatementCall refinedPrepCall : refinedPreparedStatementCalls) {
			handled = false;

			// initial case - empty list
			if (refinedPreparedStatementCalls.isEmpty()) {
				PreparedStatementCall newAbstractCall = new PreparedStatementCall(
						refinedPrepCall.getContainer(),
						refinedPrepCall.getComponent(),
						refinedPrepCall.getOperation(), "", 0, 0, 0,
						refinedPrepCall.getAbstractStatement(), "");
				PreparedStatementCall newConcreteCall = new PreparedStatementCall(
						refinedPrepCall.getContainer(),
						refinedPrepCall.getComponent(),
						refinedPrepCall.getOperation(),
						refinedPrepCall.getFormattedReturnValue(),
						refinedPrepCall.getTraceID(),
						refinedPrepCall.getTimestamp(),
						refinedPrepCall.getDuration(),
						refinedPrepCall.getAbstractStatement(),
						refinedPrepCall.getConcreteStatement());
				newAbstractCall.addChild(newConcreteCall);
				groupedRefinedPreparedStatementCalls.add(newAbstractCall);
				handled = true;

			} else {
				for (int i = 0; i < (groupedRefinedPreparedStatementCalls
						.size()); i++) {
					PreparedStatementCall existingCall = groupedRefinedPreparedStatementCalls
							.get(i);

					// operation and statement arguments match
					if (existingCall.getAbstractStatement().equals(
							refinedPrepCall.getAbstractStatement())) {
						PreparedStatementCall newConcreteCall = new PreparedStatementCall(
								refinedPrepCall.getContainer(),
								refinedPrepCall.getComponent(),
								refinedPrepCall.getOperation(),
								refinedPrepCall.getFormattedReturnValue(),
								refinedPrepCall.getTraceID(),
								refinedPrepCall.getTimestamp(),
								refinedPrepCall.getDuration(),
								refinedPrepCall.getAbstractStatement(),
								refinedPrepCall.getConcreteStatement());
						existingCall.addChild(newConcreteCall);
						handled = true;
					}
				}
				if (!handled) {
					PreparedStatementCall newAbstractCall = new PreparedStatementCall(
							refinedPrepCall.getContainer(),
							refinedPrepCall.getComponent(),
							refinedPrepCall.getOperation(), "", 0, 0, 0,
							refinedPrepCall.getAbstractStatement(), "");
					PreparedStatementCall newConcreteCall = new PreparedStatementCall(
							refinedPrepCall.getContainer(),
							refinedPrepCall.getComponent(),
							refinedPrepCall.getOperation(),
							refinedPrepCall.getFormattedReturnValue(),
							refinedPrepCall.getTraceID(),
							refinedPrepCall.getTimestamp(),
							refinedPrepCall.getDuration(),
							refinedPrepCall.getAbstractStatement(),
							refinedPrepCall.getConcreteStatement());
					newAbstractCall.addChild(newConcreteCall);
					groupedRefinedPreparedStatementCalls.add(newAbstractCall);
					handled = true;
				}
			}
		}
		return groupedRefinedPreparedStatementCalls;
	}
}