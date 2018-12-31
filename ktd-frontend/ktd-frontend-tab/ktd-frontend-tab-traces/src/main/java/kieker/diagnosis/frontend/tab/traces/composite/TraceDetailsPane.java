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

	private final TextField ivDetailsHost;
	private final TextField ivDetailsClass;
	private final TextField ivDetailsMethod;
	private final TextField ivDetailsException;
	private final TextField ivDetailsTraceDepth;
	private final TextField ivDetailsTraceSize;
	private final TextField ivDetailsDuration;
	private final TextField ivDetailsPercent;
	private final TextField ivDetailsTimestamp;
	private final TextField ivDetailsTraceId;

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
				ivDetailsHost = new TextField( );
				ivDetailsHost.setId( "tabTracesDetailHost" );
				ivDetailsHost.setEditable( false );
				ivDetailsHost.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsHost, 1 );
				GridPane.setRowIndex( ivDetailsHost, rowIndex++ );
				GridPane.setHgrow( ivDetailsHost, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsHost );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelClass" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsClass = new TextField( );
				ivDetailsClass.setEditable( false );
				ivDetailsClass.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsClass, 1 );
				GridPane.setRowIndex( ivDetailsClass, rowIndex++ );
				GridPane.setHgrow( ivDetailsClass, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsClass );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelMethod" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsMethod = new TextField( );
				ivDetailsMethod.setEditable( false );
				ivDetailsMethod.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsMethod, 1 );
				GridPane.setRowIndex( ivDetailsMethod, rowIndex++ );
				GridPane.setHgrow( ivDetailsMethod, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsMethod );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelException" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsException = new TextField( );
				ivDetailsException.setEditable( false );
				ivDetailsException.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsException, 1 );
				GridPane.setRowIndex( ivDetailsException, rowIndex++ );
				GridPane.setHgrow( ivDetailsException, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsException );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTraceDepth" ) );

				GridPane.setColumnIndex( label, 0 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsTraceDepth = new TextField( );
				ivDetailsTraceDepth.setEditable( false );
				ivDetailsTraceDepth.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsTraceDepth, 1 );
				GridPane.setRowIndex( ivDetailsTraceDepth, rowIndex++ );
				GridPane.setHgrow( ivDetailsTraceDepth, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsTraceDepth );
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
				ivDetailsTraceSize = new TextField( );
				ivDetailsTraceSize.setEditable( false );
				ivDetailsTraceSize.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsTraceSize, 3 );
				GridPane.setRowIndex( ivDetailsTraceSize, rowIndex++ );
				GridPane.setHgrow( ivDetailsTraceSize, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsTraceSize );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelPercent" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsPercent = new TextField( );
				ivDetailsPercent.setEditable( false );
				ivDetailsPercent.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsPercent, 3 );
				GridPane.setRowIndex( ivDetailsPercent, rowIndex++ );
				GridPane.setHgrow( ivDetailsPercent, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsPercent );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelDuration" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsDuration = new TextField( );
				ivDetailsDuration.setEditable( false );
				ivDetailsDuration.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsDuration, 3 );
				GridPane.setRowIndex( ivDetailsDuration, rowIndex++ );
				GridPane.setHgrow( ivDetailsDuration, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsDuration );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTimestamp" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsTimestamp = new TextField( );
				ivDetailsTimestamp.setEditable( false );
				ivDetailsTimestamp.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsTimestamp, 3 );
				GridPane.setRowIndex( ivDetailsTimestamp, rowIndex++ );
				GridPane.setHgrow( ivDetailsTimestamp, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsTimestamp );
			}

			{
				final Label label = new Label( );
				label.setText( RESOURCE_BUNDLE.getString( "labelTraceId" ) );

				GridPane.setColumnIndex( label, 2 );
				GridPane.setRowIndex( label, rowIndex );

				griPane.getChildren( ).add( label );
			}

			{
				ivDetailsTraceId = new TextField( );
				ivDetailsTraceId.setEditable( false );
				ivDetailsTraceId.getStyleClass( ).add( "details" );

				GridPane.setColumnIndex( ivDetailsTraceId, 3 );
				GridPane.setRowIndex( ivDetailsTraceId, rowIndex++ );
				GridPane.setHgrow( ivDetailsTraceId, Priority.ALWAYS );

				griPane.getChildren( ).add( ivDetailsTraceId );
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
			ivDetailsHost.setText( value.getHost( ) );
			ivDetailsClass.setText( value.getClazz( ) );
			ivDetailsMethod.setText( value.getMethod( ) );
			ivDetailsException.setText( value.getException( ) != null ? value.getException( ) : noDataAvailable );
			ivDetailsTraceDepth.setText( Integer.toString( value.getTraceDepth( ) ) );
			ivDetailsTraceSize.setText( Integer.toString( value.getTraceSize( ) ) );
			ivDetailsDuration.setText( String.format( "%d [ns]", value.getDuration( ) ) );
			ivDetailsPercent.setText( String.format( "%f %%", value.getPercent( ) ) );
			ivDetailsTimestamp.setText( Long.toString( value.getTimestamp( ) ) );
			ivDetailsTraceId.setText( Long.toString( value.getTraceId( ) ) );
		} else {
			ivDetailsHost.setText( noDataAvailable );
			ivDetailsClass.setText( noDataAvailable );
			ivDetailsMethod.setText( noDataAvailable );
			ivDetailsException.setText( noDataAvailable );
			ivDetailsTraceDepth.setText( noDataAvailable );
			ivDetailsTraceSize.setText( noDataAvailable );
			ivDetailsDuration.setText( noDataAvailable );
			ivDetailsPercent.setText( noDataAvailable );
			ivDetailsTimestamp.setText( noDataAvailable );
			ivDetailsTraceId.setText( noDataAvailable );
		}
	}

}
