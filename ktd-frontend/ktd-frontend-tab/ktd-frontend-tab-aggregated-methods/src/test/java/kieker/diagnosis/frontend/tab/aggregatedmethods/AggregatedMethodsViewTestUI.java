package kieker.diagnosis.frontend.tab.aggregatedmethods;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;

/**
 * This is a UI test which checks that the aggregated methods view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodsViewTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.addAggregatedMethods( createCalls( ) );

		final AggregatedMethodsView methodsView = injector.getInstance( AggregatedMethodsView.class );
		methodsView.initialize( );
		methodsView.prepareRefresh( );
		methodsView.performRefresh( );

		final Scene scene = new Scene( methodsView );
		stage.setScene( scene );
		stage.show( );
	}

	private List<AggregatedMethodCall> createCalls( ) {
		final AggregatedMethodCall call1 = new AggregatedMethodCall( );
		call1.setHost( "host1" );

		final AggregatedMethodCall call2 = new AggregatedMethodCall( );
		call2.setHost( "host1" );

		final AggregatedMethodCall call3 = new AggregatedMethodCall( );
		call3.setHost( "host2" );

		return Arrays.asList( call1, call2, call3 );
	}

	@Test
	public void testAggregatedMethodsView( ) {
		final TableView<Object> tableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 3 ) );

		clickOn( "#tabAggregatedMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
	}

}
