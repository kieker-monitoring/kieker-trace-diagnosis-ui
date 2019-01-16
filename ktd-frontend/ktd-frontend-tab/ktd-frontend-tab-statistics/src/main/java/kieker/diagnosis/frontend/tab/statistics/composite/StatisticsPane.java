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
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;

public final class StatisticsPane extends GridPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( StatisticsPane.class.getName( ) );

	private TextField ivProcessedBytes;
	private TextField ivProcessDuration;
	private TextField ivProcessSpeed;
	private TextField ivIgnoredRecords;
	private TextField ivDanglingRecords;
	private TextField ivIncompleteTraces;
	private TextField ivMethods;
	private TextField ivAggregatedMethods;
	private TextField ivTraces;
	private TextField ivBeginnOfMonitoring;
	private TextField ivEndOfMonitoring;
	private TextField ivDirectory;

	public StatisticsPane( ) {
		createControl( );
	}

	private void createControl( ) {
		setPadding( new Insets( 5 ) );

		int rowIndex = 0;

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "directory" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivDirectory = new TextField( );
			ivDirectory.setEditable( false );

			GridPane.setColumnIndex( ivDirectory, 1 );
			GridPane.setRowIndex( ivDirectory, rowIndex++ );
			GridPane.setHgrow( ivDirectory, Priority.ALWAYS );

			getChildren( ).add( ivDirectory );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "processedBytes" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivProcessedBytes = new TextField( );
			ivProcessedBytes.setId( "statisticsProcessedBytes" );
			ivProcessedBytes.setEditable( false );

			GridPane.setColumnIndex( ivProcessedBytes, 1 );
			GridPane.setRowIndex( ivProcessedBytes, rowIndex++ );
			GridPane.setHgrow( ivProcessedBytes, Priority.ALWAYS );

			getChildren( ).add( ivProcessedBytes );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "processDuration" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivProcessDuration = new TextField( );
			ivProcessDuration.setId( "statisticsProcessDuration" );
			ivProcessDuration.setEditable( false );

			GridPane.setColumnIndex( ivProcessDuration, 1 );
			GridPane.setRowIndex( ivProcessDuration, rowIndex++ );
			GridPane.setHgrow( ivProcessDuration, Priority.ALWAYS );

			getChildren( ).add( ivProcessDuration );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "processSpeed" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivProcessSpeed = new TextField( );
			ivProcessSpeed.setId( "statisticsProcessSpeed" );
			ivProcessSpeed.setEditable( false );

			GridPane.setColumnIndex( ivProcessSpeed, 1 );
			GridPane.setRowIndex( ivProcessSpeed, rowIndex++ );
			GridPane.setHgrow( ivProcessSpeed, Priority.ALWAYS );

			getChildren( ).add( ivProcessSpeed );
		}

		{
			final Separator separator = new Separator( );

			GridPane.setColumnIndex( separator, 0 );
			GridPane.setRowIndex( separator, rowIndex++ );
			GridPane.setColumnSpan( separator, 2 );

			getChildren( ).add( separator );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "beginnOfMonitoring" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivBeginnOfMonitoring = new TextField( );
			ivBeginnOfMonitoring.setEditable( false );

			GridPane.setColumnIndex( ivBeginnOfMonitoring, 1 );
			GridPane.setRowIndex( ivBeginnOfMonitoring, rowIndex++ );
			GridPane.setHgrow( ivBeginnOfMonitoring, Priority.ALWAYS );

			getChildren( ).add( ivBeginnOfMonitoring );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "endOfMonitoring" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivEndOfMonitoring = new TextField( );
			ivEndOfMonitoring.setEditable( false );

			GridPane.setColumnIndex( ivEndOfMonitoring, 1 );
			GridPane.setRowIndex( ivEndOfMonitoring, rowIndex++ );
			GridPane.setHgrow( ivEndOfMonitoring, Priority.ALWAYS );

			getChildren( ).add( ivEndOfMonitoring );
		}

		{
			final Separator separator = new Separator( );

			GridPane.setColumnIndex( separator, 0 );
			GridPane.setRowIndex( separator, rowIndex++ );
			GridPane.setColumnSpan( separator, 2 );

			getChildren( ).add( separator );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "traces" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivTraces = new TextField( );
			ivTraces.setEditable( false );

			GridPane.setColumnIndex( ivTraces, 1 );
			GridPane.setRowIndex( ivTraces, rowIndex++ );
			GridPane.setHgrow( ivTraces, Priority.ALWAYS );

			getChildren( ).add( ivTraces );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "methods" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivMethods = new TextField( );
			ivMethods.setEditable( false );

			GridPane.setColumnIndex( ivMethods, 1 );
			GridPane.setRowIndex( ivMethods, rowIndex++ );
			GridPane.setHgrow( ivMethods, Priority.ALWAYS );

			getChildren( ).add( ivMethods );
		}
		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "aggregatedMethods" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivAggregatedMethods = new TextField( );
			ivAggregatedMethods.setEditable( false );

			GridPane.setColumnIndex( ivAggregatedMethods, 1 );
			GridPane.setRowIndex( ivAggregatedMethods, rowIndex++ );
			GridPane.setHgrow( ivAggregatedMethods, Priority.ALWAYS );

			getChildren( ).add( ivAggregatedMethods );
		}

		{
			final Separator separator = new Separator( );

			GridPane.setColumnIndex( separator, 0 );
			GridPane.setRowIndex( separator, rowIndex++ );
			GridPane.setColumnSpan( separator, 2 );

			getChildren( ).add( separator );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "ignoredRecords" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivIgnoredRecords = new TextField( );
			ivIgnoredRecords.setEditable( false );

			GridPane.setColumnIndex( ivIgnoredRecords, 1 );
			GridPane.setRowIndex( ivIgnoredRecords, rowIndex++ );
			GridPane.setHgrow( ivIgnoredRecords, Priority.ALWAYS );

			getChildren( ).add( ivIgnoredRecords );
		}
		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "danglingRecords" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivDanglingRecords = new TextField( );
			ivDanglingRecords.setEditable( false );

			GridPane.setColumnIndex( ivDanglingRecords, 1 );
			GridPane.setRowIndex( ivDanglingRecords, rowIndex++ );
			GridPane.setHgrow( ivDanglingRecords, Priority.ALWAYS );

			getChildren( ).add( ivDanglingRecords );
		}
		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "incompleteTraces" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			getChildren( ).add( label );
		}

		{
			ivIncompleteTraces = new TextField( );
			ivIncompleteTraces.setEditable( false );

			GridPane.setColumnIndex( ivIncompleteTraces, 1 );
			GridPane.setRowIndex( ivIncompleteTraces, rowIndex++ );
			GridPane.setHgrow( ivIncompleteTraces, Priority.ALWAYS );

			getChildren( ).add( ivIncompleteTraces );
		}

		addDefaultStylesheet( );
	}

	public void setValue( final Optional<Statistics> aStatistics ) {
		aStatistics.ifPresentOrElse( statistics -> {
			final NumberFormat decimalFormat = NumberFormat.getInstance( );

			ivProcessedBytes.setText( convertToByteString( statistics.getProcessedBytes( ) ) );
			ivProcessDuration.setText( convertToDurationString( statistics.getProcessDuration( ) ) );
			ivProcessSpeed.setText( convertToSpeedString( statistics.getProcessSpeed( ) ) );
			ivMethods.setText( decimalFormat.format( statistics.getMethods( ) ) );
			ivAggregatedMethods.setText( decimalFormat.format( statistics.getAggregatedMethods( ) ) );
			ivTraces.setText( decimalFormat.format( statistics.getTraces( ) ) );
			ivIgnoredRecords.setText( decimalFormat.format( statistics.getIgnoredRecords( ) ) );
			ivDanglingRecords.setText( decimalFormat.format( statistics.getDanglingRecords( ) ) );
			ivIncompleteTraces.setText( decimalFormat.format( statistics.getIncompleteTraces( ) ) );
			ivBeginnOfMonitoring.setText( statistics.getBeginnOfMonitoring( ) );
			ivEndOfMonitoring.setText( statistics.getEndOfMonitoring( ) );
			ivDirectory.setText( statistics.getDirectory( ) );
		}, ( ) -> {
			ivProcessedBytes.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivProcessDuration.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivProcessSpeed.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivMethods.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivAggregatedMethods.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivTraces.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivIgnoredRecords.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivDanglingRecords.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivIncompleteTraces.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivBeginnOfMonitoring.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivEndOfMonitoring.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
			ivDirectory.setText( RESOURCE_BUNDLE.getString( "noDataAvailable" ) );
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
