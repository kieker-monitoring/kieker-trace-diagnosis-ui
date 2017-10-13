package kieker.diagnosis.architecture.service.cache;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * This interceptor is responsible for handling the caching of method calls to services. It handles the {@link UseCache} and {@link InvalidateCache}
 * annotations.
 *
 * @see UseCache
 * @see InvalidateCache
 *
 * @author Nils Christian Ehmke
 */
public final class CacheInterceptor implements MethodInterceptor {

	private final Cache<String, Cache<Object, Object>> ivCaches = CacheBuilder.newBuilder( ).build( );

	@Override
	public Object invoke( final MethodInvocation aMethodInvocation ) throws Throwable {
		final Method method = aMethodInvocation.getMethod( );

		// Check for a UseCache annotation first
		final UseCache useCacheAnnotation = method.getAnnotation( UseCache.class );
		if ( useCacheAnnotation != null ) {
			final String cacheName = useCacheAnnotation.cacheName( );

			// Get the cache
			final Cache<Object, Object> cache = ivCaches.get( cacheName, ( ) -> CacheBuilder.newBuilder( ).build( ) );

			// Get the method result
			final Object key = aMethodInvocation.getArguments( )[0];

			// Null values are not allowed as key though
			if ( key == null ) {
				return aMethodInvocation.proceed( );
			}

			final Object value = cache.get( key, ( ) -> {
				try {
					return aMethodInvocation.proceed( );
				} catch ( final Throwable ex ) {
					throw new RuntimeException( ex );
				}
			} );

			return value;
		}

		// Now check the InvalidateCache annotation
		final InvalidateCache invalidateCache = method.getAnnotation( InvalidateCache.class );
		if ( invalidateCache != null ) {
			final String cacheName = invalidateCache.cacheName( );
			final Cache<Object, Object> cache = ivCaches.getIfPresent( cacheName );

			// If we have a cache, we remove the entry
			if ( cache != null ) {
				final Object key = aMethodInvocation.getArguments( )[invalidateCache.keyParameter( )];
				cache.invalidate( key );
			}
		}

		return aMethodInvocation.proceed( );
	}

}
