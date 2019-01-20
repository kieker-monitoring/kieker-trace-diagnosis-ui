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

package kieker.diagnosis.backend.search.aggregatedmethods;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.data.AggregatedMethodCall;
import kieker.diagnosis.backend.data.reader.Repository;

/**
 * Test class for the {@link AggregatedMethodsService}.
 *
 * @author Nils Christian Ehmke
 */
public class AggregatedMethodsServiceTest {

	private AggregatedMethodsService methodsService;
	private Repository repository;

	@BeforeEach
	public void setUp( ) {
		final Injector injector = Guice.createInjector( );
		methodsService = injector.getInstance( AggregatedMethodsService.class );
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
		final AggregatedMethodsFilter methodsFilter = new AggregatedMethodsFilter( );
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
		final AggregatedMethodsFilter methodsFilter = new AggregatedMethodsFilter( );
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
		final AggregatedMethodsFilter methodsFilter = new AggregatedMethodsFilter( );
		methodsFilter.setSearchType( SearchType.ALL );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 4 );

		methodsFilter.setSearchType( SearchType.ONLY_FAILED );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 3 );

		methodsFilter.setSearchType( SearchType.ONLY_SUCCESSFUL );
		assertThat( methodsService.searchMethods( methodsFilter ) ).hasSize( 1 );
	}

	private void createMethodCall( final String host, final String clazz, final String method, final String exception ) {
		final AggregatedMethodCall methodCall = new AggregatedMethodCall( );
		methodCall.setHost( host );
		methodCall.setClazz( clazz );
		methodCall.setMethod( method );
		methodCall.setException( exception );

		repository.getAggreatedMethods( ).add( methodCall );
	}

}
