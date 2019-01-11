package kieker.diagnosis.frontend.main.complex;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.data.ImportType;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.Settings;
import kieker.diagnosis.backend.settings.SettingsService;
import kieker.diagnosis.frontend.base.common.ExceptionUtil;
import kieker.diagnosis.frontend.base.mixin.ErrorHandlerMixin;
import kieker.diagnosis.frontend.base.mixin.StylesheetMixin;
import kieker.diagnosis.frontend.dialog.progress.ProgressDialog;
import kieker.diagnosis.frontend.dialog.settings.SettingsDialog;
import kieker.diagnosis.frontend.main.composite.MainMenuBar;
import kieker.diagnosis.frontend.main.composite.MainTabPane;
import kieker.diagnosis.frontend.main.properties.LastImportPathProperty;

public final class MainPane extends VBox implements ErrorHandlerMixin, StylesheetMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MainPane.class.getName( ) );

	private final MainMenuBar menuBar = new MainMenuBar( );
	private final MainTabPane mainTabPane = new MainTabPane( );

	public MainPane( ) {
		configureMenuBar( );
		getChildren( ).add( menuBar );

		configureMainPane( );
		getChildren( ).add( mainTabPane );
	}

	private void configureMenuBar( ) {
		menuBar.setOnSettings( ( ) -> executeAction( this::performSettings ) );
		menuBar.setOnImportLog( ( ) -> executeAction( this::performImportLog ) );
		menuBar.setOnImportLogFromZip( ( ) -> executeAction( this::performImportLogFromZip ) );

		mainTabPane.setOnSaveAsFavorite( ( tab, filter ) -> executeAction( ( ) -> performSaveAsFavorite( tab, filter ) ) );
	}

	private void configureMainPane( ) {
		loadFonts( );
		addDefaultStylesheet( );
		mainTabPane.setId( "mainTabPane" );

		VBox.setVgrow( mainTabPane, Priority.ALWAYS );
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
		final SettingsService settingsService = ServiceFactory.getService( SettingsService.class );
		final Settings settings = settingsService.loadSettings( );

		final SettingsDialog settingsDialog = new SettingsDialog( );
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

			final ImportThread importThread = new ImportThread( directory, ImportType.DIRECTORY );
			importThread.start( );
		}
	}

	/**
	 * This method is only to be used from the UI tests to bypass the native dialogs in the test environments.
	 */
	public void performImportLog( final File directory ) {
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

			final ImportThread importThread = new ImportThread( file, ImportType.ZIP_FILE );
			importThread.start( );
		}
	}

	private File getInitialDirectory( ) {
		final PropertiesService propertiesService = ServiceFactory.getService( PropertiesService.class );
		final String lastImportPath = propertiesService.loadApplicationProperty( LastImportPathProperty.class );
		return new File( lastImportPath );
	}

	private void setNewInitialDirectory( final File lastImportDirectory, final File directory ) {
		final PropertiesService propertiesService = ServiceFactory.getService( PropertiesService.class );
		if ( !directory.equals( lastImportDirectory ) ) {
			propertiesService.saveApplicationProperty( LastImportPathProperty.class, directory.getAbsolutePath( ) );
		}
	}

	private Window getWindow( ) {
		final Scene scene = getScene( );
		return scene.getWindow( );
	}

	public void performSaveAsFavorite( final Tab tab, final Object filter ) {
		try {
			final TextInputDialog textInputDialog = new TextInputDialog( );
			textInputDialog.setTitle( RESOURCE_BUNDLE.getString( "newFilterFavorite" ) );
			textInputDialog.setHeaderText( RESOURCE_BUNDLE.getString( "newFilterFavoriteName" ) );

			final String iconPath = RESOURCE_BUNDLE.getString( "iconNewFavorite" );
			final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
			final Image icon = new Image( iconStream );
			final DialogPane dialogPane = textInputDialog.getDialogPane( );
			final Stage stage = (Stage) dialogPane.getScene( ).getWindow( );
			stage.getIcons( ).add( icon );
			dialogPane.getStylesheets( ).add( "/kieker/diagnosis/frontend/base/ui/Dialog.css" );
			dialogPane.lookupButton( ButtonType.OK ).setId( "favoriteFilterDialogOk" );
			dialogPane.lookupButton( ButtonType.CANCEL ).setId( "favoriteFilterDialogCancel" );
			final Optional<String> result = textInputDialog.showAndWait( );

			if ( result.isPresent( ) ) {
				final String text = result.get( );

				// Check whether the text is valid
				if ( text == null || text.trim( ).isEmpty( ) ) {
					throw new BusinessException( RESOURCE_BUNDLE.getString( "errorEmptyFilterName" ) );
				}

				// Now we can add the favorite to the menu
				menuBar.addFavorite( ( ) -> mainTabPane.showTab( tab, filter ), text );
			}
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performClose( ) {
		menuBar.performClose( );
	}

	private class ImportThread extends Thread {

		private final File ivDirectoryOrFile;
		private final ImportType ivType;

		public ImportThread( final File aDirectoryOrFile, final ImportType aType ) {
			ivDirectoryOrFile = aDirectoryOrFile;
			ivType = aType;
			setName( "Monitoring Import Thread" );
		}

		@Override
		public void run( ) {
			// Show the progress dialog
			final ProgressDialog importerDialogView = new ProgressDialog( );
			Platform.runLater( ( ) -> {
				importerDialogView.setMessage( RESOURCE_BUNDLE.getString( "processImport" ) );
				importerDialogView.setProgress( -1.0 );
				importerDialogView.open( getWindow( ) );
			} );

			try {
				// Load the monitoring log
				BusinessRuntimeException exception = null;
				final MonitoringLogService monitoringLogService = ServiceFactory.getService( MonitoringLogService.class );
				try {
					monitoringLogService.importMonitoringLog( ivDirectoryOrFile, ivType );
				} catch ( final BusinessRuntimeException ex ) {
					// If a business exception occurs, we still want to refresh, but we also want to display the
					// exception.
					exception = ex;
				}

				// Now refresh everything
				Platform.runLater( ( ) -> {
					importerDialogView.setMessage( RESOURCE_BUNDLE.getString( "processRefresh" ) );
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
					importerDialogView.close( );
				} );
			}
		}
	}

}
