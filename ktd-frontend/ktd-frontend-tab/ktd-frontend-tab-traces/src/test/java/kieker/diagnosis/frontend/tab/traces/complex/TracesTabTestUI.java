package kieker.diagnosis.frontend.tab.traces.complex;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.backend.settings.properties.ShowUnmonitoredTimeProperty;

/**
 * This is a UI test which checks that the traces view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesTabTestUI extends ApplicationTest {

	private TracesTab tracesTab;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		createTraces( ).forEach( monitoringLogService::addTraceRoot );

		final PropertiesService propertiesService = injector.getInstance( PropertiesService.class );
		propertiesService.saveApplicationProperty( ShowUnmonitoredTimeProperty.class, Boolean.TRUE );

		tracesTab = new TracesTab( );
		tracesTab.prepareRefresh( );
		tracesTab.performRefresh( );

		final TabPane tabPane = new TabPane( tracesTab );
		final Scene scene = new Scene( tabPane );
		stage.setScene( scene );
		stage.show( );
	}

	private List<MethodCall> createTraces( ) {
		final MethodCall call1 = new MethodCall( );
		call1.setTraceId( 1 );
		call1.setHost( "host1" );
		call1.setClazz( "class1" );
		call1.setMethod( "method1" );

		final MethodCall subcall1 = new MethodCall( );
		subcall1.setTraceId( 1 );
		subcall1.setHost( "host5" );
		subcall1.setClazz( "class5" );
		subcall1.setMethod( "method5" );
		call1.addChild( subcall1 );

		final MethodCall call2 = new MethodCall( );
		call2.setTraceId( 2 );
		call2.setHost( "host1" );
		call2.setClazz( "class1" );
		call2.setMethod( "method1" );
		call2.setException( "exception" );

		final MethodCall call3 = new MethodCall( );
		call3.setTraceId( 3 );
		call3.setHost( "host2" );
		call3.setClazz( "class1" );
		call3.setMethod( "method1" );

		final MethodCall call4 = new MethodCall( );
		call4.setTraceId( 4 );
		call4.setHost( "host3" );
		call4.setClazz( "class3" );
		call4.setMethod( "method3" );
		call4.setDuration( 150L );
		call4.setTimestamp( 1000L );
		call4.setTraceId( 42L );

		return Arrays.asList( call1, call2, call3, call4 );
	}

	@Test
	public void testNormalSearch( ) {
		final TreeTableView<?> tableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 4 ) );

		clickOn( "#tabTracesFilterHost" ).write( "host1" );
		clickOn( "#tabTracesFilterClass" ).write( "class1" );
		clickOn( "#tabTracesFilterMethod" ).write( "method1" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 2 ) );

		clickOn( "#tabTracesFilterTraceId" ).write( "1" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 1 ) );

		clickOn( "#tabTracesFilterTraceId" ).eraseText( 1 );
		clickOn( "#tabTracesFilterException" ).write( "exception" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 1 ) );

		clickOn( "#tabTracesFilterException" ).eraseText( 9 );
		clickOn( "#tabTracesFilterSearchType" ).clickOn( "Nur fehlgeschlagene" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 1 ) );

		clickOn( "#tabTracesFilterHost" ).eraseText( 5 );
		clickOn( "#tabTracesFilterSearchType" ).clickOn( "Nur erfolgreiche" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 2 ) );
	}

	@Test
	public void testDetailPanel( ) {
		clickOn( lookup( "#tabTracesTreeTable" ).lookup( ".tree-table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabTracesDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabTracesDetailException" ).queryTextInputControl( ).getText( ), is( "<Keine Daten verfügbar>" ) );

		clickOn( lookup( "#tabTracesTreeTable" ).lookup( ".tree-table-row-cell" ).nth( 1 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabTracesDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabTracesDetailException" ).queryTextInputControl( ).getText( ), is( "exception" ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		final TreeTableView<?> tableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 4 ) );

		clickOn( "#tabTracesFilterUseRegExpr" );

		clickOn( "#tabTracesFilterHost" ).write( ".*1.*" );
		clickOn( "#tabTracesFilterClass" ).write( "class1" );
		clickOn( "#tabTracesFilterMethod" ).write( "m....d\\d" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 2 ) );

		clickOn( "#tabTracesFilterException" ).write( "e.*" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 1 ) );

		clickOn( "#tabTracesFilterException" ).eraseText( 9 );
		clickOn( "#tabTracesFilterSearchType" ).clickOn( "Nur fehlgeschlagene" );
		clickOn( "#tabTracesSearch" );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchWithInvalidRegularExpressions( ) {
		final TreeTableView<?> tableView = lookup( "#tabTracesTreeTable" ).query( );
		assertThat( tableView.getRoot( ).getChildren( ).size( ), is( 4 ) );

		clickOn( "#tabTracesFilterUseRegExpr" );

		clickOn( "#tabTracesFilterHost" ).write( "(" );
		clickOn( "#tabTracesSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabTracesFilterHost" ).eraseText( 1 );
		clickOn( "#tabTracesFilterClass" ).write( "(" );
		clickOn( "#tabTracesSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabTracesFilterClass" ).eraseText( 1 );
		clickOn( "#tabTracesFilterMethod" ).write( "(" );
		clickOn( "#tabTracesSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabTracesFilterMethod" ).eraseText( 1 );
		clickOn( "#tabTracesFilterException" ).write( "(" );
		clickOn( "#tabTracesSearch" );
		clickOn( ".dialog-pane .button" );
	}

	@Test
	public void testExpandChildren( ) {
		clickOn( lookup( "#tabTracesTreeTable" ).lookup( ".tree-table-row-cell" ).nth( 0 ).lookup( ".arrow" ).queryAs( Node.class ) );

		clickOn( lookup( "#tabTracesTreeTable" ).lookup( ".tree-table-row-cell" ).nth( 1 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabTracesDetailHost" ).queryTextInputControl( ).getText( ), is( "-" ) );

		clickOn( lookup( "#tabTracesTreeTable" ).lookup( ".tree-table-row-cell" ).nth( 2 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabTracesDetailHost" ).queryTextInputControl( ).getText( ), is( "host5" ) );
	}

	@Test
	public void testSaveAsFavorite( ) {
		final Property<TracesFilter> filterHolder = new SimpleObjectProperty<>( );
		final Consumer<TracesFilter> action = filterHolder::setValue;
		tracesTab.setOnSaveAsFavorite( action );

		clickOn( "#tabTracesFilterHost" ).write( "host1" );
		clickOn( "#tabTracesSaveAsFavorite" );
		clickOn( "#tabTracesFilterHost" ).eraseText( 5 );

		assertThat( filterHolder.getValue( ), is( notNullValue( ) ) );
		interact( ( ) -> tracesTab.setFilterValue( filterHolder.getValue( ) ) );

		assertThat( lookup( "#tabTracesFilterHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
	}

}
