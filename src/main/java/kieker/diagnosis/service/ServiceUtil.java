/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import kieker.diagnosis.common.TechnicalException;

/**
 * This is a util class to retrieve (and hold) service instances within this application.
 *
 * @author Nils Christian Ehmke
 */
public class ServiceUtil {

	private static final Map<Class<? extends ServiceIfc>, ServiceIfc> cvServiceMap = new HashMap<>( );

	public static <T extends ServiceIfc> T getService( final Class<T> aServiceClass ) {
		if ( !cvServiceMap.containsKey( aServiceClass ) ) {
			createService( aServiceClass );
		}

		final ServiceIfc service = cvServiceMap.get( aServiceClass );
		if ( !aServiceClass.isInstance( service ) ) {
			throw new TechnicalException( String.format( "Invalid service class (requested: '%s', actually: '%s')", aServiceClass, service ) );
		}

		@SuppressWarnings ( "unchecked" )
		final T convertedService = (T) service;
		return convertedService;
	}

	private static <T extends ServiceIfc> void createService( final Class<T> aServiceClass ) {
		try {
			// Create a new instance and store it in the map. Otherwise we won't be able to resolve cyclic dependencies.
			final T service = aServiceClass.newInstance( );
			cvServiceMap.put( aServiceClass, service );

			// Inject the fields of the service
			final Field[] declaredFields = aServiceClass.getDeclaredFields( );
			for ( final Field field : declaredFields ) {
				// Inject only services
				if ( field.isAnnotationPresent( InjectService.class ) ) {
					field.setAccessible( true );

					final Class<?> fieldType = field.getType( );
					if ( !ServiceIfc.class.isAssignableFrom( fieldType ) ) {
						throw new TechnicalException( "Type '" + fieldType + "' is not a service class." );
					}

					@SuppressWarnings ( "unchecked" )
					final Object otherService = ServiceUtil.getService( (Class<? extends ServiceIfc>) fieldType );
					field.set( service, otherService );
				}
			}
		}
		catch ( final Exception ex ) {
			throw new TechnicalException( String.format( "Unable to create service '%s'", aServiceClass ), ex );
		}
	}
}
