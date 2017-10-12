package kieker.diagnosis.ui.statistics;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;

import com.google.inject.Singleton;

import javafx.application.Platform;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.service.statistics.Statistics;
import kieker.diagnosis.service.statistics.StatisticsService;

@Singleton
class StatisticsController extends ControllerBase<StatisticsViewModel> {

	private Statistics ivStatistics;

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
		thread.start( );
	}

	/**
	 * This action is performed once during the application's start.
	 */
	public void performInitialize( ) {
		getViewModel( ).updatePresentation( null );
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
