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
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
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
	private MethodsPage methodsPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new ServiceBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.getRepository( ).getMethods( ).addAll( createMethodCalls( ) );

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

		methodsPage = new MethodsPage( this );
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
		assertThat( methodsPage.getTable( ).countItems( ), is( 4 ) );

		methodsPage.getFilter( ).getHost( ).writeText( "host1" );
		methodsPage.getFilter( ).getClazz( ).writeText( "class1" );
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
		assertThat( lookup( "#tabMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabMethodsDetailException" ).queryTextInputControl( ).getText( ), is( "<Keine Daten verfügbar>" ) );

		methodsPage.getTable( ).clickOnNthTableRow( 1 );
		assertThat( lookup( "#tabMethodsDetailHost" ).queryTextInputControl( ).getText( ), is( "host1" ) );
		assertThat( lookup( "#tabMethodsDetailException" ).queryTextInputControl( ).getText( ), is( "exception" ) );
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
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 0 ), is( "host3" ) );

		methodsPage.getTable( ).clickOnNthHeader( 2 );
		methodsPage.getTable( ).clickOnNthHeader( 2 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 1 ), is( "class3" ) );

		methodsPage.getTable( ).clickOnNthHeader( 3 );
		methodsPage.getTable( ).clickOnNthHeader( 3 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 2 ), is( "method3" ) );

		methodsPage.getTable( ).clickOnNthHeader( 4 );
		methodsPage.getTable( ).clickOnNthHeader( 4 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 3 ), is( "150" ) );

		methodsPage.getTable( ).clickOnNthHeader( 5 );
		methodsPage.getTable( ).clickOnNthHeader( 5 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 4 ), is( "1000" ) );

		methodsPage.getTable( ).clickOnNthHeader( 6 );
		methodsPage.getTable( ).clickOnNthHeader( 6 );
		assertThat( methodsPage.getTable( ).getNthTextInFirstRow( 5 ), is( "42" ) );
	}

	@Test
	public void testSaveAsFavorite( ) {
		final Property<MethodsFilter> filterHolder = new SimpleObjectProperty<>( );
		final Consumer<MethodsFilter> action = filterHolder::setValue;
		methodsTab.setOnSaveAsFavorite( action );

		methodsPage.getFilter( ).getHost( ).writeText( "host1" );
		methodsPage.getFilter( ).getSaveAsFavorite( ).click( );
		methodsPage.getFilter( ).getHost( ).clearText( );

		assertThat( filterHolder.getValue( ), is( notNullValue( ) ) );
		interact( ( ) -> methodsTab.setFilterValue( filterHolder.getValue( ) ) );

		assertThat( methodsPage.getFilter( ).getHost( ).getText( ), is( "host1" ) );
	}

	@Test
	public void testJumpToTrace( ) {
		@SuppressWarnings ( "unchecked" )
		final Consumer<MethodCall> action = mock( Consumer.class );
		methodsTab.setOnJumpToTrace( action );

		methodsPage.getDetail( ).getJumpToTrace( ).click( );
		verify( action, never( ) ).accept( any( ) );

		methodsPage.getTable( ).clickOnNthElementInFirstRow( 0 );
		methodsPage.getDetail( ).getJumpToTrace( ).click( );
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
		assertThat( csvData.getHeaders( ), hasSize( 6 ) );
		assertThat( csvData.getRows( ), hasSize( 4 ) );
		assertThat( csvData.getRows( ).get( 0 ), hasSize( 6 ) );
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
