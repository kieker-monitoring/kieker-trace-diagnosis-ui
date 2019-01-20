/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.statistics.composite;

import java.text.NumberFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;

public final class StatisticsPane extends GridPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( StatisticsPane.class.getName( ) );

	private final ObjectProperty<Statistics> statistics = new SimpleObjectProperty<>( );

	public StatisticsPane( ) {
		createControl( );
	}

	private void createControl( ) {
		setPadding( new Insets( 5 ) );

		int rowIndex = 0;

		add( createDirectoryLabel( ), 0, rowIndex );
		add( createDirectoryField( ), 1, rowIndex++ );

		add( createProcessedBytesLabel( ), 0, rowIndex );
		add( createProcessedBytesField( ), 1, rowIndex++ );

		add( createProcessDurationLabel( ), 0, rowIndex );
		add( createProcessDurationField( ), 1, rowIndex++ );

		add( createProcessSpeedLabel( ), 0, rowIndex );
		add( createProcessSpeedField( ), 1, rowIndex++ );

		add( new Separator( ), 0, rowIndex++, 2, 1 );

		add( createBeginOfMonitoringLabel( ), 0, rowIndex );
		add( createBeginOfMonitoringField( ), 1, rowIndex++ );

		add( createEndOfMonitoringLabel( ), 0, rowIndex );
		add( createEndOfMonitoringField( ), 1, rowIndex++ );

		add( new Separator( ), 0, rowIndex++, 2, 1 );

		add( createTracesLabe( ), 0, rowIndex );
		add( createTracesField( ), 1, rowIndex++ );

		add( createMethodsLabel( ), 0, rowIndex );
		add( createMethodsField( ), 1, rowIndex++ );

		add( createAggregatedMethodsLabel( ), 0, rowIndex );
		add( createAggregatedMethodsField( ), 1, rowIndex++ );

		add( new Separator( ), 0, rowIndex++, 2, 1 );

		add( createIgnoredRecordsLabel( ), 0, rowIndex );
		add( createIgnoredRecordsField( ), 1, rowIndex++ );

		add( createDanglingRecordsLabel( ), 0, rowIndex );
		add( createDanglingRecordsField( ), 1, rowIndex++ );

		add( createIncompleteTracesLabel( ), 0, rowIndex );
		add( createIncompleteTracesField( ), 1, rowIndex++ );

		addDefaultStylesheet( );
	}

	private Node createDirectoryLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "directory" ) );

		return label;
	}

	private Node createDirectoryField( ) {
		final TextField directory = new TextField( );

		final ObservableStringValue observable = selectString( statistics, Statistics::getDirectory, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		directory.textProperty( ).bind( observable );
		directory.setEditable( false );

		GridPane.setHgrow( directory, Priority.ALWAYS );

		return directory;
	}

	private <T> ObservableStringValue selectString( final ObjectProperty<T> observable, final Function<T, String> getter, final String alternative ) {
		return Bindings.createStringBinding( ( ) -> {
			final T value = observable.get( );
			return value != null ? getter.apply( value ) : alternative;
		}, observable );
	}

	private <T> ObservableStringValue selectIntegerAsString( final ObjectProperty<T> observable, final Function<T, Integer> getter, final String alternative ) {
		final NumberFormat decimalFormat = NumberFormat.getInstance( );
		return Bindings.createStringBinding( ( ) -> {
			final T value = observable.get( );
			return value != null ? decimalFormat.format( getter.apply( value ) ) : alternative;
		}, observable );
	}

	private Node createProcessedBytesLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "processedBytes" ) );

		return label;
	}

	private Node createProcessedBytesField( ) {
		final TextField processedBytes = new TextField( );

		final ObservableStringValue observable = selectString( statistics, statistics -> convertToByteString( statistics.getProcessedBytes( ) ), RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		processedBytes.textProperty( ).bind( observable );
		processedBytes.setId( "statisticsProcessedBytes" );
		processedBytes.setEditable( false );

		GridPane.setHgrow( processedBytes, Priority.ALWAYS );

		return processedBytes;
	}

	private Node createProcessDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "processDuration" ) );

		return label;
	}

	private Node createProcessDurationField( ) {
		final TextField processDuration = new TextField( );

		final ObservableStringValue observable = selectString( statistics, statistics -> convertToDurationString( statistics.getProcessDuration( ) ), RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		processDuration.textProperty( ).bind( observable );
		processDuration.setId( "statisticsProcessDuration" );
		processDuration.setEditable( false );

		GridPane.setHgrow( processDuration, Priority.ALWAYS );

		return processDuration;
	}

	private Node createProcessSpeedLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "processSpeed" ) );

		return label;
	}

	private Node createProcessSpeedField( ) {
		final TextField processSpeed = new TextField( );

		final ObservableStringValue observable = selectString( statistics, statistics -> convertToSpeedString( statistics.getProcessSpeed( ) ), RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		processSpeed.textProperty( ).bind( observable );
		processSpeed.setId( "statisticsProcessSpeed" );
		processSpeed.setEditable( false );

		GridPane.setHgrow( processSpeed, Priority.ALWAYS );

		return processSpeed;
	}

	private Node createBeginOfMonitoringLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "beginnOfMonitoring" ) );

		return label;
	}

	private Node createBeginOfMonitoringField( ) {
		final TextField beginOfMonitoring = new TextField( );

		final ObservableStringValue observable = selectString( statistics, Statistics::getBeginnOfMonitoring, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		beginOfMonitoring.textProperty( ).bind( observable );
		beginOfMonitoring.setEditable( false );

		GridPane.setHgrow( beginOfMonitoring, Priority.ALWAYS );

		return beginOfMonitoring;
	}

	private Node createEndOfMonitoringLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "endOfMonitoring" ) );

		return label;
	}

	private Node createEndOfMonitoringField( ) {
		final TextField endOfMonitoring = new TextField( );

		final ObservableStringValue observable = selectString( statistics, Statistics::getEndOfMonitoring, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		endOfMonitoring.textProperty( ).bind( observable );
		endOfMonitoring.setEditable( false );

		GridPane.setHgrow( endOfMonitoring, Priority.ALWAYS );

		return endOfMonitoring;
	}

	private Node createTracesLabe( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "traces" ) );

		return label;
	}

	private Node createTracesField( ) {
		final TextField traces = new TextField( );

		final ObservableStringValue observable = selectIntegerAsString( statistics, Statistics::getTraces, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		traces.textProperty( ).bind( observable );
		traces.setEditable( false );

		GridPane.setHgrow( traces, Priority.ALWAYS );

		return traces;
	}

	private Node createMethodsLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "methods" ) );

		return label;
	}

	private Node createMethodsField( ) {
		final TextField methods = new TextField( );

		final ObservableStringValue observable = selectIntegerAsString( statistics, Statistics::getMethods, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		methods.textProperty( ).bind( observable );
		methods.setEditable( false );

		GridPane.setHgrow( methods, Priority.ALWAYS );

		return methods;
	}

	private Node createAggregatedMethodsLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "aggregatedMethods" ) );

		return label;
	}

	private Node createAggregatedMethodsField( ) {
		final TextField aggregatedMethods = new TextField( );

		final ObservableStringValue observable = selectIntegerAsString( statistics, Statistics::getAggregatedMethods, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		aggregatedMethods.textProperty( ).bind( observable );
		aggregatedMethods.setEditable( false );

		GridPane.setHgrow( aggregatedMethods, Priority.ALWAYS );

		return aggregatedMethods;
	}

	private Node createIgnoredRecordsLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "ignoredRecords" ) );

		return label;
	}

	private Node createIgnoredRecordsField( ) {
		final TextField ignoredRecords = new TextField( );

		final ObservableStringValue observable = selectIntegerAsString( statistics, Statistics::getIgnoredRecords, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		ignoredRecords.textProperty( ).bind( observable );
		ignoredRecords.setEditable( false );

		GridPane.setHgrow( ignoredRecords, Priority.ALWAYS );

		return ignoredRecords;
	}

	private Node createDanglingRecordsLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "danglingRecords" ) );

		return label;
	}

	private Node createDanglingRecordsField( ) {
		final TextField danglingRecords = new TextField( );

		final ObservableStringValue observable = selectIntegerAsString( statistics, Statistics::getDanglingRecords, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		danglingRecords.textProperty( ).bind( observable );
		danglingRecords.setEditable( false );

		GridPane.setHgrow( danglingRecords, Priority.ALWAYS );

		return danglingRecords;
	}

	private Node createIncompleteTracesLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "incompleteTraces" ) );

		return label;
	}

	private Node createIncompleteTracesField( ) {
		final TextField incompleteTraces = new TextField( );

		final ObservableStringValue observable = selectIntegerAsString( statistics, Statistics::getIncompleteTraces, RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		incompleteTraces.textProperty( ).bind( observable );
		incompleteTraces.setEditable( false );

		GridPane.setHgrow( incompleteTraces, Priority.ALWAYS );

		return incompleteTraces;
	}

	public void setValue( final Optional<Statistics> aStatistics ) {
		statistics.setValue( aStatistics.orElse( null ) );
	}

	private String convertToByteString( final long aBytes ) {
		long bytes = aBytes;

		if ( bytes <= 1024 ) {
			return String.format( "%d [B]", bytes );
		} else {
			bytes /= 1024;
			if ( bytes <= 1024 ) {
				return String.format( "%d [KB]", bytes );
			} else {
				bytes /= 1024;
				return String.format( "%d [MB]", bytes );
			}
		}
	}

	private String convertToDurationString( final long aDuration ) {
		long duration = aDuration;

		if ( duration <= 1000 ) {
			return String.format( "%d [ms]", duration );
		} else {
			duration /= 1000;
			if ( duration <= 60 ) {
				return String.format( "%d [s]", duration );
			} else {
				duration /= 60;
				return String.format( "%d [m]", duration );
			}
		}
	}

	private String convertToSpeedString( final long aSpeed ) {
		long speed = aSpeed * 1000;

		if ( speed <= 1024 ) {
			return String.format( "%d [B/s]", speed );
		} else {
			speed /= 1024;
			if ( speed <= 1024 ) {
				return String.format( "%d [KB/s]", speed );
			} else {
				speed /= 1024;
				return String.format( "%d [MB/s]", speed );
			}
		}
	}

}
