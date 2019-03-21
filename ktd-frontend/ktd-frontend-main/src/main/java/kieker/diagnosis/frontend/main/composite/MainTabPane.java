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

package kieker.diagnosis.frontend.main.composite;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import com.google.inject.Inject;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.export.ExportService;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsFilter;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.frontend.base.common.DelegateException;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.main.properties.LastExportPathProperty;
import kieker.diagnosis.frontend.tab.aggregatedmethods.complex.AggregatedMethodsTab;
import kieker.diagnosis.frontend.tab.methods.complex.MethodsTab;
import kieker.diagnosis.frontend.tab.statistics.complex.StatisticsTab;
import kieker.diagnosis.frontend.tab.traces.complex.TracesTab;

public final class MainTabPane extends TabPane implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MainTabPane.class.getName( ) );

	private final PropertiesService propertiesService;
	private final ExportService exportService;

	private final TracesTab tracesTab;
	private final MethodsTab methodsTab;
	private final AggregatedMethodsTab aggregatedMethodsTab;
	private final StatisticsTab statisticsTab;

	@Inject
	public MainTabPane( final PropertiesService propertiesService, final ExportService exportService, final TracesTab tracesTab, final MethodsTab methodsTab, final AggregatedMethodsTab aggregatedMethodsTab, final StatisticsTab statisticsTab ) {
		this.propertiesService = propertiesService;
		this.exportService = exportService;
		this.tracesTab = tracesTab;
		this.methodsTab = methodsTab;
		this.aggregatedMethodsTab = aggregatedMethodsTab;
		this.statisticsTab = statisticsTab;

		createControl( );
	}

	private void createControl( ) {
		setTabClosingPolicy( TabClosingPolicy.UNAVAILABLE );
		addDefaultStylesheet( );

		getTabs( ).add( configureTracesTab( ) );
		getTabs( ).add( configureMethodsTab( ) );
		getTabs( ).add( configureAggregatedMethodsTab( ) );
		getTabs( ).add( configureStatisticsTab( ) );
	}

	private Tab configureTracesTab( ) {
		tracesTab.setId( "tabTraces" );
		tracesTab.setText( RESOURCE_BUNDLE.getString( "traces" ) );

		// Only one default button is allowed - even if the other buttons are not
		// visible. Therefore we have to set the default
		// button property only for the current tab.
		tracesTab.defaultButtonProperty( ).bind( getSelectionModel( ).selectedItemProperty( ).isEqualTo( tracesTab ) );

		return tracesTab;
	}

	private Tab configureMethodsTab( ) {
		methodsTab.setId( "tabMethods" );
		methodsTab.setText( RESOURCE_BUNDLE.getString( "methods" ) );
		methodsTab.setOnJumpToTrace( this::performJumpToTrace );
		methodsTab.setOnExportToCSV( this::performExportToCSV );

		// Only one default button is allowed - even if the other buttons are not
		// visible. Therefore we have to set the default
		// button property only for the current tab.
		methodsTab.defaultButtonProperty( ).bind( getSelectionModel( ).selectedItemProperty( ).isEqualTo( methodsTab ) );

		return methodsTab;
	}

	private Tab configureAggregatedMethodsTab( ) {
		aggregatedMethodsTab.setId( "tabAggregatedMethods" );
		aggregatedMethodsTab.setText( RESOURCE_BUNDLE.getString( "aggregatedMethods" ) );
		aggregatedMethodsTab.setOnJumpToMethods( this::performJumpToMethods );
		aggregatedMethodsTab.setOnExportToCSV( this::performExportToCSV );

		// Only one default button is allowed - even if the other buttons are not
		// visible. Therefore we have to set the default
		// button property only for the current tab.
		aggregatedMethodsTab.defaultButtonProperty( ).bind( getSelectionModel( ).selectedItemProperty( ).isEqualTo( aggregatedMethodsTab ) );

		return aggregatedMethodsTab;
	}

	private Tab configureStatisticsTab( ) {
		statisticsTab.setId( "tabStatistics" );
		statisticsTab.setText( RESOURCE_BUNDLE.getString( "statistics" ) );

		return statisticsTab;
	}

	public void prepareRefresh( ) {
		tracesTab.prepareRefresh( );
		methodsTab.prepareRefresh( );
		aggregatedMethodsTab.prepareRefresh( );
		statisticsTab.prepareRefresh( );
	}

	public void performRefresh( ) {
		tracesTab.performRefresh( );
		methodsTab.performRefresh( );
		aggregatedMethodsTab.performRefresh( );
		statisticsTab.performRefresh( );
	}

	private void performJumpToTrace( final MethodCall value ) {
		getSelectionModel( ).select( tracesTab );
		tracesTab.setFilterValue( value );
	}

	private void performJumpToMethods( final AggregatedMethodCall value ) {
		getSelectionModel( ).select( methodsTab );
		methodsTab.setFilterValue( value );
	}

	private void performExportToCSV( final CSVData aCsvData ) {
		final FileChooser fileChooser = new FileChooser( );
		fileChooser.setTitle( RESOURCE_BUNDLE.getString( "titleExportToCSV" ) );

		// Set an initial directory if possible
		final String lastExportPath = propertiesService.loadApplicationProperty( LastExportPathProperty.class );
		final File lastExportDirectory = new File( lastExportPath );
		if ( lastExportDirectory.isDirectory( ) ) {
			fileChooser.setInitialDirectory( lastExportDirectory );
		}

		final File file = fileChooser.showSaveDialog( getWindow( ) );
		if ( file != null ) {
			// Remember the directory as initial directory for the next time
			final File directory = file.getParentFile( );
			if ( !lastExportDirectory.equals( directory ) ) {
				propertiesService.saveApplicationProperty( LastExportPathProperty.class, directory.getAbsolutePath( ) );
			}

			try {
				exportService.exportToCSV( file.toPath( ), aCsvData );
			} catch ( final IOException ex ) {
				throw new DelegateException( ex );
			}
		}
	}

	private Window getWindow( ) {
		final Scene scene = getScene( );
		return scene.getWindow( );
	}

	public void setOnSaveAsFavorite( final BiConsumer<Tab, Object> action ) {
		tracesTab.setOnSaveAsFavorite( filter -> action.accept( tracesTab, filter ) );
		methodsTab.setOnSaveAsFavorite( filter -> action.accept( methodsTab, filter ) );
		aggregatedMethodsTab.setOnSaveAsFavorite( filter -> action.accept( aggregatedMethodsTab, filter ) );
	}

	public void showTab( final Tab tab, final Object filter ) {
		getSelectionModel( ).select( tab );

		if ( tab == tracesTab ) {
			tracesTab.setFilterValue( (TracesFilter) filter );
		} else if ( tab == methodsTab ) {
			methodsTab.setFilterValue( (MethodsFilter) filter );
		} else if ( tab == aggregatedMethodsTab ) {
			aggregatedMethodsTab.setFilterValue( (AggregatedMethodsFilter) filter );
		}
	}

}
