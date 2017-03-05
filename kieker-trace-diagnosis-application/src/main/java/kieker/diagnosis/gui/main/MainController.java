/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.gui.main;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import kieker.diagnosis.gui.AbstractController;
import kieker.diagnosis.gui.Context;
import kieker.diagnosis.gui.ContextEntry;
import kieker.diagnosis.gui.ContextKey;
import kieker.diagnosis.gui.GUIUtil;
import kieker.diagnosis.gui.about.AboutDialogController;
import kieker.diagnosis.gui.aggregatedcalls.AggregatedCallsController;
import kieker.diagnosis.gui.aggregatedtraces.AggregatedTracesController;
import kieker.diagnosis.gui.bugreporting.BugReportingDialogController;
import kieker.diagnosis.gui.calls.CallsController;
import kieker.diagnosis.gui.monitoringstatistics.MonitoringStatisticsController;
import kieker.diagnosis.gui.settings.SettingsDialogController;
import kieker.diagnosis.gui.traces.TracesController;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.data.DataService;
import kieker.diagnosis.service.data.domain.AggregatedOperationCall;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.export.CSVData;
import kieker.diagnosis.service.export.CSVDataCollector;
import kieker.diagnosis.service.export.ExportService;
import kieker.diagnosis.service.properties.PropertiesService;

/**
 * The main controller of this application. It is responsible for controlling the application's main window.
 *
 * @author Nils Christian Ehmke
 */
public final class MainController extends AbstractController<MainView> implements MainControllerIfc {

	private static final String KEY_LAST_IMPORT_PATH = "lastimportpath";
	private static final String KEY_LAST_EXPORT_PATH = "lastexportpath";

	private static final Logger LOGGER = LogManager.getLogger( MainController.class );

	private Optional<Button> ivDisabledButton = Optional.empty( );
	@SuppressWarnings ( "rawtypes" )
	private Optional<Class<? extends AbstractController>> ivActiveController = Optional.empty( );

	private int ivFavoritesAvailable;

	@InjectService
	private PropertiesService ivPropertiesService;

	@InjectService
	private ExportService ivExportService;

	@InjectService
	private DataService ivDataService;

	public MainController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {
		// Nothing to initialize
	}

	@Override
	public void showTraces( ) {
		showTraces( new ContextEntry[0] );
	}

	private <T extends AbstractController<?>> void showCorrespondingView( final Class<T> aController, final ContextEntry... aContextEntries ) {
		if ( aController == TracesController.class ) {
			showTraces( aContextEntries );
		} else if ( aController == AggregatedTracesController.class ) {
			showAggregatedTraces( aContextEntries );
		} else if ( aController == CallsController.class ) {
			showCalls( aContextEntries );
		} else if ( aController == AggregatedCallsController.class ) {
			showAggregatedCalls( aContextEntries );
		}
	}

	private void showTraces( final ContextEntry... aContextEntries ) {
		toggleDisabledButton( getView( ).getTraces( ) );
		ivActiveController = Optional.of( TracesController.class );
		GUIUtil.loadView( TracesController.class, getView( ).getContent( ), getClass( ), ivPropertiesService.isCacheViews( ), aContextEntries );
	}

	private void showAggregatedTraces( final ContextEntry... aContextEntries ) {
		toggleDisabledButton( getView( ).getAggregatedtraces( ) );
		ivActiveController = Optional.of( AggregatedTracesController.class );
		GUIUtil.loadView( AggregatedTracesController.class, getView( ).getContent( ), getClass( ), ivPropertiesService.isCacheViews( ), aContextEntries );
	}

	private void showAggregatedCalls( final ContextEntry... aContextEntries ) {
		toggleDisabledButton( getView( ).getAggregatedcalls( ) );
		ivActiveController = Optional.of( AggregatedCallsController.class );
		GUIUtil.loadView( AggregatedCallsController.class, getView( ).getContent( ), getClass( ), ivPropertiesService.isCacheViews( ), aContextEntries );
	}

	@Override
	public void showAggregatedTraces( ) {
		showAggregatedTraces( new ContextEntry[0] );
	}

	@Override
	public void showCalls( ) {
		showCalls( new ContextEntry[0] );
	}

	public void showCalls( final ContextEntry... aContextEntries ) {
		toggleDisabledButton( getView( ).getCalls( ) );
		ivActiveController = Optional.of( CallsController.class );
		GUIUtil.loadView( CallsController.class, getView( ).getContent( ), getClass( ), ivPropertiesService.isCacheViews( ), aContextEntries );
	}

	@Override
	public void showAggregatedCalls( ) {
		showAggregatedCalls( new ContextEntry[0] );
	}

	@Override
	public void showStatistics( ) {
		toggleDisabledButton( getView( ).getStatistics( ) );
		ivActiveController = Optional.of( MonitoringStatisticsController.class );
		GUIUtil.loadView( MonitoringStatisticsController.class, getView( ).getContent( ), getClass( ), ivPropertiesService.isCacheViews( ),
				new ContextEntry[0] );
	}

	@Override
	public void showImportDialog( ) {
		final Preferences preferences = Preferences.userNodeForPackage( MainController.class );
		final File initialDirectory = new File( preferences.get( MainController.KEY_LAST_IMPORT_PATH, "." ) );

		final DirectoryChooser directoryChooser = new DirectoryChooser( );
		if ( initialDirectory.exists( ) ) {
			directoryChooser.setInitialDirectory( initialDirectory );
		}
		final File selectedDirectory = directoryChooser.showDialog( getView( ).getWindow( ) );
		if ( null != selectedDirectory ) {
			ivDataService.loadMonitoringLogFromFS( selectedDirectory );

			preferences.put( MainController.KEY_LAST_IMPORT_PATH, selectedDirectory.getAbsolutePath( ) );
			try {
				preferences.flush( );
			} catch ( final BackingStoreException ex ) {
				MainController.LOGGER.error( ex );
			}
		}
	}

	@Override
	@SuppressWarnings ( "unchecked" )
	public void showSettings( ) {
		final long propertiesVersionPre = ivPropertiesService.getVersion( );
		GUIUtil.loadDialog( SettingsDialogController.class, getClass( ), ivPropertiesService.isCacheViews( ), getView( ).getWindow( ) );

		if ( ivActiveController.isPresent( ) ) {
			final long propertiesVersionPost = ivPropertiesService.getVersion( );
			if ( propertiesVersionPre != propertiesVersionPost ) {
				GUIUtil.clearCache( );
				GUIUtil.loadView( ivActiveController.get( ), getView( ).getContent( ), getClass( ), ivPropertiesService.isCacheViews( ), new ContextEntry[0] );
			}
		}
	}

	@Override
	public void showAbout( ) {
		GUIUtil.loadDialog( AboutDialogController.class, getClass( ), ivPropertiesService.isCacheViews( ), getView( ).getWindow( ) );
	}

	@Override
	public void showBugReporting( ) {
		GUIUtil.loadDialog( BugReportingDialogController.class, getClass( ), ivPropertiesService.isCacheViews( ), getView( ).getWindow( ) );
	}

	@Override
	public void close( ) {
		final Window window = getView( ).getWindow( );
		window.hide( );
	}

	private void toggleDisabledButton( final Button aDisabledButton ) {
		ivDisabledButton.ifPresent( b -> b.setDisable( false ) );
		ivDisabledButton = Optional.of( aDisabledButton );
		aDisabledButton.setDisable( true );
	}

	public void jumpToTrace( final OperationCall aCall ) {
		showTraces( new ContextEntry( ContextKey.OPERATION_CALL, aCall ) );
	}

	public void jumpToCalls( final AggregatedOperationCall aCall ) {
		showCalls( new ContextEntry( ContextKey.AGGREGATED_OPERATION_CALL, aCall ) );
	}

	public void exportToCSV( final CSVDataCollector aDataCollector ) throws IOException {
		final Preferences preferences = Preferences.userNodeForPackage( MainController.class );
		final File initialDirectory = new File( preferences.get( MainController.KEY_LAST_EXPORT_PATH, "." ) );

		final FileChooser fileChooser = new FileChooser( );
		if ( initialDirectory.exists( ) ) {
			fileChooser.setInitialDirectory( initialDirectory );
		}

		final File selectedFile = fileChooser.showSaveDialog( getView( ).getWindow( ) );
		if ( null != selectedFile ) {
			final CSVData data = aDataCollector.collectData( );
			ivExportService.exportToCSV( data, selectedFile );

			preferences.put( MainController.KEY_LAST_EXPORT_PATH, selectedFile.getParent( ) );
			try {
				preferences.flush( );
			} catch ( final BackingStoreException ex ) {
				MainController.LOGGER.error( ex );
			}
		}
	}

	public <T extends AbstractController<?>> void saveAsFavorite( final Object aFilterContent, final Class<T> aFilterLoader ) {
		final TextInputDialog textInputDialog = new TextInputDialog( );
		textInputDialog.setTitle( getView( ).getResourceBundle( ).getString( "newFilterFavorite" ) );
		textInputDialog.setHeaderText( getView( ).getResourceBundle( ).getString( "newFilterFavoriteName" ) );
		final Optional<String> result = textInputDialog.showAndWait( );

		if ( result.isPresent( ) ) {
			// If necessary, add a horizontal line first
			if ( ivFavoritesAvailable == 0 ) {
				final Separator separator = new Separator( );
				VBox.setMargin( separator, new Insets( 10, 0, 0, 10 ) );
				getView( ).getLeftButtonBox( ).getChildren( ).add( separator );
			}

			// Now we can add the button for the favorite and the button to remove it
			final HBox hbox = new HBox( );
			hbox.setSpacing( 10 );
			VBox.setMargin( hbox, new Insets( 10, 0, 0, 10 ) );

			final Button favoriteButton = new Button( result.get( ) );
			favoriteButton.setPrefWidth( 155 );
			favoriteButton.setOnAction( event -> {
				loadFavorite( aFilterContent, aFilterLoader );
			} );
			hbox.getChildren( ).add( favoriteButton );

			final Button removeButton = new Button( "-" );
			removeButton.setPrefWidth( 20 );
			removeButton.setOnAction( event -> {
				getView( ).getLeftButtonBox( ).getChildren( ).remove( hbox );
				ivFavoritesAvailable--;

				if ( ivFavoritesAvailable == 0 ) {
					final Optional<Node> first = getView( ).getLeftButtonBox( ).getChildren( ).stream( ).filter( node -> node instanceof Separator )
							.findFirst( );
					if ( first.isPresent( ) ) {
						getView( ).getLeftButtonBox( ).getChildren( ).remove( first.get( ) );
					}
				}
			} );
			hbox.getChildren( ).add( removeButton );

			ivFavoritesAvailable++;
			getView( ).getLeftButtonBox( ).getChildren( ).add( hbox );
		}
	}

	private <T extends AbstractController<?>> void loadFavorite( final Object aFilterContent, final Class<T> aFilterLoader ) {
		final ContextEntry contextEntry = new ContextEntry( ContextKey.FILTER_CONTENT, aFilterContent );
		showCorrespondingView( aFilterLoader, contextEntry );
	}

}
