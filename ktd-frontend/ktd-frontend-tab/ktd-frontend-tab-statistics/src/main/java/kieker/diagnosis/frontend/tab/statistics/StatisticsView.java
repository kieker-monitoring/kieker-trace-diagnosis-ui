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

package kieker.diagnosis.frontend.tab.statistics;

import com.google.inject.Singleton;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import kieker.diagnosis.frontend.base.ui.ViewBase;

/**
 * The view of the statistics tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class StatisticsView extends ViewBase<StatisticsController> {

	private final TextField ivProcessedBytes;
	private final TextField ivProcessDuration;
	private final TextField ivProcessSpeed;
	private final TextField ivIgnoredRecords;
	private final TextField ivDanglingRecords;
	private final TextField ivIncompleteTraces;
	private final TextField ivMethods;
	private final TextField ivAggregatedMethods;
	private final TextField ivTraces;
	private final TextField ivBeginnOfMonitoring;
	private final TextField ivEndOfMonitoring;
	private final TextField ivDirectory;

	private final ProgressBar ivProgressBar;
	private final Text ivProgressText;

	public StatisticsView( ) {
		final GridPane gridPane = new GridPane( );
		VBox.setVgrow( gridPane, Priority.ALWAYS );
		gridPane.setPadding( new Insets( 5 ) );

		int rowIndex = 0;

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "directory" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivDirectory = new TextField( );
			ivDirectory.setEditable( false );

			GridPane.setColumnIndex( ivDirectory, 1 );
			GridPane.setRowIndex( ivDirectory, rowIndex++ );
			GridPane.setHgrow( ivDirectory, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivDirectory );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "processedBytes" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivProcessedBytes = new TextField( );
			ivProcessedBytes.setId( "statisticsProcessedBytes" );
			ivProcessedBytes.setEditable( false );

			GridPane.setColumnIndex( ivProcessedBytes, 1 );
			GridPane.setRowIndex( ivProcessedBytes, rowIndex++ );
			GridPane.setHgrow( ivProcessedBytes, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivProcessedBytes );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "processDuration" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivProcessDuration = new TextField( );
			ivProcessDuration.setId( "statisticsProcessDuration" );
			ivProcessDuration.setEditable( false );

			GridPane.setColumnIndex( ivProcessDuration, 1 );
			GridPane.setRowIndex( ivProcessDuration, rowIndex++ );
			GridPane.setHgrow( ivProcessDuration, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivProcessDuration );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "processSpeed" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivProcessSpeed = new TextField( );
			ivProcessSpeed.setId( "statisticsProcessSpeed" );
			ivProcessSpeed.setEditable( false );

			GridPane.setColumnIndex( ivProcessSpeed, 1 );
			GridPane.setRowIndex( ivProcessSpeed, rowIndex++ );
			GridPane.setHgrow( ivProcessSpeed, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivProcessSpeed );
		}

		{
			final Separator separator = new Separator( );

			GridPane.setColumnIndex( separator, 0 );
			GridPane.setRowIndex( separator, rowIndex++ );
			GridPane.setColumnSpan( separator, 2 );

			gridPane.getChildren( ).add( separator );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "beginnOfMonitoring" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivBeginnOfMonitoring = new TextField( );
			ivBeginnOfMonitoring.setEditable( false );

			GridPane.setColumnIndex( ivBeginnOfMonitoring, 1 );
			GridPane.setRowIndex( ivBeginnOfMonitoring, rowIndex++ );
			GridPane.setHgrow( ivBeginnOfMonitoring, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivBeginnOfMonitoring );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "endOfMonitoring" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivEndOfMonitoring = new TextField( );
			ivEndOfMonitoring.setEditable( false );

			GridPane.setColumnIndex( ivEndOfMonitoring, 1 );
			GridPane.setRowIndex( ivEndOfMonitoring, rowIndex++ );
			GridPane.setHgrow( ivEndOfMonitoring, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivEndOfMonitoring );
		}

		{
			final Separator separator = new Separator( );

			GridPane.setColumnIndex( separator, 0 );
			GridPane.setRowIndex( separator, rowIndex++ );
			GridPane.setColumnSpan( separator, 2 );

			gridPane.getChildren( ).add( separator );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "traces" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivTraces = new TextField( );
			ivTraces.setEditable( false );

			GridPane.setColumnIndex( ivTraces, 1 );
			GridPane.setRowIndex( ivTraces, rowIndex++ );
			GridPane.setHgrow( ivTraces, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivTraces );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "methods" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivMethods = new TextField( );
			ivMethods.setEditable( false );

			GridPane.setColumnIndex( ivMethods, 1 );
			GridPane.setRowIndex( ivMethods, rowIndex++ );
			GridPane.setHgrow( ivMethods, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivMethods );
		}
		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "aggregatedMethods" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivAggregatedMethods = new TextField( );
			ivAggregatedMethods.setEditable( false );

			GridPane.setColumnIndex( ivAggregatedMethods, 1 );
			GridPane.setRowIndex( ivAggregatedMethods, rowIndex++ );
			GridPane.setHgrow( ivAggregatedMethods, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivAggregatedMethods );
		}

		{
			final Separator separator = new Separator( );

			GridPane.setColumnIndex( separator, 0 );
			GridPane.setRowIndex( separator, rowIndex++ );
			GridPane.setColumnSpan( separator, 2 );

			gridPane.getChildren( ).add( separator );
		}

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "ignoredRecords" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivIgnoredRecords = new TextField( );
			ivIgnoredRecords.setEditable( false );

			GridPane.setColumnIndex( ivIgnoredRecords, 1 );
			GridPane.setRowIndex( ivIgnoredRecords, rowIndex++ );
			GridPane.setHgrow( ivIgnoredRecords, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivIgnoredRecords );
		}
		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "danglingRecords" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivDanglingRecords = new TextField( );
			ivDanglingRecords.setEditable( false );

			GridPane.setColumnIndex( ivDanglingRecords, 1 );
			GridPane.setRowIndex( ivDanglingRecords, rowIndex++ );
			GridPane.setHgrow( ivDanglingRecords, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivDanglingRecords );
		}
		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "incompleteTraces" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			ivIncompleteTraces = new TextField( );
			ivIncompleteTraces.setEditable( false );

			GridPane.setColumnIndex( ivIncompleteTraces, 1 );
			GridPane.setRowIndex( ivIncompleteTraces, rowIndex++ );
			GridPane.setHgrow( ivIncompleteTraces, Priority.ALWAYS );

			gridPane.getChildren( ).add( ivIncompleteTraces );
		}

		getChildren( ).add( gridPane );

		// Status bar
		{
			final TitledPane titledPane = new TitledPane( );
			titledPane.setText( getLocalizedString( "memoryUsage" ) );
			titledPane.setCollapsible( false );

			{
				final StackPane stackPane = new StackPane( );
				VBox.setMargin( stackPane, new Insets( 2 ) );

				{
					ivProgressBar = new ProgressBar( );
					ivProgressBar.setMaxWidth( Double.POSITIVE_INFINITY );
					ivProgressBar.setPrefHeight( 30 );

					stackPane.getChildren( ).add( ivProgressBar );
				}

				{
					ivProgressText = new Text( );

					stackPane.getChildren( ).add( ivProgressText );
				}

				titledPane.setContent( stackPane );
			}

			getChildren( ).add( titledPane );
		}
	}

	TextField getProcessedBytes( ) {
		return ivProcessedBytes;
	}

	TextField getProcessDuration( ) {
		return ivProcessDuration;
	}

	TextField getProcessSpeed( ) {
		return ivProcessSpeed;
	}

	TextField getIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

	TextField getDanglingRecords( ) {
		return ivDanglingRecords;
	}

	TextField getIncompleteTraces( ) {
		return ivIncompleteTraces;
	}

	TextField getMethods( ) {
		return ivMethods;
	}

	TextField getAggregatedMethods( ) {
		return ivAggregatedMethods;
	}

	TextField getTraces( ) {
		return ivTraces;
	}

	TextField getBeginnOfMonitoring( ) {
		return ivBeginnOfMonitoring;
	}

	TextField getEndOfMonitoring( ) {
		return ivEndOfMonitoring;
	}

	TextField getDirectory( ) {
		return ivDirectory;
	}

	ProgressBar getProgressBar( ) {
		return ivProgressBar;
	}

	Text getProgressText( ) {
		return ivProgressText;
	}

	@Override
	public void setParameter( final Object aParameter ) {
	}

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

}
