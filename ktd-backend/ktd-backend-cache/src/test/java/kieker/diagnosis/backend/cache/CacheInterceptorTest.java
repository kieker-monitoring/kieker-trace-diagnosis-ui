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

package kieker.diagnosis.backend.cache;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;

/**
 * This is a unit test for {@link CacheInterceptor}.
 *
 * @author Nils Christian Ehmke
 */
public final class CacheInterceptorTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none( );

	private TestService testService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new CacheModule( ) );
		testService = injector.getInstance( TestService.class );
	}

	@Test
	public void testWithoutUseCacheAnnotation( ) throws Throwable {
		testService.methodA( "test-key" );
		testService.methodA( "test-key" );

		assertThat( testService.getCounter( ), is( 2 ) );
	}

	@Test
	public void testWithUseCacheAnnotation( ) throws Throwable {
		testService.methodB( "test-key" );
		testService.methodB( "test-key" );

		assertThat( testService.getCounter( ), is( 1 ) );
	}

	@Test
	public void testWithUseCacheAnnotationWithNullValue( ) throws Throwable {
		final UseCache useCache = mock( UseCache.class );
		when( useCache.cacheName( ) ).thenReturn( "test-cache" );

		final Method method = mock( Method.class );
		when( method.getAnnotation( UseCache.class ) ).thenReturn( useCache );

		final MethodInvocation methodInvocation = mock( MethodInvocation.class );
		when( methodInvocation.getMethod( ) ).thenReturn( method );
		when( methodInvocation.getArguments( ) ).thenReturn( new Object[] { null } );

		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		cacheInterceptor.invoke( methodInvocation );
		cacheInterceptor.invoke( methodInvocation );

		verify( methodInvocation, times( 2 ) ).proceed( );
	}

	@Test
	public void testWithoutAnyAnnotation( ) throws Throwable {
		final Method method = mock( Method.class );
		final MethodInvocation methodInvocation = mock( MethodInvocation.class );
		when( methodInvocation.getMethod( ) ).thenReturn( method );

		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		cacheInterceptor.invoke( methodInvocation );

		verify( methodInvocation, times( 1 ) ).proceed( );
	}

	@Test
	public void testWithInvalidateCacheAnnotation( ) throws Throwable {
		testService.methodB( "test-key" );
		testService.methodC( "test-key" );
		testService.methodB( "test-key" );

		assertThat( testService.getCounter( ), is( 2 ) );
	}

	@Test
	public void testWithInvalidateCacheAnnotationWithoutCache( ) throws Throwable {
		final InvalidateCache invalidateCache = mock( InvalidateCache.class );
		when( invalidateCache.cacheName( ) ).thenReturn( "test-cache" );
		when( invalidateCache.keyParameter( ) ).thenReturn( 0 );

		final Method method = mock( Method.class );
		when( method.getAnnotation( InvalidateCache.class ) ).thenReturn( invalidateCache );

		final MethodInvocation methodInvocation = mock( MethodInvocation.class );
		when( methodInvocation.getMethod( ) ).thenReturn( method );
		when( methodInvocation.getArguments( ) ).thenReturn( new Object[] { "test-key" } );

		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		cacheInterceptor.invoke( methodInvocation );

		verify( methodInvocation, times( 1 ) ).proceed( );
	}

	@Test
	public void testException( ) throws Throwable {
		expectedException.expect( Exception.class );
		testService.methodD( "test-key" );
	}

	@Singleton
	public static class TestService implements Service {

		private int counter = 0;

		public String methodA( final String key ) {
			counter++;
			return "test-value";
		}

		@UseCache ( cacheName = "test-cache" )
		public String methodB( final String key ) {
			counter++;
			return "test-value";
		}

		@InvalidateCache ( cacheName = "test-cache", keyParameter = 0 )
		public void methodC( final String key ) {
		}

		@UseCache ( cacheName = "test-cache" )
		public String methodD( final String key ) {
			throw new IllegalArgumentException( );
		}

		public int getCounter( ) {
			return counter;
		}

	}

}
