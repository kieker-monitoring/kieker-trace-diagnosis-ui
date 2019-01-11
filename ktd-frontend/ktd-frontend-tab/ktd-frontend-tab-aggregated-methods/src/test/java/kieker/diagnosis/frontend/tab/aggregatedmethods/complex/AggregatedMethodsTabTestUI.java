package kieker.diagnosis.frontend.tab.aggregatedmethods.complex;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.settings.properties.TimeUnitProperty;

/**
 * This is a UI test which checks that the aggregated methods view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodsTabTestUI extends ApplicationTest {

	private AggregatedMethodsTab methodsTab;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.addAggregatedMethods( createAggregatedMethodCalls( ) );

		final PropertiesService propertiesService = injector.getInstance( PropertiesService.class );
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.NANOSECONDS );

		methodsTab = new AggregatedMethodsTab( );
		methodsTab.prepareRefresh( );
		methodsTab.performRefresh( );

		final TabPane tabPane = new TabPane( methodsTab );
		final Scene scene = new Scene( tabPane );
		stage.setScene( scene );
		stage.show( );
	}

	private List<AggregatedMethodCall> createAggregatedMethodCalls( ) {
		final AggregatedMethodCall call1 = new AggregatedMethodCall( );
		call1.setCount( 2 );
		call1.setHost( "host1" );
		call1.setClazz( "class1" );
		call1.setMethod( "method1" );

		final AggregatedMethodCall call2 = new AggregatedMethodCall( );
		call2.setCount( 1 );
		call2.setHost( "host1" );
		call2.setClazz( "class1" );
		call2.setMethod( "method1" );
		call2.setException( "exception" );

		final AggregatedMethodCall call3 = new AggregatedMethodCall( );
		call3.setCount( 10 );
		call3.setHost( "host2" );
		call3.setClazz( "class1" );
		call3.setMethod( "method1" );

		final AggregatedMethodCall call4 = new AggregatedMethodCall( );
		call4.setCount( 1 );
		call4.setHost( "host3" );
		call4.setClazz( "class3" );
		call4.setMethod( "method3" );
		call4.setAvgDuration( 42L );
		call4.setMaxDuration( 60L );
		call4.setMedianDuration( 50L );
		call4.setMinDuration( 10L );
		call4.setTotalDuration( 500L );

		return Arrays.asList( call1, call2, call3, call4 );
	}

	@Test
	public void testNormalSearch( ) {
		final TableView<Object> tableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 4 ) );

		clickOn( "#tabAggregatedMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabAggregatedMethodsFilterClass" ).write( "class1" );
		clickOn( "#tabAggregatedMethodsFilterMethod" ).write( "method1" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );

		clickOn( "#tabAggregatedMethodsFilterException" ).write( "exception" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );

		clickOn( "#tabAggregatedMethodsFilterException" ).eraseText( 9 );
		clickOn( "#tabAggregatedMethodsFilterSearchType" ).clickOn( "Nur fehlgeschlagene" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );

		clickOn( "#tabAggregatedMethodsFilterHost" ).eraseText( 5 );
		clickOn( "#tabAggregatedMethodsFilterSearchType" ).clickOn( "Nur erfolgreiche " );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );
	}

	@Test
	public void testDetailPanel( ) {
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabAggregatedMethodsDetailException" ).queryTextInputControl( ).getText( ), is( "<Keine Daten verfügbar>" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-row-cell" ).nth( 1 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabAggregatedMethodsDetailException" ).queryTextInputControl( ).getText( ), is( "exception" ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		final TableView<Object> tableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 4 ) );

		clickOn( "#tabAggregatedMethodsFilterUseRegExpr" );

		clickOn( "#tabAggregatedMethodsFilterHost" ).write( ".*1.*" );
		clickOn( "#tabAggregatedMethodsFilterClass" ).write( "class1" );
		clickOn( "#tabAggregatedMethodsFilterMethod" ).write( "m....d\\d" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );

		clickOn( "#tabAggregatedMethodsFilterException" ).write( "e.*" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );

		clickOn( "#tabAggregatedMethodsFilterException" ).eraseText( 9 );
		clickOn( "#tabAggregatedMethodsFilterSearchType" ).clickOn( "Nur fehlgeschlagene" );
		clickOn( "#tabAggregatedMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchWithInvalidRegularExpressions( ) {
		final TableView<Object> tableView = lookup( "#tabAggregatedMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 4 ) );

		clickOn( "#tabAggregatedMethodsFilterUseRegExpr" );

		clickOn( "#tabAggregatedMethodsFilterHost" ).write( "(" );
		clickOn( "#tabAggregatedMethodsSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabAggregatedMethodsFilterHost" ).eraseText( 1 );
		clickOn( "#tabAggregatedMethodsFilterClass" ).write( "(" );
		clickOn( "#tabAggregatedMethodsSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabAggregatedMethodsFilterClass" ).eraseText( 1 );
		clickOn( "#tabAggregatedMethodsFilterMethod" ).write( "(" );
		clickOn( "#tabAggregatedMethodsSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabAggregatedMethodsFilterMethod" ).eraseText( 1 );
		clickOn( "#tabAggregatedMethodsFilterException" ).write( "(" );
		clickOn( "#tabAggregatedMethodsSearch" );
		clickOn( ".dialog-pane .button" );
	}

	@Test
	public void testSorting( ) {
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 1 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 1 ).queryAs( Node.class ) );

		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 0 ).queryLabeled( ).getText( ), is( "10" ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 1 ).queryLabeled( ).getText( ), is( "host2" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 2 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 2 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 1 ).queryLabeled( ).getText( ), is( "host3" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 3 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 3 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 2 ).queryLabeled( ).getText( ), is( "class3" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 4 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 4 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 3 ).queryLabeled( ).getText( ), is( "method3" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 5 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 5 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 4 ).queryLabeled( ).getText( ), is( "10" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 6 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 6 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 5 ).queryLabeled( ).getText( ), is( "42" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 7 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 7 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 6 ).queryLabeled( ).getText( ), is( "50" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 8 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 8 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 7 ).queryLabeled( ).getText( ), is( "60" ) );

		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 9 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabAggregatedMethodsTable" ).lookup( ".column-header" ).nth( 9 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabAggregatedMethodsTable" ).lookup( ".table-cell" ).nth( 8 ).queryLabeled( ).getText( ), is( "500" ) );
	}

}
