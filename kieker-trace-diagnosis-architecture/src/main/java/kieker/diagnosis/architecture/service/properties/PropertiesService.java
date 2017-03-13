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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * This service is responsible for handling system and application properties.
 *
 * @author Nils Christian Ehmke
 *
 * @see ApplicationProperty
 * @see SystemProperty
 */
public interface PropertiesService {

	/**
	 * Loads the application property.
	 *
	 * @param aPropertyClass
	 *            The class of the property.
	 *
	 * @return The value of the property.
	 */
	@Cacheable ( "applicationProperties" )
	public <T> T loadApplicationProperty( Class<? extends ApplicationProperty<T>> aPropertyClass );

	@Cacheable ( "applicationProperties" )
	public boolean loadBooleanApplicationProperty( Class<? extends ApplicationProperty<Boolean>> aPropertyClass );

	/**
	 * Loads the system property.
	 *
	 * @param aPropertyClass
	 *            The class of the property.
	 *
	 * @return The value of the property.
	 */
	@Cacheable ( "systemProperties" )
	public <T> T loadSystemProperty( Class<? extends SystemProperty<T>> aPropertyClass );

	/**
	 * Changes and saves the application property.
	 *
	 * @param aPropertyClass
	 *            The class of the property.
	 * @param aValue
	 *            The new value of the property.
	 */
	@CacheEvict ( cacheNames = "applicationProperties", key = "#root.args[0]" )
	public <T> void saveApplicationProperty( Class<? extends ApplicationProperty<T>> aPropertyClass, T aValue );

	/**
	 * Delivers the current version number of the application properties. Each call to {@link #saveApplicationProperty(Class, Object)} increments the version
	 * number.
	 *
	 * @return The current version number.
	 */
	public long getVersion( );

}
