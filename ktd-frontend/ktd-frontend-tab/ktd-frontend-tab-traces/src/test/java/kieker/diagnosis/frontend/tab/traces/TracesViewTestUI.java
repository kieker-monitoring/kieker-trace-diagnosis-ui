package kieker.diagnosis.frontend.tab.traces;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;

/**
 * This is a UI test which checks that the traces view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesViewTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.addTraceRoot( createTrace1( ) );
		monitoringLogService.addTraceRoot( createTrace2( ) );

		final TracesView tracesView = injector.getInstance( TracesView.class );
		tracesView.initialize( );
		tracesView.prepareRefresh( );
		tracesView.performRefresh( );

		final Scene scene = new Scene( tracesView );
		stage.setScene( scene );
		stage.show( );
	}

	private MethodCall createTrace1( ) {
		final MethodCall call1 = new MethodCall( );
		call1.setHost( "host1" );

		final MethodCall call2 = new MethodCall( );
		call2.setHost( "host1" );

		call1.addChild( call2 );

		return call1;
	}

	private MethodCall createTrace2( ) {
		final MethodCall call1 = new MethodCall( );
		call1.setHost( "host2" );

		final MethodCall call2 = new MethodCall( );
		call2.setHost( "host2" );

		call1.addChild( call2 );

		return call1;
	}

	@Test
	public void testMethodsView( ) {
		final TreeTableView<?> treeTableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( treeTableView.getRoot( ).getChildren( ).size( ), is( 2 ) );

		clickOn( "#tabTracesFilterHost" ).write( "host1" );
		clickOn( "#tabTracesSearch" );
		assertThat( treeTableView.getRoot( ).getChildren( ).size( ), is( 1 ) );

		clickOn( lookup( ".tree-table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabTracesDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
	}

}
