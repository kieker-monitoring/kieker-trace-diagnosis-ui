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

package kieker.diagnosis.architecture.ui;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Injector;

import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.service.ServiceBase;

/**
 * This is the abstract base for a controller. It provides convenient methods, like retrieving a service or localizing a string. For each class extending this
 * base, a resource bundle has to be available in the classpath with the name of the implementing class.
 *
 * @param <VM>
 *            The type of the view model.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ControllerBase<VM extends ViewModelBase<?>> {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	private Logger ivLogger;

	@Inject
	private VM ivViewModel;

	@Inject
	private Injector ivInjector;

	public ControllerBase( ) {
		// Make sure that the singleton annotation is present
		ClassUtil.assertSingletonAnnotation( getClass( ) );
	}

	/**
	 * Gets the view model for this controller.
	 *
	 * @return The view model.
	 */
	protected final VM getViewModel( ) {
		return ivViewModel;
	}

	/**
	 * Gets a service of the given type.
	 *
	 * @param aServiceClass
	 *            The type of the service.
	 *
	 * @return The service.
	 */
	protected final <S extends ServiceBase> S getService( final Class<S> aServiceClass ) {
		return ivInjector.getInstance( aServiceClass );
	}

	/**
	 * Returns the logger for the current class.
	 *
	 * @return The logger.
	 */
	protected final Logger getLogger( ) {
		if ( ivLogger == null ) {
			ivLogger = LogManager.getLogger( ClassUtil.getRealClass( getClass( ) ) );
		}

		return ivLogger;
	}

	/**
	 * Gets a controller of the given type. Use this method with care, as it can lead to incomprehensible execution flow.
	 *
	 * @param aControllerClass
	 *            The type of the controller.
	 *
	 * @return The controller.
	 */
	protected final <C extends ControllerBase<?>> C getController( final Class<C> aControllerClass ) {
		return ivInjector.getInstance( aControllerClass );
	}

	/**
	 * Delivers the localized string for the given key for the current class.
	 *
	 * @param aKey
	 *            The resource key.
	 *
	 * @return The localized string.
	 */
	protected final String getLocalizedString( final String aKey ) {
		return ivResourceBundle.getString( aKey );
	}

}
