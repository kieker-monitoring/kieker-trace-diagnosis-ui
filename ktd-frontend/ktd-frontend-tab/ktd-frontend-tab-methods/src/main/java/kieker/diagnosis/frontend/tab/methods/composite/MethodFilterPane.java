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

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import jfxtras.scene.control.LocalTimeTextField;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.backend.search.methods.SearchType;
import kieker.diagnosis.frontend.base.atom.LongTextField;
import kieker.diagnosis.frontend.base.mixin.IconMixin;
import kieker.diagnosis.frontend.base.mixin.StringMixin;
import kieker.diagnosis.frontend.base.ui.EnumStringConverter;

/**
 * This component represents the method filter on the UI.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodFilterPane extends TitledPane implements IconMixin, StringMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MethodFilterPane.class.getName( ) );

	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final LongTextField traceId;
	private final CheckBox useRegExpr;

	private final DatePicker lowerDate;
	private final LocalTimeTextField lowerTime;
	private final DatePicker upperDate;
	private final LocalTimeTextField upperTime;
	private final ComboBox<SearchType> searchType;

	private final Button searchButton;
	private final Hyperlink saveAsFavoriteLink;

	public MethodFilterPane( ) {
		setText( RESOURCE_BUNDLE.getString( "filterTitle" ) );

		final GridPane outerGridPane = new GridPane( );
		outerGridPane.setHgap( 5 );
		outerGridPane.setVgap( 5 );

		for ( int i = 0; i < 3; i++ ) {
			final RowConstraints constraint = new RowConstraints( );
			constraint.setPercentHeight( 100.0 / 3 );
			outerGridPane.getRowConstraints( ).add( constraint );
		}

		{
			final GridPane gridPane = new GridPane( );
			gridPane.setHgap( 5 );
			gridPane.setVgap( 5 );

			for ( int i = 0; i < 5; i++ ) {
				final ColumnConstraints constraint = new ColumnConstraints( );
				constraint.setPercentWidth( 100.0 / 5 );
				gridPane.getColumnConstraints( ).add( constraint );
			}

			int columnIndex = 0;
			int rowIndex = 0;

			{
				host = new TextField( );
				host.setId( "tabMethodsFilterHost" );
				host.setPromptText( RESOURCE_BUNDLE.getString( "filterByHost" ) );
				GridPane.setColumnIndex( host, columnIndex++ );
				GridPane.setRowIndex( host, rowIndex );
				GridPane.setHgrow( host, Priority.ALWAYS );

				gridPane.getChildren( ).add( host );
			}

			{
				clazz = new TextField( );
				clazz.setPromptText( RESOURCE_BUNDLE.getString( "filterByClass" ) );
				GridPane.setColumnIndex( clazz, columnIndex++ );
				GridPane.setRowIndex( clazz, rowIndex );
				GridPane.setHgrow( clazz, Priority.ALWAYS );

				gridPane.getChildren( ).add( clazz );
			}

			{
				method = new TextField( );
				method.setPromptText( RESOURCE_BUNDLE.getString( "filterByMethod" ) );
				GridPane.setColumnIndex( method, columnIndex++ );
				GridPane.setRowIndex( method, rowIndex );
				GridPane.setHgrow( method, Priority.ALWAYS );

				gridPane.getChildren( ).add( method );
			}

			{
				exception = new TextField( );
				exception.setPromptText( RESOURCE_BUNDLE.getString( "filterByException" ) );
				GridPane.setColumnIndex( exception, columnIndex++ );
				GridPane.setRowIndex( exception, rowIndex );
				GridPane.setHgrow( exception, Priority.ALWAYS );

				gridPane.getChildren( ).add( exception );
			}

			{
				traceId = new LongTextField( );
				traceId.setPromptText( RESOURCE_BUNDLE.getString( "filterByTraceId" ) );
				GridPane.setColumnIndex( traceId, columnIndex++ );
				GridPane.setRowIndex( traceId, rowIndex );
				GridPane.setHgrow( traceId, Priority.ALWAYS );

				gridPane.getChildren( ).add( traceId );
			}

			columnIndex = 0;
			rowIndex++;

			{
				lowerDate = new DatePicker( );
				lowerDate.setPromptText( RESOURCE_BUNDLE.getString( "filterByLowerDate" ) );
				lowerDate.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setColumnIndex( lowerDate, columnIndex++ );
				GridPane.setRowIndex( lowerDate, rowIndex );
				GridPane.setHgrow( lowerDate, Priority.ALWAYS );

				gridPane.getChildren( ).add( lowerDate );
			}

			{
				lowerTime = new LocalTimeTextField( );
				lowerTime.setPromptText( RESOURCE_BUNDLE.getString( "filterByLowerTime" ) );

				GridPane.setColumnIndex( lowerTime, columnIndex++ );
				GridPane.setRowIndex( lowerTime, rowIndex );
				GridPane.setHgrow( lowerTime, Priority.ALWAYS );

				gridPane.getChildren( ).add( lowerTime );
			}

			{
				upperDate = new DatePicker( );
				upperDate.setPromptText( RESOURCE_BUNDLE.getString( "filterByUpperDate" ) );
				upperDate.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setColumnIndex( upperDate, columnIndex++ );
				GridPane.setRowIndex( upperDate, rowIndex );
				GridPane.setHgrow( upperDate, Priority.ALWAYS );

				gridPane.getChildren( ).add( upperDate );
			}

			{
				upperTime = new LocalTimeTextField( );
				upperTime.setPromptText( RESOURCE_BUNDLE.getString( "filterByUpperTime" ) );

				GridPane.setColumnIndex( upperTime, columnIndex++ );
				GridPane.setRowIndex( upperTime, rowIndex );
				GridPane.setHgrow( upperTime, Priority.ALWAYS );

				gridPane.getChildren( ).add( upperTime );
			}

			{
				searchType = new ComboBox<>( );
				searchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
				searchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
				searchType.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setColumnIndex( searchType, columnIndex++ );
				GridPane.setRowIndex( searchType, rowIndex );

				gridPane.getChildren( ).add( searchType );
			}

			GridPane.setColumnIndex( gridPane, 0 );
			GridPane.setRowIndex( gridPane, 0 );
			GridPane.setRowSpan( gridPane, 2 );
			GridPane.setHgrow( gridPane, Priority.ALWAYS );

			outerGridPane.getChildren( ).add( gridPane );
		}

		{
			saveAsFavoriteLink = new Hyperlink( );
			saveAsFavoriteLink.setText( RESOURCE_BUNDLE.getString( "saveAsFavorite" ) );

			GridPane.setColumnIndex( saveAsFavoriteLink, 0 );
			GridPane.setRowIndex( saveAsFavoriteLink, 2 );

			outerGridPane.getChildren( ).add( saveAsFavoriteLink );
		}

		{
			useRegExpr = new CheckBox( );
			useRegExpr.setText( RESOURCE_BUNDLE.getString( "filterUseRegExpr" ) );

			GridPane.setColumnIndex( useRegExpr, 1 );
			GridPane.setRowIndex( useRegExpr, 0 );
			GridPane.setValignment( useRegExpr, VPos.CENTER );

			outerGridPane.getChildren( ).add( useRegExpr );
		}

		{
			searchButton = new Button( );
			searchButton.setId( "tabMethodsSearch" );
			searchButton.setText( RESOURCE_BUNDLE.getString( "search" ) );
			searchButton.setMinWidth( 140 );
			searchButton.setMaxWidth( Double.POSITIVE_INFINITY );
			searchButton.setGraphic( createIcon( Icon.SEARCH ) );

			GridPane.setColumnIndex( searchButton, 1 );
			GridPane.setRowIndex( searchButton, 2 );

			outerGridPane.getChildren( ).add( searchButton );
		}

		{
			// The CalendarTimeTextField doesn't recognize the default button

			lowerTime.setOnKeyReleased( e -> {
				if ( e.getCode( ) == KeyCode.ENTER ) {
					searchButton.fire( );
				}
			} );

			upperTime.setOnKeyReleased( e -> {
				if ( e.getCode( ) == KeyCode.ENTER ) {
					searchButton.fire( );
				}
			} );
		}

		setContent( outerGridPane );
	}

	/**
	 * Sets the action which is performed when the user wants to search for methods.
	 *
	 * @param value
	 *            The action.
	 */
	public void setOnSearch( final EventHandler<ActionEvent> value ) {
		searchButton.setOnAction( value );
	}

	/**
	 * Sets the action which is performed when the user wants to save the filter as favorite.
	 *
	 * @param value
	 *            The action.
	 */
	public void setOnSaveAsFavorite( final EventHandler<ActionEvent> value ) {
		saveAsFavoriteLink.setOnAction( value );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Must not be {@code null}.
	 */
	public void setValue( final MethodsFilter value ) {
		host.setText( value.getHost( ) );
		clazz.setText( value.getClazz( ) );
		method.setText( value.getMethod( ) );
		exception.setText( value.getException( ) );
		searchType.setValue( value.getSearchType( ) );
		traceId.setText( value.getTraceId( ) != null ? Long.toString( value.getTraceId( ) ) : null );
		useRegExpr.setSelected( value.isUseRegExpr( ) );
		lowerDate.setValue( value.getLowerDate( ) );
		lowerTime.setLocalTime( value.getLowerTime( ) );
		upperDate.setValue( value.getUpperDate( ) );
		upperTime.setLocalTime( value.getUpperTime( ) );
	}

	/**
	 * Returns the current value of the component.
	 *
	 * @return The current value.
	 */
	public MethodsFilter getValue( ) {
		final MethodsFilter filter = new MethodsFilter( );

		filter.setHost( trimToNull( host.getText( ) ) );
		filter.setClazz( trimToNull( clazz.getText( ) ) );
		filter.setMethod( trimToNull( method.getText( ) ) );
		filter.setException( trimToNull( exception.getText( ) ) );
		filter.setSearchType( searchType.getValue( ) );
		filter.setUseRegExpr( useRegExpr.isSelected( ) );
		filter.setLowerDate( lowerDate.getValue( ) );
		filter.setLowerTime( lowerTime.getLocalTime( ) );
		filter.setUpperDate( upperDate.getValue( ) );
		filter.setUpperTime( upperTime.getLocalTime( ) );
		filter.setTraceId( traceId.getValue( ) );

		return filter;
	}

	/**
	 * Returns the default button property of the search button.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return searchButton.defaultButtonProperty( );
	}

}
