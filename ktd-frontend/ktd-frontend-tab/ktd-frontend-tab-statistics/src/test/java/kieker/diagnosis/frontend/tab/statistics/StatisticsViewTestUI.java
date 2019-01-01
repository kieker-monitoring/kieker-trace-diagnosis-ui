package kieker.diagnosis.frontend.tab.statistics;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Stage;
import kieker.diagnosis.backend.search.statistics.Statistics;
import kieker.diagnosis.backend.search.statistics.StatisticsService;

/**
 * This is a UI test which checks that the statistics view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class StatisticsViewTestUI extends ApplicationTest {

	private StatisticsService statisticsService;
	private StatisticsView statisticsView;
	private StatisticsPage statisticsPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new Module( ) );

		statisticsService = injector.getInstance( StatisticsService.class );

		statisticsView = injector.getInstance( StatisticsView.class );
		statisticsView.setParameter( null );
		statisticsView.initialize( );

		final Scene scene = new Scene( statisticsView );
		stage.setScene( scene );
		stage.show( );

		statisticsPage = new StatisticsPage( this );
	}

	@Test
	public void testWithoutData( ) {
		loadData( createFirstStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ), is( "<Keine Daten verfügbar> " ) );
		assertThat( statisticsPage.getProcessDuration( ).getText( ), is( "<Keine Daten verfügbar> " ) );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ), is( "<Keine Daten verfügbar> " ) );
	}

	private void loadData( final Optional<Statistics> statistics ) {
		when( statisticsService.getStatistics( ) ).thenReturn( statistics );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );
	}

	private Optional<Statistics> createFirstStatistics( ) {
		return Optional.empty( );
	}

	@Test
	public void testWithFirstUnits( ) {
		loadData( createSecondStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ), is( "250 [B]" ) );
		assertThat( statisticsPage.getProcessDuration( ).getText( ), is( "10 [ms]" ) );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ), is( "1000 [B/s]" ) );
	}

	private Optional<Statistics> createSecondStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250L )
				.processDuration( 10L )
				.processSpeed( 1L )
				.build( ) );
	}

	@Test
	public void testWithSecondsUnits( ) {
		loadData( createThirdStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ), is( "244 [KB]" ) );
		assertThat( statisticsPage.getProcessDuration( ).getText( ), is( "10 [s]" ) );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ), is( "9 [KB/s]" ) );
	}

	private Optional<Statistics> createThirdStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250000L )
				.processDuration( 10000L )
				.processSpeed( 10L )
				.build( ) );
	}

	@Test
	public void testWithThirdUnits( ) {
		loadData( createFourthStatistics( ) );

		assertThat( statisticsPage.getProcessedBytes( ).getText( ), is( "238 [MB]" ) );
		assertThat( statisticsPage.getProcessDuration( ).getText( ), is( "10 [m]" ) );
		assertThat( statisticsPage.getProcessSpeed( ).getText( ), is( "9 [MB/s]" ) );
	}

	private Optional<Statistics> createFourthStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250000000L )
				.processDuration( 600000L )
				.processSpeed( 9999L )
				.build( ) );
	}

	private static final class Module extends AbstractModule {

		@Override
		protected void configure( ) {
			bind( StatisticsService.class ).toInstance( mock( StatisticsService.class ) );
		}

	}

}
