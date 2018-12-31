/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.aggregatedmethods.composite;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;

/**
 * This component shows the details of a single aggregated method call.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodDetailsPane extends TitledPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AggregatedMethodDetailsPane.class.getName( ) );

	private final TextField count;
	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final TextField minDuration;
	private final TextField avgDuration;
	private final TextField medianDuration;
	private final TextField maxDuration;
	private final TextField totalDuration;

	private Hyperlink jumpToMethodsLink;

	public AggregatedMethodDetailsPane( ) {
		setText( RESOURCE_BUNDLE.getString( "detailTitle" ) );

		{
			final GridPane gridPane = new GridPane( );

			int rowIndex = 0;

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelCount" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}
			{
				count = new TextField( );
				count.setEditable( false );
				count.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( count, 1 );
				GridPane.setRowIndex( count, rowIndex++ );
				GridPane.setHgrow( count, Priority.ALWAYS );

				gridPane.getChildren( ).add( count );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelHost" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				host = new TextField( );
				host.setId( "tabAggregatedMethodsDetailHost" );
				host.setEditable( false );
				host.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( host, 1 );
				GridPane.setRowIndex( host, rowIndex++ );
				GridPane.setHgrow( host, Priority.ALWAYS );

				gridPane.getChildren( ).add( host );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelClass" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				clazz = new TextField( );
				clazz.setEditable( false );
				clazz.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( clazz, 1 );
				GridPane.setRowIndex( clazz, rowIndex++ );
				GridPane.setHgrow( clazz, Priority.ALWAYS );

				gridPane.getChildren( ).add( clazz );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelMethod" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				method = new TextField( );
				method.setEditable( false );
				method.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( method, 1 );
				GridPane.setRowIndex( method, rowIndex++ );
				GridPane.setHgrow( method, Priority.ALWAYS );

				gridPane.getChildren( ).add( method );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelException" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				exception = new TextField( );
				exception.setEditable( false );
				exception.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( exception, 1 );
				GridPane.setRowIndex( exception, rowIndex++ );
				GridPane.setHgrow( exception, Priority.ALWAYS );

				gridPane.getChildren( ).add( exception );
			}

			{
				jumpToMethodsLink = new Hyperlink( );
				jumpToMethodsLink.setText( RESOURCE_BUNDLE.getString( "jumpToMethods" ) );

				GridPane.setColumnIndex( jumpToMethodsLink, 0 );
				GridPane.setColumnSpan( jumpToMethodsLink, 2 );
				GridPane.setRowIndex( jumpToMethodsLink, rowIndex++ );
				GridPane.setHgrow( jumpToMethodsLink, Priority.ALWAYS );
				GridPane.setMargin( jumpToMethodsLink, new Insets( 0, 0, 0, -5 ) );

				gridPane.getChildren( ).add( jumpToMethodsLink );
			}

			rowIndex = 0;

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelMinDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				minDuration = new TextField( );
				minDuration.setEditable( false );
				minDuration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( minDuration, 3 );
				GridPane.setRowIndex( minDuration, rowIndex++ );
				GridPane.setHgrow( minDuration, Priority.ALWAYS );

				gridPane.getChildren( ).add( minDuration );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelAvgDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				avgDuration = new TextField( );
				avgDuration.setEditable( false );
				avgDuration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( avgDuration, 3 );
				GridPane.setRowIndex( avgDuration, rowIndex++ );
				GridPane.setHgrow( avgDuration, Priority.ALWAYS );

				gridPane.getChildren( ).add( avgDuration );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelMedianDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				medianDuration = new TextField( );
				medianDuration.setEditable( false );
				medianDuration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( medianDuration, 3 );
				GridPane.setRowIndex( medianDuration, rowIndex++ );
				GridPane.setHgrow( medianDuration, Priority.ALWAYS );

				gridPane.getChildren( ).add( medianDuration );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelMaxDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				maxDuration = new TextField( );
				maxDuration.setEditable( false );
				maxDuration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( maxDuration, 3 );
				GridPane.setRowIndex( maxDuration, rowIndex++ );
				GridPane.setHgrow( maxDuration, Priority.ALWAYS );

				gridPane.getChildren( ).add( maxDuration );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTotalDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				gridPane.getChildren( ).add( label );
			}

			{
				totalDuration = new TextField( );
				totalDuration.setEditable( false );
				totalDuration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( totalDuration, 3 );
				GridPane.setRowIndex( totalDuration, rowIndex++ );
				GridPane.setHgrow( totalDuration, Priority.ALWAYS );

				gridPane.getChildren( ).add( totalDuration );
			}

			setContent( gridPane );
		}

		addDefaultStylesheet( );
	}

	/**
	 * Sets the action which is performed when the user wants to jump to the corresponding methods.
	 *
	 * @param value
	 *            The action.
	 */
	public void setOnJumpToMethods( final EventHandler<ActionEvent> value ) {
		jumpToMethodsLink.setOnAction( value );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Can also be {@code null}.
	 */
	public void setValue( final AggregatedMethodCall value ) {
		final String noDataAvailable = RESOURCE_BUNDLE.getString( "noDataAvailable" );

		if ( value != null ) {
			count.setText( Integer.toString( value.getCount( ) ) );
			host.setText( value.getHost( ) );
			clazz.setText( value.getClazz( ) );
			method.setText( value.getMethod( ) );
			exception.setText( value.getException( ) != null ? value.getException( ) : noDataAvailable );
			minDuration.setText( String.format( "%d [ns]", value.getMinDuration( ) ) );
			avgDuration.setText( String.format( "%d [ns]", value.getAvgDuration( ) ) );
			medianDuration.setText( String.format( "%d [ns]", value.getMedianDuration( ) ) );
			maxDuration.setText( String.format( "%d [ns]", value.getMaxDuration( ) ) );
			totalDuration.setText( String.format( "%d [ns]", value.getTotalDuration( ) ) );
		} else {
			count.setText( noDataAvailable );
			host.setText( noDataAvailable );
			clazz.setText( noDataAvailable );
			method.setText( noDataAvailable );
			exception.setText( noDataAvailable );
			minDuration.setText( noDataAvailable );
			avgDuration.setText( noDataAvailable );
			medianDuration.setText( noDataAvailable );
			maxDuration.setText( noDataAvailable );
			totalDuration.setText( noDataAvailable );
		}
	}

}
