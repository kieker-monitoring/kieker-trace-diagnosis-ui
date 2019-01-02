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

package kieker.diagnosis.frontend.tab.methods;

import com.google.inject.Singleton;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.frontend.base.ui.ViewBase;
import kieker.diagnosis.frontend.tab.methods.composite.MethodDetailsPane;
import kieker.diagnosis.frontend.tab.methods.composite.MethodFilterPane;
import kieker.diagnosis.frontend.tab.methods.composite.MethodStatusBar;
import kieker.diagnosis.frontend.tab.methods.composite.MethodsTableView;

/**
 * The view of the methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MethodsView extends ViewBase<MethodsController> {

	private final MethodFilterPane filterPane;
	private final MethodsTableView methodsTableView;
	private final MethodDetailsPane detailsPane;
	private final MethodStatusBar statusBar;

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	public MethodsView( ) {
		{
			filterPane = new MethodFilterPane( );
			filterPane.setOnSearch( e -> getController( ).performSearch( ) );
			filterPane.setOnSaveAsFavorite( e -> getController( ).performSaveAsFavorite( ) );

			getChildren( ).add( filterPane );
		}

		{
			methodsTableView = new MethodsTableView( );
			methodsTableView.setId( "tabMethodsTable" );
			methodsTableView.addSelectionChangeListener( ( aObservable, aOldValue, aNewValue ) -> getController( ).performSelectionChange( ) );

			VBox.setVgrow( methodsTableView, Priority.ALWAYS );

			getChildren( ).add( methodsTableView );
		}

		{
			detailsPane = new MethodDetailsPane( );
			detailsPane.setOnJumpToTrace( e -> getController( ).performJumpToTrace( ) );

			getChildren( ).add( detailsPane );
		}

		{
			statusBar = new MethodStatusBar( );
			statusBar.setOnExportToCsv( e -> getController( ).performExportToCSV( ) );

			VBox.setMargin( statusBar, new Insets( 5 ) );

			getChildren( ).add( statusBar );
		}
	}

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	TableView<MethodCall> getMethods( ) {
		return methodsTableView;
	}

	TableColumn<MethodCall, String> getDurationColumn( ) {
		return methodsTableView.getDurationColumn( );
	}

	MethodFilterPane getFilter( ) {
		return filterPane;
	}

	/**
	 * Returns the default button property of the search button.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	MethodStatusBar getStatus( ) {
		return statusBar;
	}

	MethodDetailsPane getDetails( ) {
		return detailsPane;
	}

	@Override
	public void setParameter( final Object aParameter ) {
		getController( ).performSetParameter( aParameter );
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

}
