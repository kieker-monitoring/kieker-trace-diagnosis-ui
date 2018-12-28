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

import com.google.inject.Inject;
import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Window;
import kieker.diagnosis.backend.base.common.ClassUtil;
import kieker.diagnosis.backend.base.service.ServiceBase;

/**
 * This is the abstract base for a view model. It provides convenient methods, like retrieving a service or localizing a string. For each class extending this base, a resource bundle has to be available in the classpath with the name of the implementing class.
 *
 * @param <V> The type of the view.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ViewModelBase<V extends ViewBase<?>> {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	@Inject
	private V ivView;

	@Inject
	private Injector ivInjector;

	/**
	 * Gets the view for this view model.
	 *
	 * @return The view.
	 */
	protected final V getView( ) {
		return ivView;
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

	/**
	 * Gets a service of the given type. Use this method with care, as view models should use services only in very rare cases.
	 *
	 * @param aServiceClass The type of the service.
	 *
	 * @return The service.
	 */
	protected final <S extends ServiceBase> S getService( final Class<S> aServiceClass ) {
		return ivInjector.getInstance( aServiceClass );
	}

	/**
	 * Closes the corresponding window of the view. If the view is a dialog, this closes the dialog. Otherwise the whole application window will be closed.
	 */
	public final void close( ) {
		final Scene scene = getView( ).getScene( );
		final Window window = scene.getWindow( );
		window.hide( );
	}

	/**
	 * A convenient helper method to trim a user input.
	 *
	 * @param aString The string to trim. Can be {@code null}.
	 *
	 * @return The trimmed string. If the string becomes empty, {@code null} will be returned.
	 */
	protected final String trimToNull( final String aString ) {
		if ( aString == null ) {
			return aString;
		} else {
			final String string = aString.trim( );
			if ( string.isEmpty( ) ) {
				return null;
			} else {
				return string;
			}
		}
	}

}
