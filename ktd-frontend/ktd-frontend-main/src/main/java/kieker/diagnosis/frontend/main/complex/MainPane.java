/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.main.complex;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import kieker.diagnosis.backend.data.ImportType;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.data.exception.CorruptStreamException;
import kieker.diagnosis.backend.data.exception.ImportFailedException;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.Settings;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.common.DelegateException;
import kieker.diagnosis.frontend.base.common.ExceptionUtil;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.dialog.alert.Alert;
import kieker.diagnosis.frontend.dialog.favorite.FavoriteDialog;
import kieker.diagnosis.frontend.dialog.progress.ProgressDialog;
import kieker.diagnosis.frontend.dialog.settings.SettingsDialog;
import kieker.diagnosis.frontend.main.composite.MainMenuBar;
import kieker.diagnosis.frontend.main.composite.MainTabPane;
import kieker.diagnosis.frontend.main.properties.LastImportPathProperty;

public final class MainPane extends VBox implements StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MainPane.class.getName( ) );

	private final SettingsService settingsService;
	private final PropertiesService propertiesService;
	private final MonitoringLogService monitoringLogService;

	private final MainMenuBar menuBar;
	private final MainTabPane mainTabPane;

	@Inject
	public MainPane( final SettingsService settingsService, final PropertiesService propertiesService, final MonitoringLogService monitoringLogService, final MainTabPane mainTabPane, final MainMenuBar menuBar ) {
		this.settingsService = settingsService;
		this.propertiesService = propertiesService;
		this.monitoringLogService = monitoringLogService;
		this.mainTabPane = mainTabPane;
		this.menuBar = menuBar;

		createControl( );
	}

	private void createControl( ) {
		getChildren( ).add( configureMenuBar( ) );
		getChildren( ).add( configureMainPane( ) );
	}

	private Node configureMenuBar( ) {
		menuBar.setOnSettings( ( ) -> performSettings( ) );
		menuBar.setOnImportLog( ( ) -> performImportLog( ) );
		menuBar.setOnImportLogFromZip( ( ) -> performImportLogFromZip( ) );

		return menuBar;
	}

	private Node configureMainPane( ) {
		loadFonts( );
		addDefaultStylesheet( );
		mainTabPane.setId( "mainTabPane" );
		mainTabPane.setOnSaveAsFavorite( ( tab, filter ) -> performSaveAsFavorite( tab, filter ) );

		VBox.setVgrow( mainTabPane, Priority.ALWAYS );

		return mainTabPane;
	}

	private void loadFonts( ) {
		final URL fontAwesomeUrl = MainPane.class.getResource( "fa-solid-900.ttf" );
		Font.loadFont( fontAwesomeUrl.toExternalForm( ), 12 );

		final URL openSansUrl = MainPane.class.getResource( "OpenSans-Regular.ttf" );
		Font.loadFont( openSansUrl.toExternalForm( ), 12 );

		final URL openSansBoldUrl = MainPane.class.getResource( "OpenSans-Bold.ttf" );
		Font.loadFont( openSansBoldUrl.toExternalForm( ), 12 );

		final URL openSansItalicUrl = MainPane.class.getResource( "OpenSans-Italic.ttf" );
		Font.loadFont( openSansItalicUrl.toExternalForm( ), 12 );

		final URL openSansBoldItalicUrl = MainPane.class.getResource( "OpenSans-BoldItalic.ttf" );
		Font.loadFont( openSansBoldItalicUrl.toExternalForm( ), 12 );
	}

	/**
	 * This action is performed, when the user wants to configure the settings.
	 */
	private void performSettings( ) {
		final Settings settings = settingsService.loadSettings( );

		final SettingsDialog settingsDialog = new SettingsDialog( getScene( ).getWindow( ) );
		settingsDialog.setValue( settings );
		final Optional<Settings> result = settingsDialog.showAndWait( );

		result.ifPresent( newSettings -> {
			settingsService.saveSettings( newSettings );
			mainTabPane.prepareRefresh( );
			mainTabPane.performRefresh( );
		} );
	}

	/**
	 * This action is performed, when the user wants to import a monitoring log.
	 */
	public void performImportLog( ) {
		final DirectoryChooser directoryChooser = new DirectoryChooser( );
		directoryChooser.setTitle( RESOURCE_BUNDLE.getString( "titleImportLog" ) );

		final File lastImportDirectory = getInitialDirectory( );
		if ( lastImportDirectory.isDirectory( ) ) {
			directoryChooser.setInitialDirectory( lastImportDirectory );
		}

		final File directory = directoryChooser.showDialog( getWindow( ) );
		if ( directory != null ) {
			// Remember the directory as initial directory for the next time
			setNewInitialDirectory( lastImportDirectory, directory );

			final ImportThread importThread = new ImportThread( directory.toPath( ), ImportType.DIRECTORY );
			importThread.start( );
		}
	}

	/**
	 * This method is only to be used from the UI tests to bypass the native dialogs in the test environments.
	 *
	 * @param directory
	 *            The import directory.
	 */
	public void performImportLog( final Path directory ) {
		final ImportThread importThread = new ImportThread( directory, ImportType.DIRECTORY );
		importThread.start( );
	}

	/**
	 * This action is performed, when the user wants to import a monitoring log from a ZIP file.
	 */
	public void performImportLogFromZip( ) {
		final FileChooser fileChooser = new FileChooser( );
		fileChooser.setTitle( RESOURCE_BUNDLE.getString( "titleImportLog" ) );

		final File lastImportDirectory = getInitialDirectory( );
		if ( lastImportDirectory.isDirectory( ) ) {
			fileChooser.setInitialDirectory( lastImportDirectory );
		}

		final File file = fileChooser.showOpenDialog( getWindow( ) );
		if ( file != null ) {
			// Remember the directory as initial directory for the next time
			final File directory = file.getParentFile( );
			setNewInitialDirectory( lastImportDirectory, directory );

			final ImportThread importThread = new ImportThread( file.toPath( ), ImportType.ZIP_FILE );
			importThread.start( );
		}
	}

	private File getInitialDirectory( ) {
		final String lastImportPath = propertiesService.loadApplicationProperty( LastImportPathProperty.class );
		return new File( lastImportPath );
	}

	private void setNewInitialDirectory( final File lastImportDirectory, final File directory ) {
		if ( !directory.equals( lastImportDirectory ) ) {
			propertiesService.saveApplicationProperty( LastImportPathProperty.class, directory.getAbsolutePath( ) );
		}
	}

	private Window getWindow( ) {
		final Scene scene = getScene( );
		return scene.getWindow( );
	}

	public void performSaveAsFavorite( final Tab tab, final Object filter ) {
		final FavoriteDialog favoriteDialog = new FavoriteDialog( getScene( ).getWindow( ) );

		final Optional<String> result = favoriteDialog.showAndWait( );

		if ( result.isPresent( ) ) {
			final String text = result.get( );

			// Check whether the text is valid
			if ( text == null || text.trim( ).isEmpty( ) ) {
				final Alert alert = new Alert( AlertType.WARNING, getScene( ).getWindow( ) );
				alert.setContentText( RESOURCE_BUNDLE.getString( "errorEmptyFilterName" ) );
				alert.showAndWait( );

				return;
			}

			// Now we can add the favorite to the menu
			menuBar.addFavorite( ( ) -> mainTabPane.showTab( tab, filter ), text );
		}
	}

	public void performClose( ) {
		menuBar.performClose( );
	}

	private class ImportThread extends Thread {

		private final Path directoryOrFile;
		private final ImportType tye;
		private ProgressDialog progressDialog;

		ImportThread( final Path aDirectoryOrFile, final ImportType aType ) {
			directoryOrFile = aDirectoryOrFile;
			tye = aType;
			setName( "Monitoring Import Thread" );
		}

		@Override
		public void run( ) {
			Platform.runLater( ( ) -> {
				progressDialog = new ProgressDialog( getScene( ).getWindow( ) );
				progressDialog.setMessage( RESOURCE_BUNDLE.getString( "processImport" ) );
				progressDialog.setProgress( -1.0 );
				progressDialog.show( );
			} );

			try {
				// Load the monitoring log
				Exception exception = null;
				try {
					monitoringLogService.importMonitoringLog( directoryOrFile, tye );
				} catch ( final CorruptStreamException | ImportFailedException ex ) {
					// We still want to refresh, but we also want to display the exception.
					exception = new DelegateException( ex );
				}

				// Now refresh everything
				Platform.runLater( ( ) -> {
					progressDialog.setMessage( RESOURCE_BUNDLE.getString( "processRefresh" ) );
				} );

				mainTabPane.prepareRefresh( );

				Platform.runLater( ( ) -> {
					mainTabPane.performRefresh( );
				} );

				if ( exception != null ) {
					ExceptionUtil.handleException( exception, MainPane.class.getCanonicalName( ) );
				}
			} catch ( final Exception ex ) {
				// At this point we have no exception handling. We have to perform this ourselves.
				ExceptionUtil.handleException( ex, MainPane.class.getCanonicalName( ) );
			} finally {
				Platform.runLater( ( ) -> {
					progressDialog.closeDialog( );
				} );
			}
		}
	}

}
