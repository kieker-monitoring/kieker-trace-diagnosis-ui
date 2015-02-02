/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.common.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.common.domain.AggregatedTrace;
import kieker.diagnosis.common.domain.Trace;
import kieker.diagnosis.common.model.importer.ImportAnalysisConfiguration;
import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 * 
 * @author Nils Christian Ehmke
 */
public final class DataModel extends Observable {

	private List<Trace> traces = Collections.emptyList();
	private List<Trace> failureContainingTraces = Collections.emptyList();
	private List<Trace> failedTraces = Collections.emptyList();
	private List<AggregatedTrace> aggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failedAggregatedTraces = Collections.emptyList();
	private List<AggregatedTrace> failureAggregatedContainingTraces = Collections.emptyList();
	private String shortTimeUnit = "";

	public void loadMonitoringLogFromFS(final String directory) {
		// Load and analyze the monitoring logs from the given directory
		final File importDirectory = new File(directory);
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(importDirectory);
		final Analysis analysis = new Analysis(analysisConfiguration);
		analysis.init();
		analysis.start();

		// Store the results from the analysis
		this.traces = analysisConfiguration.getTracesList();
		this.failedTraces = analysisConfiguration.getFailedTracesList();
		this.failureContainingTraces = analysisConfiguration.getFailureContainingTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();
		this.failedAggregatedTraces = analysisConfiguration.getFailedAggregatedTracesList();
		this.failureAggregatedContainingTraces = analysisConfiguration.getFailureContainingAggregatedTracesList();

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords();
		if (!metadataRecords.isEmpty()) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get(0);
			this.shortTimeUnit = this.convertToShortTimeUnit(TimeUnit.valueOf(metadataRecord.getTimeUnit()));
		} else {
			this.shortTimeUnit = "";
		}

		this.setChanged();
		this.notifyObservers();
	}

	private String convertToShortTimeUnit(final TimeUnit timeUnit) {
		final String result;

		switch (timeUnit) {
		case DAYS:
			result = "d";
			break;
		case HOURS:
			result = "h";
			break;
		case MICROSECONDS:
			result = "us";
			break;
		case MILLISECONDS:
			result = "ms";
			break;
		case MINUTES:
			result = "m";
			break;
		case NANOSECONDS:
			result = "ns";
			break;
		case SECONDS:
			result = "s";
			break;
		default:
			result = "";
			break;
		}

		return result;
	}

	public String getShortTimeUnit() {
		return this.shortTimeUnit;
	}

	public List<Trace> getTracesCopy() {
		return new ArrayList<>(this.traces);
	}

	public List<Trace> getFailedTracesCopy() {
		return new ArrayList<>(this.failedTraces);
	}

	public List<Trace> getFailureContainingTracesCopy() {
		return new ArrayList<>(this.failureContainingTraces);
	}

	public List<AggregatedTrace> getAggregatedTracesCopy() {
		return new ArrayList<>(this.aggregatedTraces);
	}

	public List<AggregatedTrace> getFailedAggregatedTracesCopy() {
		return new ArrayList<>(this.failedAggregatedTraces);
	}

	public List<AggregatedTrace> getFailureContainingAggregatedTracesCopy() {
		return new ArrayList<>(this.failureAggregatedContainingTraces);
	}

}
