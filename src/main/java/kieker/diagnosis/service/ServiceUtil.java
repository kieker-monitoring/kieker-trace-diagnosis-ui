package kieker.diagnosis.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ServiceUtil {

	private static final Map<Class<? extends ServiceIfc>, ServiceIfc> cvServiceMap = new HashMap<>( );

	@SuppressWarnings ( "unchecked" )
	public static <T extends ServiceIfc> T getService( final Class<T> serviceClass ) {
		if ( !cvServiceMap.containsKey( serviceClass ) ) {
			createService( serviceClass );
		}

		return (T) cvServiceMap.get( serviceClass );
	}

	private static <T extends ServiceIfc> void createService( final Class<T> aServiceClass ) {
		try {
			final T service = aServiceClass.newInstance( );
			cvServiceMap.put( aServiceClass, service );

			// Inject the fields of the service
			final Field[] declaredFields = aServiceClass.getDeclaredFields( );
			for ( final Field field : declaredFields ) {
				// Inject services
				if ( ServiceIfc.class.isAssignableFrom( field.getType( ) ) ) {
					field.setAccessible( true );

					@SuppressWarnings ( "unchecked" )
					final Object otherService = ServiceUtil.getService( (Class<? extends ServiceIfc>) field.getType( ) );
					field.set( service, otherService );
				}
			}
		}
		catch ( final Exception ex ) {
			throw new RuntimeException( ex );
		}
	}
}
