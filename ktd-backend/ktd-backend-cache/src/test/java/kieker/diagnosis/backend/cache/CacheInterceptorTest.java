/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;

/**
 * This is a unit test for {@link CacheInterceptor}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for CacheInterceptor" )
public final class CacheInterceptorTest {

	private static final String TEST_KEY = "test-key";
	
	private TestService testService;

	@BeforeEach
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new CacheModule( ) );
		testService = injector.getInstance( TestService.class );
	}

	@Test
	@DisplayName ( "Test without the use of the cache annotation" )
	public void testWithoutUseCacheAnnotation( ) throws Throwable {
		testService.methodWithoutAnnotations( TEST_KEY );
		testService.methodWithoutAnnotations( TEST_KEY );

		assertThat( testService.getCounter( ) ).isEqualTo( 2 );
	}

	@Test
	@DisplayName ( "Test with the use of the cache annotation" )
	public void testWithUseCacheAnnotation( ) throws Throwable {
		testService.methodWithUseCacheAnnotation( TEST_KEY );
		testService.methodWithUseCacheAnnotation( TEST_KEY );

		assertThat( testService.getCounter( ) ).isEqualTo( 1 );
	}

	@Test
	@DisplayName ( "Test with the use of the cache annotation and a null value" )
	public void testWithUseCacheAnnotationWithNullValue( ) throws Throwable {
		testService.methodWithUseCacheAnnotation( null );
		testService.methodWithUseCacheAnnotation( null );

		assertThat( testService.getCounter( ) ).isEqualTo( 2 );
	}

	@Test
	@DisplayName ( "Test with the use of the invalidate cache annotation" )
	public void testWithInvalidateCacheAnnotation( ) throws Throwable {
		testService.methodWithUseCacheAnnotation( TEST_KEY );
		testService.methodWithInvalidateCacheAnnotation( TEST_KEY );
		testService.methodWithUseCacheAnnotation( TEST_KEY );

		assertThat( testService.getCounter( ) ).isEqualTo( 2 );
	}

	@Test
	@DisplayName ( "Test with the use of the invalidate cache annotation but without cache" )
	public void testWithInvalidateCacheAnnotationWithoutCache( ) throws Throwable {
		testService.methodWithInvalidateCacheAnnotation( TEST_KEY );
		
		assertThat( testService.getCounter( ) ).isEqualTo( 0 );
	}

	@Test
	@DisplayName ( "Test with an occuring exception" )
	public void testException( ) throws Throwable {
		assertThrows( Exception.class, ( ) -> testService.methodWithUseCacheAnnotationThatThrowsException( TEST_KEY ) );
	}

	@Singleton
	public static class TestService implements Service {

		private int counter = 0;

		public String methodWithoutAnnotations( final String key ) {
			counter++;
			return "test-value";
		}

		@UseCache ( cacheName = "test-cache" )
		public String methodWithUseCacheAnnotation( final String key ) {
			counter++;
			return "test-value";
		}

		@InvalidateCache ( cacheName = "test-cache", keyParameter = 0 )
		public void methodWithInvalidateCacheAnnotation( final String key ) {
		}

		@UseCache ( cacheName = "test-cache" )
		public String methodWithUseCacheAnnotationThatThrowsException( final String key ) {
			throw new IllegalArgumentException( );
		}
		
		public int getCounter( ) {
			return counter;
		}

	}

}
