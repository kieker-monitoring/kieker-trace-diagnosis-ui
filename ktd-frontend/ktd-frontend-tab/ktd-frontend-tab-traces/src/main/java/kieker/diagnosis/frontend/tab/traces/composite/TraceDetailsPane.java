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

package kieker.diagnosis.frontend.tab.traces.composite;

import java.util.ResourceBundle;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;

/**
 * This component shows the details of a single trace.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceDetailsPane extends TitledPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( TraceDetailsPane.class.getName( ) );

	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final TextField traceDepth;
	private final TextField traceSize;
	private final TextField duration;
	private final TextField percent;
	private final TextField timestamp;
	private final TextField traceId;

	public TraceDetailsPane( ) {
		setText( RESOURCE_BUNDLE.getString( "detailTitle" ) );

		{
			final GridPane griPane = new GridPane( );

			int rowIndex = 0;

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelHost" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				host = new TextField( );
				host.setId( "tabTracesDetailHost" );
				host.setEditable( false );
				host.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( host, 1 );
				GridPane.setRowIndex( host, rowIndex++ );
				GridPane.setHgrow( host, Priority.ALWAYS );

				griPane.getChildren( ).add( host );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelClass" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				clazz = new TextField( );
				clazz.setEditable( false );
				clazz.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( clazz, 1 );
				GridPane.setRowIndex( clazz, rowIndex++ );
				GridPane.setHgrow( clazz, Priority.ALWAYS );

				griPane.getChildren( ).add( clazz );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelMethod" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				method = new TextField( );
				method.setEditable( false );
				method.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( method, 1 );
				GridPane.setRowIndex( method, rowIndex++ );
				GridPane.setHgrow( method, Priority.ALWAYS );

				griPane.getChildren( ).add( method );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelException" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				exception = new TextField( );
				exception.setEditable( false );
				exception.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( exception, 1 );
				GridPane.setRowIndex( exception, rowIndex++ );
				GridPane.setHgrow( exception, Priority.ALWAYS );

				griPane.getChildren( ).add( exception );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTraceDepth" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				traceDepth = new TextField( );
				traceDepth.setEditable( false );
				traceDepth.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( traceDepth, 1 );
				GridPane.setRowIndex( traceDepth, rowIndex++ );
				GridPane.setHgrow( traceDepth, Priority.ALWAYS );

				griPane.getChildren( ).add( traceDepth );
			}

			rowIndex = 0;

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTraceSize" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				traceSize = new TextField( );
				traceSize.setEditable( false );
				traceSize.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( traceSize, 3 );
				GridPane.setRowIndex( traceSize, rowIndex++ );
				GridPane.setHgrow( traceSize, Priority.ALWAYS );

				griPane.getChildren( ).add( traceSize );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelPercent" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				percent = new TextField( );
				percent.setEditable( false );
				percent.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( percent, 3 );
				GridPane.setRowIndex( percent, rowIndex++ );
				GridPane.setHgrow( percent, Priority.ALWAYS );

				griPane.getChildren( ).add( percent );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				duration = new TextField( );
				duration.setEditable( false );
				duration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( duration, 3 );
				GridPane.setRowIndex( duration, rowIndex++ );
				GridPane.setHgrow( duration, Priority.ALWAYS );

				griPane.getChildren( ).add( duration );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTimestamp" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				timestamp = new TextField( );
				timestamp.setEditable( false );
				timestamp.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( timestamp, 3 );
				GridPane.setRowIndex( timestamp, rowIndex++ );
				GridPane.setHgrow( timestamp, Priority.ALWAYS );

				griPane.getChildren( ).add( timestamp );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTraceId" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				traceId = new TextField( );
				traceId.setEditable( false );
				traceId.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( traceId, 3 );
				GridPane.setRowIndex( traceId, rowIndex++ );
				GridPane.setHgrow( traceId, Priority.ALWAYS );

				griPane.getChildren( ).add( traceId );
			}

			setContent( griPane );
		}

		addDefaultStylesheet( );
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
			traceDepth.setText( Integer.toString( value.getTraceDepth( ) ) );
			traceSize.setText( Integer.toString( value.getTraceSize( ) ) );
			duration.setText( String.format( "%d [ns]", value.getDuration( ) ) );
			percent.setText( String.format( "%f %%", value.getPercent( ) ) );
			timestamp.setText( Long.toString( value.getTimestamp( ) ) );
			traceId.setText( Long.toString( value.getTraceId( ) ) );
		} else {
			host.setText( noDataAvailable );
			clazz.setText( noDataAvailable );
			method.setText( noDataAvailable );
			exception.setText( noDataAvailable );
			traceDepth.setText( noDataAvailable );
			traceSize.setText( noDataAvailable );
			duration.setText( noDataAvailable );
			percent.setText( noDataAvailable );
			timestamp.setText( noDataAvailable );
			traceId.setText( noDataAvailable );
		}
	}

}
