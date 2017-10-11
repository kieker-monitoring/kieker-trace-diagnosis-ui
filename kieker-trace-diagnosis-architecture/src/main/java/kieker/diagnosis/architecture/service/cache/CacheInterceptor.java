package kieker.diagnosis.architecture.service.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * This interceptor is responsible for handling the caching of method calls to services. It handles the {@link UseCache} and {@link InvalidateCache}
 * annotations.
 *
 * @author Nils Christian Ehmke
 */
public final class CacheInterceptor implements MethodInterceptor {

	private final Map<String, Map<Object, Object>> caches = new HashMap<>( );

	@Override
	public Object invoke( final MethodInvocation aMethodInvocation ) throws Throwable {
		final Method method = aMethodInvocation.getMethod( );

		// Check for a UseCache annotation first
		final UseCache useCacheAnnotation = method.getAnnotation( UseCache.class );
		if ( useCacheAnnotation != null ) {
			Map<Object, Object> cache = caches.get( useCacheAnnotation.cacheName( ) );

			// Create the cache if necessary
			if ( cache == null ) {
				cache = new HashMap<>( );
				caches.put( useCacheAnnotation.cacheName( ), cache );
			}

			// Check if we already have the entry in the cache
			final Object key = aMethodInvocation.getArguments( )[0];

			Object value = cache.get( key );
			if ( value == null ) {
				// No. We actually have to call the method
				value = aMethodInvocation.proceed( );
				cache.put( key, value );
			}

			return value;
		}

		// Now check the InvalidateCache annotation
		final InvalidateCache invalidateCache = method.getAnnotation( InvalidateCache.class );
		if ( invalidateCache != null ) {
			final Map<Object, Object> cache = caches.get( invalidateCache.cacheName( ) );

			// If we have a cache, we remove the entry
			if ( cache != null ) {
				final Object key = aMethodInvocation.getArguments( )[invalidateCache.keyParameter( )];
				cache.remove( key );
			}
		}

		return aMethodInvocation.proceed( );
	}

}
