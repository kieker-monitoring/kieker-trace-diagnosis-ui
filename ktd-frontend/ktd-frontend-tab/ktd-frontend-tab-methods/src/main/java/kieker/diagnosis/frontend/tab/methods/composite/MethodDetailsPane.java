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

package kieker.diagnosis.frontend.tab.methods.composite;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
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

	private TextField host;
	private TextField clazz;
	private TextField method;
	private TextField exception;
	private TextField duration;
	private TextField timestamp;
	private TextField traceId;

	private Hyperlink jumpToTraceLink;

	public MethodDetailsPane( ) {
		createControl( );
	}

	private void createControl( ) {
		setText( RESOURCE_BUNDLE.getString( "detailTitle" ) );
		setContent( createGridPane( ) );
		addDefaultStylesheet( );
	}

	private Node createGridPane( ) {
		final GridPane gridPane = new GridPane( );

		int rowIndex = 0;

		gridPane.add( createHostLabel( ), 0, rowIndex );
		gridPane.add( createHostField( ), 1, rowIndex++ );

		gridPane.add( createClassLabel( ), 0, rowIndex );
		gridPane.add( createClassField( ), 1, rowIndex++ );

		gridPane.add( createMethodLabel( ), 0, rowIndex );
		gridPane.add( createMethodField( ), 1, rowIndex++ );

		gridPane.add( createExceptionLabel( ), 0, rowIndex );
		gridPane.add( createExceptionField( ), 1, rowIndex++ );

		gridPane.add( createJumpToTraceLink( ), 0, rowIndex, 2, 1 );

		rowIndex = 0;

		gridPane.add( createDurationLabel( ), 2, rowIndex );
		gridPane.add( createDurationField( ), 3, rowIndex++ );

		gridPane.add( createTimestampLabel( ), 2, rowIndex );
		gridPane.add( createTimestampField( ), 3, rowIndex++ );

		gridPane.add( createTraceIdLabel( ), 2, rowIndex );
		gridPane.add( createTraceIdField( ), 3, rowIndex++ );

		return gridPane;
	}

	private Node createHostLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelHost" ) );

		return label;
	}

	private Node createHostField( ) {
		host = new TextField( );

		host.setId( "tabMethodsDetailHost" );
		host.setEditable( false );
		host.getStyleClass( ).add( "details" );
		GridPane.setHgrow( host, Priority.ALWAYS );

		return host;
	}

	private Node createClassLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelClass" ) );

		return label;
	}

	private Node createClassField( ) {
		clazz = new TextField( );

		clazz.setEditable( false );
		clazz.getStyleClass( ).add( "details" );
		GridPane.setHgrow( clazz, Priority.ALWAYS );

		return clazz;
	}

	private Node createMethodLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelMethod" ) );

		return label;
	}

	private Node createMethodField( ) {
		method = new TextField( );

		method.setEditable( false );
		method.getStyleClass( ).add( "details" );
		GridPane.setHgrow( method, Priority.ALWAYS );

		return method;
	}

	private Node createExceptionLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelException" ) );

		return label;
	}

	private Node createExceptionField( ) {
		exception = new TextField( );

		exception.setId( "tabMethodsDetailException" );
		exception.setEditable( false );
		exception.getStyleClass( ).add( "details" );
		GridPane.setHgrow( exception, Priority.ALWAYS );

		return exception;
	}

	private Node createJumpToTraceLink( ) {
		jumpToTraceLink = new Hyperlink( );

		jumpToTraceLink.setId( "tabMethodsJumpToTrace" );
		jumpToTraceLink.setText( RESOURCE_BUNDLE.getString( "jumpToTrace" ) );

		GridPane.setHgrow( jumpToTraceLink, Priority.ALWAYS );
		GridPane.setMargin( jumpToTraceLink, new Insets( 0, 0, 0, -5 ) );

		return jumpToTraceLink;
	}

	private Node createTraceIdField( ) {
		traceId = new TextField( );

		traceId.setEditable( false );
		traceId.getStyleClass( ).add( "details" );

		GridPane.setHgrow( traceId, Priority.ALWAYS );

		return traceId;
	}

	private Node createTraceIdLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelTraceId" ) );

		return label;
	}

	private Node createTimestampField( ) {
		timestamp = new TextField( );

		timestamp.setEditable( false );
		timestamp.getStyleClass( ).add( "details" );

		GridPane.setHgrow( timestamp, Priority.ALWAYS );

		return timestamp;
	}

	private Node createTimestampLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelTimestamp" ) );

		return label;
	}

	private Node createDurationField( ) {
		duration = new TextField( );

		duration.setEditable( false );
		duration.getStyleClass( ).add( "details" );

		GridPane.setHgrow( duration, Priority.ALWAYS );

		return duration;
	}

	private Node createDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelDuration" ) );

		return label;
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
