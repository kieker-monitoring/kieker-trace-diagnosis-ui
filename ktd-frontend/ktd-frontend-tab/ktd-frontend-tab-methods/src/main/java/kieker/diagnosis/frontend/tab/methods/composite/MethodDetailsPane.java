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

package kieker.diagnosis.frontend.tab.methods.composite;

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
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;

/**
 * This component shows the details of a single method call.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodDetailsPane extends TitledPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MethodDetailsPane.class.getName( ) );

	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final TextField duration;
	private final TextField timestamp;
	private final TextField traceId;

	private final Hyperlink jumpToTraceLink;

	public MethodDetailsPane( ) {
		setText( RESOURCE_BUNDLE.getString( "detailTitle" ) );

		final GridPane gridPane = new GridPane( );

		int rowIndex = 0;

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "labelHost" ) );

			GridPane.setColumnIndex( label, 0 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			host = new TextField( );
			host.setId( "tabMethodsDetailHost" );
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
			jumpToTraceLink = new Hyperlink( );
			jumpToTraceLink.setText( RESOURCE_BUNDLE.getString( "jumpToTrace" ) );

			GridPane.setColumnIndex( jumpToTraceLink, 0 );
			GridPane.setColumnSpan( jumpToTraceLink, 2 );
			GridPane.setRowIndex( jumpToTraceLink, rowIndex++ );
			GridPane.setHgrow( jumpToTraceLink, Priority.ALWAYS );
			GridPane.setMargin( jumpToTraceLink, new Insets( 0, 0, 0, -5 ) );

			gridPane.getChildren( ).add( jumpToTraceLink );
		}

		rowIndex = 0;

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "labelDuration" ) );

			GridPane.setColumnIndex( label, 2 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			duration = new TextField( );
			duration.setEditable( false );
			duration.getStyleClass( ).add( "details" );

			GridPane.setColumnIndex( duration, 3 );
			GridPane.setRowIndex( duration, rowIndex++ );
			GridPane.setHgrow( duration, Priority.ALWAYS );

			gridPane.getChildren( ).add( duration );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "labelTimestamp" ) );

			GridPane.setColumnIndex( label, 2 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			timestamp = new TextField( );
			timestamp.setEditable( false );
			timestamp.getStyleClass( ).add( "details" );

			GridPane.setColumnIndex( timestamp, 3 );
			GridPane.setRowIndex( timestamp, rowIndex++ );
			GridPane.setHgrow( timestamp, Priority.ALWAYS );

			gridPane.getChildren( ).add( timestamp );
		}

		{
			final Label label = new Label( );
			label.setText( RESOURCE_BUNDLE.getString( "labelTraceId" ) );

			GridPane.setColumnIndex( label, 2 );
			GridPane.setRowIndex( label, rowIndex );

			gridPane.getChildren( ).add( label );
		}

		{
			traceId = new TextField( );
			traceId.setEditable( false );
			traceId.getStyleClass( ).add( "details" );

			GridPane.setColumnIndex( traceId, 3 );
			GridPane.setRowIndex( traceId, rowIndex++ );
			GridPane.setHgrow( traceId, Priority.ALWAYS );

			gridPane.getChildren( ).add( traceId );
		}

		setContent( gridPane );
		addDefaultStylesheet( );
	}

	/**
	 * Sets the action which is performed when the user wants to jump to the corresponding trace.
	 *
	 * @param value
	 *            The action.
	 */
	public void setOnJumpToTrace( final EventHandler<ActionEvent> value ) {
		jumpToTraceLink.setOnAction( value );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Can also be {@code null}.
	 */
	public void setValue( final MethodCall value ) {
		final String noDataAvailable = RESOURCE_BUNDLE.getString( "noDataAvailable" );

		if ( value != null ) {
			host.setText( value.getHost( ) );
			clazz.setText( value.getClazz( ) );
			method.setText( value.getMethod( ) );
			exception.setText( value.getException( ) != null ? value.getException( ) : noDataAvailable );
			duration.setText( String.format( "%d [ns]", value.getDuration( ) ) );
			timestamp.setText( Long.toString( value.getTimestamp( ) ) );
			traceId.setText( Long.toString( value.getTraceId( ) ) );
		} else {
			host.setText( noDataAvailable );
			clazz.setText( noDataAvailable );
			method.setText( noDataAvailable );
			exception.setText( noDataAvailable );
			duration.setText( noDataAvailable );
			timestamp.setText( noDataAvailable );
			traceId.setText( noDataAvailable );
		}
	}

}
