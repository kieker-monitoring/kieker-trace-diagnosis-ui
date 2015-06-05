/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.model;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.czi.DatabaseImportAnalysisConfiguration;
import kieker.diagnosis.czi.Utils;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.domain.AbstractTrace;
import kieker.diagnosis.domain.AggregatedDatabaseOperationCall;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.DatabaseOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.PreparedStatementCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.importer.ImportAnalysisConfiguration;

import org.springframework.stereotype.Repository;

import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 * @author Christian Zirkelbach
 */
@Repository
public final class DataModel extends Observable {

	private List<Trace> traces = Collections.emptyList();
	private List<Trace> failureContainingTraces = Collections.emptyList();
	private List<Trace> failedTraces = Collections.emptyList();
	private List<AggregatedTrace> aggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failedAggregatedTraces = Collections
			.emptyList();
	private List<AggregatedTrace> failureAggregatedContainingTraces = Collections
			.emptyList();
	private List<OperationCall> operationCalls = Collections.emptyList();
	private List<OperationCall> failedOperationCalls = Collections.emptyList();
	private List<AggregatedOperationCall> aggregatedOperationCalls = Collections
			.emptyList();
	private List<AggregatedOperationCall> aggregatedFailedOperationCalls = Collections
			.emptyList();

	// TODO czi
	private List<DatabaseOperationCall> databaseOperationCalls = Collections
			.emptyList();
	private List<AggregatedDatabaseOperationCall> aggregatedDatabaseStatementCalls = Collections
			.emptyList();
	private List<DatabaseOperationCall> databaseStatementCalls = Collections
			.emptyList();
	private List<DatabaseOperationCall> databasePreparedStatementCalls = Collections
			.emptyList();
	private List<PreparedStatementCall> refinedDatabasePreparedStatementCalls = Collections
			.emptyList();

	private File importDirectory;
	private TimeUnit timeUnit;
	private long analysisDurationInMS;
	private int incompleteTraces;
	private long beginTimestamp;
	private long endTimestamp;

	public void loadMonitoringLogFromFS(final String directory) {
		final long tin = System.currentTimeMillis();

		// Load and analyze the monitoring logs from the given directory
		this.importDirectory = new File(directory);
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(
				this.importDirectory);
		final Analysis<ImportAnalysisConfiguration> analysis = new Analysis<>(
				analysisConfiguration);
		analysis.executeBlocking();

		// Store the results from the analysis
		this.traces = analysisConfiguration.getTracesList();
		this.failedTraces = analysisConfiguration.getFailedTracesList();
		this.failureContainingTraces = analysisConfiguration
				.getFailureContainingTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();
		this.failedAggregatedTraces = analysisConfiguration
				.getFailedAggregatedTracesList();
		this.failureAggregatedContainingTraces = analysisConfiguration
				.getFailureContainingAggregatedTracesList();
		this.operationCalls = analysisConfiguration.getOperationCalls();
		this.failedOperationCalls = analysisConfiguration
				.getFailedOperationCalls();
		this.aggregatedOperationCalls = analysisConfiguration
				.getAggregatedOperationCalls();
		this.aggregatedFailedOperationCalls = analysisConfiguration
				.getAggregatedFailedOperationCalls();
		this.incompleteTraces = analysisConfiguration.countIncompleteTraces();

		// ///////////////////////////////////////////
		// TODO czi
		final DatabaseImportAnalysisConfiguration databaseOperationAnalysis = new DatabaseImportAnalysisConfiguration(
				this.importDirectory);
		final Analysis<DatabaseImportAnalysisConfiguration> databaseAnalysis = new Analysis<>(
				databaseOperationAnalysis);
		databaseAnalysis.executeBlocking();

		// Stores the results from the analysis & merge before and after events
		// into one database call
		// TODO transfer into own stage
		List<DatabaseOperationCall> originalCalls = databaseOperationAnalysis
				.getDatabaseOperationCalls();
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

		this.databaseOperationCalls = mergedCalls;

		System.out.println("DatabaseOperationCalls merged (before and after events): "
				+ databaseOperationCalls.size());

		// Merges Statements (createStatement and executors)
		// into one call including children
		// TODO transfer into own stage
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
			}

			else if (mergedCalls.get(i).getOperation()
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

		this.databaseStatementCalls = mergedStatements;

		System.out.println("Statements merged (createStatement and executors): " + mergedStatements.size());
		
		// refines merged statement with focus on the statement -> calculate
		// overall response time and keep just the child
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

		System.out.println("DatabaseStatementCalls refined (cropped createStatement): "
				+ refinedDatabaseStatementCalls.size());

		// // TODO
		// // DEBUGGING OUTPUT
		// System.out.println("Refined DatabaseOperationCalls:");
		// for (int i = 0; i < (refinedDatabaseStatementCalls.size()); i += 1) {
		// DatabaseOperationCall call = refinedDatabaseStatementCalls.get(i);
		//
		// System.out.println("Operation: " + call.getOperation());
		// System.out.println("Statement: " + call.getStringClassArgs());
		// System.out.println("Duration (in ns): " + call.getDuration());
		// System.out
		// .println("-----------------------------------------------------------");
		// }

		// Aggregates StatementCalls (based on operation and statement (whole
		// hierarchy - including children)
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
						duration, duration, duration, 1);
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
						final long existingCallMedianDuration = existingCall
								.getMedianDuration();
						final long existingCallMeanDuration = existingCall
								.getMeanDuration();

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

						final long medianDuration = existingCallMedianDuration;
						final long meanDuration = existingCallTotalDuration
								/ calls;

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
						duration, duration, duration, 1);
				aggregatedDatabaseStatementCalls
						.add(newAggregatedDatabaseOperationCall);
				handled = true;
			}
		}

		this.aggregatedDatabaseStatementCalls = aggregatedDatabaseStatementCalls;

		System.out.println("DatabaseStatementCalls aggregated: "
				+ aggregatedDatabaseStatementCalls.size());

		// // // TODO
		// // // DEBUGGING OUTPUT
		// System.out.println("Aggregated DatabaseOperationCalls:");
		// for (int i = 0; i < (aggregatedDatabaseStatementCalls.size()); i +=
		// 1) {
		// AggregatedDatabaseOperationCall call =
		// aggregatedDatabaseStatementCalls.get(i);
		//
		// System.out.println("Operation: " + call.getOperation());
		// System.out.println("Statement: " + call.getStringClassArgs());
		// System.out.println("Total Duration (in ns): " +
		// call.getTotalDuration());
		// System.out.println("Calls: " + call.getCalls());
		// System.out
		// .println("-----------------------------------------------------------");
		// }

		// Merges PreparedStatements (prepareStatement, setter and executors)
		// into one call including children
		// TODO transfer into own stage
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

				// System.out.println("Prepared Statement created!");

			}

			else if (mergedCalls.get(i).getOperation()
					.contains("java.sql.PreparedStatement.set")) {
				if (numOfPrepCalls > 0) {
					DatabaseOperationCall parentPreparedCall = mergedPreparedStatementCalls
							.get(mergedPreparedStatementCalls.size() - 1);

					DatabaseOperationCall child = mergedCalls.get(i);
					parentPreparedCall.addChild(child);
				}

				// System.out.println("Prepared Statement setter used!");
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

				// System.out.println("Prepared Statement executed!");
			}
		}

		this.databasePreparedStatementCalls = mergedPreparedStatementCalls;

		System.out.println("PreparedStatements merged: "
				+ mergedPreparedStatementCalls.size());
		
		// Refine PreparedStatemens based on their statement
		// TODO transfer into own stage
		List<PreparedStatementCall> refinedPreparedStatementCalls = new LinkedList<PreparedStatementCall>();

		// convert into PreparedStatementCalls
		for (DatabaseOperationCall mergedPrepCall : mergedPreparedStatementCalls) {
			PreparedStatementCall newCall = new PreparedStatementCall(mergedPrepCall.getContainer(), mergedPrepCall.getComponent(), 
					mergedPrepCall.getOperation(), mergedPrepCall.getFormattedReturnValue(), 
					mergedPrepCall.getTraceID(), mergedPrepCall.getTimestamp(), mergedPrepCall.getDuration());
			
			// merge into root of call hierarchy
			// customizes the SQL-Statement for visualization purposes
			String abstractStatement = mergedPrepCall.getStringClassArgs().toUpperCase();
								
			// insert Values into PreparedStatement to display concrete Statement
			String concreteStatement = Utils.insertParametersIntoPreparedStatment(mergedPrepCall);
			
			// duration including the repsonse time of the children
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
		
		this.refinedDatabasePreparedStatementCalls = refinedPreparedStatementCalls;
		
		System.out.println("PreparedStatements (refined): "
				+ refinedPreparedStatementCalls.size());
		
		// Testing
//		 System.out.println("Refined Prepared Statements");
//		 for (int i = 0; i < (refinedPreparedStatementCalls.size()); i += 1) {
//	     PreparedStatementCall call = refinedPreparedStatementCalls.get(i);
//		
//		 System.out.println("Operation: " + call.getOperation());
//		 System.out.println("Abstract Statement: " + call.getAbstractStatement());
//		 System.out.println("Concrete Statement: " + call.getConcreteStatement());
//		 System.out.println("Duration (in ns): " + call.getDuration());
//		 System.out.println("Children:");
//		 System.out.println("-----------------------------------------------------------");
//		 }
		
		// Group based on the abstract statement includes creating a new root node
		List<PreparedStatementCall> groupedRefinedPreparedStatementCalls = new LinkedList<PreparedStatementCall>();
		
		for (PreparedStatementCall refinedPrepCall : refinedPreparedStatementCalls) {
			handled = false;

			// initial case - empty list
			if (refinedPreparedStatementCalls.isEmpty()) {
				PreparedStatementCall newAbstractCall = new PreparedStatementCall(refinedPrepCall.getContainer(), refinedPrepCall.getComponent(), 
						refinedPrepCall.getOperation(), "", 0, 0, 0, refinedPrepCall.getAbstractStatement(), "");
				PreparedStatementCall newConcreteCall = new PreparedStatementCall(refinedPrepCall.getContainer(), refinedPrepCall.getComponent(), 
						refinedPrepCall.getOperation(), refinedPrepCall.getFormattedReturnValue(), refinedPrepCall.getTraceID(), refinedPrepCall.getTimestamp(), refinedPrepCall.getDuration(), refinedPrepCall.getAbstractStatement(), refinedPrepCall.getConcreteStatement());
				newAbstractCall.addChild(newConcreteCall);
				groupedRefinedPreparedStatementCalls.add(newAbstractCall);
				handled = true;

			} else {
				for (int i = 0; i < (groupedRefinedPreparedStatementCalls.size()); i++) {
					PreparedStatementCall existingCall = groupedRefinedPreparedStatementCalls
							.get(i);
					
					// operation and statement arguments match
					if (existingCall.getAbstractStatement().equals(refinedPrepCall
							.getAbstractStatement())) {
						PreparedStatementCall newConcreteCall = new PreparedStatementCall(refinedPrepCall.getContainer(), refinedPrepCall.getComponent(), 
								refinedPrepCall.getOperation(), refinedPrepCall.getFormattedReturnValue(), refinedPrepCall.getTraceID(), refinedPrepCall.getTimestamp(), refinedPrepCall.getDuration(), refinedPrepCall.getAbstractStatement(), refinedPrepCall.getConcreteStatement());
						existingCall.addChild(newConcreteCall);
						handled = true;
					}
				}
				if (!handled) {
					PreparedStatementCall newAbstractCall = new PreparedStatementCall(refinedPrepCall.getContainer(), refinedPrepCall.getComponent(), 
							refinedPrepCall.getOperation(), "", 0, 0, 0, refinedPrepCall.getAbstractStatement(), "");
					PreparedStatementCall newConcreteCall = new PreparedStatementCall(refinedPrepCall.getContainer(), refinedPrepCall.getComponent(), 
							refinedPrepCall.getOperation(), refinedPrepCall.getFormattedReturnValue(), refinedPrepCall.getTraceID(), refinedPrepCall.getTimestamp(), refinedPrepCall.getDuration(), refinedPrepCall.getAbstractStatement(), refinedPrepCall.getConcreteStatement());
					newAbstractCall.addChild(newConcreteCall);
					groupedRefinedPreparedStatementCalls.add(newAbstractCall);
					handled = true;
				}
			}
		}
		
		this.refinedDatabasePreparedStatementCalls = groupedRefinedPreparedStatementCalls;
		
		System.out.println("PreparedStatements (refined and grouped): "
				+ groupedRefinedPreparedStatementCalls.size());
		
		
		 //Simple output for testing purposes
//		 System.out.println("Merged Statements");
//		 for (int i = 0; i < (groupedRefinedPreparedStatementCalls.size()); i += 1) {
//		 PreparedStatementCall call = groupedRefinedPreparedStatementCalls.get(i);
//		
//		 System.out.println("Operation: " + call.getOperation());
//		 System.out.println("Abstract Statement: " + call.getAbstractStatement());
//		 System.out.println("Concrete Statement: " + call.getConcreteStatement());
//		 System.out.println("Duration (in ns): " + call.getDuration());
//		 System.out.println("Children:");
//		
//		 for (PreparedStatementCall childCall : call.getChildren()) {
//		 System.out.println("	Operation: " + childCall.getOperation());
//		 System.out.println("Abstract Statement: " + childCall.getAbstractStatement());
//		 System.out.println("Concrete Statement: " + childCall.getConcreteStatement());
//		 }
//		 System.out
//		 .println("-----------------------------------------------------------");
//		 }
		 
		// Simple output for testing purposes
		// System.out.println("Merged Statements");
		// for (int i = 0; i < (mergedStatements.size()); i += 1) {
		// DatabaseOperationCall call = mergedStatements.get(i);
		//
		// System.out.println("Operation: " + call.getOperation());
		// System.out.println("Statement: " + call.getStringClassArgs());
		// System.out.println("Duration (in ns): " + call.getDuration());
		// System.out.println("Children:");
		//
		// for (DatabaseOperationCall childCall : call.getChildren()) {
		// System.out.println("	Operation: " + childCall.getOperation());
		// System.out.println("	Statement: "
		// + childCall.getStringClassArgs());
		// }
		// System.out
		// .println("-----------------------------------------------------------");
		// }
		//
		// System.out.println("Merged Prepared Statements");
		// for (int i = 0; i < (mergedPreparedStatements.size()); i += 1) {
		// DatabaseOperationCall call = mergedPreparedStatements.get(i);
		//
		// System.out.println("Operation: " + call.getOperation());
		// System.out.println("Statement: " + call.getStringClassArgs());
		// System.out.println("Duration (in ns): " + call.getDuration());
		// System.out.println("Children:");
		//
		// for (DatabaseOperationCall childCall : call.getChildren()) {
		// System.out.println("	Operation: " + childCall.getOperation());
		// System.out.println("	Statement: "
		// + childCall.getStringClassArgs());
		// }
		// System.out
		// .println("-----------------------------------------------------------");
		// }

		// ///////////////////////////////////////////

		this.beginTimestamp = analysisConfiguration.getBeginTimestamp();
		this.endTimestamp = analysisConfiguration.getEndTimestamp();

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration
				.getMetadataRecords();
		if (!metadataRecords.isEmpty()) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get(0);
			this.timeUnit = TimeUnit.valueOf(metadataRecord.getTimeUnit());
		} else {
			this.timeUnit = TimeUnit.NANOSECONDS;
		}

		final long tout = System.currentTimeMillis();

		this.analysisDurationInMS = tout - tin;

		this.setChanged();
		this.notifyObservers();
	}

	public long getBeginTimestamp() {
		return this.beginTimestamp;
	}

	public long getEndTimestamp() {
		return this.endTimestamp;
	}

	public int countIncompleteTraces() {
		return this.incompleteTraces;
	}

	public File getImportDirectory() {
		return this.importDirectory;
	}

	public long getAnalysisDurationInMS() {
		return this.analysisDurationInMS;
	}

	public List<Trace> getTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.traces, regExpr);
	}

	public List<Trace> getFailedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failedTraces, regExpr);
	}

	public List<Trace> getFailureContainingTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failureContainingTraces,
				regExpr);
	}

	public List<AggregatedTrace> getAggregatedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.aggregatedTraces, regExpr);
	}

	public List<AggregatedTrace> getFailedAggregatedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failedAggregatedTraces,
				regExpr);
	}

	public List<AggregatedTrace> getFailureContainingAggregatedTraces(
			final String regExpr) {
		return this.filterTracesIfNecessary(
				this.failureAggregatedContainingTraces, regExpr);
	}

	public List<OperationCall> getOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.operationCalls, regExpr);
	}

	public List<OperationCall> getFailedOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.failedOperationCalls, regExpr);
	}

	public List<AggregatedOperationCall> getAggregatedOperationCalls(
			final String regExpr) {
		return this.filterCallsIfNecessary(this.aggregatedOperationCalls,
				regExpr);
	}

	public List<AggregatedOperationCall> getAggregatedFailedOperationCalls(
			final String regExpr) {
		return this.filterCallsIfNecessary(this.aggregatedFailedOperationCalls,
				regExpr);
	}

	public List<DatabaseOperationCall> getDatabaseOperationCalls(
			final String regExpr) {
		return this
				.filterCallsIfNecessary(this.databaseOperationCalls, regExpr);
	}

	public List<DatabaseOperationCall> getDatabaseStatementCalls(
			final String regExpr) {
		return this
				.filterCallsIfNecessary(this.databaseStatementCalls, regExpr);
	}

	public List<AggregatedDatabaseOperationCall> getAggregatedDatabaseStatementCalls(
			final String regExpr) {
		return this.filterCallsIfNecessary(
				this.aggregatedDatabaseStatementCalls, regExpr);
	}

	public List<DatabaseOperationCall> getDatabasePreparedStatementCalls(
			final String regExpr) {
		return this.filterCallsIfNecessary(this.databasePreparedStatementCalls,
				regExpr);
	}

	public List<PreparedStatementCall> getRefinedDatabasePreparedStatementCalls(
			final String regExpr) {
		return this.filterCallsIfNecessary(
				this.refinedDatabasePreparedStatementCalls, regExpr);
	}

	private <T extends AbstractTrace<?>> List<T> filterTracesIfNecessary(
			final List<T> traces, final String regExpr) {
		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			return traces;
		}

		return traces
				.parallelStream()
				.filter(trace -> trace.getRootOperationCall().getOperation()
						.matches(regExpr)).collect(Collectors.toList());
	}

	private <T extends AbstractOperationCall<?>> List<T> filterCallsIfNecessary(
			final List<T> calls, final String regExpr) {
		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			return calls;
		}

		return calls.parallelStream()
				.filter(call -> call.getOperation().matches(regExpr))
				.collect(Collectors.toList());
	}

	private boolean isRegex(final String str) {
		try {
			Pattern.compile(str);
			return true;
		} catch (final PatternSyntaxException e) {
			return false;
		}
	}

	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

}
