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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.domain.AbstractOperationCall;
import kieker.diagnosis.domain.AbstractTrace;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.importer.ImportAnalysisConfiguration;

import org.springframework.stereotype.Repository;

import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 * 
 * @author Nils Christian Ehmke
 */
@Repository
public final class DataModel extends Observable {

	private List<Trace> traces = Collections.emptyList();
	private List<Trace> failureContainingTraces = Collections.emptyList();
	private List<Trace> failedTraces = Collections.emptyList();
	private List<AggregatedTrace> aggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failedAggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failureAggregatedContainingTraces = Collections.emptyList();
	private List<OperationCall> operationCalls = Collections.emptyList();
	private List<OperationCall> failedOperationCalls = Collections.emptyList();
	private List<AggregatedOperationCall> aggregatedOperationCalls = Collections.emptyList();
	private List<AggregatedOperationCall> aggregatedFailedOperationCalls = Collections.emptyList();
	private TimeUnit timeUnit;

	public void loadMonitoringLogFromFS(final String directory) {
		// Load and analyze the monitoring logs from the given directory
		final File importDirectory = new File(directory);
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(importDirectory);
		final Analysis analysis = new Analysis(analysisConfiguration);
		analysis.start();

		// Store the results from the analysis
		this.traces = analysisConfiguration.getTracesList();
		this.failedTraces = analysisConfiguration.getFailedTracesList();
		this.failureContainingTraces = analysisConfiguration.getFailureContainingTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();
		this.failedAggregatedTraces = analysisConfiguration.getFailedAggregatedTracesList();
		this.failureAggregatedContainingTraces = analysisConfiguration.getFailureContainingAggregatedTracesList();
		this.operationCalls = analysisConfiguration.getOperationCalls();
		this.failedOperationCalls = analysisConfiguration.getFailedOperationCalls();
		this.aggregatedOperationCalls = analysisConfiguration.getAggregatedOperationCalls();
		this.aggregatedFailedOperationCalls = analysisConfiguration.getAggregatedFailedOperationCalls();

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords();
		if (!metadataRecords.isEmpty()) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get(0);
			this.timeUnit = TimeUnit.valueOf(metadataRecord.getTimeUnit());
		} else {
			this.timeUnit = TimeUnit.NANOSECONDS;
		}

		this.setChanged();
		this.notifyObservers();
	}

	public List<Trace> getTracesCopy(final String regExpr) {
		return this.filterTracesIfNecessary(this.traces, regExpr);
	}

	public List<Trace> getFailedTracesCopy(final String regExpr) {
		return this.filterTracesIfNecessary(this.failedTraces, regExpr);
	}

	public List<Trace> getFailureContainingTracesCopy(final String regExpr) {
		return this.filterTracesIfNecessary(this.failureContainingTraces, regExpr);
	}

	public List<AggregatedTrace> getAggregatedTracesCopy(final String regExpr) {
		return this.filterTracesIfNecessary(this.aggregatedTraces, regExpr);
	}

	public List<AggregatedTrace> getFailedAggregatedTracesCopy(final String regExpr) {
		return this.filterTracesIfNecessary(this.failedAggregatedTraces, regExpr);
	}

	public List<AggregatedTrace> getFailureContainingAggregatedTracesCopy(final String regExpr) {
		return this.filterTracesIfNecessary(this.failureAggregatedContainingTraces, regExpr);
	}

	public List<OperationCall> getOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.operationCalls, regExpr);
	}

	public List<OperationCall> getFailedOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.failedOperationCalls, regExpr);
	}

	public List<AggregatedOperationCall> getAggregatedOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.aggregatedOperationCalls, regExpr);
	}

	public List<AggregatedOperationCall> getAggregatedFailedOperationCalls(final String regExpr) {
		return this.filterCallsIfNecessary(this.aggregatedFailedOperationCalls, regExpr);
	}

	private <T extends AbstractTrace<?>> List<T> filterTracesIfNecessary(final List<T> traces, final String regExpr) {
		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			return traces;
		}

		final List<T> result = new ArrayList<>();
		for (final T trace : traces) {
			if (trace.getRootOperationCall().getOperation().matches(regExpr)) {
				result.add(trace);
			}
		}
		return result;
	}

	private <T extends AbstractOperationCall<?>> List<T> filterCallsIfNecessary(final List<T> calls, final String regExpr) {
		if ((regExpr == null) || regExpr.isEmpty() || !this.isRegex(regExpr)) {
			return calls;
		}

		final List<T> result = new ArrayList<>();
		for (final T call : calls) {
			if (call.getOperation().matches(regExpr)) {
				result.add(call);
			}
		}
		return result;
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
