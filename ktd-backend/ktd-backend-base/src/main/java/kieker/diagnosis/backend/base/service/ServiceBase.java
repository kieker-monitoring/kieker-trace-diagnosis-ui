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

package kieker.diagnosis.backend.base.service;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kieker.diagnosis.backend.base.common.ClassUtil;

/**
 * This is the abstract base for a service. It provides convenient methods, like retrieving a service or localizing a string. For each class extending this base, a resource bundle has to be available in the classpath with the name of the implementing class.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ServiceBase {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	private static Logger cvLogger;

	/**
	 * Gets a service of the given type.
	 *
	 * @param aServiceClass The type of the service.
	 *
	 * @return The service.
	 */
	protected final <S extends ServiceBase> S getService( final Class<S> aServiceClass ) {
		return ServiceFactory.getService( aServiceClass );
	}

	/**
	 * Returns the logger for the current class.
	 *
	 * @return The logger.
	 */
	protected final Logger getLogger( ) {
		if ( cvLogger == null ) {
			cvLogger = LogManager.getLogger( ClassUtil.getRealClass( getClass( ) ) );
		}

		return cvLogger;
	}

	/**
	 * Delivers the localized string for the given key for the current class.
	 *
	 * @param aKey The resource key.
	 *
	 * @return The localized string.
	 */
	protected final String getLocalizedString( final String aKey ) {
		return ivResourceBundle.getString( aKey );
	}

}
