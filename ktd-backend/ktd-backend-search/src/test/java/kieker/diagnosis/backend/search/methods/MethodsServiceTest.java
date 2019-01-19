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

package kieker.diagnosis.backend.search.methods;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.data.reader.Repository;

/**
 * Test class for the {@link MethodsService}.
 *
 * @author Nils Christian Ehmke
 */
public class MethodsServiceTest {

	private MethodsService methodsService;
	private Repository repository;

	@BeforeEach
	public void setUp( ) {
		final Injector injector = Guice.createInjector( );

		methodsService = injector.getInstance( MethodsService.class );
		repository = injector.getInstance( Repository.class );
	}

	@Test
	public void testSimpleSearch( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( methodsService.countMethods( ) ).isEqualTo( 4 );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setHost( "host1" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setClazz( "class1" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setMethod( "op3" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 2 );

		methodsFilter.setException( "cause4" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );
	}

	@Test
	public void testSearchWithRegularExpressions( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( methodsService.countMethods( ) ).isEqualTo( 4 );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setUseRegExpr( true );
		methodsFilter.setHost( ".*host1.*" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setClazz( ".*class1.*" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setMethod( ".*op3.*" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 2 );

		methodsFilter.setException( ".*cause4.*" );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );
	}

	@Test
	public void testSearchTypeFilter( ) {
		// Prepare some data for the search
		createMethodCall( "host1", "class1", "op1", "cause1" );
		createMethodCall( "host1", "class2", "op1", null );
		createMethodCall( "host1", "class1", "op3", "cause1" );
		createMethodCall( "host1", "class1", "op3", "cause4" );

		assertThat( methodsService.countMethods( ) ).isEqualTo( 4 );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setSearchType( SearchType.ALL );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setSearchType( SearchType.ONLY_FAILED );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setSearchType( SearchType.ONLY_SUCCESSFUL );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );
	}

	@Test
	public void testTraceIdFilter( ) {
		// Prepare some data for the search
		createMethodCall( 1L );
		createMethodCall( 1L );
		createMethodCall( 2L );
		createMethodCall( 3L );

		assertThat( methodsService.countMethods( ) ).isEqualTo( 4 );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setTraceId( 1L );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 2 );

		methodsFilter.setTraceId( 2L );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );

		methodsFilter.setTraceId( 3L );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );

		methodsFilter.setTraceId( 4L );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 0 );
	}

	@Test
	public void testLowerTimeFilter( ) {
		// Prepare some data for the search
		createMethodCall( 2000, 05, 01, 15, 20 );
		createMethodCall( 2000, 05, 05, 15, 20 );
		createMethodCall( 2015, 01, 04, 15, 25 );
		createMethodCall( 2015, 01, 01, 15, 20 );

		assertThat( methodsService.countMethods( ) ).isEqualTo( 4 );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( LocalTime.of( 14, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( LocalTime.of( 16, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setLowerDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setLowerTime( LocalTime.of( 15, 21 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setLowerTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 2 );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setLowerTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 05 ) );
		methodsFilter.setLowerTime( LocalTime.of( 10, 10 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 0 );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setLowerTime( LocalTime.of( 15, 26 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 0 );

		methodsFilter.setLowerDate( null );
		methodsFilter.setLowerTime( null );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setLowerDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setLowerTime( null );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 2 );

		methodsFilter.setLowerDate( null );
		methodsFilter.setLowerTime( LocalTime.of( 15, 25 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );
	}

	@Test
	public void testUpperTimeFilter( ) {
		// Prepare some data for the search
		createMethodCall( 2000, 05, 01, 15, 20 );
		createMethodCall( 2000, 05, 05, 15, 20 );
		createMethodCall( 2015, 01, 04, 15, 25 );
		createMethodCall( 2015, 01, 01, 15, 20 );

		assertThat( methodsService.countMethods( ) ).isEqualTo( 4 );

		// Now search with a filter
		final MethodsFilter methodsFilter = new MethodsFilter( );
		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );

		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setUpperTime( LocalTime.of( 14, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 0 );

		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 01 ) );
		methodsFilter.setUpperTime( LocalTime.of( 16, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );

		methodsFilter.setUpperDate( LocalDate.of( 2000, 05, 05 ) );
		methodsFilter.setUpperTime( LocalTime.of( 15, 21 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 2 );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 05 ) );
		methodsFilter.setUpperTime( LocalTime.of( 10, 10 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 04 ) );
		methodsFilter.setUpperTime( LocalTime.of( 15, 26 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setUpperDate( null );
		methodsFilter.setUpperTime( null );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setUpperDate( LocalDate.of( 2015, 01, 01 ) );
		methodsFilter.setUpperTime( null );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setUpperDate( null );
		methodsFilter.setUpperTime( LocalTime.of( 15, 25 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setUpperDate( null );
		methodsFilter.setUpperTime( LocalTime.of( 15, 20 ) );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );
	}

	private void createMethodCall( final String aHost, final String aClazz, final String aMethod, final String aException ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setHost( aHost );
		methodCall.setClazz( aClazz );
		methodCall.setMethod( aMethod );
		methodCall.setException( aException );

		repository.getMethods( ).add( methodCall );
	}

	private void createMethodCall( final long aTraceId ) {
		final MethodCall methodCall = new MethodCall( );
		methodCall.setTraceId( aTraceId );

		repository.getMethods( ).add( methodCall );
	}

	private void createMethodCall( final int aYear, final int aMonth, final int aDay, final int aHour, final int aMinute ) {
		final Calendar calendar = Calendar.getInstance( );
		calendar.set( aYear, aMonth - 1, aDay, aHour, aMinute, 0 );

		final MethodCall methodCall = new MethodCall( );
		methodCall.setTimestamp( calendar.getTimeInMillis( ) );

		repository.getMethods( ).add( methodCall );
	}

}
