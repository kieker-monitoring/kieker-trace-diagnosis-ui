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
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.properties.PropertiesService;
import kieker.diagnosis.backend.search.traces.TracesFilter;
import kieker.diagnosis.backend.settings.properties.ShowUnmonitoredTimeProperty;
import kieker.diagnosis.frontend.base.FrontendBaseModule;

/**
 * This is a UI test which checks that the traces view is working as expected.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesTabTestUI extends ApplicationTest {

	private TracesTab tracesTab;
	private TracesPage tracesPage;

	@Override
	public void start( final Stage stage ) throws Exception {
		final Injector injector = Guice.createInjector( new FrontendBaseModule( ) );

		final MonitoringLogService monitoringLogService = injector.getInstance( MonitoringLogService.class );
		monitoringLogService.getRepository( ).getTraceRoots( ).addAll( createTraces( ) );

		final PropertiesService propertiesService = injector.getInstance( PropertiesService.class );
		propertiesService.saveApplicationProperty( ShowUnmonitoredTimeProperty.class, Boolean.TRUE );

		tracesTab = new TracesTab( );
		tracesTab.prepareRefresh( );
		tracesTab.performRefresh( );

		final TabPane tabPane = new TabPane( tracesTab );
		final Scene scene = new Scene( tabPane );
		stage.setScene( scene );
		stage.show( );

		tracesPage = new TracesPage( this );
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
		assertThat( tracesPage.getTable( ).countItems( ), is( 4 ) );

		tracesPage.getFilter( ).getHost( ).writeText( "host1" );
		tracesPage.getFilter( ).getClazz( ).writeText( "class1" );
		tracesPage.getFilter( ).getMethod( ).writeText( "method1" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 2 ) );

		tracesPage.getFilter( ).getTraceId( ).writeText( "1" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 1 ) );

		tracesPage.getFilter( ).getTraceId( ).clearText( );
		tracesPage.getFilter( ).getException( ).writeText( "exception" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 1 ) );

		tracesPage.getFilter( ).getException( ).clearText( );
		tracesPage.getFilter( ).getSearchType( ).select( "Nur fehlgeschlagene" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 1 ) );

		tracesPage.getFilter( ).getHost( ).clearText( );
		tracesPage.getFilter( ).getSearchType( ).select( "Nur erfolgreiche" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 2 ) );
	}

	@Test
	public void testDetailPanel( ) {
		tracesPage.getTable( ).clickOnNthTableRow( 0 );
		assertThat( tracesPage.getDetail( ).getHost( ).getText( ), is( "host1" ) );
		assertThat( tracesPage.getDetail( ).getException( ).getText( ), is( "<Keine Daten verfügbar>" ) );

		tracesPage.getTable( ).clickOnNthTableRow( 1 );
		assertThat( tracesPage.getDetail( ).getHost( ).getText( ), is( "host1" ) );
		assertThat( tracesPage.getDetail( ).getException( ).getText( ), is( "exception" ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		assertThat( tracesPage.getTable( ).countItems( ), is( 4 ) );

		tracesPage.getFilter( ).getUseRegularExpression( ).click( );

		tracesPage.getFilter( ).getHost( ).writeText( ".*1.*" );
		tracesPage.getFilter( ).getClazz( ).writeText( "class1" );
		tracesPage.getFilter( ).getMethod( ).writeText( "m....d\\d" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 2 ) );

		tracesPage.getFilter( ).getException( ).writeText( "e.*" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 1 ) );

		tracesPage.getFilter( ).getException( ).clearText( );
		tracesPage.getFilter( ).getSearchType( ).select( "Nur fehlgeschlagene" );
		tracesPage.getFilter( ).getSearch( ).click( );
		assertThat( tracesPage.getTable( ).countItems( ), is( 1 ) );
	}

	@Test
	public void testSearchWithInvalidRegularExpressions( ) {
		assertThat( tracesPage.getTable( ).countItems( ), is( 4 ) );

		tracesPage.getFilter( ).getUseRegularExpression( ).click( );

		tracesPage.getFilter( ).getHost( ).writeText( "(" );
		tracesPage.getFilter( ).getSearch( ).click( );
		tracesPage.getDialog( ).getOk( ).click( );

		tracesPage.getFilter( ).getHost( ).clearText( );
		tracesPage.getFilter( ).getClazz( ).writeText( "(" );
		tracesPage.getFilter( ).getSearch( ).click( );
		tracesPage.getDialog( ).getOk( ).click( );

		tracesPage.getFilter( ).getClazz( ).clearText( );
		tracesPage.getFilter( ).getMethod( ).writeText( "(" );
		tracesPage.getFilter( ).getSearch( ).click( );
		tracesPage.getDialog( ).getOk( ).click( );

		tracesPage.getFilter( ).getMethod( ).clearText( );
		tracesPage.getFilter( ).getException( ).writeText( "(" );
		tracesPage.getFilter( ).getSearch( ).click( );
		tracesPage.getDialog( ).getOk( ).click( );
	}

	@Test
	public void testExpandChildren( ) {
		tracesPage.getTable( ).expandNthRow( 0 );

		tracesPage.getTable( ).clickOnNthTableRow( 1 );
		assertThat( tracesPage.getDetail( ).getHost( ).getText( ), is( "-" ) );

		tracesPage.getTable( ).clickOnNthTableRow( 2 );
		assertThat( tracesPage.getDetail( ).getHost( ).getText( ), is( "host5" ) );
	}

	@Test
	public void testSaveAsFavorite( ) {
		final Property<TracesFilter> filterHolder = new SimpleObjectProperty<>( );
		final Consumer<TracesFilter> action = filterHolder::setValue;
		tracesTab.setOnSaveAsFavorite( action );

		tracesPage.getFilter( ).getHost( ).writeText( "host1" );
		tracesPage.getFilter( ).getSaveAsFavorite( ).click( );
		tracesPage.getFilter( ).getHost( ).clearText( );

		assertThat( filterHolder.getValue( ), is( notNullValue( ) ) );
		interact( ( ) -> tracesTab.setFilterValue( filterHolder.getValue( ) ) );

		assertThat( tracesPage.getFilter( ).getHost( ).getText( ), is( "host1" ) );
	}

}
