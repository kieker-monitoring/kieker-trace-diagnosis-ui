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

package kieker.diagnosis.frontend.tab.aggregatedmethods.complex;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.pattern.PatternService;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsFilter;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsService;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.dialog.alert.Alert;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodDetailsPane;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodFilterPane;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodStatusBar;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodsTableView;

public final class AggregatedMethodsTab extends Tab {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AggregatedMethodsTab.class.getName( ) );

	private final AggregatedMethodFilterPane filterPane = new AggregatedMethodFilterPane( );
	private final AggregatedMethodsTableView methodsTableView = new AggregatedMethodsTableView( );
	private final AggregatedMethodDetailsPane detailsPane = new AggregatedMethodDetailsPane( );
	private final AggregatedMethodStatusBar statusBar = new AggregatedMethodStatusBar( );

	private List<AggregatedMethodCall> methodsForRefresh;
	private int totalMethodsForRefresh;
	private String durationSuffixForRefresh;

	private Consumer<AggregatedMethodsFilter> onSaveAsFavorite;
	private Consumer<AggregatedMethodCall> onJumpToMethods;
	private Consumer<CSVData> onExportToCSV;

	public AggregatedMethodsTab( ) {
		final VBox vbox = new VBox( );
		setContent( vbox );

		configureFilterPane( );
		vbox.getChildren( ).add( filterPane );

		configureMethodsTableView( );
		vbox.getChildren( ).add( methodsTableView );

		configureDetailsPane( );
		vbox.getChildren( ).add( detailsPane );

		configureStatusBar( );
		vbox.getChildren( ).add( statusBar );

		performInitialize( );
	}

	private void configureFilterPane( ) {
		filterPane.setOnSearch( e -> performSearch( ) );
		filterPane.setOnSaveAsFavorite( e -> performSaveAsFavorite( ) );
	}

	private void configureMethodsTableView( ) {
		methodsTableView.setId( "tabAggregatedMethodsTable" );
		methodsTableView.addSelectionChangeListener( ( aObservable, aOldValue, aNewValue ) -> detailsPane.setValue( aNewValue ) );

		VBox.setVgrow( methodsTableView, Priority.ALWAYS );
	}

	private void configureDetailsPane( ) {
		detailsPane.setOnJumpToMethods( e -> performJumpToMethods( ) );
	}

	private void configureStatusBar( ) {
		statusBar.setOnExportToCsv( e -> performExportToCSV( ) );

		VBox.setMargin( statusBar, new Insets( 5 ) );
	}

	private void performInitialize( ) {
		statusBar.setValue( 0, 0 );
		filterPane.setValue( new AggregatedMethodsFilter( ) );
		detailsPane.setValue( null );
	}

	public void prepareRefresh( ) {
		// Get the data
		final AggregatedMethodsService methodsService = ServiceFactory.getService( AggregatedMethodsService.class );
		methodsForRefresh = methodsService.searchMethods( new AggregatedMethodsFilter( ) );
		totalMethodsForRefresh = methodsService.countMethods( );

		final SettingsService settingsService = ServiceFactory.getService( SettingsService.class );
		durationSuffixForRefresh = settingsService.getCurrentDurationSuffix( );
	}

	public void performRefresh( ) {
		// Reset the filter
		final AggregatedMethodsFilter filter = new AggregatedMethodsFilter( );
		filterPane.setValue( filter );

		// Update the table
		methodsTableView.setItems( FXCollections.observableList( methodsForRefresh ) );
		statusBar.setValue( methodsForRefresh.size( ), totalMethodsForRefresh );

		// Update the column header of the table
		methodsTableView.setDurationSuffix( durationSuffixForRefresh );
	}

	/**
	 * Returns the default button property of the search button.
	 *
	 * @return The default button property.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	public void performSearch( ) {
		// Get the filter input from the user
		final AggregatedMethodsFilter filter = filterPane.getValue( );
		if ( checkFilter( filter ) ) {

			// Find the methods to display
			final AggregatedMethodsService methodsService = ServiceFactory.getService( AggregatedMethodsService.class );
			final List<AggregatedMethodCall> methods = methodsService.searchMethods( filter );
			final int totalMethods = methodsService.countMethods( );

			// Update the view
			methodsTableView.setItems( FXCollections.observableList( methods ) );
			statusBar.setValue( methods.size( ), totalMethods );
		}
	}

	public void setOnSaveAsFavorite( final Consumer<AggregatedMethodsFilter> action ) {
		onSaveAsFavorite = action;
	}

	public void performSaveAsFavorite( ) {
		final AggregatedMethodsFilter filter = filterPane.getValue( );
		if ( checkFilter( filter ) ) {
			onSaveAsFavorite.accept( filter );
		}
	}

	private boolean checkFilter( final AggregatedMethodsFilter filter ) {
		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
			final PatternService patternService = ServiceFactory.getService( PatternService.class );

			final StringBuilder strBuilder = new StringBuilder( );

			if ( !( patternService.isValidPattern( filter.getHost( ) ) || filter.getHost( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getHost( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getClazz( ) ) || filter.getClazz( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getClazz( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getMethod( ) ) || filter.getMethod( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getMethod( ) ) );
			}

			if ( !( patternService.isValidPattern( filter.getException( ) ) || filter.getException( ) == null ) ) {
				strBuilder.append( "\n* " ).append( String.format( RESOURCE_BUNDLE.getString( "errorMessageRegExpr" ), filter.getException( ) ) );
			}

			if ( strBuilder.length( ) > 0 ) {
				final Alert alert = new Alert( AlertType.WARNING );
				final String msg = RESOURCE_BUNDLE.getString( "errorMessageInvalidInput" ) + strBuilder.toString( );
				alert.setContentText( msg );
				alert.showAndWait( );

				return false;
			}
		}

		return true;
	}

	public void setOnJumpToMethods( final Consumer<AggregatedMethodCall> action ) {
		onJumpToMethods = action;
	}

	public void performJumpToMethods( ) {
		final AggregatedMethodCall methodCall = methodsTableView.getSelectionModel( ).getSelectedItem( );

		if ( methodCall != null ) {
			onJumpToMethods.accept( methodCall );
		}
	}

	public void setOnExportToCSV( final Consumer<CSVData> action ) {
		onExportToCSV = action;
	}

	public void performExportToCSV( ) {
		final CSVData csvData = methodsTableView.getValueAsCsv( );
		onExportToCSV.accept( csvData );
	}

	public void setFilterValue( final AggregatedMethodsFilter filter ) {
		filterPane.setValue( filter );
		performSearch( );
	}

}
