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

package kieker.diagnosis.frontend.complex.statistics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Optional;

import com.google.inject.Singleton;

import javafx.application.Platform;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.backend.search.statistics.StatisticsService;
import kieker.diagnosis.frontend.base.ui.ControllerBase;

/**
 * The controller of the statistics tab.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class StatisticsController extends ControllerBase<StatisticsViewModel> {

	private Optional<Statistics> ivStatistics;

	public StatisticsController( ) {
		// Start a thread to update the memory usage regularly
		final Thread thread = new Thread( ( ) -> {
			while ( true ) {
				final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean( );
				final long usedHeap = memoryMXBean.getHeapMemoryUsage( ).getUsed( ) / 1024 / 1024;
				final long committedHeap = memoryMXBean.getHeapMemoryUsage( ).getCommitted( ) / 1024 / 1024;

				Platform.runLater( ( ) -> {
					getViewModel( ).updatePresentationMemoryUsage( usedHeap, committedHeap );
				} );

				try {
					Thread.sleep( 2500 );
				} catch ( final InterruptedException ex ) {
					// Can be ignored
				}
			}
		} );
		thread.setDaemon( true );
		thread.setName( "Statistics Memory Refresh Thread" );
		thread.start( );
	}

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentation( Optional.empty( ) );
	}

	/**
	 * This action is performed, when a refresh of the view is required
	 */
	public void performRefresh( ) {
		// Show the data on the view
		getViewModel( ).updatePresentation( ivStatistics );
	}

	public void performPrepareRefresh( ) {
		// Get the current data
		final StatisticsService statisticsService = getService( StatisticsService.class );
		ivStatistics = statisticsService.getStatistics( );
	}

}
