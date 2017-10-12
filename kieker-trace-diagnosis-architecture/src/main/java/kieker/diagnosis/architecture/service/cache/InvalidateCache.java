package kieker.diagnosis.architecture.service.cache;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be used at service methods and makes sure that an entry of a cached method call is invalidated. It is assumed that the method has at
 * least one parameter which provides the key to the cache. Entries can be cached by using the {@link UseCache} annotation.
 *
 * @see UseCache
 *
 * @author Nils Christian Ehmke
 */
@Retention ( RUNTIME )
@Target ( METHOD )
public @interface InvalidateCache {

	/**
	 * @return The name of the cache.
	 */
	String cacheName();

	/**
	 * @return Determines which parameter of the method is the key.
	 */
	int keyParameter();

}
