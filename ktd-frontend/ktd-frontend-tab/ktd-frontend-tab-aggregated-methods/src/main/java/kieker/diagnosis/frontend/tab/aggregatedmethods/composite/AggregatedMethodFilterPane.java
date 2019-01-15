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

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsFilter;
import kieker.diagnosis.backend.search.aggregatedmethods.SearchType;
import kieker.diagnosis.frontend.base.mixin.IconMixin;
import kieker.diagnosis.frontend.base.mixin.StringMixin;
import kieker.diagnosis.frontend.base.ui.EnumStringConverter;

/**
 * This component represents the aggregated method filter on the UI.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodFilterPane extends TitledPane implements StringMixin, IconMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AggregatedMethodFilterPane.class.getName( ) );

	private TextField host;
	private TextField clazz;
	private TextField method;
	private TextField exception;
	private CheckBox useRegExpr;
	private ComboBox<SearchType> searchType;

	private Button searchButton;
	private Hyperlink saveAsFavoriteLink;

	public AggregatedMethodFilterPane( ) {
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

		for ( int i = 0; i < 2; i++ ) {
			final RowConstraints constraint = new RowConstraints( );
			constraint.setPercentHeight( 100.0 / 2 );
			gridPane.getRowConstraints( ).add( constraint );
		}

		gridPane.add( createInputFieldsGridPane( ), 0, 0 );
		gridPane.add( createSaveAsFavoriteLink( ), 0, 1 );
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
		gridPane.add( createSearchTypeField( ), 4, 0 );

		GridPane.setHgrow( gridPane, Priority.ALWAYS );

		return gridPane;
	}

	private Node createHostField( ) {
		host = new TextField( );

		host.setId( "tabAggregatedMethodsFilterHost" );
		host.setPromptText( RESOURCE_BUNDLE.getString( "filterByHost" ) );
		GridPane.setHgrow( host, Priority.ALWAYS );

		return host;
	}

	private Node createClassField( ) {
		clazz = new TextField( );

		clazz.setId( "tabAggregatedMethodsFilterClass" );
		clazz.setPromptText( RESOURCE_BUNDLE.getString( "filterByClass" ) );
		GridPane.setHgrow( clazz, Priority.ALWAYS );

		return clazz;
	}

	private Node createMethodField( ) {
		method = new TextField( );

		method.setId( "tabAggregatedMethodsFilterMethod" );
		method.setPromptText( RESOURCE_BUNDLE.getString( "filterByMethod" ) );
		GridPane.setHgrow( method, Priority.ALWAYS );

		return method;
	}

	private Node createExceptionField( ) {
		exception = new TextField( );

		exception.setId( "tabAggregatedMethodsFilterException" );
		exception.setPromptText( RESOURCE_BUNDLE.getString( "filterByException" ) );
		GridPane.setHgrow( exception, Priority.ALWAYS );

		return exception;
	}

	private Node createSearchTypeField( ) {
		searchType = new ComboBox<>( );

		searchType.setId( "tabAggregatedMethodsFilterSearchType" );
		searchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
		searchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
		searchType.setMaxWidth( Double.POSITIVE_INFINITY );

		return searchType;
	}

	private Node createSaveAsFavoriteLink( ) {
		saveAsFavoriteLink = new Hyperlink( );
		saveAsFavoriteLink.setText( RESOURCE_BUNDLE.getString( "saveAsFavorite" ) );

		return saveAsFavoriteLink;
	}

	private Node createUseRegularExpressionField( ) {
		useRegExpr = new CheckBox( );

		useRegExpr.setId( "tabAggregatedMethodsFilterUseRegExpr" );
		useRegExpr.setText( RESOURCE_BUNDLE.getString( "filterUseRegExpr" ) );

		GridPane.setValignment( useRegExpr, VPos.CENTER );

		return useRegExpr;
	}

	private Node createSearchButton( ) {
		searchButton = new Button( );

		searchButton.setId( "tabAggregatedMethodsSearch" );
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
	public void setValue( final AggregatedMethodsFilter value ) {
		host.setText( value.getHost( ) );
		clazz.setText( value.getClazz( ) );
		method.setText( value.getMethod( ) );
		exception.setText( value.getException( ) );
		useRegExpr.setSelected( value.isUseRegExpr( ) );
		searchType.setValue( value.getSearchType( ) );
	}

	/**
	 * Returns the current value of the component.
	 *
	 * @return The current value.
	 */
	public AggregatedMethodsFilter getValue( ) {
		final AggregatedMethodsFilter filter = new AggregatedMethodsFilter( );

		filter.setHost( trimToNull( host.getText( ) ) );
		filter.setClazz( trimToNull( clazz.getText( ) ) );
		filter.setMethod( trimToNull( method.getText( ) ) );
		filter.setException( trimToNull( exception.getText( ) ) );
		filter.setUseRegExpr( useRegExpr.isSelected( ) );
		filter.setSearchType( searchType.getValue( ) );

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
