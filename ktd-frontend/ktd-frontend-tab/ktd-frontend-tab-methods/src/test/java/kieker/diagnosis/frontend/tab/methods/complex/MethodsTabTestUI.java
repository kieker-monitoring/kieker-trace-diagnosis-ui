package kieker.diagnosis.frontend.tab.methods.complex;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.methods.MethodsFilter;
import kieker.diagnosis.backend.settings.TimestampAppearance;
import kieker.diagnosis.backend.settings.properties.TimeUnitProperty;
import kieker.diagnosis.backend.settings.properties.TimestampProperty;

/**
 * This is a UI test which checks that the methods tab is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodsTabTestUI extends ApplicationTest {

	private MethodsTab methodsTab;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.addMethods( createMethodCalls( ) );

		final PropertiesService propertiesService = injector.getInstance( PropertiesService.class );
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.NANOSECONDS );
		propertiesService.saveApplicationProperty( TimestampProperty.class, TimestampAppearance.TIMESTAMP );

		methodsTab = new MethodsTab( );
		methodsTab.prepareRefresh( );
		methodsTab.performRefresh( );

		final TabPane tabPane = new TabPane( methodsTab );
		final Scene scene = new Scene( tabPane );
		stage.setScene( scene );
		stage.show( );
	}

	private List<MethodCall> createMethodCalls( ) {
		final MethodCall call1 = new MethodCall( );
		call1.setHost( "host1" );
		call1.setClazz( "class1" );
		call1.setMethod( "method1" );

		final MethodCall call2 = new MethodCall( );
		call2.setHost( "host1" );
		call2.setClazz( "class1" );
		call2.setMethod( "method1" );
		call2.setException( "exception" );

		final MethodCall call3 = new MethodCall( );
		call3.setHost( "host2" );
		call3.setClazz( "class1" );
		call3.setMethod( "method1" );

		final MethodCall call4 = new MethodCall( );
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
		final TableView<Object> tableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 4 ) );

		clickOn( "#tabMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabMethodsFilterClass" ).write( "class1" );
		clickOn( "#tabMethodsFilterMethod" ).write( "method1" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );

		clickOn( "#tabMethodsFilterException" ).write( "exception" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );

		clickOn( "#tabMethodsFilterException" ).eraseText( 9 );
		clickOn( "#tabMethodsFilterSearchType" ).clickOn( "Nur fehlgeschlagene" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );

		clickOn( "#tabMethodsFilterHost" ).eraseText( 5 );
		clickOn( "#tabMethodsFilterSearchType" ).clickOn( "Nur erfolgreiche " );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );
	}

	@Test
	public void testDetailPanel( ) {
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabMethodsDetailException" ).queryTextInputControl( ).getText( ), is( "<Keine Daten verfügbar>" ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".table-row-cell" ).nth( 1 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabMethodsDetailException" ).queryTextInputControl( ).getText( ), is( "exception" ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		final TableView<Object> tableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 4 ) );

		clickOn( "#tabMethodsFilterUseRegExpr" );

		clickOn( "#tabMethodsFilterHost" ).write( ".*1.*" );
		clickOn( "#tabMethodsFilterClass" ).write( "class1" );
		clickOn( "#tabMethodsFilterMethod" ).write( "m....d\\d" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 2 ) );

		clickOn( "#tabMethodsFilterException" ).write( "e.*" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );

		clickOn( "#tabMethodsFilterException" ).eraseText( 9 );
		clickOn( "#tabMethodsFilterSearchType" ).clickOn( "Nur fehlgeschlagene" );
		clickOn( "#tabMethodsSearch" );
		assertThat( tableView.getItems( ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchWithInvalidRegularExpressions( ) {
		final TableView<Object> tableView = lookup( "#tabMethodsTable" ).queryTableView( );
		assertThat( tableView.getItems( ).size( ), is( 4 ) );

		clickOn( "#tabMethodsFilterUseRegExpr" );

		clickOn( "#tabMethodsFilterHost" ).write( "(" );
		clickOn( "#tabMethodsSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabMethodsFilterHost" ).eraseText( 1 );
		clickOn( "#tabMethodsFilterClass" ).write( "(" );
		clickOn( "#tabMethodsSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabMethodsFilterClass" ).eraseText( 1 );
		clickOn( "#tabMethodsFilterMethod" ).write( "(" );
		clickOn( "#tabMethodsSearch" );
		clickOn( ".dialog-pane .button" );

		clickOn( "#tabMethodsFilterMethod" ).eraseText( 1 );
		clickOn( "#tabMethodsFilterException" ).write( "(" );
		clickOn( "#tabMethodsSearch" );
		clickOn( ".dialog-pane .button" );
	}

	@Test
	public void testSorting( ) {
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 1 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 1 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsTable" ).lookup( ".table-cell" ).nth( 0 ).queryLabeled( ).getText( ), is( "host3" ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 2 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 2 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsTable" ).lookup( ".table-cell" ).nth( 1 ).queryLabeled( ).getText( ), is( "class3" ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 3 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 3 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsTable" ).lookup( ".table-cell" ).nth( 2 ).queryLabeled( ).getText( ), is( "method3" ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 4 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 4 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsTable" ).lookup( ".table-cell" ).nth( 3 ).queryLabeled( ).getText( ), is( "150" ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 5 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 5 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsTable" ).lookup( ".table-cell" ).nth( 4 ).queryLabeled( ).getText( ), is( "1000" ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 6 ).queryAs( Node.class ) );
		clickOn( lookup( "#tabMethodsTable" ).lookup( ".column-header" ).nth( 6 ).queryAs( Node.class ) );
		assertThat( lookup( "#tabMethodsTable" ).lookup( ".table-cell" ).nth( 5 ).queryLabeled( ).getText( ), is( "42" ) );
	}

	@Test
	public void testSaveAsFavorite( ) {
		final Property<MethodsFilter> filterHolder = new SimpleObjectProperty<>( );
		final Consumer<MethodsFilter> action = filterHolder::setValue;
		methodsTab.setOnSaveAsFavorite( action );

		clickOn( "#tabMethodsFilterHost" ).write( "host1" );
		clickOn( "#tabMethodsFilteSaveAsFavorite" );
		clickOn( "#tabMethodsFilterHost" ).eraseText( 5 );

		assertThat( filterHolder.getValue( ), is( notNullValue( ) ) );
		interact( ( ) -> methodsTab.setFilterValue( filterHolder.getValue( ) ) );

		assertThat( lookup( "#tabMethodsFilterHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
	}

	@Test
	public void testJumpToTrace( ) {
		@SuppressWarnings ( "unchecked" )
		final Consumer<MethodCall> action = mock( Consumer.class );
		methodsTab.setOnJumpToTrace( action );

		clickOn( "#tabMethodsJumpToTrace" );
		verify( action, never( ) ).accept( any( ) );

		clickOn( lookup( "#tabMethodsTable" ).lookup( ".table-row-cell" ).nth( 0 ).queryAs( Node.class ) );
		clickOn( "#tabMethodsJumpToTrace" );
		verify( action ).accept( any( ) );
	}

	@Test
	public void testExportToCsv( ) {
		final Property<CSVData> dataHolder = new SimpleObjectProperty<>( );
		final Consumer<CSVData> action = csvData -> dataHolder.setValue( csvData );
		methodsTab.setOnExportToCSV( action );

		clickOn( "#methodCallTabExportToCsv" );

		final CSVData csvData = dataHolder.getValue( );
		assertThat( csvData, is( notNullValue( ) ) );
		assertThat( csvData.getHeaders( ), hasSize( 6 ) );
		assertThat( csvData.getRows( ), hasSize( 4 ) );
		assertThat( csvData.getRows( ).get( 0 ), hasSize( 6 ) );
	}

}
