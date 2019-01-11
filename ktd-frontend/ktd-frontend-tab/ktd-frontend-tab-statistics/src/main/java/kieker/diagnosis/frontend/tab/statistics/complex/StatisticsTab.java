package kieker.diagnosis.frontend.tab.statistics.complex;

import java.util.Optional;

import javafx.scene.control.Tab;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import kieker.diagnosis.backend.base.service.ServiceFactory;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.backend.search.statistics.StatisticsService;
import kieker.diagnosis.frontend.tab.statistics.composite.StatisticsMemoryUsageBar;
import kieker.diagnosis.frontend.tab.statistics.composite.StatisticsPane;

public final class StatisticsTab extends Tab {

	private final StatisticsPane statisticsPane = new StatisticsPane( );
	private final StatisticsMemoryUsageBar memoryUsageBar = new StatisticsMemoryUsageBar( );

	private Optional<Statistics> statisticsForRefresh;

	public StatisticsTab( ) {
		final VBox vbox = new VBox( );
		setContent( vbox );

		VBox.setVgrow( statisticsPane, Priority.ALWAYS );
		vbox.getChildren( ).add( statisticsPane );

		vbox.getChildren( ).add( memoryUsageBar );

		initialize( );
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
