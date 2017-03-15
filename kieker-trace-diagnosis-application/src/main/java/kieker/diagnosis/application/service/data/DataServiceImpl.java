/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.service.data;

import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.application.service.data.domain.AggregatedTrace;
import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;
import kieker.diagnosis.application.service.properties.AdditionalLogChecksProperty;
import kieker.diagnosis.application.service.properties.PercentCalculationProperty;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import kieker.common.record.misc.KiekerMetadataRecord;

import teetime.framework.Execution;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class DataServiceImpl implements DataService {

	private final ObservableList<Trace> ivTraces = FXCollections.observableArrayList( );
	private final ObservableList<AggregatedTrace> ivAggregatedTraces = FXCollections.observableArrayList( );
	private final ObservableList<OperationCall> ivOperationCalls = FXCollections.observableArrayList( );
	private final ObservableList<AggregatedOperationCall> ivAggregatedOperationCalls = FXCollections.observableArrayList( );

	private final ObjectProperty<File> ivImportDirectory = new SimpleObjectProperty<>( );
	private final ObjectProperty<Long> ivAnalysisDurationInMS = new SimpleObjectProperty<>( 0L );

	private final ObjectProperty<Integer> ivIncompleteTraces = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Integer> ivFaultyTraces = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Integer> ivDanglingRecords = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Integer> ivIgnoredRecords = new SimpleObjectProperty<>( 0 );
	private final ObjectProperty<Long> ivBeginTimestamp = new SimpleObjectProperty<>( );
	private final ObjectProperty<Long> ivEndTimestamp = new SimpleObjectProperty<>( );
	private TimeUnit ivTimeUnit;

	@Autowired
	private PropertiesService ivPropertiesService;

	@Override
	public void loadMonitoringLogFromFS( final File aImportDirectory ) {
		ivImportDirectory.set( aImportDirectory );
		final long tin = System.currentTimeMillis( );

		// Load and analyze the monitoring logs from the given directory
		final boolean additionalLogChecks = ivPropertiesService.loadBooleanApplicationProperty( AdditionalLogChecksProperty.class );
		final boolean percentCalculation = ivPropertiesService.loadBooleanApplicationProperty( PercentCalculationProperty.class );
		final ImportAnalysisConfiguration analysisConfiguration = new ImportAnalysisConfiguration( aImportDirectory, additionalLogChecks, percentCalculation );
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

	@Override
	public ObjectProperty<Long> getBeginTimestamp( ) {
		return ivBeginTimestamp;
	}

	@Override
	public ObjectProperty<Long> getEndTimestamp( ) {
		return ivEndTimestamp;
	}

	@Override
	public ObjectProperty<Integer> countIncompleteTraces( ) {
		return ivIncompleteTraces;
	}

	@Override
	public ObjectProperty<Integer> countDanglingRecords( ) {
		return ivDanglingRecords;
	}

	@Override
	public ObjectProperty<Integer> countFaultyTraces( ) {
		return ivFaultyTraces;
	}

	@Override
	public ObjectProperty<Integer> countIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

	@Override
	public ObjectProperty<File> getImportDirectory( ) {
		return ivImportDirectory;
	}

	@Override
	public ObjectProperty<Long> getAnalysisDurationInMS( ) {
		return ivAnalysisDurationInMS;
	}

	@Override
	public ObservableList<Trace> getTraces( ) {
		return ivTraces;
	}

	@Override
	public ObservableList<AggregatedTrace> getAggregatedTraces( ) {
		return ivAggregatedTraces;
	}

	@Override
	public ObservableList<OperationCall> getOperationCalls( ) {
		return ivOperationCalls;
	}

	@Override
	public ObservableList<AggregatedOperationCall> getAggregatedOperationCalls( ) {
		return ivAggregatedOperationCalls;
	}

	@Override
	public TimeUnit getTimeUnit( ) {
		return ivTimeUnit;
	}

}
