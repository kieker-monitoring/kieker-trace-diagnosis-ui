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

import java.util.Optional;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.application.HostServices;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.Window;
import kieker.diagnosis.backend.monitoring.MonitoringConfiguration;
import kieker.diagnosis.backend.monitoring.MonitoringService;
import kieker.diagnosis.backend.monitoring.Status;
import kieker.diagnosis.backend.properties.DevelopmentModeProperty;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.frontend.base.common.HostServicesHolder;
import kieker.diagnosis.frontend.base.mixin.IconMixin;
import kieker.diagnosis.frontend.dialog.about.AboutDialog;
import kieker.diagnosis.frontend.dialog.alert.Alert;
import kieker.diagnosis.frontend.dialog.monitoring.MonitoringDialog;
import kieker.diagnosis.frontend.main.properties.CloseWithoutPromptProperty;

public final class MainMenuBar extends MenuBar implements IconMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MainMenuBar.class.getName( ) );

	private final PropertiesService propertiesService;
	private final MonitoringService monitoringService;

	private Runnable onSettings;
	private Runnable onImportLogFromZip;
	private Runnable onImportLog;

	private int favorites;
	private Menu favoritesMenu;

	@Inject
	public MainMenuBar( final PropertiesService propertiesService, final MonitoringService monitoringService ) {
		this.propertiesService = propertiesService;
		this.monitoringService = monitoringService;
		createControl( );
	}

	private void createControl( ) {
		getMenus( ).add( createFileMenu( ) );
		getMenus( ).add( createFavoritesMenu( ) );
		getMenus( ).add( createHelpMenu( ) );
	}

	private Menu createFileMenu( ) {
		final Menu menu = new Menu( );

		menu.setText( RESOURCE_BUNDLE.getString( "file" ) );
		menu.setId( "menuFile" );

		menu.getItems( ).add( createImportMonitoringLogMenuItem( ) );
		menu.getItems( ).add( createImportMonitoringZipMenuItem( ) );
		menu.getItems( ).add( new SeparatorMenuItem( ) );
		menu.getItems( ).add( createMonitoringMenuItem( ) );
		menu.getItems( ).add( createSettingsMenuItem( ) );
		menu.getItems( ).add( new SeparatorMenuItem( ) );
		menu.getItems( ).add( createCloseMenuItem( ) );

		return menu;
	}

	private MenuItem createImportMonitoringLogMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );

		menuItem.setText( RESOURCE_BUNDLE.getString( "importLog" ) );
		menuItem.setOnAction( ( e ) -> performImportLog( ) );
		menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+O" ) );
		menuItem.setGraphic( createIcon( Icon.FOLDER_OPEN ) );

		return menuItem;
	}

	private MenuItem createImportMonitoringZipMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );
		menuItem.setText( RESOURCE_BUNDLE.getString( "importLogFromZip" ) );
		menuItem.setOnAction( ( e ) -> performImportLogFromZip( ) );
		menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+Z" ) );
		menuItem.setGraphic( createIcon( Icon.ZIP_ARCHIVE ) );
		return menuItem;
	}

	private MenuItem createMonitoringMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );
		menuItem.setId( "menuItemMonitoringSettings" );
		menuItem.setText( RESOURCE_BUNDLE.getString( "monitoring" ) );
		menuItem.setOnAction( e -> performMonitoring( ) );
		menuItem.setGraphic( createIcon( Icon.CHART ) );
		return menuItem;
	}

	private MenuItem createSettingsMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );

		menuItem.setId( "menuItemSettings" );
		menuItem.setText( RESOURCE_BUNDLE.getString( "settings" ) );
		menuItem.setOnAction( e -> performSettings( ) );
		menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+S" ) );
		menuItem.setGraphic( createIcon( Icon.COGS ) );

		return menuItem;
	}

	private MenuItem createCloseMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );

		menuItem.setId( "menuFileClose" );
		menuItem.setText( RESOURCE_BUNDLE.getString( "close" ) );
		menuItem.setOnAction( ( e ) -> performClose( ) );
		menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+X" ) );
		menuItem.setGraphic( createIcon( Icon.TIMES ) );

		return menuItem;
	}

	private Menu createFavoritesMenu( ) {
		favoritesMenu = new Menu( );

		favoritesMenu.setId( "menuFavorites" );
		favoritesMenu.setText( RESOURCE_BUNDLE.getString( "favorites" ) );

		favoritesMenu.getItems( ).add( createNoFavoritesAvailableMenuItem( ) );

		return favoritesMenu;
	}

	private MenuItem createNoFavoritesAvailableMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );

		menuItem.setText( RESOURCE_BUNDLE.getString( "noFavoritesAvailable" ) );
		menuItem.setDisable( true );

		return menuItem;
	}

	private Menu createHelpMenu( ) {
		final Menu menu = new Menu( );

		menu.setId( "menuHelp" );
		menu.setText( RESOURCE_BUNDLE.getString( "help" ) );

		menu.getItems( ).add( createDocumentationMenuItem( ) );
		menu.getItems( ).add( new SeparatorMenuItem( ) );
		menu.getItems( ).add( createAboutMenuItem( ) );

		return menu;
	}

	private MenuItem createDocumentationMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );

		menuItem.setId( "menuItemManual" );
		menuItem.setText( RESOURCE_BUNDLE.getString( "documentation" ) );
		menuItem.setOnAction( e -> performDocumentation( ) );
		menuItem.setAccelerator( KeyCombination.keyCombination( "F1" ) );
		menuItem.setGraphic( createIcon( Icon.QUESTION_CIRCLE ) );

		return menuItem;
	}

	private MenuItem createAboutMenuItem( ) {
		final MenuItem menuItem = new MenuItem( );

		menuItem.setId( "menuItemAbout" );
		menuItem.setText( RESOURCE_BUNDLE.getString( "about" ) );
		menuItem.setOnAction( e -> performAbout( ) );
		menuItem.setGraphic( createIcon( Icon.INFO_CIRCLE ) );

		return menuItem;
	}

	private void performAbout( ) {
		final AboutDialog aboutDialog = new AboutDialog( );
		aboutDialog.showAndWait( );
	}

	private void performMonitoring( ) {
		final MonitoringConfiguration monitoringConfiguration = monitoringService.getCurrentConfiguration( );
		final Status status = monitoringService.getCurrentStatus( );

		final MonitoringDialog monitoringDialog = new MonitoringDialog( );
		monitoringDialog.setValue( monitoringConfiguration );
		monitoringDialog.setStatus( status );

		final Optional<MonitoringConfiguration> result = monitoringDialog.showAndWait( );
		result.ifPresent( monitoringService::configureMonitoring );
	}

	private void performDocumentation( ) {
		final HostServices hostServices = HostServicesHolder.getHostServices( );
		hostServices.showDocument( "https://github.com/kieker-monitoring/kieker-trace-diagnosis-ui/wiki" );
	}

	/**
	 * This action is performed, when the user tries to close the application.
	 */
	public void performClose( ) {
		final boolean developmentMode = propertiesService.loadSystemProperty( DevelopmentModeProperty.class );
		final boolean closeWithoutPrompt = propertiesService.loadApplicationProperty( CloseWithoutPromptProperty.class );

		if ( developmentMode || closeWithoutPrompt ) {
			// Just close the dialog
			close( );
		} else {
			// We should ask the user beforehand
			final Alert alert = new Alert( AlertType.CONFIRMATION );
			alert.setTitle( RESOURCE_BUNDLE.getString( "titleReallyClose" ) );
			alert.setHeaderText( RESOURCE_BUNDLE.getString( "headerReallyClose" ) );

			// Modify the buttons a little bit
			alert.getButtonTypes( ).remove( ButtonType.OK );
			alert.getButtonTypes( ).add( ButtonType.YES );

			final ButtonType alwaysYesButtonType = new ButtonType( RESOURCE_BUNDLE.getString( "buttonAlwaysYes" ) );
			alert.getButtonTypes( ).add( alwaysYesButtonType );

			final DialogPane dialogPane = alert.getDialogPane( );
			final Node yesButton = dialogPane.lookupButton( ButtonType.YES );
			yesButton.setId( "mainCloseDialogYes" );
			final Node cancelButton = dialogPane.lookupButton( ButtonType.CANCEL );
			cancelButton.setId( "mainCloseDialogCancel" );

			// If the user clicked ok, we close the window
			final Optional<ButtonType> result = alert.showAndWait( );
			if ( result.isPresent( ) && ( result.get( ) == ButtonType.YES || result.get( ) == alwaysYesButtonType ) ) {
				if ( result.get( ) == alwaysYesButtonType ) {
					propertiesService.saveApplicationProperty( CloseWithoutPromptProperty.class, Boolean.TRUE );
				}

				close( );
			}
		}
	}

	private void close( ) {
		final Scene scene = getScene( );
		final Window window = scene.getWindow( );
		window.hide( );
	}

	public void setOnSettings( final Runnable action ) {
		onSettings = action;
	}

	private void performSettings( ) {
		if ( onSettings != null ) {
			onSettings.run( );
		}
	}

	public void addFavorite( final Runnable aCallback, final String aText ) {
		// Remove the placeholder menu item
		if ( favorites == 0 ) {
			favoritesMenu.getItems( ).clear( );
		}

		// Add the new menu item
		final MenuItem menuItem = new MenuItem( );
		menuItem.setText( aText );
		menuItem.setOnAction( e -> aCallback.run( ) );

		favoritesMenu.getItems( ).add( menuItem );

		// Remember the number of favorites
		favorites++;
	}

	public void setOnImportLogFromZip( final Runnable action ) {
		onImportLogFromZip = action;
	}

	private void performImportLogFromZip( ) {
		if ( onImportLogFromZip != null ) {
			onImportLogFromZip.run( );
		}
	}

	public void setOnImportLog( final Runnable action ) {
		onImportLog = action;
	}

	private void performImportLog( ) {
		if ( onImportLog != null ) {
			onImportLog.run( );
		}
	}

}
