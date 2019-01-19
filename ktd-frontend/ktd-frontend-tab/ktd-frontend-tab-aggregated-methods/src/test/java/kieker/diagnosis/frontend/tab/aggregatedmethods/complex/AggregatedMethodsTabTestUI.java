/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.frontend.tab.aggregatedmethods.complex;

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
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import kieker.diagnosis.backend.base.ServiceBaseModule;
import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.export.CSVData;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.aggregatedmethods.AggregatedMethodsFilter;
import kieker.diagnosis.backend.settings.properties.TimeUnitProperty;

/**
 * This is a UI test which checks that the aggregated methods tab is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedMethodsTabTestUI extends ApplicationTest {

	private AggregatedMethodsTab methodsTab;
	private AggregatedMethodsPage methodsPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.getRepository( ).getAggreatedMethods( ).addAll( createAggregatedMethodCalls( ) );

		final PropertiesService propertiesService = injector.getInstance( PropertiesService.class );
		propertiesService.saveApplicationProperty( TimeUnitProperty.class, TimeUnit.NANOSECONDS );

		methodsTab = new AggregatedMethodsTab( );
		methodsTab.prepareRefresh( );
		methodsTab.performRefresh( );

		final TabPane tabPane = new TabPane( methodsTab );
		final Scene scene = new Scene( tabPane );
		stage.setScene( scene );
		stage.show( );

		methodsPage = new AggregatedMethodsPage( this );
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
		assertThat( methodsPage.getTable( ).countItems( ), is( 4 ) );

		methodsPage.getFilter( ).getHost( ).writeText( "host1" );
		methodsPage.getFilter( ).getMethod( ).writeText( "method1" );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 2 ) );

		methodsPage.getFilter( ).getException( ).writeText( "exception" );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 1 ) );

		methodsPage.getFilter( ).getException( ).clearText( );
		methodsPage.getFilter( ).getSearchType( ).select( "Nur fehlgeschlagene" );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 1 ) );

		methodsPage.getFilter( ).getHost( ).clearText( );
		methodsPage.getFilter( ).getSearchType( ).select( "Nur erfolgreiche " );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 2 ) );
	}

	@Test
	public void testDetailPanel( ) {
		methodsPage.getTable( ).clickOnNthTableRow( 0 );
		assertThat( methodsPage.getDetail( ).getHost( ).getText( ), is( "host1" ) );
		assertThat( methodsPage.getDetail( ).getException( ).getText( ), is( "<Keine Daten verfügbar>" ) );

		methodsPage.getTable( ).clickOnNthTableRow( 1 );
		assertThat( methodsPage.getDetail( ).getHost( ).getText( ), is( "host1" ) );
		assertThat( methodsPage.getDetail( ).getException( ).getText( ), is( "exception" ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		assertThat( methodsPage.getTable( ).countItems( ), is( 4 ) );

		methodsPage.getFilter( ).getUseRegularExpression( ).click( );

		methodsPage.getFilter( ).getHost( ).writeText( ".*1.*" );
		methodsPage.getFilter( ).getClazz( ).writeText( "class1" );
		methodsPage.getFilter( ).getMethod( ).writeText( "m....d\\d" );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 2 ) );

		methodsPage.getFilter( ).getException( ).writeText( "e.*" );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 1 ) );

		methodsPage.getFilter( ).getException( ).clearText( );
		methodsPage.getFilter( ).getSearchType( ).select( "Nur fehlgeschlagene" );
		methodsPage.getFilter( ).getSearch( ).click( );
		assertThat( methodsPage.getTable( ).countItems( ), is( 1 ) );
	}

	@Test
	public void testSearchWithInvalidRegularExpressions( ) {
		assertThat( methodsPage.getTable( ).countItems( ), is( 4 ) );

		methodsPage.getFilter( ).getUseRegularExpression( ).click( );

		methodsPage.getFilter( ).getHost( ).writeText( "(" );
		methodsPage.getFilter( ).getSearch( ).click( );
		methodsPage.getDialog( ).getOk( ).click( );

		methodsPage.getFilter( ).getHost( ).clearText( );
		methodsPage.getFilter( ).getClazz( ).writeText( "(" );
		methodsPage.getFilter( ).getSearch( ).click( );
		methodsPage.getDialog( ).getOk( ).click( );

		methodsPage.getFilter( ).getClazz( ).clearText( );
		methodsPage.getFilter( ).getMethod( ).writeText( "(" );
		methodsPage.getFilter( ).getSearch( ).click( );
		methodsPage.getDialog( ).getOk( ).click( );

		methodsPage.getFilter( ).getMethod( ).clearText( );
		methodsPage.getFilter( ).getException( ).writeText( "(" );
		methodsPage.getFilter( ).getSearch( ).click( );
		methodsPage.getDialog( ).getOk( ).click( );
	}

	@Test
	public void testSorting( ) {
		methodsPage.getTable( ).clickOnNthHeader( 1 );
		methodsPage.getTable( ).clickOnNthHeader( 1 );

		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 0 ), is( "10" ) );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 1 ), is( "host2" ) );

		methodsPage.getTable( ).clickOnNthHeader( 2 );
		methodsPage.getTable( ).clickOnNthHeader( 2 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 1 ), is( "host3" ) );

		methodsPage.getTable( ).clickOnNthHeader( 3 );
		methodsPage.getTable( ).clickOnNthHeader( 3 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 2 ), is( "class3" ) );

		methodsPage.getTable( ).clickOnNthHeader( 4 );
		methodsPage.getTable( ).clickOnNthHeader( 4 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 3 ), is( "method3" ) );

		methodsPage.getTable( ).clickOnNthHeader( 5 );
		methodsPage.getTable( ).clickOnNthHeader( 5 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 4 ), is( "10" ) );

		methodsPage.getTable( ).clickOnNthHeader( 6 );
		methodsPage.getTable( ).clickOnNthHeader( 6 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 5 ), is( "42" ) );

		methodsPage.getTable( ).clickOnNthHeader( 7 );
		methodsPage.getTable( ).clickOnNthHeader( 7 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 6 ), is( "50" ) );

		methodsPage.getTable( ).clickOnNthHeader( 8 );
		methodsPage.getTable( ).clickOnNthHeader( 8 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 7 ), is( "60" ) );

		methodsPage.getTable( ).clickOnNthHeader( 9 );
		methodsPage.getTable( ).clickOnNthHeader( 9 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 8 ), is( "500" ) );
	}

	@Test
	public void testSaveAsFavorite( ) {
		final Property<AggregatedMethodsFilter> filterHolder = new SimpleObjectProperty<>( );
		final Consumer<AggregatedMethodsFilter> action = filterHolder::setValue;
		methodsTab.setOnSaveAsFavorite( action );

		methodsPage.getFilter( ).getHost( ).writeText( "host1" );
		methodsPage.getFilter( ).getSaveAsFavorite( ).click( );
		methodsPage.getFilter( ).getHost( ).clearText( );

		assertThat( filterHolder.getValue( ), is( notNullValue( ) ) );
		interact( ( ) -> methodsTab.setFilterValue( filterHolder.getValue( ) ) );

		assertThat( methodsPage.getFilter( ).getHost( ).getText( ), is( "host1" ) );
	}

	@Test
	public void testJumpToMethods( ) {
		@SuppressWarnings ( "unchecked" )
		final Consumer<AggregatedMethodCall> action = mock( Consumer.class );
		methodsTab.setOnJumpToMethods( action );

		methodsPage.getDetail( ).getJumpToMethods( ).click( );
		verify( action, never( ) ).accept( any( ) );

		methodsPage.getTable( ).clickOnNthElementInFirstRow( 0 );
		methodsPage.getDetail( ).getJumpToMethods( ).click( );
		verify( action ).accept( any( ) );
	}

	@Test
	public void testExportToCsv( ) {
		final Property<CSVData> dataHolder = new SimpleObjectProperty<>( );
		final Consumer<CSVData> action = csvData -> dataHolder.setValue( csvData );
		methodsTab.setOnExportToCSV( action );

		methodsPage.getStatusBar( ).getExportToCsv( ).click( );

		final CSVData csvData = dataHolder.getValue( );
		assertThat( csvData, is( notNullValue( ) ) );
		assertThat( csvData.getHeaders( ), hasSize( 9 ) );
		assertThat( csvData.getRows( ), hasSize( 4 ) );
		assertThat( csvData.getRows( ).get( 0 ), hasSize( 9 ) );
	}

	@Test
	public void testDefaultButtonProperty( ) {
		methodsPage.getFilter( ).getHost( ).writeText( "host1" );
		methodsPage.getFilter( ).getMethod( ).writeText( "method1" );

		push( KeyCode.ENTER );
		assertThat( methodsPage.getTable( ).countItems( ), is( 4 ) );

		methodsTab.defaultButtonProperty( ).set( true );
		push( KeyCode.ENTER );
		assertThat( methodsPage.getTable( ).countItems( ), is( 2 ) );
	}

}
