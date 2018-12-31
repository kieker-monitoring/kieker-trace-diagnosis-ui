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
import kieker.diagnosis.backend.search.traces.SearchType;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.frontend.base.atom.LongTextField;
import kieker.diagnosis.frontend.base.mixin.IconMixin;
import kieker.diagnosis.frontend.base.mixin.StringMixin;
import kieker.diagnosis.frontend.base.ui.EnumStringConverter;

/**
 * This component represents the trace filter on the UI.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceFilterPane extends TitledPane implements StringMixin, IconMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( TraceFilterPane.class.getName( ) );

	private final TextField ivFilterHost;
	private final TextField ivFilterClass;
	private final TextField ivFilterMethod;
	private final TextField ivFilterException;
	private final LongTextField ivFilterTraceId;
	private final CheckBox ivFilterUseRegExpr;

	private final DatePicker ivFilterLowerDate;
	private final LocalTimeTextField ivFilterLowerTime;
	private final DatePicker ivFilterUpperDate;
	private final LocalTimeTextField ivFilterUpperTime;
	private final CheckBox ivFilterSearchWholeTrace;
	private final ComboBox<SearchType> ivFilterSearchType;

	private final Button searchButton;
	private final Hyperlink saveAsFavoriteLink;

	public TraceFilterPane( ) {
		setText( RESOURCE_BUNDLE.getString( "filterTitle" ) );

		{
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
					ivFilterHost = new TextField( );
					ivFilterHost.setId( "tabTracesFilterHost" );
					ivFilterHost.setPromptText( RESOURCE_BUNDLE.getString( "filterByHost" ) );
					GridPane.setColumnIndex( ivFilterHost, columnIndex++ );
					GridPane.setRowIndex( ivFilterHost, rowIndex );
					GridPane.setHgrow( ivFilterHost, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterHost );
				}

				{
					ivFilterClass = new TextField( );
					ivFilterClass.setPromptText( RESOURCE_BUNDLE.getString( "filterByClass" ) );
					GridPane.setColumnIndex( ivFilterClass, columnIndex++ );
					GridPane.setRowIndex( ivFilterClass, rowIndex );
					GridPane.setHgrow( ivFilterClass, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterClass );
				}

				{
					ivFilterMethod = new TextField( );
					ivFilterMethod.setPromptText( RESOURCE_BUNDLE.getString( "filterByMethod" ) );
					GridPane.setColumnIndex( ivFilterMethod, columnIndex++ );
					GridPane.setRowIndex( ivFilterMethod, rowIndex );
					GridPane.setHgrow( ivFilterMethod, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterMethod );
				}

				{
					ivFilterException = new TextField( );
					ivFilterException.setPromptText( RESOURCE_BUNDLE.getString( "filterByException" ) );
					GridPane.setColumnIndex( ivFilterException, columnIndex++ );
					GridPane.setRowIndex( ivFilterException, rowIndex );
					GridPane.setHgrow( ivFilterException, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterException );
				}

				{
					ivFilterTraceId = new LongTextField( );
					ivFilterTraceId.setPromptText( RESOURCE_BUNDLE.getString( "filterByTraceId" ) );
					GridPane.setColumnIndex( ivFilterTraceId, columnIndex++ );
					GridPane.setRowIndex( ivFilterTraceId, rowIndex );
					GridPane.setHgrow( ivFilterTraceId, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterTraceId );
				}

				columnIndex = 0;
				rowIndex++;

				{
					ivFilterLowerDate = new DatePicker( );
					ivFilterLowerDate.setPromptText( RESOURCE_BUNDLE.getString( "filterByLowerDate" ) );
					ivFilterLowerDate.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setColumnIndex( ivFilterLowerDate, columnIndex++ );
					GridPane.setRowIndex( ivFilterLowerDate, rowIndex );
					GridPane.setHgrow( ivFilterLowerDate, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterLowerDate );
				}

				{
					ivFilterLowerTime = new LocalTimeTextField( );
					ivFilterLowerTime.setPromptText( RESOURCE_BUNDLE.getString( "filterByLowerTime" ) );

					GridPane.setColumnIndex( ivFilterLowerTime, columnIndex++ );
					GridPane.setRowIndex( ivFilterLowerTime, rowIndex );
					GridPane.setHgrow( ivFilterLowerTime, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterLowerTime );
				}

				{
					ivFilterUpperDate = new DatePicker( );
					ivFilterUpperDate.setPromptText( RESOURCE_BUNDLE.getString( "filterByUpperDate" ) );
					ivFilterUpperDate.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setColumnIndex( ivFilterUpperDate, columnIndex++ );
					GridPane.setRowIndex( ivFilterUpperDate, rowIndex );
					GridPane.setHgrow( ivFilterUpperDate, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterUpperDate );
				}

				{
					ivFilterUpperTime = new LocalTimeTextField( );
					ivFilterUpperTime.setPromptText( RESOURCE_BUNDLE.getString( "filterByUpperTime" ) );

					GridPane.setColumnIndex( ivFilterUpperTime, columnIndex++ );
					GridPane.setRowIndex( ivFilterUpperTime, rowIndex );
					GridPane.setHgrow( ivFilterUpperTime, Priority.ALWAYS );

					gridPane.getChildren( ).add( ivFilterUpperTime );
				}

				{
					ivFilterSearchType = new ComboBox<>( );
					ivFilterSearchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
					ivFilterSearchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
					ivFilterSearchType.setMaxWidth( Double.POSITIVE_INFINITY );

					GridPane.setColumnIndex( ivFilterSearchType, columnIndex++ );
					GridPane.setRowIndex( ivFilterSearchType, rowIndex );

					gridPane.getChildren( ).add( ivFilterSearchType );
				}

				GridPane.setColumnIndex( gridPane, 0 );
				GridPane.setRowIndex( gridPane, 0 );
				GridPane.setRowSpan( gridPane, 2 );
				GridPane.setHgrow( gridPane, Priority.ALWAYS );

				outerGridPane.getChildren( ).add( gridPane );
			}

			{
				ivFilterUseRegExpr = new CheckBox( );
				ivFilterUseRegExpr.setText( RESOURCE_BUNDLE.getString( "filterUseRegExpr" ) );

				GridPane.setColumnIndex( ivFilterUseRegExpr, 1 );
				GridPane.setRowIndex( ivFilterUseRegExpr, 0 );
				GridPane.setValignment( ivFilterUseRegExpr, VPos.CENTER );

				outerGridPane.getChildren( ).add( ivFilterUseRegExpr );
			}

			{
				ivFilterSearchWholeTrace = new CheckBox( );
				ivFilterSearchWholeTrace.setText( RESOURCE_BUNDLE.getString( "filterSearchWholeTrace" ) );

				GridPane.setColumnIndex( ivFilterSearchWholeTrace, 1 );
				GridPane.setRowIndex( ivFilterSearchWholeTrace, 1 );
				GridPane.setValignment( ivFilterSearchWholeTrace, VPos.CENTER );

				outerGridPane.getChildren( ).add( ivFilterSearchWholeTrace );
			}

			{
				saveAsFavoriteLink = new Hyperlink( );
				saveAsFavoriteLink.setText( RESOURCE_BUNDLE.getString( "saveAsFavorite" ) );

				GridPane.setColumnIndex( saveAsFavoriteLink, 0 );
				GridPane.setRowIndex( saveAsFavoriteLink, 2 );

				outerGridPane.getChildren( ).add( saveAsFavoriteLink );
			}

			{
				searchButton = new Button( );
				searchButton.setId( "tabTracesSearch" );
				searchButton.setText( RESOURCE_BUNDLE.getString( "search" ) );
				searchButton.setMinWidth( 140 );
				searchButton.setMaxWidth( Double.POSITIVE_INFINITY );
				searchButton.setGraphic( createIcon( Icon.SEARCH ) );

				GridPane.setColumnIndex( searchButton, 1 );
				GridPane.setRowIndex( searchButton, 2 );

				outerGridPane.getChildren( ).add( searchButton );
			}

			setContent( outerGridPane );

		}

		{
			// The CalendarTimeTextField doesn't recognize the default button

			ivFilterLowerTime.setOnKeyReleased( e -> {
				if ( e.getCode( ) == KeyCode.ENTER ) {
					searchButton.fire( );
				}
			} );

			ivFilterUpperTime.setOnKeyReleased( e -> {
				if ( e.getCode( ) == KeyCode.ENTER ) {
					searchButton.fire( );
				}
			} );
		}
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
	 * Returns the default button property of the search button.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return searchButton.defaultButtonProperty( );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Must not be {@code null}.
	 */
	public void setValue( final TracesFilter filter ) {
		ivFilterHost.setText( filter.getHost( ) );
		ivFilterClass.setText( filter.getClazz( ) );
		ivFilterMethod.setText( filter.getMethod( ) );
		ivFilterException.setText( filter.getException( ) );
		ivFilterTraceId.setText( filter.getTraceId( ) != null ? Long.toString( filter.getTraceId( ) ) : null );
		ivFilterUseRegExpr.setSelected( filter.isUseRegExpr( ) );
		ivFilterSearchWholeTrace.setSelected( filter.isSearchWholeTrace( ) );
		ivFilterLowerDate.setValue( filter.getLowerDate( ) );
		ivFilterLowerTime.setLocalTime( filter.getLowerTime( ) );
		ivFilterUpperDate.setValue( filter.getUpperDate( ) );
		ivFilterUpperTime.setLocalTime( filter.getUpperTime( ) );
		ivFilterSearchType.setValue( filter.getSearchType( ) );
	}

	/**
	 * Returns the current value of the component.
	 *
	 * @return The current value.
	 */
	public TracesFilter getValue( ) {
		final TracesFilter filter = new TracesFilter( );

		filter.setHost( trimToNull( ivFilterHost.getText( ) ) );
		filter.setClazz( trimToNull( ivFilterClass.getText( ) ) );
		filter.setMethod( trimToNull( ivFilterMethod.getText( ) ) );
		filter.setException( trimToNull( ivFilterException.getText( ) ) );
		filter.setUseRegExpr( ivFilterUseRegExpr.isSelected( ) );
		filter.setSearchWholeTrace( ivFilterSearchWholeTrace.isSelected( ) );
		filter.setLowerDate( ivFilterLowerDate.getValue( ) );
		filter.setLowerTime( ivFilterLowerTime.getLocalTime( ) );
		filter.setUpperDate( ivFilterUpperDate.getValue( ) );
		filter.setUpperTime( ivFilterUpperTime.getLocalTime( ) );
		filter.setSearchType( ivFilterSearchType.getValue( ) );
		filter.setTraceId( ivFilterTraceId.getValue( ) );

		return filter;
	}

}
