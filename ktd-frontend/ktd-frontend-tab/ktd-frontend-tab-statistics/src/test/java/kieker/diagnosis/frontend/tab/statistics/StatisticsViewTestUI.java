package kieker.diagnosis.frontend.tab.statistics;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import kieker.diagnosis.backend.data.MonitoringLogService;

/**
 * This is a UI test which checks that the statistics view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class StatisticsViewTestUI extends ApplicationTest {

	private MonitoringLogService monitoringLogService;
	private StatisticsView statisticsView;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new Module( ) );

		monitoringLogService = injector.getInstance( MonitoringLogService.class );

		statisticsView = injector.getInstance( StatisticsView.class );
		statisticsView.initialize( );

		final Scene scene = new Scene( statisticsView );
		stage.setScene( scene );
		stage.show( );
	}

	@Test
	public void testStatisticsView( ) {
		System.out.println( lookup( "#statisticsProcessedBytes" ).query( ) );
		final TextInputControl processedBytesTextField = lookup( "#statisticsProcessedBytes" ).queryTextInputControl( );
		assertThat( processedBytesTextField.getText( ), is( "<Keine Daten verfügbar> " ) );

		monitoringLogService.setDataAvailable( true );
		monitoringLogService.setProcessedBytes( 250 );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );

		assertThat( processedBytesTextField.getText( ), is( "250 [B]" ) );

		monitoringLogService.setProcessedBytes( 250000 );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );

		assertThat( processedBytesTextField.getText( ), is( "244 [KB]" ) );

		monitoringLogService.setProcessedBytes( 250000000 );
		statisticsView.prepareRefresh( );
		statisticsView.performRefresh( );

		assertThat( processedBytesTextField.getText( ), is( "238 [MB]" ) );
	}

	private static final class Module extends AbstractModule {

	}

}
