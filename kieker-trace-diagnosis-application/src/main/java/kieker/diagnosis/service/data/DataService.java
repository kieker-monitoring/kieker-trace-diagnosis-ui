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

package kieker.diagnosis.service.data;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.service.ServiceIfc;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.data.domain.AggregatedTrace;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import teetime.framework.Execution;

/**
 * A container for data used within this application.
 *
 * @author Nils Christian Ehmke
 */
public final class DataService implements ServiceIfc {

	private final ObservableList<Trace> ivTraces = FXCollections.observableArrayList( );
	private final ObservableList<AggregatedTrace> ivAggregatedTraces = FXCollections.observableArrayList( );
	private final ObservableList<OperationCall> ivOperationCalls = FXCollections.observableArrayList( );
	private final ObservableList<AggregatedOperationCall> ivAggregatedOperationCalls = FXCollections.observableArrayList( );

	private final ObjectProperty<File> ivImportDirectory = new SimpleObjectProperty<>( );
	private final ObjectProperty<Long> ivAnalysisDurationInMS = new SimpleObjectProperty<>( 0L );

	private TimeUnit ivTimeUnit;
	private final ObjectProperty<Integer> ivIncompleteTraces = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Integer> ivFaultyTraces = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Integer> ivDanglingRecords = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Integer> ivIgnoredRecords = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Long> ivBeginTimestamp = new SimpleObjectProperty<>( );
	private final ObjectProperty<Long> ivEndTimestamp = new SimpleObjectProperty<>( );

	public void loadMonitoringLogFromFS( final File aImportDirectory ) {
		ivImportDirectory.set( aImportDirectory );
		final long tin = System.currentTimeMillis( );

		// Load and analyze the monitoring logs from the given directory
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration( aImportDirectory );
		final Execution<ImportAnalysisConfiguration> analysis = new Execution<>( analysisConfiguration );
		analysis.executeBlocking( );

		// Store the results from the analysis
		ivTraces.setAll( analysisConfiguration.getTracesList( ) );
		ivAggregatedTraces.setAll( analysisConfiguration.getAggregatedTraces( ) );
		ivOperationCalls.setAll( analysisConfiguration.getOperationCalls( ) );
		ivAggregatedOperationCalls.setAll( analysisConfiguration.getAggregatedOperationCalls( ) );

		ivIncompleteTraces.set( analysisConfiguration.countIncompleteTraces( ) );
		ivDanglingRecords.set( analysisConfiguration.countDanglingEvents( ) );
		ivIgnoredRecords.set( analysisConfiguration.countIgnoredRecords( ) );
		ivBeginTimestamp.set( analysisConfiguration.getBeginTimestamp( ) );
		ivEndTimestamp.set( analysisConfiguration.getEndTimestamp( ) );

		final List<KiekerMetadataRecord> metadataRecords = analysisConfiguration.getMetadataRecords( );
		if ( !metadataRecords.isEmpty( ) ) {
			final KiekerMetadataRecord metadataRecord = metadataRecords.get( 0 );
			ivTimeUnit = TimeUnit.valueOf( metadataRecord.getTimeUnit( ) );
		} else {
			ivTimeUnit = TimeUnit.NANOSECONDS;
		}

		final long tout = System.currentTimeMillis( );

		ivAnalysisDurationInMS.set( tout - tin );
	}

	public ObjectProperty<Long> getBeginTimestamp( ) {
		return ivBeginTimestamp;
	}

	public ObjectProperty<Long> getEndTimestamp( ) {
		return ivEndTimestamp;
	}

	public ObjectProperty<Integer> countIncompleteTraces( ) {
		return ivIncompleteTraces;
	}

	public ObjectProperty<Integer> countDanglingRecords( ) {
		return ivDanglingRecords;
	}

	public ObjectProperty<Integer> countFaultyTraces( ) {
		return ivFaultyTraces;
	}

	public ObjectProperty<Integer> countIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

	public ObjectProperty<File> getImportDirectory( ) {
		return ivImportDirectory;
	}

	public ObjectProperty<Long> getAnalysisDurationInMS( ) {
		return ivAnalysisDurationInMS;
	}

	public ObservableList<Trace> getTraces( ) {
		return ivTraces;
	}

	public ObservableList<AggregatedTrace> getAggregatedTraces( ) {
		return ivAggregatedTraces;
	}

	public ObservableList<OperationCall> getOperationCalls( ) {
		return ivOperationCalls;
	}

	public ObservableList<AggregatedOperationCall> getAggregatedOperationCalls( ) {
		return ivAggregatedOperationCalls;
	}

	public TimeUnit getTimeUnit( ) {
		return ivTimeUnit;
	}

}
