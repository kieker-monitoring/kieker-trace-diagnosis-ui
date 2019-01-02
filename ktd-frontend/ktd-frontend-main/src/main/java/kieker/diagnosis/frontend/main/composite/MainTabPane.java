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

package kieker.diagnosis.frontend.main.composite;

import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import kieker.diagnosis.frontend.tab.aggregatedmethods.AggregatedMethodsView;
import kieker.diagnosis.frontend.tab.methods.MethodsView;
import kieker.diagnosis.frontend.tab.statistics.StatisticsView;
import kieker.diagnosis.frontend.tab.traces.TracesView;

public class MainTabPane extends TabPane {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( MainTabPane.class.getName( ) );

	private final TracesView tracesView;
	private final MethodsView methodsView;
	private final AggregatedMethodsView aggregatedMethodsView;
	private final StatisticsView statisticsView;

	@Inject
	public MainTabPane( final TracesView tracesView, final MethodsView methodsView, final AggregatedMethodsView aggregatedMethodsView, final StatisticsView statisticsView ) {
		{
			setTabClosingPolicy( TabClosingPolicy.UNAVAILABLE );

			{
				this.tracesView = tracesView;
				tracesView.initialize( );

				final Tab tab = new Tab( );
				tab.setId( "tabTraces" );

				tab.setText( RESOURCE_BUNDLE.getString( "traces" ) );
				tab.setContent( tracesView );

				getTabs( ).add( tab );

				// Only one default button is allowed - even if the other buttons are not
				// visible. Therefore we have to set the default
				// button property only for the current tab.
				tracesView.defaultButtonProperty( ).bind( getSelectionModel( ).selectedItemProperty( ).isEqualTo( tab ) );
			}

			{
				this.methodsView = methodsView;
				methodsView.initialize( );

				final Tab tab = new Tab( );
				tab.setId( "tabMethods" );

				tab.setText( RESOURCE_BUNDLE.getString( "methods" ) );
				tab.setContent( methodsView );

				getTabs( ).add( tab );
				// Only one default button is allowed - even if the other buttons are not
				// visible. Therefore we have to set the default
				// button property only for the current tab.
				methodsView.defaultButtonProperty( ).bind( getSelectionModel( ).selectedItemProperty( ).isEqualTo( tab ) );
			}

			{

				this.aggregatedMethodsView = aggregatedMethodsView;
				aggregatedMethodsView.initialize( );

				final Tab tab = new Tab( );
				tab.setId( "tabAggregatedMethods" );

				tab.setText( RESOURCE_BUNDLE.getString( "aggregatedMethods" ) );
				tab.setContent( aggregatedMethodsView );

				getTabs( ).add( tab );

				// Only one default button is allowed - even if the other buttons are not
				// visible. Therefore we have to set the default
				// button property only for the current tab.
				aggregatedMethodsView.defaultButtonProperty( ).bind( getSelectionModel( ).selectedItemProperty( ).isEqualTo( tab ) );
			}

			{

				this.statisticsView = statisticsView;
				statisticsView.initialize( );

				final Tab tab = new Tab( );
				tab.setId( "tabStatistics" );

				tab.setText( RESOURCE_BUNDLE.getString( "statistics" ) );
				tab.setContent( statisticsView );

				getTabs( ).add( tab );
			}
		}
	}

	public void prepareRefresh( ) {
		tracesView.prepareRefresh( );
		methodsView.prepareRefresh( );
		aggregatedMethodsView.prepareRefresh( );
		statisticsView.prepareRefresh( );
	}

	public void performRefresh( ) {
		tracesView.performRefresh( );
		methodsView.performRefresh( );
		aggregatedMethodsView.performRefresh( );
		statisticsView.performRefresh( );
	}

}
