package kieker.diagnosis.frontend.tab.methods;

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
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;

/**
 * This is a UI test which checks that the methods view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodsViewTestUI extends ApplicationTest {

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.addMethods( createCalls( ) );

		final MethodsView methodsView = injector.getInstance( MethodsView.class );
		methodsView.initialize( );
		methodsView.prepareRefresh( );
		methodsView.performRefresh( );

		final Scene scene = new Scene( methodsView );
		stage.setScene( scene );
		stage.show( );
	}

	private List<MethodCall> createCalls( ) {
		final MethodCall call1 = new MethodCall( );
		call1.setHost( "host1" );

		final MethodCall call2 = new MethodCall( );
		call2.setHost( "host1" );

		final MethodCall call3 = new MethodCall( );
		call3.setHost( "host2" );

		return Arrays.asList( call1, call2, call3 );
	}

	@Test
	public void testMethodsView( ) {
		final TableView<Object> tableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 3 ) );

		clickOn( "#tabMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
	}

}
