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

package kieker.diagnosis.frontend.tab.methods.complex;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.google.inject.Inject;

import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.pattern.PatternService;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.backend.search.methods.MethodsService;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.mixin.CdiMixin;
import kieker.diagnosis.frontend.dialog.alert.Alert;
import kieker.diagnosis.frontend.tab.methods.composite.MethodDetailsPane;
import kieker.diagnosis.frontend.tab.methods.composite.MethodFilterPane;
import kieker.diagnosis.frontend.tab.methods.composite.MethodStatusBar;
import kieker.diagnosis.frontend.tab.methods.composite.MethodsTableView;

public final class MethodsTab extends Tab implements CdiMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MethodsTab.class.getName( ) );

	@Inject
	private MethodsService methodsService;

	@Inject
	private SettingsService settingsService;

	@Inject
	private PatternService patternService;

	private MethodFilterPane filterPane;
	private MethodsTableView methodsTableView;
	private MethodDetailsPane detailsPane;
	private MethodStatusBar statusBar;

	private Consumer<CSVData> onExportToCSV;
	private Consumer<MethodCall> onJumpToTrace;
	private Consumer<MethodsFilter> onSaveAsFavorite;

	private List<MethodCall> methodsForRefresh;
	private int totalMethodsForRefresh;
	private String durationSuffixForRefresh;

	public MethodsTab( ) {
		injectFields( );
		createControl( );
		performInitialize( );
	}

	private void createControl( ) {
		setContent( createVBox( ) );
	}

	private VBox createVBox( ) {
		final VBox vbox = new VBox( );

		vbox.getChildren( ).add( createFilterPane( ) );
		vbox.getChildren( ).add( createMethodsTableView( ) );
		vbox.getChildren( ).add( createDetailsPane( ) );
		vbox.getChildren( ).add( createStatusBar( ) );

		return vbox;
	}

	private Node createFilterPane( ) {
		filterPane = new MethodFilterPane( );

		filterPane.setOnSearch( e -> performSearch( ) );
		filterPane.setOnSaveAsFavorite( e -> performSaveAsFavorite( ) );

		return filterPane;
	}

	private Node createMethodsTableView( ) {
		methodsTableView = new MethodsTableView( );

		methodsTableView.setId( "tabMethodsTable" );
		methodsTableView.addSelectionChangeListener( ( observable, oldValue, newValue ) -> detailsPane.setValue( newValue ) );

		VBox.setVgrow( methodsTableView, Priority.ALWAYS );

		return methodsTableView;
	}

	private Node createDetailsPane( ) {
		detailsPane = new MethodDetailsPane( );

		detailsPane.setOnJumpToTrace( e -> performJumpToTrace( ) );

		return detailsPane;
	}

	private Node createStatusBar( ) {
		statusBar = new MethodStatusBar( );

		statusBar.setOnExportToCsv( e -> performExportToCSV( ) );
		VBox.setMargin( statusBar, new Insets( 5 ) );

		return statusBar;
	}

	private void performInitialize( ) {
		detailsPane.setValue( null );
		statusBar.setValue( 0, 0 );
		filterPane.setValue( new MethodsFilter( ) );
	}

	private void performSearch( ) {
		final MethodsFilter filter = filterPane.getValue( );
		if ( checkFilter( filter ) ) {
			final List<MethodCall> methods = methodsService.searchMethods( filter );
			final int totalMethods = methodsService.countMethods( );

			methodsTableView.setItems( FXCollections.observableList( methods ) );
			methodsTableView.refresh( );

			statusBar.setValue( methods.size( ), totalMethods );
		}
	}

	private void performSaveAsFavorite( ) {
		if ( onSaveAsFavorite != null ) {
			final MethodsFilter filter = filterPane.getValue( );
			if ( checkFilter( filter ) ) {
				onSaveAsFavorite.accept( filter );
			}
		}
	}

	private boolean checkFilter( final MethodsFilter filter ) {
		// If we are using regular expressions, we should check them
		if ( filter.isUseRegExpr( ) ) {
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

	public void setOnSaveAsFavorite( final Consumer<MethodsFilter> consumer ) {
		onSaveAsFavorite = consumer;
	}

	private void performJumpToTrace( ) {
		if ( onJumpToTrace != null ) {
			final MethodCall methodCall = methodsTableView.getSelectionModel( ).getSelectedItem( );
			if ( methodCall != null ) {
				onJumpToTrace.accept( methodCall );
			}
		}
	}

	public void setOnJumpToTrace( final Consumer<MethodCall> consumer ) {
		onJumpToTrace = consumer;
	}

	private void performExportToCSV( ) {
		if ( onExportToCSV != null ) {
			final CSVData csvData = methodsTableView.getValueAsCsv( );
			onExportToCSV.accept( csvData );
		}
	}

	public void setOnExportToCSV( final Consumer<CSVData> consumer ) {
		onExportToCSV = consumer;
	}

	public void setFilterValue( final MethodsFilter value ) {
		filterPane.setValue( value );
		performSearch( );
	}

	public void setFilterValue( final AggregatedMethodCall value ) {
		filterPane.setValue( value );
		performSearch( );
	}

	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	public void prepareRefresh( ) {
		methodsForRefresh = methodsService.searchMethods( new MethodsFilter( ) );
		totalMethodsForRefresh = methodsService.countMethods( );

		durationSuffixForRefresh = settingsService.getCurrentDurationSuffix( );
	}

	public void performRefresh( ) {
		filterPane.setValue( new MethodsFilter( ) );
		methodsTableView.setItems( FXCollections.observableList( methodsForRefresh ) );
		statusBar.setValue( methodsForRefresh.size( ), totalMethodsForRefresh );
		methodsTableView.setDurationSuffix( durationSuffixForRefresh );
	}

}
