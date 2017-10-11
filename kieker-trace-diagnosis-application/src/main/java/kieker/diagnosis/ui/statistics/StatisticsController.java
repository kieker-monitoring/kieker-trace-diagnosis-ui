package kieker.diagnosis.ui.statistics;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.service.statistics.Statistics;
import kieker.diagnosis.service.statistics.StatisticsService;

@Singleton
class StatisticsController extends ControllerBase<StatisticsViewModel> {

	private Statistics ivStatistics;

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
