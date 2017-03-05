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

package kieker.diagnosis.service.properties;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kieker.diagnosis.common.TechnicalException;
import kieker.diagnosis.service.ServiceIfc;

/**
 * @author Nils Christian Ehmke
 */
public final class PropertiesService implements ServiceIfc {

	private static final Logger cvLogger = LogManager.getLogger( PropertiesService.class );

	private final Map<String, Object> ivCachedProperties = new HashMap<>( );
	private final Map<Class<?>, Object> ivCachedPropertyInstances = new HashMap<>( );
	private long ivVersion = 0L;

	public <T> T loadSystemProperty( final Class<? extends SystemProperty<T>> aPropertyClass ) {
		final SystemProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		@SuppressWarnings ( "unchecked" )
		T value = (T) ivCachedProperties.get( key );

		if ( value == null ) {
			final Properties properties = new Properties( );
			final ClassLoader classLoader = PropertiesService.class.getClassLoader( );

			try ( InputStream inputStream = classLoader.getResourceAsStream( "config.properties" ) ) {
				properties.load( inputStream );
				final String serializedValue = properties.getProperty( key );

				value = property.deserialize( serializedValue );
				ivCachedProperties.put( key, value );
			} catch ( final IOException e ) {
				cvLogger.error( e );
			}

		}

		return value;
	}

	public boolean loadPrimitiveProperty( final Class<? extends ApplicationProperty<Boolean>> aPropertyClass ) {
		return loadProperty( aPropertyClass ).booleanValue( );
	}

	public <T> T loadProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		@SuppressWarnings ( "unchecked" )
		T value = (T) ivCachedProperties.get( key );

		if ( value == null ) {
			final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );
			final String serializedValue = preferences.get( key, property.getDefaultValue( ) );

			value = property.deserialize( serializedValue );
			ivCachedProperties.put( key, value );
		}

		return value;
	}

	public <T> void saveProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass, final T aValue ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );
		final String serializedValue = property.serialize( aValue );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );

		preferences.put( key, serializedValue );

		try {
			preferences.flush( );
		} catch ( final BackingStoreException e ) {
			cvLogger.error( e );
		}

		ivCachedProperties.put( key, aValue );
		ivVersion++;
	}

	private <T> T getProperty( final Class<? extends T> aPropertyClass ) {
		@SuppressWarnings ( "unchecked" )
		T property = (T) ivCachedPropertyInstances.get( aPropertyClass );

		if ( property == null ) {
			try {
				property = aPropertyClass.newInstance( );
			} catch ( InstantiationException | IllegalAccessException ex ) {
				throw new TechnicalException( ex );
			}
			ivCachedPropertyInstances.put( aPropertyClass, property );
		}

		return property;
	}

	public long getVersion( ) {
		return ivVersion;
	}
}
