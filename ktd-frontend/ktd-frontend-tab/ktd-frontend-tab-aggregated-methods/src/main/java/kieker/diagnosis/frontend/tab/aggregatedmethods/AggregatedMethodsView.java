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

package kieker.diagnosis.frontend.tab.aggregatedmethods;

import com.google.inject.Singleton;

import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.frontend.base.ui.ViewBase;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodDetailsPane;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodFilterPane;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodStatusBar;
import kieker.diagnosis.frontend.tab.aggregatedmethods.composite.AggregatedMethodsTableView;

/**
 * The view of the aggregated methods tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class AggregatedMethodsView extends ViewBase<AggregatedMethodsController> {

	private final AggregatedMethodFilterPane filterPane;
	private final AggregatedMethodsTableView methodsTableView;
	private final AggregatedMethodDetailsPane detailsPane;
	private final AggregatedMethodStatusBar statusBar;

	public void initialize( ) {
		getController( ).performInitialize( );
	}

	public AggregatedMethodsView( ) {

		{
			filterPane = new AggregatedMethodFilterPane( );
			filterPane.setOnSearch( e -> getController( ).performSearch( ) );
			filterPane.setOnSaveAsFavorite( e -> getController( ).performSaveAsFavorite( ) );

			getChildren( ).add( filterPane );
		}

		{
			methodsTableView = new AggregatedMethodsTableView( );
			methodsTableView.setId( "tabAggregatedMethodsTable" );
			methodsTableView.addSelectionChangeListener( ( aObservable, aOldValue, aNewValue ) -> getController( ).performSelectionChange( ) );

			VBox.setVgrow( methodsTableView, Priority.ALWAYS );

			getChildren( ).add( methodsTableView );

		}

		{
			detailsPane = new AggregatedMethodDetailsPane( );
			detailsPane.setOnJumpToMethods( e -> getController( ).performJumpToMethods( ) );

			getChildren( ).add( detailsPane );
		}

		{
			statusBar = new AggregatedMethodStatusBar( );
			statusBar.setOnExportToCsv( e -> getController( ).performExportToCSV( ) );

			VBox.setMargin( statusBar, new Insets( 5 ) );

			getChildren( ).add( statusBar );
		}
	}

	AggregatedMethodFilterPane getFilter( ) {
		return filterPane;
	}

	TableColumn<AggregatedMethodCall, String> getColumnMinDuration( ) {
		return methodsTableView.getColumnMinDuration( );
	}

	TableColumn<AggregatedMethodCall, String> getColumnAvgDuration( ) {
		return methodsTableView.getColumnAvgDuration( );
	}

	TableColumn<AggregatedMethodCall, String> getColumnMedianDuration( ) {
		return methodsTableView.getColumnMedianDuration( );
	}

	TableColumn<AggregatedMethodCall, String> getColumnMaxDuration( ) {
		return methodsTableView.getColumnMaxDuration( );
	}

	TableColumn<AggregatedMethodCall, String> getColumnTotalDuration( ) {
		return methodsTableView.getColumnTotalDuration( );
	}

	/**
	 * Returns the default button property of the search button.
	 */
	public BooleanProperty defaultButtonProperty( ) {
		return filterPane.defaultButtonProperty( );
	}

	AggregatedMethodStatusBar getStatus( ) {
		return statusBar;
	}

	AggregatedMethodDetailsPane getDetails( ) {
		return detailsPane;
	}

	@Override
	public void setParameter( final Object aParameter ) {
		getController( ).performSetParameter( aParameter );
	}

	public void prepareRefresh( ) {
		getController( ).performPrepareRefresh( );
	}

	public void performRefresh( ) {
		getController( ).performRefresh( );
	}

	AggregatedMethodsTableView getTableView( ) {
		return methodsTableView;
	}

}
