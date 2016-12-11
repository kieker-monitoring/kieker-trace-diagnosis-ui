/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.util.ImportAnalysisConfiguration;
import teetime.framework.Execution;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class DataModel {

	private static final DataModel INSTANCE = new DataModel();

	private final ObservableList<Trace> ivTraces = FXCollections.observableArrayList();
	private final ObservableList<AggregatedTrace> ivAggregatedTraces = FXCollections.observableArrayList();
	private final ObservableList<OperationCall> ivOperationCalls = FXCollections.observableArrayList();
	private final ObservableList<AggregatedOperationCall> ivAggregatedOperationCalls = FXCollections.observableArrayList();

	private final ObjectProperty<File> ivImportDirectory = new SimpleObjectProperty<>();
	private final ObjectProperty<Long> ivAnalysisDurationInMS = new SimpleObjectProperty<>(0L);

	private TimeUnit ivTimeUnit;
	private final ObjectProperty<Integer> ivIncompleteTraces = new SimpleObjectProperty<>(0);
	private final ObjectProperty<Integer> ivFaultyTraces = new SimpleObjectProperty<>(0);
	private final ObjectProperty<Integer> ivDanglingRecords = new SimpleObjectProperty<>(0);
	private final ObjectProperty<Integer> ivIgnoredRecords = new SimpleObjectProperty<>(0);
	private final ObjectProperty<Long> ivBeginTimestamp = new SimpleObjectProperty<>();
	private final ObjectProperty<Long> ivEndTimestamp = new SimpleObjectProperty<>();

	private DataModel() {
	}

	public void loadMonitoringLogFromFS(final File aImportDirectory) {
		this.ivImportDirectory.set(aImportDirectory);
		final long tin = System.currentTimeMillis();

		// Load and analyze the monitoring logs from the given directory
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration(aImportDirectory);
		final Execution<ImportAnalysisConfiguration> analysis = new Execution<>(analysisConfiguration);
		analysis.executeBlocking();

		// Store the results from the analysis
		this.ivTraces.setAll(analysisConfiguration.getTracesList());
		this.ivAggregatedTraces.setAll(analysisConfiguration.getAggregatedTraces());
		this.ivOperationCalls.setAll(analysisConfiguration.getOperationCalls());
		this.ivAggregatedOperationCalls.setAll(analysisConfiguration.getAggregatedOperationCalls());

		this.ivIncompleteTraces.set(analysisConfiguration.countIncompleteTraces());
		this.ivDanglingRecords.set(analysisConfiguration.countDanglingEvents());
		this.ivIgnoredRecords.set(analysisConfiguration.countIgnoredRecords());
		this.ivBeginTimestamp.set(analysisConfiguration.getBeginTimestamp());
		this.ivEndTimestamp.set(analysisConfiguration.getEndTimestamp());

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords();
		if (!metadataRecords.isEmpty()) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get(0);
			this.ivTimeUnit = TimeUnit.valueOf(metadataRecord.getTimeUnit());
		} else {
			this.ivTimeUnit = TimeUnit.NANOSECONDS;
		}

		final long tout = System.currentTimeMillis();

		this.ivAnalysisDurationInMS.set(tout - tin);
	}

	public ObjectProperty<Long> getBeginTimestamp() {
		return this.ivBeginTimestamp;
	}

	public ObjectProperty<Long> getEndTimestamp() {
		return this.ivEndTimestamp;
	}

	public ObjectProperty<Integer> countIncompleteTraces() {
		return this.ivIncompleteTraces;
	}

	public ObjectProperty<Integer> countDanglingRecords() {
		return this.ivDanglingRecords;
	}

	public ObjectProperty<Integer> countFaultyTraces() {
		return this.ivFaultyTraces;
	}

	public ObjectProperty<Integer> countIgnoredRecords() {
		return this.ivIgnoredRecords;
	}
	
	public ObjectProperty<File> getImportDirectory() {
		return this.ivImportDirectory;
	}

	public ObjectProperty<Long> getAnalysisDurationInMS() {
		return this.ivAnalysisDurationInMS;
	}

	public ObservableList<Trace> getTraces() {
		return this.ivTraces;
	}

	public ObservableList<AggregatedTrace> getAggregatedTraces() {
		return this.ivAggregatedTraces;
	}

	public ObservableList<OperationCall> getOperationCalls() {
		return this.ivOperationCalls;
	}

	public ObservableList<AggregatedOperationCall> getAggregatedOperationCalls() {
		return this.ivAggregatedOperationCalls;
	}

	public TimeUnit getTimeUnit() {
		return this.ivTimeUnit;
	}

	public static DataModel getInstance() {
		return DataModel.INSTANCE;
	}

}
