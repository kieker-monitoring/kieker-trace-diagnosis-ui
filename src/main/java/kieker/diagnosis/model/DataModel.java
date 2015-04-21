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
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.domain.AbstractTrace;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.importer.ImportAnalysisConfiguration;
import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class DataModel extends Observable {

	private static final DataModel INSTANCE = new DataModel();

	private final ObservableList<Trace> traces = FXCollections.observableArrayList();
	private List<Trace> failureContainingTraces = Collections.emptyList();
	private List<Trace> failedTraces = Collections.emptyList();
	private List<AggregatedTrace> aggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failedAggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failureAggregatedContainingTraces = Collections.emptyList();
	private final ObservableList<OperationCall> operationCalls = FXCollections.observableArrayList();
	private List<OperationCall> failedOperationCalls = Collections.emptyList();
	private final ObservableList<AggregatedOperationCall> aggregatedOperationCalls = FXCollections.observableArrayList();
	private List<AggregatedOperationCall> aggregatedFailedOperationCalls = Collections.emptyList();
	private File importDirectory;
	private TimeUnit timeUnit;
	private long analysisDurationInMS;
	private int incompleteTraces;
	private long beginTimestamp;
	private long endTimestamp;

	private DataModel() {}

	public void loadMonitoringLogFromFS(final File importDirectory) {
		this.importDirectory = importDirectory;
		final long tin = System.currentTimeMillis();

		// Load and analyze the monitoring logs from the given directory
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(this.importDirectory);
		final Analysis<ImportAnalysisConfiguration> analysis = new Analysis<>(analysisConfiguration);
		analysis.executeBlocking();

		// Store the results from the analysis
		this.traces.setAll(analysisConfiguration.getTracesList());
		this.failedTraces = analysisConfiguration.getFailedTracesList();
		this.failureContainingTraces = analysisConfiguration.getFailureContainingTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();
		this.failedAggregatedTraces = analysisConfiguration.getFailedAggregatedTracesList();
		this.failureAggregatedContainingTraces = analysisConfiguration.getFailureContainingAggregatedTracesList();
		this.operationCalls.setAll(analysisConfiguration.getOperationCalls());
		this.failedOperationCalls = analysisConfiguration.getFailedOperationCalls();
		this.aggregatedOperationCalls.setAll(analysisConfiguration.getAggregatedOperationCalls());
		this.aggregatedFailedOperationCalls = analysisConfiguration.getAggregatedFailedOperationCalls();
		this.incompleteTraces = analysisConfiguration.countIncompleteTraces();
		this.beginTimestamp = analysisConfiguration.getBeginTimestamp();
		this.endTimestamp = analysisConfiguration.getEndTimestamp();

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords();
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

	public ObservableList<Trace> getTraces(final String regExpr) {
		return this.traces;
		// return this.filterTracesIfNecessary(this.traces, regExpr);
	}

	public List<Trace> getFailedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failedTraces, regExpr);
	}

	public List<Trace> getFailureContainingTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failureContainingTraces, regExpr);
	}

	public List<AggregatedTrace> getAggregatedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.aggregatedTraces, regExpr);
	}

	public List<AggregatedTrace> getFailedAggregatedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failedAggregatedTraces, regExpr);
	}

	public List<AggregatedTrace> getFailureContainingAggregatedTraces(final String regExpr) {
		return this.filterTracesIfNecessary(this.failureAggregatedContainingTraces, regExpr);
	}

	public ObservableList<OperationCall> getOperationCalls() {
		return this.operationCalls;
	}

	public List<OperationCall> getFailedOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.failedOperationCalls, regExpr);
	}

	public ObservableList<AggregatedOperationCall> getAggregatedOperationCalls(final String regExpr) {
		return this.aggregatedOperationCalls;
		// return this.filterCallsIfNecessary(this.aggregatedOperationCalls, regExpr);
	}

	public List<AggregatedOperationCall> getAggregatedFailedOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.aggregatedFailedOperationCalls, regExpr);
	}

	private <T extends AbstractTrace<?>> List<T> filterTracesIfNecessary(final List<T> traces, final String regExpr) {
		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			return traces;
		}

		return traces.parallelStream().filter(trace -> trace.getRootOperationCall().getOperation().matches(regExpr)).collect(Collectors.toList());
	}

	private <T extends AbstractOperationCall<?>> List<T> filterCallsIfNecessary(final List<T> calls, final String regExpr) {
		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			return calls;
		}

		return calls.parallelStream().filter(call -> call.getOperation().matches(regExpr)).collect(Collectors.toList());
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

	public static DataModel getInstance() {
		return DataModel.INSTANCE;
	}

}
