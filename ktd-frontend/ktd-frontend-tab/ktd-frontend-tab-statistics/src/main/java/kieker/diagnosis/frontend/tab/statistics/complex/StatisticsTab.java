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

package kieker.diagnosis.frontend.tab.statistics.complex;

import java.util.Optional;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.backend.search.statistics.StatisticsService;
import kieker.diagnosis.frontend.tab.statistics.composite.StatisticsCPUUsageBar;
import kieker.diagnosis.frontend.tab.statistics.composite.StatisticsMemoryUsageBar;
import kieker.diagnosis.frontend.tab.statistics.composite.StatisticsPane;

public final class StatisticsTab extends Tab {

	private StatisticsPane statisticsPane;

	private Optional<Statistics> statisticsForRefresh;

	public StatisticsTab( ) {
		createControl( );

		initialize( );
	}

	private void createControl( ) {
		final VBox vbox = new VBox( );

		vbox.getChildren( ).add( createStatisticsPane( ) );
		vbox.getChildren( ).add( createCPUUsageBar( ) );
		vbox.getChildren( ).add( createMemoryUsageBar( ) );

		setContent( vbox );
	}

	private Node createStatisticsPane( ) {
		statisticsPane = new StatisticsPane( );

		VBox.setVgrow( statisticsPane, Priority.ALWAYS );

		return statisticsPane;
	}

	private Node createCPUUsageBar( ) {
		return new StatisticsCPUUsageBar( );
	}

	private Node createMemoryUsageBar( ) {
		return new StatisticsMemoryUsageBar( );
	}

	private void initialize( ) {
		statisticsPane.setValue( Optional.empty( ) );
	}

	public void prepareRefresh( ) {
		final StatisticsService statisticsService = ServiceFactory.getService( StatisticsService.class );
		statisticsForRefresh = statisticsService.getStatistics( );
	}

	public void performRefresh( ) {
		statisticsPane.setValue( statisticsForRefresh );
	}

}
