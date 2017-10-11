package kieker.diagnosis.architecture.service.cache;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used at service methods and makes sure that the results of the method call is cached. It is assumed that the method has exactly one
 * parameter which provides the key to the cache. Entries can be invalidated by using the {@link InvalidateCache} annotation.
 *
 * @author Nils Christian Ehmke
 */
@Retention ( RUNTIME )
@Target ( METHOD )
public @interface UseCache {

	/**
	 * @return The name of the cache.
	 */
	String cacheName();

}
