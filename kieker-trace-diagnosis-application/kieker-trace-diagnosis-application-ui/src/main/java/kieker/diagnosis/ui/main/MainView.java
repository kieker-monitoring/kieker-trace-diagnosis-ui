package kieker.diagnosis.ui.main;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.ui.aggregatedmethods.AggregatedMethodsView;
import kieker.diagnosis.ui.methods.MethodsView;
import kieker.diagnosis.ui.statistics.StatisticsView;
import kieker.diagnosis.ui.traces.TracesView;

@Singleton
public class MainView extends ViewBase<MainController> {

	private final TracesView ivTracesView;
	private final MethodsView ivMethodsView;
	private final AggregatedMethodsView ivAggregatedMethodsView;
	private final StatisticsView ivStatisticsView;
	private final TabPane ivTabPane;
	private final Menu ivFavorites;

	@Inject
	public MainView( final TracesView aTracesView, final MethodsView aMethodsView, final AggregatedMethodsView aAggregatedMethodsView, final StatisticsView aStatisticsView ) {
		// Main menu
		{
			final MenuBar menuBar = new MenuBar( );

			{
				final Menu menu = new Menu( );
				menu.setText( getLocalizedString( "file" ) );
				menu.setId( "file" );

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "importLog" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performImportLog( ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem( );

					menu.getItems( ).add( separatorMenuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "monitoring" ) );
					menuItem.setOnAction( e -> getController( ).performMonitoring( ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "settings" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performSettings( ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem( );

					menu.getItems( ).add( separatorMenuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setId( "close" );
					menuItem.setText( getLocalizedString( "close" ) );
					menuItem.setOnAction( ( e ) -> getController( ).performClose( ) );

					menu.getItems( ).add( menuItem );
				}

				menuBar.getMenus( ).add( menu );
			}

			{
				ivFavorites = new Menu( );
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
				menu.setText( getLocalizedString( "help" ) );

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "documentation" ) );
					menuItem.setOnAction( e -> getController( ).performDocumentation( ) );

					menu.getItems( ).add( menuItem );
				}

				{
					final SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem( );

					menu.getItems( ).add( separatorMenuItem );
				}

				{
					final MenuItem menuItem = new MenuItem( );
					menuItem.setText( getLocalizedString( "about" ) );
					menuItem.setOnAction( e -> getController( ).performAbout( ) );

					menu.getItems( ).add( menuItem );
				}

				menuBar.getMenus( ).add( menu );
			}

			getChildren( ).add( menuBar );
		}

		// Tab pane
		{
			ivTabPane = new TabPane( );
			ivTabPane.setTabClosingPolicy( TabClosingPolicy.UNAVAILABLE );

			VBox.setVgrow( ivTabPane, Priority.ALWAYS );

			{
				ivTracesView = aTracesView;
				ivTracesView.initialize( );

				final Tab tab = new Tab( );

				tab.setText( getLocalizedString( "traces" ) );
				tab.setContent( aTracesView );

				ivTabPane.getTabs( ).add( tab );

				// Only one default button is allowed - even if the other buttons are not visible. Therefore we have to set the default button property only for
				// the current tab.
				ivTracesView.getSearchButton( ).defaultButtonProperty( ).bind( ivTabPane.getSelectionModel( ).selectedItemProperty( ).isEqualTo( tab ) );
			}

			{
				ivMethodsView = aMethodsView;
				ivMethodsView.initialize( );

				final Tab tab = new Tab( );

				tab.setText( getLocalizedString( "methods" ) );
				tab.setContent( aMethodsView );

				ivTabPane.getTabs( ).add( tab );
				// Only one default button is allowed - even if the other buttons are not visible. Therefore we have to set the default button property only for
				// the current tab.
				ivMethodsView.getSearchButton( ).defaultButtonProperty( ).bind( ivTabPane.getSelectionModel( ).selectedItemProperty( ).isEqualTo( tab ) );
			}

			{

				ivAggregatedMethodsView = aAggregatedMethodsView;
				ivAggregatedMethodsView.initialize( );

				final Tab tab = new Tab( );

				tab.setText( getLocalizedString( "aggregatedMethods" ) );
				tab.setContent( aAggregatedMethodsView );

				ivTabPane.getTabs( ).add( tab );

				// Only one default button is allowed - even if the other buttons are not visible. Therefore we have to set the default button property only for
				// the current tab.
				ivAggregatedMethodsView.getSearchButton( ).defaultButtonProperty( ).bind( ivTabPane.getSelectionModel( ).selectedItemProperty( ).isEqualTo( tab ) );
			}

			{

				ivStatisticsView = aStatisticsView;
				ivStatisticsView.initialize( );

				final Tab tab = new Tab( );

				tab.setText( getLocalizedString( "statistics" ) );
				tab.setContent( aStatisticsView );

				ivTabPane.getTabs( ).add( tab );
			}

			getChildren( ).add( ivTabPane );
		}
	}

	Menu getFavorites( ) {
		return ivFavorites;
	}

	@Override
	public void setParameter( final Object aParameter ) {

	}

	public void prepareRefresh( ) {
		ivTracesView.prepareRefresh( );
		ivMethodsView.prepareRefresh( );
		ivAggregatedMethodsView.prepareRefresh( );
		ivStatisticsView.prepareRefresh( );
	}

	public void performRefresh( ) {
		ivTracesView.performRefresh( );
		ivMethodsView.performRefresh( );
		ivAggregatedMethodsView.performRefresh( );
		ivStatisticsView.performRefresh( );
	}

	TabPane getTabPane( ) {
		return ivTabPane;
	}

}
