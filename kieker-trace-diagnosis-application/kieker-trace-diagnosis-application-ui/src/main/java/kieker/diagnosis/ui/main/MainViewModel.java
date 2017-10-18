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

import com.google.inject.Singleton;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.stage.Window;
import kieker.diagnosis.architecture.ui.ViewBase;
import kieker.diagnosis.architecture.ui.ViewModelBase;

@Singleton
public class MainViewModel extends ViewModelBase<MainView> {

	private int ivFavorites;

	/**
	 * Returns the window of the view. Only necessary to show dialogs.
	 *
	 * @return The window of the view.
	 */
	public Window getWindow( ) {
		final Scene scene = getView( ).getScene( );
		return scene.getWindow( );
	}

	public void refresh( ) {
		getView( ).prepareRefresh( );
		getView( ).performRefresh( );
	}

	public void prepareRefresh( ) {
		getView( ).prepareRefresh( );
	}

	public void performRefresh( ) {
		getView( ).performRefresh( );
	}

	public void showTab( final Class<? extends ViewBase<?>> aViewClass, final Object aParameter ) {
		final ObservableList<Tab> tabs = getView( ).getTabPane( ).getTabs( );
		for ( final Tab tab : tabs ) {
			final Node content = tab.getContent( );
			if ( aViewClass.isInstance( content ) ) {
				final ViewBase<?> view = (ViewBase<?>) content;

				// We found the correct tab
				getView( ).getTabPane( ).getSelectionModel( ).select( tab );
				view.setParameter( aParameter );

				break;
			}
		}
	}

	public void addFavorite( final Runnable aCallback, final String aText ) {
		// Remove the placeholder menu item
		if ( ivFavorites == 0 ) {
			getView( ).getFavorites( ).getItems( ).clear( );
		}

		// Add the new menu item
		final MenuItem menuItem = new MenuItem( );
		menuItem.setText( aText );
		menuItem.setOnAction( e -> aCallback.run( ) );

		getView( ).getFavorites( ).getItems( ).add( menuItem );

		// Remember the number of favorites
		ivFavorites++;
	}

}
