/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.ui.main;

import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import kieker.diagnosis.architecture.common.ExceptionUtil;
import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.service.properties.DevelopmentModeProperty;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.service.data.AggregatedMethodCall;
import kieker.diagnosis.service.data.MethodCall;
import kieker.diagnosis.service.data.MonitoringLogService;
import kieker.diagnosis.service.export.CSVData;
import kieker.diagnosis.service.export.ExportService;
import kieker.diagnosis.ui.about.AboutDialogView;
import kieker.diagnosis.ui.main.properties.LastExportPathProperty;
import kieker.diagnosis.ui.main.properties.LastImportPathProperty;
import kieker.diagnosis.ui.manual.ManualDialogView;
import kieker.diagnosis.ui.methods.MethodsView;
import kieker.diagnosis.ui.monitoring.MonitoringDialogView;
import kieker.diagnosis.ui.progress.ProgressDialog;
import kieker.diagnosis.ui.settings.SettingsDialogView;
import kieker.diagnosis.ui.traces.TracesView;

@Singleton
public class MainController extends ControllerBase<MainViewModel> {

	@Inject
	SettingsDialogView ivSettingsDialogView;

	@Inject
	AboutDialogView ivAboutDialogView;

	@Inject
	MonitoringDialogView ivMonitoringDialogView;

	@Inject
	ManualDialogView ivDocumentationDialogView;

	/**
	 * This action is performed, when the user wants to import a monitoring log.
	 */
	public void performImportLog( ) {
		final DirectoryChooser directoryChooser = new DirectoryChooser( );
		directoryChooser.setTitle( getLocalizedString( "titleImportLog" ) );

		// Set an initial directory if possible
		final PropertiesService propertiesService = getService( PropertiesService.class );
		final String lastImportPath = propertiesService.loadApplicationProperty( LastImportPathProperty.class );
		final File lastImportDirectory = new File( lastImportPath );
		if ( lastImportDirectory.isDirectory( ) ) {
			directoryChooser.setInitialDirectory( lastImportDirectory );
		}

		final File directory = directoryChooser.showDialog( getViewModel( ).getWindow( ) );
		if ( directory != null ) {
			// Remember the directory as initial directory for the next time
			if ( !directory.equals( lastImportDirectory ) ) {
				propertiesService.saveApplicationProperty( LastImportPathProperty.class, directory.getAbsolutePath( ) );
			}

			final ImportThread importThread = new ImportThread( directory );
			importThread.start( );
		}
	}

	/**
	 * This action is performed, when the user wants to configure the settings.
	 */
	public void performSettings( ) {
		final boolean settingsChanged = ivSettingsDialogView.open( getViewModel( ).getWindow( ) );

		// Now refresh everything - if necessary
		if ( settingsChanged ) {
			getViewModel( ).refresh( );
		}
	}

	public void performAbout( ) {
		ivAboutDialogView.open( getViewModel( ).getWindow( ) );
	}

	/**
	 * This action is performed, when the user tries to close the application.
	 */
	public void performClose( ) {
		final PropertiesService propertiesService = getService( PropertiesService.class );
		final boolean developmentMode = propertiesService.loadSystemProperty( DevelopmentModeProperty.class );
		if ( developmentMode ) {
			// Just close the dialog
			getViewModel( ).close( );
		} else {
			// We should ask the user beforehand
			final Alert alert = new Alert( AlertType.CONFIRMATION );
			alert.setTitle( getLocalizedString( "titleReallyClose" ) );
			alert.setHeaderText( getLocalizedString( "headerReallyClose" ) );

			// Add the logo
			final String iconPath = getLocalizedString( "iconReallyClose" );
			final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
			final Image icon = new Image( iconStream );
			final Stage stage = (Stage) alert.getDialogPane( ).getScene( ).getWindow( );
			stage.getIcons( ).add( icon );

			// If the user clicked ok, we close the window
			final Optional<ButtonType> result = alert.showAndWait( );
			if ( result.isPresent( ) && result.get( ) == ButtonType.OK ) {
				getViewModel( ).close( );
			}
		}
	}

	public void performJumpToTrace( final MethodCall aMethodCall ) {
		getViewModel( ).showTab( TracesView.class, aMethodCall );
	}

	public void performJumpToMethods( final AggregatedMethodCall aMethodCall ) {
		getViewModel( ).showTab( MethodsView.class, aMethodCall );
	}

	private class ImportThread extends Thread {

		private final File ivDirectory;

		public ImportThread( final File aDirectory ) {
			ivDirectory = aDirectory;
			setName( "Monitoring Import Thread" );
		}

		@Override
		public void run( ) {
			// Show the progress dialog
			final ProgressDialog importerDialogView = new ProgressDialog( );
			Platform.runLater( ( ) -> {
				importerDialogView.setMessage( getLocalizedString( "processImport" ) );
				importerDialogView.setProgress( -1.0 );
				importerDialogView.open( getViewModel( ).getWindow( ) );
			} );

			// Load the monitoring log
			try {
				BusinessException exception = null;
				final MonitoringLogService monitoringLogService = getService( MonitoringLogService.class );
				try {
					monitoringLogService.importMonitoringLog( ivDirectory );
				} catch ( final BusinessException ex ) {
					// If a business exception occurs, we still want to refresh, but we also want to display the exception.
					exception = ex;
				}

				// Now refresh everything
				Platform.runLater( ( ) -> {
					importerDialogView.setMessage( getLocalizedString( "processRefresh" ) );
				} );

				getViewModel( ).prepareRefresh( );

				Platform.runLater( ( ) -> {
					getViewModel( ).performRefresh( );
				} );

				if ( exception != null ) {
					ExceptionUtil.handleException( exception, getLogger( ).getName( ) );
				}
			} catch ( final Exception ex ) {
				// At this point we have no exception handling. We have to perform this ourselves.
				ExceptionUtil.handleException( ex, getLogger( ).getName( ) );
			} finally {
				Platform.runLater( ( ) -> {
					importerDialogView.close( );
				} );
			}
		}

	}

	public void performSaveAsFavorite( final Class<? extends ViewBase<?>> aClass, final Object aFilter ) {
		try {
			final TextInputDialog textInputDialog = new TextInputDialog( );
			textInputDialog.setTitle( getLocalizedString( "newFilterFavorite" ) );
			textInputDialog.setHeaderText( getLocalizedString( "newFilterFavoriteName" ) );
			final Optional<String> result = textInputDialog.showAndWait( );

			if ( result.isPresent( ) ) {
				final String text = result.get( );

				// Check whether the text is valid
				if ( text == null || text.trim( ).isEmpty( ) ) {
					throw new BusinessException( getLocalizedString( "errorEmptyFilterName" ) );
				}

				// Now we can add the favorite to the menu
				getViewModel( ).addFavorite( ( ) -> getViewModel( ).showTab( aClass, aFilter ), text );
			}
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performExportToCSV( final CSVData aCsvData ) {
		try {

			final FileChooser fileChooser = new FileChooser( );
			fileChooser.setTitle( getLocalizedString( "titleExportToCSV" ) );

			// Set an initial directory if possible
			final PropertiesService propertiesService = getService( PropertiesService.class );
			final String lastExportPath = propertiesService.loadApplicationProperty( LastExportPathProperty.class );
			final File lastExportDirectory = new File( lastExportPath );
			if ( lastExportDirectory.isDirectory( ) ) {
				fileChooser.setInitialDirectory( lastExportDirectory );
			}

			final File file = fileChooser.showSaveDialog( getViewModel( ).getWindow( ) );
			if ( file != null ) {
				// Remember the directory as initial directory for the next time
				final File directory = file.getParentFile( );
				if ( !lastExportDirectory.equals( directory ) ) {
					propertiesService.saveApplicationProperty( LastExportPathProperty.class, directory.getAbsolutePath( ) );
				}

				final ExportService exportService = getService( ExportService.class );
				exportService.exportToCSV( file, aCsvData );
			}
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performMonitoring( ) {
		ivMonitoringDialogView.open( getViewModel( ).getWindow( ) );
	}

	public void performDocumentation( ) {
		ivDocumentationDialogView.open( getViewModel( ).getWindow( ) );
	}

}
