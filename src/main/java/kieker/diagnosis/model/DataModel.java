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
import java.util.List;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kieker.common.record.misc.KiekerMetadataRecord;
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
	private final ObservableList<AggregatedTrace> aggregatedTraces = FXCollections.observableArrayList();
	private final ObservableList<OperationCall> operationCalls = FXCollections.observableArrayList();
	private final ObservableList<AggregatedOperationCall> aggregatedOperationCalls = FXCollections.observableArrayList();

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
		this.aggregatedTraces.setAll(analysisConfiguration.getAggregatedTraces());
		this.operationCalls.setAll(analysisConfiguration.getOperationCalls());
		this.aggregatedOperationCalls.setAll(analysisConfiguration.getAggregatedOperationCalls());

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

	public ObservableList<Trace> getTraces() {
		return this.traces;
	}

	public ObservableList<AggregatedTrace> getAggregatedTraces() {
		return this.aggregatedTraces;
	}

	public ObservableList<OperationCall> getOperationCalls() {
		return this.operationCalls;
	}

	public ObservableList<AggregatedOperationCall> getAggregatedOperationCalls() {
		return this.aggregatedOperationCalls;
	}

	public TimeUnit getTimeUnit() {
		return this.timeUnit;
	}

	public static DataModel getInstance() {
		return DataModel.INSTANCE;
	}

}
