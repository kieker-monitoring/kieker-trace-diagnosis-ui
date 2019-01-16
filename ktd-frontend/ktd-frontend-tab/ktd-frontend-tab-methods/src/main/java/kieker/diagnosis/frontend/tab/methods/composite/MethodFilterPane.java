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

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
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
import kieker.diagnosis.backend.data.AggregatedMethodCall;
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

	private TextField host;
	private TextField clazz;
	private TextField method;
	private TextField exception;
	private LongTextField traceId;
	private CheckBox useRegExpr;

	private DatePicker lowerDate;
	private LocalTimeTextField lowerTime;
	private DatePicker upperDate;
	private LocalTimeTextField upperTime;
	private ComboBox<SearchType> searchType;

	private Button searchButton;
	private Hyperlink saveAsFavoriteLink;

	public MethodFilterPane( ) {
		createControl( );
	}

	private void createControl( ) {
		setText( RESOURCE_BUNDLE.getString( "filterTitle" ) );
		setContent( createOuterGridPane( ) );
	}

	private Node createOuterGridPane( ) {
		final GridPane gridPane = new GridPane( );
		gridPane.setHgap( 5 );
		gridPane.setVgap( 5 );

		for ( int i = 0; i < 3; i++ ) {
			final RowConstraints constraint = new RowConstraints( );
			constraint.setPercentHeight( 100.0 / 3 );
			gridPane.getRowConstraints( ).add( constraint );
		}

		gridPane.add( createInputFieldsGridPane( ), 0, 0 );
		gridPane.add( createSaveAsFavoriteLink( ), 0, 2 );
		gridPane.add( createUseRegularExpressionField( ), 1, 0 );
		gridPane.add( createSearchButton( ), 1, 1 );

		return gridPane;
	}

	private Node createInputFieldsGridPane( ) {
		final GridPane gridPane = new GridPane( );
		gridPane.setHgap( 5 );
		gridPane.setVgap( 5 );

		for ( int i = 0; i < 5; i++ ) {
			final ColumnConstraints constraint = new ColumnConstraints( );
			constraint.setPercentWidth( 100.0 / 5 );
			gridPane.getColumnConstraints( ).add( constraint );
		}

		gridPane.add( createHostField( ), 0, 0 );
		gridPane.add( createClassField( ), 1, 0 );
		gridPane.add( createMethodField( ), 2, 0 );
		gridPane.add( createExceptionField( ), 3, 0 );
		gridPane.add( createTraceIdField( ), 4, 0 );

		gridPane.add( createLowerDateField( ), 0, 1 );
		gridPane.add( createLowerTimeField( ), 1, 1 );
		gridPane.add( createUpperDateField( ), 2, 1 );
		gridPane.add( createUpperTimeField( ), 3, 1 );
		gridPane.add( createSearchTypeField( ), 4, 1 );

		GridPane.setColumnIndex( gridPane, 0 );
		GridPane.setRowIndex( gridPane, 0 );
		GridPane.setRowSpan( gridPane, 2 );
		GridPane.setHgrow( gridPane, Priority.ALWAYS );

		return gridPane;
	}

	private Node createHostField( ) {
		host = new TextField( );

		host.setId( "tabMethodsFilterHost" );
		host.setPromptText( RESOURCE_BUNDLE.getString( "filterByHost" ) );

		GridPane.setHgrow( host, Priority.ALWAYS );

		return host;
	}

	private Node createClassField( ) {
		clazz = new TextField( );

		clazz.setId( "tabMethodsFilterClass" );
		clazz.setPromptText( RESOURCE_BUNDLE.getString( "filterByClass" ) );

		GridPane.setHgrow( clazz, Priority.ALWAYS );

		return clazz;
	}

	private Node createMethodField( ) {
		method = new TextField( );

		method.setId( "tabMethodsFilterMethod" );
		method.setPromptText( RESOURCE_BUNDLE.getString( "filterByMethod" ) );

		GridPane.setHgrow( method, Priority.ALWAYS );

		return method;
	}

	private Node createExceptionField( ) {
		exception = new TextField( );

		exception.setId( "tabMethodsFilterException" );
		exception.setPromptText( RESOURCE_BUNDLE.getString( "filterByException" ) );

		GridPane.setHgrow( exception, Priority.ALWAYS );

		return exception;
	}

	private Node createTraceIdField( ) {
		traceId = new LongTextField( );

		traceId.setPromptText( RESOURCE_BUNDLE.getString( "filterByTraceId" ) );
		GridPane.setHgrow( traceId, Priority.ALWAYS );

		return traceId;
	}

	private Node createLowerDateField( ) {
		lowerDate = new DatePicker( );

		lowerDate.setPromptText( RESOURCE_BUNDLE.getString( "filterByLowerDate" ) );
		lowerDate.setMaxWidth( Double.POSITIVE_INFINITY );

		GridPane.setHgrow( lowerDate, Priority.ALWAYS );

		return lowerDate;
	}

	private Node createLowerTimeField( ) {
		lowerTime = new LocalTimeTextField( );

		lowerTime.setPromptText( RESOURCE_BUNDLE.getString( "filterByLowerTime" ) );

		// The CalendarTimeTextField doesn't recognize the default button

		lowerTime.setOnKeyReleased( e -> {
			if ( e.getCode( ) == KeyCode.ENTER ) {
				searchButton.fire( );
			}
		} );

		GridPane.setHgrow( lowerTime, Priority.ALWAYS );

		return lowerTime;
	}

	private Node createUpperDateField( ) {
		upperDate = new DatePicker( );

		upperDate.setPromptText( RESOURCE_BUNDLE.getString( "filterByUpperDate" ) );
		upperDate.setMaxWidth( Double.POSITIVE_INFINITY );

		GridPane.setHgrow( upperDate, Priority.ALWAYS );

		return upperDate;
	}

	private Node createUpperTimeField( ) {
		upperTime = new LocalTimeTextField( );

		upperTime.setPromptText( RESOURCE_BUNDLE.getString( "filterByUpperTime" ) );

		// The CalendarTimeTextField doesn't recognize the default button

		upperTime.setOnKeyReleased( e -> {
			if ( e.getCode( ) == KeyCode.ENTER ) {
				searchButton.fire( );
			}
		} );

		GridPane.setHgrow( upperTime, Priority.ALWAYS );

		return upperTime;
	}

	private Node createSearchTypeField( ) {
		searchType = new ComboBox<>( );

		searchType.setId( "tabMethodsFilterSearchType" );
		searchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
		searchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
		searchType.setMaxWidth( Double.POSITIVE_INFINITY );

		return searchType;
	}

	private Node createSaveAsFavoriteLink( ) {
		saveAsFavoriteLink = new Hyperlink( );

		saveAsFavoriteLink.setId( "tabMethodsFilteSaveAsFavorite" );
		saveAsFavoriteLink.setText( RESOURCE_BUNDLE.getString( "saveAsFavorite" ) );

		return saveAsFavoriteLink;
	}

	private Node createUseRegularExpressionField( ) {
		useRegExpr = new CheckBox( );

		useRegExpr.setId( "tabMethodsFilterUseRegExpr" );
		useRegExpr.setText( RESOURCE_BUNDLE.getString( "filterUseRegExpr" ) );

		GridPane.setValignment( useRegExpr, VPos.CENTER );

		return useRegExpr;
	}

	private Node createSearchButton( ) {
		searchButton = new Button( );

		searchButton.setId( "tabMethodsSearch" );
		searchButton.setText( RESOURCE_BUNDLE.getString( "search" ) );
		searchButton.setMinWidth( 140 );
		searchButton.setMaxWidth( Double.POSITIVE_INFINITY );
		searchButton.setGraphic( createIcon( Icon.SEARCH ) );

		return searchButton;
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
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Must not be {@code null}.
	 */
	public void setValue( final AggregatedMethodCall value ) {
		// We have to prepare a filter which matches only the method call
		final MethodsFilter filter = new MethodsFilter( );
		filter.setHost( value.getHost( ) );
		filter.setClazz( value.getClazz( ) );
		filter.setMethod( value.getMethod( ) );
		filter.setException( value.getException( ) );
		filter.setSearchType( value.getException( ) != null ? SearchType.ONLY_FAILED : SearchType.ONLY_SUCCESSFUL );

		setValue( filter );
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
	 *
	 * @return The default button property.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return searchButton.defaultButtonProperty( );
	}

}
