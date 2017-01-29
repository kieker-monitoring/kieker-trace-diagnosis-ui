package kieker.diagnosis.service;

import java.util.HashMap;
import java.util.Map;

public class ServiceUtil {

	private static final Map<Class<? extends ServiceIfc>, ServiceIfc> cvServiceMap = new HashMap<>( );

	@SuppressWarnings ( "unchecked" )
	public static <T extends ServiceIfc> T getService( final Class<T> serviceClass ) throws Exception {
		if ( !cvServiceMap.containsKey( serviceClass ) ) {
			createService( serviceClass );
		}

		return (T) cvServiceMap.get( serviceClass );
	}

	private static <T extends ServiceIfc> void createService( final Class<T> aServiceClass ) throws Exception {
		final T service = aServiceClass.newInstance( );
		cvServiceMap.put( aServiceClass, service );
	}

}
