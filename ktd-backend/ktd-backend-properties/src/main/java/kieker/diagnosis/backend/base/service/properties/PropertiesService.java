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

package kieker.diagnosis.backend.base.service.properties;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.ServiceBase;
import kieker.diagnosis.backend.base.service.cache.InvalidateCache;
import kieker.diagnosis.backend.base.service.cache.UseCache;

/**
 * This service is responsible for loading and saving properties within the application.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class PropertiesService extends ServiceBase {

	@Inject
	private Injector ivInjector;

	public PropertiesService( ) {
		super( false );
	}

	/**
	 * Delivers the value of the given application property or its default value, if the property has not been set yet. This method is cached.
	 *
	 * @param aPropertyClass
	 *            The property.
	 *
	 * @return The value of the given property.
	 */
	@UseCache ( cacheName = "applicationProperties" )
	public <T> T loadApplicationProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );
		final String defaultValue = property.serialize( property.getDefaultValue( ) );
		final String serializedValue = preferences.get( key, defaultValue );

		return property.deserialize( serializedValue );
	}

	/**
	 * Saves the given application property.
	 *
	 * @param aPropertyClass
	 *            The property.
	 * @param aValue
	 *            The new value of the property.
	 */
	@InvalidateCache ( cacheName = "applicationProperties", keyParameter = 0 )
	public <T> void saveApplicationProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass, final T aValue ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );
		final String serializedValue = property.serialize( aValue );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );

		preferences.put( key, serializedValue );

		try {
			preferences.flush( );
		} catch ( final BackingStoreException ex ) {
			getLogger( ).error( getLocalizedString( "errorMessage" ), ex );
		}
	}

	private <T> T getProperty( final Class<? extends T> aPropertyClass ) {
		return ivInjector.getInstance( aPropertyClass );
	}

	/**
	 * Delivers the value of the given system property. This method is cached.
	 *
	 * @param aPropertyClass
	 *            The property.
	 *
	 * @return The value of the given property.
	 */
	@UseCache ( cacheName = "systemProperties" )
	public <T> T loadSystemProperty( final Class<? extends SystemProperty<T>> aPropertyClass ) {
		final SystemProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		final String serializedValue = System.getProperty( key );
		return property.deserialize( serializedValue );
	}

}
