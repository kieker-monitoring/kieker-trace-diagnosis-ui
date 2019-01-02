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

package kieker.diagnosis.frontend.main;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.frontend.base.ui.ViewBase;
import kieker.diagnosis.frontend.main.composite.MainTabPane;

@Singleton
public class MainView extends ViewBase<MainController> {

	private final Menu ivFavorites;
	private final MainTabPane mainTabPane;

	@Inject
	public MainView( final MainTabPane mainTabPane ) {
		// Main menu
		{
			final MenuBar menuBar = new MenuBar( );

			{
				final Menu menu = new Menu( );
				menu.setText( getLocalizedString( "file" ) );
				menu.setId( "menuFile" );

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "importLog" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performImportLog( ) );
					menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+O" ) );
					menuItem.setGraphic( createIcon( Icon.FOLDER_OPEN ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "importLogFromZip" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performImportLogFromZip( ) );
					menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+Z" ) );
					menuItem.setGraphic( createIcon( Icon.ZIP_ARCHIVE ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem( );

					menu.getItems( ).add( separatorMenuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setId( "menuItemMonitoringSettings" );
					menuItem.setText( getLocalizedString( "monitoring" ) );
					menuItem.setOnAction( e -> getController( ).performMonitoring( ) );
					menuItem.setGraphic( createIcon( Icon.CHART ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setId( "menuItemSettings" );
					menuItem.setText( getLocalizedString( "settings" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performSettings( ) );
					menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+S" ) );
					menuItem.setGraphic( createIcon( Icon.COGS ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem( );

					menu.getItems( ).add( separatorMenuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setId( "menuFileClose" );
					menuItem.setText( getLocalizedString( "close" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performClose( ) );
					menuItem.setAccelerator( KeyCombination.keyCombination( "Ctrl+X" ) );
					menuItem.setGraphic( createIcon( Icon.TIMES ) );

					menu.getItems( ).add( menuItem );
				}

				menuBar.getMenus( ).add( menu );
			}

			{
				ivFavorites = new Menu( );
				ivFavorites.setId( "menuFavorites" );
				ivFavorites.setText( getLocalizedString( "favorites" ) );

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "noFavoritesAvailable" ) );
					menuItem.setDisable( true );

					ivFavorites.getItems( ).add( menuItem );
				}

				menuBar.getMenus( ).add( ivFavorites );
			}

			{
				final Menu menu = new Menu( );
				menu.setId( "menuHelp" );
				menu.setText( getLocalizedString( "help" ) );

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setId( "menuItemManual" );
					menuItem.setText( getLocalizedString( "documentation" ) );
					menuItem.setOnAction( e -> getController( ).performDocumentation( ) );
					menuItem.setAccelerator( KeyCombination.keyCombination( "F1" ) );
					menuItem.setGraphic( createIcon( Icon.QUESTION_CIRCLE ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem( );

					menu.getItems( ).add( separatorMenuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setId( "menuItemAbout" );
					menuItem.setText( getLocalizedString( "about" ) );
					menuItem.setOnAction( e -> getController( ).performAbout( ) );
					menuItem.setGraphic( createIcon( Icon.INFO_CIRCLE ) );

					menu.getItems( ).add( menuItem );
				}

				menuBar.getMenus( ).add( menu );
			}

			getChildren( ).add( menuBar );
		}

		{
			this.mainTabPane = mainTabPane;
			mainTabPane.setId( "mainTabPane" );

			VBox.setVgrow( mainTabPane, Priority.ALWAYS );

			getChildren( ).add( mainTabPane );
		}
	}

	Menu getFavorites( ) {
		return ivFavorites;
	}

	@Override
	public void setParameter( final Object aParameter ) {

	}

	public void prepareRefresh( ) {
		mainTabPane.prepareRefresh( );
	}

	public void performRefresh( ) {
		mainTabPane.performRefresh( );
	}

	TabPane getTabPane( ) {
		return mainTabPane;
	}

}
