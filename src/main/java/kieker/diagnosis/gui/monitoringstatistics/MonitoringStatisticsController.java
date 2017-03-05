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

package kieker.diagnosis.gui.monitoringstatistics;

import java.io.File;
import java.util.Arrays;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.Context;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.data.DataService;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.data.domain.AggregatedTrace;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import kieker.tools.util.LoggingTimestampConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class MonitoringStatisticsController extends AbstractController<MonitoringStatisticsView> implements MonitoringStatisticsControllerIfc {

	private static final String[] UNITS = { "Bytes", "Kilobytes", "Megabytes", "Gigabytes", };
	private static final float SIZE_OF_BYTE = 1024.0f;

	@InjectService
	private DataService ivDataService;

	public MonitoringStatisticsController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		final ObjectProperty<File> importDirectory = ivDataService.getImportDirectory( );
		getView( ).getMonitoringlog( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> assemblePathString( importDirectory.get( ) ), importDirectory ) );
		getView( ).getMonitoringsize( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> assembleSizeString( importDirectory.get( ) ), importDirectory ) );

		final ObjectProperty<Long> duration = ivDataService.getAnalysisDurationInMS( );
		getView( ).getAnalysistime( ).textProperty( ).bind( Bindings.createStringBinding( ( ) -> assembleDurationString( duration.get( ) ), duration ) );

		final ObjectProperty<Long> beginTimestamp = ivDataService.getBeginTimestamp( );
		final ObjectProperty<Long> endTimestamp = ivDataService.getEndTimestamp( );
		getView( ).getBeginofmonitoring( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> assembleTimeString( beginTimestamp.get( ) ), beginTimestamp ) );
		getView( ).getEndofmonitoring( ).textProperty( ).bind( Bindings.createStringBinding( ( ) -> assembleTimeString( endTimestamp.get( ) ), endTimestamp ) );

		final ObservableList<OperationCall> operationCalls = ivDataService.getOperationCalls( );
		final FilteredList<OperationCall> failedOperationCalls = new FilteredList<>( operationCalls, OperationCall::isFailed );
		getView( ).getNumberofcalls( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( operationCalls.size( ) ), operationCalls ) );
		getView( ).getNumberoffailedcalls( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedOperationCalls.size( ) ), failedOperationCalls ) );

		final ObservableList<AggregatedOperationCall> aggOperationCalls = ivDataService.getAggregatedOperationCalls( );
		final FilteredList<AggregatedOperationCall> failedAggOperationCalls = new FilteredList<>( aggOperationCalls, AggregatedOperationCall::isFailed );
		getView( ).getNumberofaggcalls( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( aggOperationCalls.size( ) ), aggOperationCalls ) );
		getView( ).getNumberoffailedaggcalls( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedAggOperationCalls.size( ) ), failedAggOperationCalls ) );

		final ObservableList<Trace> traces = ivDataService.getTraces( );
		final FilteredList<Trace> failedTraces = new FilteredList<>( traces, t -> t.getRootOperationCall( ).isFailed( ) );
		final FilteredList<Trace> failureTraces = new FilteredList<>( traces, t -> t.getRootOperationCall( ).containsFailure( ) );
		getView( ).getNumberoftraces( ).textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( traces.size( ) ), traces ) );
		getView( ).getNumberoffailedtraces( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedTraces.size( ) ), failedTraces ) );
		getView( ).getNumberoffailuretraces( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failureTraces.size( ) ), failureTraces ) );

		final ObservableList<AggregatedTrace> aggTraces = ivDataService.getAggregatedTraces( );
		final FilteredList<AggregatedTrace> failedAggTraces = new FilteredList<>( aggTraces, t -> t.getRootOperationCall( ).isFailed( ) );
		final FilteredList<AggregatedTrace> failureAggTraces = new FilteredList<>( aggTraces, t -> t.getRootOperationCall( ).containsFailure( ) );
		getView( ).getNumberofaggtraces( ).textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( aggTraces.size( ) ), aggTraces ) );
		getView( ).getNumberofaggfailedtraces( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedAggTraces.size( ) ), failedAggTraces ) );
		getView( ).getNumberofaggfailuretraces( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failureAggTraces.size( ) ), failureAggTraces ) );

		final ObjectProperty<Integer> countIncompleteTraces = ivDataService.countIncompleteTraces( );
		getView( ).getIncompletetraces( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( countIncompleteTraces.get( ) ), countIncompleteTraces ) );

		final ObjectProperty<Integer> countDanglingRecords = ivDataService.countDanglingRecords( );
		getView( ).getDanglingrecords( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( countDanglingRecords.get( ) ), countDanglingRecords ) );

		final ObjectProperty<Integer> countIgnoredRecords = ivDataService.countIgnoredRecords( );
		getView( ).getIgnoredRecords( ).textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( countIgnoredRecords.get( ) ), countIgnoredRecords ) );
	}

	private String assembleTimeString( final Long aTimestamp ) {
		return ( aTimestamp == null ) ? getNotAvailableString( ) : LoggingTimestampConverter.convertLoggingTimestampLocalTimeZoneString( aTimestamp );
	}

	private String assembleDurationString( final Long aDuration ) {
		return ( aDuration == null ) ? getNotAvailableString( ) : aDuration + " ms";
	}

	private String assemblePathString( final File aFile ) {
		return ( aFile == null ) ? getNotAvailableString( ) : aFile.getAbsolutePath( );
	}

	private String assembleSizeString( final File aFile ) {
		String importDirectorySizeString = getNotAvailableString( );

		if ( aFile == null ) {
			return importDirectorySizeString;
		}
		final float size = MonitoringStatisticsController.calculateDirectorySize( aFile );

		float newSize = size;
		for ( final String unit : MonitoringStatisticsController.UNITS ) {
			if ( newSize >= MonitoringStatisticsController.SIZE_OF_BYTE ) {
				newSize /= MonitoringStatisticsController.SIZE_OF_BYTE;
			} else {
				importDirectorySizeString = String.format( "%.1f %s", newSize, unit );
				break;
			}
		}

		return importDirectorySizeString;
	}

	private String getNotAvailableString( ) {
		return getView( ).getResourceBundle( ).getString( "notAvailable" );
	}

	private static long calculateDirectorySize( final File aFile ) {
		return ( aFile.isFile( ) ) ? aFile.length( )
				: Arrays.stream( aFile.listFiles( ) ).mapToLong( MonitoringStatisticsController::calculateDirectorySize ).sum( );
	}

}
