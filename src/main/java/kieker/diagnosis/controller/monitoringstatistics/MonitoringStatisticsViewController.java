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

package kieker.diagnosis.controller.monitoringstatistics;

import java.io.File;
import java.util.Arrays;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import kieker.diagnosis.controller.AbstractController;
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.AggregatedTrace;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ErrorHandling;
import kieker.tools.util.LoggingTimestampConverter;

/**
 * @author Nils Christian Ehmke
 */
public final class MonitoringStatisticsViewController extends AbstractController {

	private static final String[] UNITS = { "Bytes", "Kilobytes", "Megabytes", "Gigabytes", };
	private static final float SIZE_OF_BYTE = 1024.0f;

	private final DataModel ivDataModel = DataModel.getInstance( );

	@FXML
	private TextField ivMonitoringlog;
	@FXML
	private TextField ivMonitoringsize;
	@FXML
	private TextField ivAnalysistime;
	@FXML
	private TextField ivBeginofmonitoring;
	@FXML
	private TextField ivEndofmonitoring;
	@FXML
	private TextField ivNumberofcalls;
	@FXML
	private TextField ivNumberoffailedcalls;
	@FXML
	private TextField ivNumberofaggcalls;
	@FXML
	private TextField ivNumberoffailedaggcalls;
	@FXML
	private TextField ivNumberoftraces;
	@FXML
	private TextField ivNumberoffailedtraces;
	@FXML
	private TextField ivNumberoffailuretraces;
	@FXML
	private TextField ivNumberofaggtraces;
	@FXML
	private TextField ivNumberofaggfailedtraces;
	@FXML
	private TextField ivNumberofaggfailuretraces;
	@FXML
	private TextField ivIncompletetraces;
	@FXML
	private TextField ivDanglingrecords;
	@FXML
	private TextField ivIgnoredRecords;

	public MonitoringStatisticsViewController( final Context aContext ) {
		super( aContext );
	}

	@ErrorHandling
	public void initialize( ) {
		final ObjectProperty<File> importDirectory = ivDataModel.getImportDirectory( );
		ivMonitoringlog.textProperty( ).bind( Bindings.createStringBinding( ( ) -> assemblePathString( importDirectory.get( ) ), importDirectory ) );
		ivMonitoringsize.textProperty( ).bind( Bindings.createStringBinding( ( ) -> assembleSizeString( importDirectory.get( ) ), importDirectory ) );

		final ObjectProperty<Long> duration = ivDataModel.getAnalysisDurationInMS( );
		ivAnalysistime.textProperty( ).bind( Bindings.createStringBinding( ( ) -> assembleDurationString( duration.get( ) ), duration ) );

		final ObjectProperty<Long> beginTimestamp = ivDataModel.getBeginTimestamp( );
		final ObjectProperty<Long> endTimestamp = ivDataModel.getEndTimestamp( );
		ivBeginofmonitoring.textProperty( ).bind( Bindings.createStringBinding( ( ) -> assembleTimeString( beginTimestamp.get( ) ), beginTimestamp ) );
		ivEndofmonitoring.textProperty( ).bind( Bindings.createStringBinding( ( ) -> assembleTimeString( endTimestamp.get( ) ), endTimestamp ) );

		final ObservableList<OperationCall> operationCalls = ivDataModel.getOperationCalls( );
		final FilteredList<OperationCall> failedOperationCalls = new FilteredList<>( operationCalls, OperationCall::isFailed );
		ivNumberofcalls.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( operationCalls.size( ) ), operationCalls ) );
		ivNumberoffailedcalls.textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedOperationCalls.size( ) ), failedOperationCalls ) );

		final ObservableList<AggregatedOperationCall> aggOperationCalls = ivDataModel.getAggregatedOperationCalls( );
		final FilteredList<AggregatedOperationCall> failedAggOperationCalls = new FilteredList<>( aggOperationCalls, AggregatedOperationCall::isFailed );
		ivNumberofaggcalls.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( aggOperationCalls.size( ) ), aggOperationCalls ) );
		ivNumberoffailedaggcalls.textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedAggOperationCalls.size( ) ), failedAggOperationCalls ) );

		final ObservableList<Trace> traces = ivDataModel.getTraces( );
		final FilteredList<Trace> failedTraces = new FilteredList<>( traces, t -> t.getRootOperationCall( ).isFailed( ) );
		final FilteredList<Trace> failureTraces = new FilteredList<>( traces, t -> t.getRootOperationCall( ).containsFailure( ) );
		ivNumberoftraces.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( traces.size( ) ), traces ) );
		ivNumberoffailedtraces.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedTraces.size( ) ), failedTraces ) );
		ivNumberoffailuretraces.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( failureTraces.size( ) ), failureTraces ) );

		final ObservableList<AggregatedTrace> aggTraces = ivDataModel.getAggregatedTraces( );
		final FilteredList<AggregatedTrace> failedAggTraces = new FilteredList<>( aggTraces, t -> t.getRootOperationCall( ).isFailed( ) );
		final FilteredList<AggregatedTrace> failureAggTraces = new FilteredList<>( aggTraces, t -> t.getRootOperationCall( ).containsFailure( ) );
		ivNumberofaggtraces.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( aggTraces.size( ) ), aggTraces ) );
		ivNumberofaggfailedtraces.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( failedAggTraces.size( ) ), failedAggTraces ) );
		ivNumberofaggfailuretraces.textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( failureAggTraces.size( ) ), failureAggTraces ) );

		final ObjectProperty<Integer> countIncompleteTraces = ivDataModel.countIncompleteTraces( );
		ivIncompletetraces.textProperty( )
				.bind( Bindings.createStringBinding( ( ) -> Integer.toString( countIncompleteTraces.get( ) ), countIncompleteTraces ) );

		final ObjectProperty<Integer> countDanglingRecords = ivDataModel.countDanglingRecords( );
		ivDanglingrecords.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( countDanglingRecords.get( ) ), countDanglingRecords ) );

		final ObjectProperty<Integer> countIgnoredRecords = ivDataModel.countIgnoredRecords( );
		ivIgnoredRecords.textProperty( ).bind( Bindings.createStringBinding( ( ) -> Integer.toString( countIgnoredRecords.get( ) ), countIgnoredRecords ) );
	}

	private String assembleTimeString( final Long aTimestamp ) {
		return (aTimestamp == null) ? "N/A" : LoggingTimestampConverter.convertLoggingTimestampLocalTimeZoneString( aTimestamp );
	}

	private String assembleDurationString( final Long aDuration ) {
		return (aDuration == null) ? "N/A" : aDuration + " ms";
	}

	private String assemblePathString( final File aFile ) {
		return (aFile == null) ? "N/A" : aFile.getAbsolutePath( );
	}

	private String assembleSizeString( final File aFile ) {
		String importDirectorySizeString = "N/A";
		if ( aFile == null ) {
			return importDirectorySizeString;
		}
		final float size = MonitoringStatisticsViewController.calculateDirectorySize( aFile );

		float newSize = size;
		for ( final String unit : MonitoringStatisticsViewController.UNITS ) {
			if ( newSize >= MonitoringStatisticsViewController.SIZE_OF_BYTE ) {
				newSize /= MonitoringStatisticsViewController.SIZE_OF_BYTE;
			}
			else {
				importDirectorySizeString = String.format( "%.1f %s", newSize, unit );
				break;
			}
		}

		return importDirectorySizeString;
	}

	private static long calculateDirectorySize( final File aFile ) {
		return (aFile.isFile( )) ? aFile.length( )
				: Arrays.stream( aFile.listFiles( ) ).mapToLong( MonitoringStatisticsViewController::calculateDirectorySize ).sum( );
	}

}
