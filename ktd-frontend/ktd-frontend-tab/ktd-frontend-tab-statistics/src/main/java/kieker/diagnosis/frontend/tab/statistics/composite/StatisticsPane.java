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

	private TextField processedBytes;
	private TextField processDuration;
	private TextField processSpeed;
	private TextField ignoredRecords;
	private TextField danglingRecords;
	private TextField incompleteTraces;
	private TextField methods;
	private TextField aggregatedMethods;
	private TextField traces;
	private TextField beginOfMonitoring;
	private TextField endOfMonitoring;
	private TextField directory;

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
		directory = new TextField( );

		directory.setEditable( false );

		GridPane.setHgrow( directory, Priority.ALWAYS );

		return directory;
	}

	private Node createProcessedBytesLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "processedBytes" ) );

		return label;
	}

	private Node createProcessedBytesField( ) {
		processedBytes = new TextField( );

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
		processDuration = new TextField( );

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
		processSpeed = new TextField( );

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
		beginOfMonitoring = new TextField( );

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
		endOfMonitoring = new TextField( );

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
		traces = new TextField( );

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
		methods = new TextField( );

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
		aggregatedMethods = new TextField( );

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
		ignoredRecords = new TextField( );

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
		danglingRecords = new TextField( );

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
		incompleteTraces = new TextField( );

		incompleteTraces.setEditable( false );

		GridPane.setHgrow( incompleteTraces, Priority.ALWAYS );

		return incompleteTraces;
	}

	public void setValue( final Optional<Statistics> aStatistics ) {
		aStatistics.ifPresentOrElse( statistics -> {
			final NumberFormat decimalFormat = NumberFormat.getInstance( );

			processedBytes.setText( convertToByteString( statistics.getProcessedBytes( ) ) );
			processDuration.setText( convertToDurationString( statistics.getProcessDuration( ) ) );
			processSpeed.setText( convertToSpeedString( statistics.getProcessSpeed( ) ) );
			methods.setText( decimalFormat.format( statistics.getMethods( ) ) );
			aggregatedMethods.setText( decimalFormat.format( statistics.getAggregatedMethods( ) ) );
			traces.setText( decimalFormat.format( statistics.getTraces( ) ) );
			ignoredRecords.setText( decimalFormat.format( statistics.getIgnoredRecords( ) ) );
			danglingRecords.setText( decimalFormat.format( statistics.getDanglingRecords( ) ) );
			incompleteTraces.setText( decimalFormat.format( statistics.getIncompleteTraces( ) ) );
			beginOfMonitoring.setText( statistics.getBeginnOfMonitoring( ) );
			endOfMonitoring.setText( statistics.getEndOfMonitoring( ) );
			directory.setText( statistics.getDirectory( ) );
		}, ( ) -> {
			processedBytes.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			processDuration.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			processSpeed.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			methods.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			aggregatedMethods.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			traces.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ignoredRecords.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			danglingRecords.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			incompleteTraces.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			beginOfMonitoring.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			endOfMonitoring.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			directory.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
		} );
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
