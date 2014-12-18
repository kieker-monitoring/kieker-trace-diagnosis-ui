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

package kieker.gui.common.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.gui.common.domain.AggregatedExecution;
import kieker.gui.common.domain.Execution;
import kieker.gui.common.domain.Record;
import kieker.gui.common.model.importer.ImportAnalysisConfiguration;
import teetime.framework.Analysis;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class DataModel extends Observable {

	private List<Record> records = Collections.emptyList();
	private List<Execution> traces = Collections.emptyList();
	private List<Execution> failureContainingTraces = Collections.emptyList();
	private List<Execution> failedTraces = Collections.emptyList();
	private List<AggregatedExecution> aggregatedTraces = Collections.emptyList();
	private List<AggregatedExecution> failedAggregatedTraces = Collections.emptyList();
	private List<AggregatedExecution> failureAggregatedContainingTraces = Collections.emptyList();
	private String shortTimeUnit = "";

	public DataModel() {}

	public void loadMonitoringLogFromFS(final String directory) {
		// Load and analyze the monitoring logs from the given directory
		final File importDirectory = new File(directory);
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(importDirectory);
		final Analysis analysis = new Analysis(analysisConfiguration);
		analysis.init();
		analysis.start();

		// Store the results from the analysis
		this.records = analysisConfiguration.getRecordsList();
		this.traces = analysisConfiguration.getTracesList();
		this.failedTraces = analysisConfiguration.getFailedTracesList();
		this.failureContainingTraces = analysisConfiguration.getFailureContainingTracesList();
		this.aggregatedTraces = analysisConfiguration.getAggregatedTraces();
		this.failedAggregatedTraces = analysisConfiguration.getFailedAggregatedTracesList();
		this.failureAggregatedContainingTraces = analysisConfiguration.getFailureContainingAggregatedTracesList();

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords();
		if (metadataRecords.size() == 1) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get(0);
			this.shortTimeUnit = this.convertToShortTimeUnit(TimeUnit.valueOf(metadataRecord.getTimeUnit()));
		} else {
			this.shortTimeUnit = this.convertToShortTimeUnit(null);
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

	public List<Record> getRecordsCopy() {
		return new ArrayList<>(this.records);
	}

	public List<Execution> getTracesCopy() {
		return new ArrayList<>(this.traces);
	}

	public List<Execution> getFailedTracesCopy() {
		return new ArrayList<>(this.failedTraces);
	}

	public List<Execution> getFailureContainingTracesCopy() {
		return new ArrayList<>(this.failureContainingTraces);
	}

	public List<AggregatedExecution> getAggregatedTracesCopy() {
		return new ArrayList<>(this.aggregatedTraces);
	}

	public String getShortTimeUnit() {
		return this.shortTimeUnit;
	}

	public List<AggregatedExecution> getFailedAggregatedTracesCopy() {
		return new ArrayList<>(this.failedAggregatedTraces);
	}

	public List<AggregatedExecution> getFailureContainingAggregatedTracesCopy() {
		return new ArrayList<>(this.failureAggregatedContainingTraces);
	}

}
