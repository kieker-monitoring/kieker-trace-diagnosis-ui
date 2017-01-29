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

package kieker.diagnosis.gui;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
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
import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.gui.about.AboutDialogController;
import kieker.diagnosis.gui.aggregatedcalls.AggregatedCallsController;
import kieker.diagnosis.gui.aggregatedtraces.AggregatedTracesController;
import kieker.diagnosis.gui.bugreporting.BugReportingDialogController;
import kieker.diagnosis.gui.calls.CallsController;
import kieker.diagnosis.gui.monitoringstatistics.MonitoringStatisticsController;
import kieker.diagnosis.gui.settings.SettingsDialogController;
import kieker.diagnosis.gui.traces.TracesController;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.CSVData;
import kieker.diagnosis.util.CSVDataCollector;
import kieker.diagnosis.util.CSVExporter;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextEntry;
import kieker.diagnosis.util.ContextKey;
import kieker.diagnosis.util.ErrorHandling;

/**
 * The main controller of this application. It is responsible for controlling the application's main window.
 *
 * @author Nils Christian Ehmke
 */
public final class MainController extends AbstractController<MainView> {

	private static final String KEY_LAST_IMPORT_PATH = "lastimportpath";
	private static final String KEY_LAST_EXPORT_PATH = "lastexportpath";

	private static final Logger LOGGER = LogManager.getLogger( MainController.class );

	private static MainController cvInstance;

	private final DataModel ivDataModel = DataModel.getInstance( );

	private Optional<Button> ivDisabledButton = Optional.empty( );
	private Optional<Class<? extends AbstractController<?>>> ivActiveController = Optional.empty( );

	private int ivFavoritesAvailable;

	public MainController( final Context aContext ) {
		super( aContext );
	}

	@Override
	public void doInitialize( ) {

	}

	@ErrorHandling
	public void showTraces( ) throws Exception {
		this.showTraces( new ContextEntry[0] );
	}

	private <T extends AbstractController<?>> void showCorrespondingView( final Class<T> aController, final ContextEntry... aContextEntries ) throws Exception {
		if ( aController == TracesController.class ) {
			showTraces( aContextEntries );
		}
		else if ( aController == AggregatedTracesController.class ) {
			showAggregatedTraces( aContextEntries );
		}
		else if ( aController == CallsController.class ) {
			showCalls( aContextEntries );
		}
		else if ( aController == AggregatedCallsController.class ) {
			showAggregatedCalls( aContextEntries );
		}
	}

	private void showTraces( final ContextEntry... aContextEntries ) throws Exception {
		toggleDisabledButton( getView( ).getTraces( ) );
		ivActiveController = Optional.of( TracesController.class );
		GUIUtil.loadView( TracesController.class, getView( ).getContent( ), aContextEntries );
	}

	private void showAggregatedTraces( final ContextEntry... aContextEntries ) throws Exception {
		toggleDisabledButton( getView( ).getAggregatedtraces( ) );
		ivActiveController = Optional.of( AggregatedTracesController.class );
		GUIUtil.loadView( AggregatedTracesController.class, getView( ).getContent( ), aContextEntries );
	}

	private void showAggregatedCalls( final ContextEntry... aContextEntries ) throws Exception {
		toggleDisabledButton( getView( ).getAggregatedcalls( ) );
		ivActiveController = Optional.of( AggregatedCallsController.class );
		GUIUtil.loadView( AggregatedCallsController.class, getView( ).getContent( ), aContextEntries );
	}

	@ErrorHandling
	public void showAggregatedTraces( ) throws Exception {
		this.showAggregatedTraces( new ContextEntry[0] );
	}

	@ErrorHandling
	public void showCalls( ) throws Exception {
		this.showCalls( new ContextEntry[0] );
	}

	@ErrorHandling
	public void showCalls( final ContextEntry... aContextEntries ) throws Exception {
		toggleDisabledButton( getView( ).getCalls( ) );
		ivActiveController = Optional.of( CallsController.class );
		GUIUtil.loadView( CallsController.class, getView( ).getContent( ), aContextEntries );
	}

	@ErrorHandling
	public void showAggregatedCalls( ) throws Exception {
		this.showAggregatedCalls( new ContextEntry[0] );
	}

	@ErrorHandling
	public void showStatistics( ) throws Exception {
		toggleDisabledButton( getView( ).getStatistics( ) );
		ivActiveController = Optional.of( MonitoringStatisticsController.class );
		GUIUtil.loadView( MonitoringStatisticsController.class, getView( ).getContent( ) );
	}

	@ErrorHandling
	public void showImportDialog( ) {
		final Preferences preferences = Preferences.userNodeForPackage( MainController.class );
		final File initialDirectory = new File( preferences.get( MainController.KEY_LAST_IMPORT_PATH, "." ) );

		final DirectoryChooser directoryChooser = new DirectoryChooser( );
		if ( initialDirectory.exists( ) ) {
			directoryChooser.setInitialDirectory( initialDirectory );
		}
		final File selectedDirectory = directoryChooser.showDialog( getView( ).getWindow( ) );
		if ( null != selectedDirectory ) {
			ivDataModel.loadMonitoringLogFromFS( selectedDirectory );

			preferences.put( MainController.KEY_LAST_IMPORT_PATH, selectedDirectory.getAbsolutePath( ) );
			try {
				preferences.flush( );
			}
			catch ( final BackingStoreException ex ) {
				MainController.LOGGER.error( ex );
			}
		}
	}

	@ErrorHandling
	public void showSettings( ) throws Exception {
		final long propertiesVersionPre = PropertiesModel.getInstance( ).getVersion( );
		GUIUtil.loadDialog( SettingsDialogController.class, getView( ).getWindow( ) );

		if ( ivActiveController.isPresent( ) ) {
			final long propertiesVersionPost = PropertiesModel.getInstance( ).getVersion( );
			if ( propertiesVersionPre != propertiesVersionPost ) {
				GUIUtil.clearCache( );
				GUIUtil.loadView( ivActiveController.get( ), getView( ).getContent( ) );
			}
		}
	}

	@ErrorHandling
	public void showAbout( ) throws Exception {
		GUIUtil.loadDialog( AboutDialogController.class, getView( ).getWindow( ) );
	}

	@ErrorHandling
	public void showBugReporting( ) throws Exception {
		GUIUtil.loadDialog( BugReportingDialogController.class, getView( ).getWindow( ) );
	}

	@ErrorHandling
	public void close( ) {
		final Window window = getView( ).getWindow( );
		window.hide( );
	}

	private void toggleDisabledButton( final Button aDisabledButton ) {
		ivDisabledButton.ifPresent( b -> b.setDisable( false ) );
		ivDisabledButton = Optional.of( aDisabledButton );
		aDisabledButton.setDisable( true );
	}

	public static MainController instance( ) {
		return MainController.cvInstance;
	}

	public void jumpToTrace( final OperationCall aCall ) throws Exception {
		this.showTraces( new ContextEntry( ContextKey.OPERATION_CALL, aCall ) );
	}

	public void jumpToCalls( final AggregatedOperationCall aCall ) throws Exception {
		this.showCalls( new ContextEntry( ContextKey.AGGREGATED_OPERATION_CALL, aCall ) );
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
			CSVExporter.exportToCSV( data, selectedFile );

			preferences.put( MainController.KEY_LAST_EXPORT_PATH, selectedFile.getParent( ) );
			try {
				preferences.flush( );
			}
			catch ( final BackingStoreException ex ) {
				MainController.LOGGER.error( ex );
			}
		}
	}

	public <T extends AbstractController<?>> void saveAsFavorite( final Object aFilterContent, final Class<T> aFilterLoader ) {
		// Ask the user for the name of the favorite
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( "kieker.diagnosis.gui.view", Locale.getDefault( ) );

		final TextInputDialog textInputDialog = new TextInputDialog( );
		textInputDialog.setTitle( resourceBundle.getString( "newFilterFavorite" ) );
		textInputDialog.setHeaderText( resourceBundle.getString( "newFilterFavoriteName" ) );
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

	@ErrorHandling
	private <T extends AbstractController<?>> void loadFavorite( final Object aFilterContent, final Class<T> aFilterLoader ) {
		try {
			final ContextEntry contextEntry = new ContextEntry( ContextKey.FILTER_CONTENT, aFilterContent );
			showCorrespondingView( aFilterLoader, contextEntry );
		}
		catch ( final Exception ex ) {
			silentRethrow( ex );
		}
	}

	@SuppressWarnings ( "unchecked" )
	private static <T extends Throwable> RuntimeException silentRethrow( final Throwable aThrowable ) throws T {
		throw (T) aThrowable;
	}

}
