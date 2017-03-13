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

package kieker.diagnosis.architecture.service.properties;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * The default implementation for a {@link PropertiesService}.
 *
 * @author Nils Christian Ehmke
 */
@Component
class PropertiesServiceImpl implements PropertiesService {

	private static final Logger cvLogger = LoggerFactory.getLogger( PropertiesService.class );

	@Autowired
	private BeanFactory ivBeanFactory;

	@Autowired
	private Environment ivEnvironment;

	private long ivVersion = 0L;

	@Override
	public <T> T loadApplicationProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );
		final String defaultValue = property.serialize( property.getDefaultValue( ) );
		final String serializedValue = preferences.get( key, defaultValue );

		return property.deserialize( serializedValue );
	}

	@Override
	public boolean loadBooleanApplicationProperty( final Class<? extends ApplicationProperty<Boolean>> aPropertyClass ) {
		final Boolean propertyValue = loadApplicationProperty( aPropertyClass );

		return Boolean.TRUE.equals( propertyValue );
	}

	@Override
	public <T> T loadSystemProperty( final Class<? extends SystemProperty<T>> aPropertyClass ) {
		final SystemProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );

		final String serializedValue = ivEnvironment.getProperty( key );
		return property.deserialize( serializedValue );
	}

	@Override
	public <T> void saveApplicationProperty( final Class<? extends ApplicationProperty<T>> aPropertyClass, final T aValue ) {
		final ApplicationProperty<T> property = getProperty( aPropertyClass );
		final String key = property.getKey( );
		final String serializedValue = property.serialize( aValue );

		final Preferences preferences = Preferences.userNodeForPackage( PropertiesService.class );

		preferences.put( key, serializedValue );

		try {
			preferences.flush( );
		} catch ( final BackingStoreException ex ) {
			cvLogger.error( "An error occured while trying to save the preferences", ex );
		}

		ivVersion++;
	}

	private <T> T getProperty( final Class<? extends T> aPropertyClass ) {
		return ivBeanFactory.getBean( aPropertyClass );
	}

	@Override
	public long getVersion( ) {
		return ivVersion;
	}

}
