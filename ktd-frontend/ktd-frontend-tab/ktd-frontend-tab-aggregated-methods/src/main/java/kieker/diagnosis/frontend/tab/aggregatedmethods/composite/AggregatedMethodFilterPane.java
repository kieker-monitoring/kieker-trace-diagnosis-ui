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

	private final TextField host;
	private final TextField clazz;
	private final TextField method;
	private final TextField exception;
	private final CheckBox useRegExpr;
	private final ComboBox<SearchType> searchType;

	private final Button searchButton;
	private final Hyperlink saveAsFavoriteLink;

	public AggregatedMethodFilterPane( ) {
		setText( RESOURCE_BUNDLE.getString( "filterTitle" ) );

		final GridPane outerGridPane = new GridPane( );
		outerGridPane.setHgap( 5 );
		outerGridPane.setVgap( 5 );

		for ( int i = 0; i < 2; i++ ) {
			final RowConstraints constraint = new RowConstraints( );
			constraint.setPercentHeight( 100.0 / 2 );
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

			{
				host = new TextField( );
				host.setId( "tabAggregatedMethodsFilterHost" );
				host.setPromptText( RESOURCE_BUNDLE.getString( "filterByHost" ) );
				GridPane.setColumnIndex( host, columnIndex++ );
				GridPane.setRowIndex( host, 0 );
				GridPane.setHgrow( host, Priority.ALWAYS );

				gridPane.getChildren( ).add( host );
			}

			{
				clazz = new TextField( );
				clazz.setId( "tabAggregatedMethodsFilterClass" );
				clazz.setPromptText( RESOURCE_BUNDLE.getString( "filterByClass" ) );
				GridPane.setColumnIndex( clazz, columnIndex++ );
				GridPane.setRowIndex( clazz, 0 );
				GridPane.setHgrow( clazz, Priority.ALWAYS );

				gridPane.getChildren( ).add( clazz );
			}

			{
				method = new TextField( );
				method.setId( "tabAggregatedMethodsFilterMethod" );
				method.setPromptText( RESOURCE_BUNDLE.getString( "filterByMethod" ) );
				GridPane.setColumnIndex( method, columnIndex++ );
				GridPane.setRowIndex( method, 0 );
				GridPane.setHgrow( method, Priority.ALWAYS );

				gridPane.getChildren( ).add( method );
			}

			{
				exception = new TextField( );
				exception.setId( "tabAggregatedMethodsFilterException" );
				exception.setPromptText( RESOURCE_BUNDLE.getString( "filterByException" ) );
				GridPane.setColumnIndex( exception, columnIndex++ );
				GridPane.setRowIndex( exception, 0 );
				GridPane.setHgrow( exception, Priority.ALWAYS );

				gridPane.getChildren( ).add( exception );
			}

			{
				searchType = new ComboBox<>( );
				searchType.setId( "tabAggregatedMethodsFilterSearchType" );
				searchType.setItems( FXCollections.observableArrayList( SearchType.values( ) ) );
				searchType.setConverter( new EnumStringConverter<>( SearchType.class ) );
				searchType.setMaxWidth( Double.POSITIVE_INFINITY );

				GridPane.setColumnIndex( searchType, columnIndex++ );
				GridPane.setRowIndex( searchType, 0 );

				gridPane.getChildren( ).add( searchType );
			}

			GridPane.setColumnIndex( gridPane, 0 );
			GridPane.setRowIndex( gridPane, 0 );
			GridPane.setHgrow( gridPane, Priority.ALWAYS );

			outerGridPane.getChildren( ).add( gridPane );
		}

		{
			saveAsFavoriteLink = new Hyperlink( );
			saveAsFavoriteLink.setText( RESOURCE_BUNDLE.getString( "saveAsFavorite" ) );

			GridPane.setColumnIndex( saveAsFavoriteLink, 0 );
			GridPane.setRowIndex( saveAsFavoriteLink, 1 );

			outerGridPane.getChildren( ).add( saveAsFavoriteLink );
		}

		{
			useRegExpr = new CheckBox( );
			useRegExpr.setId( "tabAggregatedMethodsFilterUseRegExpr" );
			useRegExpr.setText( RESOURCE_BUNDLE.getString( "filterUseRegExpr" ) );

			GridPane.setColumnIndex( useRegExpr, 1 );
			GridPane.setRowIndex( useRegExpr, 0 );
			GridPane.setValignment( useRegExpr, VPos.CENTER );

			outerGridPane.getChildren( ).add( useRegExpr );
		}

		{
			searchButton = new Button( );
			searchButton.setId( "tabAggregatedMethodsSearch" );
			searchButton.setText( RESOURCE_BUNDLE.getString( "search" ) );
			searchButton.setMinWidth( 140 );
			searchButton.setMaxWidth( Double.POSITIVE_INFINITY );
			searchButton.setGraphic( createIcon( Icon.SEARCH ) );

			GridPane.setColumnIndex( searchButton, 1 );
			GridPane.setRowIndex( searchButton, 1 );

			outerGridPane.getChildren( ).add( searchButton );
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
