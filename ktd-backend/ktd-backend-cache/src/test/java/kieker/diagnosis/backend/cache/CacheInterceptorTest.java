/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;

/**
 * Test class for {@link CacheInterceptor}.
 *
 * @author Nils Christian Ehmke
 */
public final class CacheInterceptorTest {

	@Test
	public void testWithoutUseCacheAnnotation( ) throws Throwable {
		final Method method = mock( Method.class );
		final MethodInvocation methodInvocation = mock( MethodInvocation.class );
		when( methodInvocation.getMethod( ) ).thenReturn( method );

		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		cacheInterceptor.invoke( methodInvocation );
		cacheInterceptor.invoke( methodInvocation );

		verify( methodInvocation, times( 2 ) ).proceed( );
	}

	@Test
	public void testWithUseCacheAnnotation( ) throws Throwable {
		final UseCache useCache = mock( UseCache.class );
		when( useCache.cacheName( ) ).thenReturn( "test-cache" );

		final Method method = mock( Method.class );
		when( method.getAnnotation( UseCache.class ) ).thenReturn( useCache );

		final MethodInvocation methodInvocation = mock( MethodInvocation.class );
		when( methodInvocation.getMethod( ) ).thenReturn( method );
		when( methodInvocation.getArguments( ) ).thenReturn( new Object[] { "test-key" } );
		when( methodInvocation.proceed( ) ).thenReturn( "test-value" );

		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		cacheInterceptor.invoke( methodInvocation );
		cacheInterceptor.invoke( methodInvocation );

		verify( methodInvocation, times( 1 ) ).proceed( );
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
	public void testWithInvalidateCacheAnnotation( ) throws Throwable {
		final UseCache useCache = mock( UseCache.class );
		when( useCache.cacheName( ) ).thenReturn( "test-cache" );

		final Method fstMethod = mock( Method.class );
		when( fstMethod.getAnnotation( UseCache.class ) ).thenReturn( useCache );

		final MethodInvocation fstMethodInvocation = mock( MethodInvocation.class );
		when( fstMethodInvocation.getMethod( ) ).thenReturn( fstMethod );
		when( fstMethodInvocation.getArguments( ) ).thenReturn( new Object[] { "test-key" } );
		when( fstMethodInvocation.proceed( ) ).thenReturn( "test-value" );

		final InvalidateCache invalidateCache = mock( InvalidateCache.class );
		when( invalidateCache.cacheName( ) ).thenReturn( "test-cache" );
		when( invalidateCache.keyParameter( ) ).thenReturn( 0 );

		final Method sndMethod = mock( Method.class );
		when( sndMethod.getAnnotation( InvalidateCache.class ) ).thenReturn( invalidateCache );

		final MethodInvocation sndMethodInvocation = mock( MethodInvocation.class );
		when( sndMethodInvocation.getMethod( ) ).thenReturn( sndMethod );
		when( sndMethodInvocation.getArguments( ) ).thenReturn( new Object[] { "test-key" } );

		final CacheInterceptor cacheInterceptor = new CacheInterceptor( );
		cacheInterceptor.invoke( fstMethodInvocation );
		cacheInterceptor.invoke( sndMethodInvocation );
		cacheInterceptor.invoke( fstMethodInvocation );

		verify( fstMethodInvocation, times( 2 ) ).proceed( );
		verify( sndMethodInvocation, times( 1 ) ).proceed( );
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

}
