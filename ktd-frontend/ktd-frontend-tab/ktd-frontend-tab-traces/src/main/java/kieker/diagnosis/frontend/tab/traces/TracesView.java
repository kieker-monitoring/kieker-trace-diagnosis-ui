/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.traces;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.frontend.base.ui.ViewBase;
import kieker.diagnosis.frontend.tab.traces.composite.TraceDetailsPane;
import kieker.diagnosis.frontend.tab.traces.composite.TraceFilterPane;
import kieker.diagnosis.frontend.tab.traces.composite.TraceStatusBar;
import kieker.diagnosis.frontend.tab.traces.composite.TracesTreeTableView;

/**
 * The view of the traces tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class TracesView extends ViewBase<TracesController> {

	private final TraceFilterPane filterPane;
	private final TracesTreeTableView treeTableView;
	private final TraceDetailsPane detailsPane;
	private final TraceStatusBar statusBar;

	@Inject
	public TracesView( ) {
		{
			filterPane = new TraceFilterPane( );
			filterPane.setOnSearch( e -> getController( ).performSearch( ) );
			filterPane.setOnSaveAsFavorite( e -> getController( ).performSaveAsFavorite( ) );

			getChildren( ).add( filterPane );
		}

		{
			treeTableView = new TracesTreeTableView( );
			treeTableView.setId( "tabTracesTreeTable" );
			treeTableView.addSelectionChangeListener( ( aObservable, aOldValue, aNewValue ) -> getController( ).performSelectionChange( ) );

			VBox.setVgrow( treeTableView, Priority.ALWAYS );

			getChildren( ).add( treeTableView );
		}

		{
			detailsPane = new TraceDetailsPane( );

			getChildren( ).add( detailsPane );
		}

		{
			statusBar = new TraceStatusBar( );

			VBox.setMargin( statusBar, new Insets( 5 ) );

			getChildren( ).add( statusBar );
		}

	}

	TraceFilterPane getFilter( ) {
		return filterPane;
	}

	TracesTreeTableView getTreeTableView( ) {
		return treeTableView;
	}

	TraceDetailsPane getDetails( ) {
		return detailsPane;
	}

	TraceStatusBar getStatus( ) {
		return statusBar;
	}

	TreeTableColumn<MethodCall, Long> getDurationColumn( ) {
		return treeTableView.getDurationColumn( );
	}

	@Override
	public void setParameter( final Object aParameter ) {
		getController( ).performSetParameter( aParameter );
	}

	/**
	 * Returns the default button property of the search button.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

}
