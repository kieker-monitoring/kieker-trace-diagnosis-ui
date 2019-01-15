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

package kieker.diagnosis.frontend.tab.aggregatedmethods.composite;

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
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;

/**
 * This component shows the details of a single aggregated method call.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodDetailsPane extends TitledPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AggregatedMethodDetailsPane.class.getName( ) );

	private TextField count;
	private TextField host;
	private TextField clazz;
	private TextField method;
	private TextField exception;
	private TextField minDuration;
	private TextField avgDuration;
	private TextField medianDuration;
	private TextField maxDuration;
	private TextField totalDuration;

	private Hyperlink jumpToMethodsLink;

	public AggregatedMethodDetailsPane( ) {
		createControl( );
	}

	private void createControl( ) {
		setText( RESOURCE_BUNDLE.getString( "detailTitle" ) );
		addDefaultStylesheet( );

		setContent( createGridPane( ) );
	}

	private Node createGridPane( ) {
		final GridPane gridPane = new GridPane( );

		int rowIndex = 0;

		gridPane.add( createCountLabel( ), 0, rowIndex );
		gridPane.add( createCountField( ), 1, rowIndex++ );

		gridPane.add( createHostLabel( ), 0, rowIndex );
		gridPane.add( createHostField( ), 1, rowIndex++ );

		gridPane.add( createClassLabel( ), 0, rowIndex );
		gridPane.add( createClassField( ), 1, rowIndex++ );

		gridPane.add( createMethodLabel( ), 0, rowIndex );
		gridPane.add( createMethodField( ), 1, rowIndex++ );

		gridPane.add( createExceptionLabel( ), 0, rowIndex );
		gridPane.add( createExceptionField( ), 1, rowIndex++ );

		gridPane.add( createJumpToMethodsLink( ), 0, rowIndex, 2, 1 );

		rowIndex = 0;

		gridPane.add( createMinDurationLabel( ), 2, rowIndex );
		gridPane.add( createMinDurationField( ), 3, rowIndex++ );

		gridPane.add( createAvgDurationLabel( ), 2, rowIndex );
		gridPane.add( createAvgDurationField( ), 3, rowIndex++ );

		gridPane.add( createMedianDurationLabel( ), 2, rowIndex );
		gridPane.add( createMedianDurationField( ), 3, rowIndex++ );

		gridPane.add( createMaxDurationLabel( ), 2, rowIndex );
		gridPane.add( createMaxDurationField( ), 3, rowIndex++ );

		gridPane.add( createTotalDurationLabel( ), 2, rowIndex );
		gridPane.add( createTotalDurationField( ), 3, rowIndex++ );

		return gridPane;
	}

	private Node createCountLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelCount" ) );

		return label;
	}

	private Node createCountField( ) {
		count = new TextField( );

		count.setEditable( false );
		count.getStyleClass( ).add( "details" );

		GridPane.setHgrow( count, Priority.ALWAYS );

		return count;
	}

	private Node createHostLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelHost" ) );

		return label;
	}

	private Node createHostField( ) {
		host = new TextField( );

		host.setId( "tabAggregatedMethodsDetailHost" );
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

		exception.setId( "tabAggregatedMethodsDetailException" );
		exception.setEditable( false );
		exception.getStyleClass( ).add( "details" );

		GridPane.setHgrow( exception, Priority.ALWAYS );

		return exception;
	}

	private Node createJumpToMethodsLink( ) {
		jumpToMethodsLink = new Hyperlink( );

		jumpToMethodsLink.setId( "tabAggregatedMethodsJumpToMethods" );
		jumpToMethodsLink.setText( RESOURCE_BUNDLE.getString( "jumpToMethods" ) );

		GridPane.setHgrow( jumpToMethodsLink, Priority.ALWAYS );
		GridPane.setMargin( jumpToMethodsLink, new Insets( 0, 0, 0, -5 ) );

		return jumpToMethodsLink;
	}

	private Node createMinDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelMinDuration" ) );

		return label;
	}

	private Node createMinDurationField( ) {
		minDuration = new TextField( );

		minDuration.setEditable( false );
		minDuration.getStyleClass( ).add( "details" );

		GridPane.setHgrow( minDuration, Priority.ALWAYS );

		return minDuration;
	}

	private Node createAvgDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelAvgDuration" ) );

		return label;
	}

	private Node createAvgDurationField( ) {
		avgDuration = new TextField( );

		avgDuration.setEditable( false );
		avgDuration.getStyleClass( ).add( "details" );

		GridPane.setHgrow( avgDuration, Priority.ALWAYS );

		return avgDuration;
	}

	private Node createMedianDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelMedianDuration" ) );

		return label;
	}

	private Node createMedianDurationField( ) {
		medianDuration = new TextField( );

		medianDuration.setEditable( false );
		medianDuration.getStyleClass( ).add( "details" );

		GridPane.setHgrow( medianDuration, Priority.ALWAYS );

		return medianDuration;
	}

	private Node createMaxDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelMaxDuration" ) );

		return label;
	}

	private Node createMaxDurationField( ) {
		maxDuration = new TextField( );

		maxDuration.setEditable( false );
		maxDuration.getStyleClass( ).add( "details" );

		GridPane.setHgrow( maxDuration, Priority.ALWAYS );

		return maxDuration;
	}

	private Node createTotalDurationLabel( ) {
		final Label label = new Label( );

		label.setText( RESOURCE_BUNDLE.getString( "labelTotalDuration" ) );

		return label;
	}

	private Node createTotalDurationField( ) {
		totalDuration = new TextField( );

		totalDuration.setEditable( false );
		totalDuration.getStyleClass( ).add( "details" );

		GridPane.setHgrow( totalDuration, Priority.ALWAYS );

		return totalDuration;
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
