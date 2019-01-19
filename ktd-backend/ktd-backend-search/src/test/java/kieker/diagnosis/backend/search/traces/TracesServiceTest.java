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

package kieker.diagnosis.backend.search.traces;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.MonitoringLogService;

/**
 * Test class for the {@link TracesService}.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesServiceTest {

	private TracesService tracesService;
	private MonitoringLogService dataService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( );
		tracesService = injector.getInstance( TracesService.class );
		dataService = injector.getInstance( MonitoringLogService.class );
	}

	@Test
	public void testSimpleSearch( ) {
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( tracesService.countTraces( ), is( 4 ) );

		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setHost( "host1" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setClazz( "class1" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setMethod( "op3" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setException( "cause4" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( tracesService.countTraces( ), is( 4 ) );

		// Now search with a filter
		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setUseRegExpr( true );
		tracesFilter.setHost( ".*host1.*" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setClazz( ".*class1.*" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setMethod( ".*op3.*" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setException( ".*cause4.*" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testRecursiveSearch( ) {
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCallWithChild( "host1", "host2" );

		assertThat( tracesService.countTraces( ), is( 2 ) );

		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setHost( "host1" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setHost( "host2" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 0 ) );

		tracesFilter.setSearchWholeTrace( true );
		tracesFilter.setHost( "host2" );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testSearchTypeFilter( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", null );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( tracesService.countTraces( ), is( 4 ) );

		// Now search with a filter
		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setSearchType( SearchType.ALL );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setSearchType( SearchType.ONLY_FAILED );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setSearchType( SearchType.ONLY_SUCCESSFUL );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testTraceIdFilter( ) {
		// Prepare some data for the search
		createMethodCall( 1L );
		createMethodCall( 1L );
		createMethodCall( 2L );
		createMethodCall( 3L );

		assertThat( tracesService.countTraces( ), is( 4 ) );

		// Now search with a filter
		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setTraceId( 1L );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setTraceId( 2L );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );

		tracesFilter.setTraceId( 3L );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );

		tracesFilter.setTraceId( 4L );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 0 ) );
	}

	@Test
	public void testLowerTimeFilter( ) {
		// Prepare some data for the search
		createMethodCall( 2000, 05, 01, 15, 20 );
		createMethodCall( 2000, 05, 05, 15, 20 );
		createMethodCall( 2015, 01, 04, 15, 25 );
		createMethodCall( 2015, 01, 01, 15, 20 );

		assertThat( tracesService.countTraces( ), is( 4 ) );

		// Now search with a filter
		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setLowerTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setLowerTime( LocalTime.of( 14, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setLowerTime( LocalTime.of( 16, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setLowerTime( LocalTime.of( 15, 21 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2015, 01, 01 ) );
		tracesFilter.setLowerTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2015, 01, 04 ) );
		tracesFilter.setLowerTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2015, 01, 05 ) );
		tracesFilter.setLowerTime( LocalTime.of( 10, 10 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 0 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2015, 01, 04 ) );
		tracesFilter.setLowerTime( LocalTime.of( 15, 26 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 0 ) );

		tracesFilter.setLowerDate( null );
		tracesFilter.setLowerTime( null );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setLowerDate( LocalDate.of( 2015, 01, 01 ) );
		tracesFilter.setLowerTime( null );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setLowerDate( null );
		tracesFilter.setLowerTime( LocalTime.of( 15, 25 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );
	}

	@Test
	public void testUpperTimeFilter( ) {
		// Prepare some data for the search
		createMethodCall( 2000, 05, 01, 15, 20 );
		createMethodCall( 2000, 05, 05, 15, 20 );
		createMethodCall( 2015, 01, 04, 15, 25 );
		createMethodCall( 2015, 01, 01, 15, 20 );

		assertThat( tracesService.countTraces( ), is( 4 ) );

		// Now search with a filter
		final TracesFilter tracesFilter = new TracesFilter( );
		tracesFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setUpperTime( LocalTime.of( 14, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 0 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		tracesFilter.setUpperTime( LocalTime.of( 16, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 1 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2000, 05, 05 ) );
		tracesFilter.setUpperTime( LocalTime.of( 15, 21 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 2 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2015, 01, 01 ) );
		tracesFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2015, 01, 04 ) );
		tracesFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2015, 01, 05 ) );
		tracesFilter.setUpperTime( LocalTime.of( 10, 10 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2015, 01, 04 ) );
		tracesFilter.setUpperTime( LocalTime.of( 15, 26 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setUpperDate( null );
		tracesFilter.setUpperTime( null );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setUpperDate( LocalDate.of( 2015, 01, 01 ) );
		tracesFilter.setUpperTime( null );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );

		tracesFilter.setUpperDate( null );
		tracesFilter.setUpperTime( LocalTime.of( 15, 25 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 4 ) );

		tracesFilter.setUpperDate( null );
		tracesFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( tracesService.searchTraces( tracesFilter ).size( ), is( 3 ) );
	}

	private void createMethodCall( final String aHost, final String aClazz, final String aMethod, final String aException ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setHost( aHost );
		methodCall.setClazz( aClazz );
		methodCall.setMethod( aMethod );
		methodCall.setException( aException );

		dataService.getRepository( ).getTraceRoots( ).add( methodCall );
	}

	private void createMethodCall( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );

		final MethodCall methodCall = new MethodCall( );
		methodCall.setTimestamp( calendar.getTimeInMillis( ) );

		dataService.getRepository( ).getTraceRoots( ).add( methodCall );
	}

	private void createMethodCall( final long aTraceId ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setTraceId( aTraceId );

		dataService.getRepository( ).getTraceRoots( ).add( methodCall );
	}

	private void createMethodCallWithChild( final String aHost1, final String aHost2 ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setHost( aHost1 );

		final MethodCall child = new MethodCall( );
		child.setHost( aHost2 );
		methodCall.addChild( child );

		dataService.getRepository( ).getTraceRoots( ).add( methodCall );
	}

}
