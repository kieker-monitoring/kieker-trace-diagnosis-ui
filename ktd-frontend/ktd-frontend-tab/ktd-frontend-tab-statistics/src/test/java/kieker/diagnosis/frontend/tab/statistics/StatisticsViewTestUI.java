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
import javafx.scene.control.TextInputControl;
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

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new Module( ) );

		statisticsService = injector.getInstance( StatisticsService.class );
		when( statisticsService.getStatistics( ) ).thenReturn( Optional.empty( ) );

		statisticsView = injector.getInstance( StatisticsView.class );
		statisticsView.setParameter( null );
		statisticsView.initialize( );

		final Scene scene = new Scene( statisticsView );
		stage.setScene( scene );
		stage.show( );
	}

	@Test
	public void testStatisticsView( ) {
		final TextInputControl processedBytesTextField = lookup( "#statisticsProcessedBytes" ).queryTextInputControl( );
		assertThat( processedBytesTextField.getText( ), is( "<Keine Daten verfügbar> " ) );

		final TextInputControl processDuration = lookup( "#statisticsProcessDuration" ).queryTextInputControl( );
		assertThat( processDuration.getText( ), is( "<Keine Daten verfügbar> " ) );

		final TextInputControl processSpeed = lookup( "#statisticsProcessSpeed" ).queryTextInputControl( );
		assertThat( processSpeed.getText( ), is( "<Keine Daten verfügbar> " ) );

		when( statisticsService.getStatistics( ) ).thenReturn( createFirstStatistics( ) );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );

		assertThat( processedBytesTextField.getText( ), is( "250 [B]" ) );
		assertThat( processDuration.getText( ), is( "10 [ms]" ) );
		assertThat( processSpeed.getText( ), is( "1000 [B/s]" ) );

		when( statisticsService.getStatistics( ) ).thenReturn( createSecondStatistics( ) );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );

		assertThat( processedBytesTextField.getText( ), is( "244 [KB]" ) );
		assertThat( processDuration.getText( ), is( "10 [s]" ) );
		assertThat( processSpeed.getText( ), is( "9 [KB/s]" ) );

		when( statisticsService.getStatistics( ) ).thenReturn( createThirdStatistics( ) );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );

		assertThat( processedBytesTextField.getText( ), is( "238 [MB]" ) );
		assertThat( processDuration.getText( ), is( "10 [m]" ) );
		assertThat( processSpeed.getText( ), is( "9 [MB/s]" ) );
	}

	private Optional<Statistics> createFirstStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250L )
				.processDuration( 10L )
				.processSpeed( 1L )
				.build( ) );
	}

	private Optional<Statistics> createSecondStatistics( ) {
		return Optional.of( Statistics
				.builder( )
				.processedBytes( 250000L )
				.processDuration( 10000L )
				.processSpeed( 10L )
				.build( ) );
	}

	private Optional<Statistics> createThirdStatistics( ) {
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
