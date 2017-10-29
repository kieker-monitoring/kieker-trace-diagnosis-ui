package kieker.diagnosis.architecture.service;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * This factory can be used to retrieve a service from the CDI context, even if the requesting class is not in the context.
 *
 * @author Nils Christian Ehmke
 */
public final class ServiceFactory {

	@Inject
	private static Injector cvInjector;

	private ServiceFactory( ) {
		// Avoid instantiation
	}

	public static <S extends ServiceBase> S getService( final Class<S> aServiceClass ) {
		return cvInjector.getInstance( aServiceClass );
	}

}
