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

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This interceptor is responsible for handling the caching of method calls to services. It handles the {@link UseCache}
 * and {@link InvalidateCache} annotations.
 *
 * @see UseCache
 * @see InvalidateCache
 *
 * @author Nils Christian Ehmke
 */
public final class CacheInterceptor implements MethodInterceptor {

	private final Cache<String, Cache<Object, Object>> caches = CacheBuilder.newBuilder( ).build( );

	@Override
	public Object invoke( final MethodInvocation methodInvocation ) throws Throwable {
		checkAndHandleInvalidateCacheAnnotation( methodInvocation );
		return checkAndHandleUseCacheAnnotation( methodInvocation );
	}

	private Object checkAndHandleUseCacheAnnotation( final MethodInvocation methodInvocation ) throws ExecutionException, Throwable {
		final Method method = methodInvocation.getMethod( );
		final UseCache useCacheAnnotation = method.getAnnotation( UseCache.class );

		final Object methodResult;
		if ( useCacheAnnotation != null ) {
			final String cacheName = useCacheAnnotation.cacheName( );

			// Keep in mind that null values are not allowed as keys
			final Object key = methodInvocation.getArguments( )[0];

			if ( key == null ) {
				methodResult = methodInvocation.proceed( );
			} else {
				final Cache<Object, Object> cache = caches.get( cacheName, ( ) -> CacheBuilder.newBuilder( ).build( ) );
				final Object value = cache.get( key, ( ) -> {
					try {
						return methodInvocation.proceed( );
					} catch ( final Throwable ex ) {
						throw new Exception( ex );
					}
				} );

				methodResult = value;
			}
		} else {
			methodResult = methodInvocation.proceed( );
		}

		return methodResult;
	}

	private void checkAndHandleInvalidateCacheAnnotation( final MethodInvocation methodInvocation ) {
		final Method method = methodInvocation.getMethod( );
		final InvalidateCache invalidateCache = method.getAnnotation( InvalidateCache.class );

		if ( invalidateCache != null ) {
			final String cacheName = invalidateCache.cacheName( );
			final Cache<Object, Object> cache = caches.getIfPresent( cacheName );

			// If we have a cache, we remove the entry
			if ( cache != null ) {
				final Object key = methodInvocation.getArguments( )[invalidateCache.keyParameter( )];
				cache.invalidate( key );
			}
		}
	}

}
