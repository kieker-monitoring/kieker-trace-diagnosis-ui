/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.base.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;

import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import kieker.diagnosis.backend.base.common.ClassUtil;
import kieker.diagnosis.frontend.base.mixin.IconMixin;

/**
 * This is the abstract base for a view. It provides a convenient method to localize a string. Also a corresponding
 * stylesheet file is applied. For each class extending this base, a resource bundle has to be available in the
 * classpath with the name of the implementing class.
 *
 * @param <C>
 *            The type of the controller.
 *
 * @author Nils Christian Ehmke
 */
public abstract class ViewBase<C extends ControllerBase<?>> extends VBox implements IconMixin {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ClassUtil.getRealName( getClass( ) ) );

	@Inject
	private C ivController;

	static {
		final URL fontAwesomeUrl = ViewBase.class.getResource( "fa-solid-900.ttf" );
		Font.loadFont( fontAwesomeUrl.toExternalForm( ), 12 );

		final URL openSansUrl = ViewBase.class.getResource( "OpenSans-Regular.ttf" );
		Font.loadFont( openSansUrl.toExternalForm( ), 12 );

		final URL openSansBoldUrl = ViewBase.class.getResource( "OpenSans-Bold.ttf" );
		Font.loadFont( openSansBoldUrl.toExternalForm( ), 12 );

		final URL openSansItalicUrl = ViewBase.class.getResource( "OpenSans-Italic.ttf" );
		Font.loadFont( openSansItalicUrl.toExternalForm( ), 12 );

		final URL openSansBoldItalicUrl = ViewBase.class.getResource( "OpenSans-BoldItalic.ttf" );
		Font.loadFont( openSansBoldItalicUrl.toExternalForm( ), 12 );
	}

	public ViewBase( ) {
		getStylesheets( ).add( getBaseStylesheetUrl( ) );
		getStylesheets( ).add( getStylsheetUrl( ) );
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

	/**
	 * Gets the controller for this view.
	 *
	 * @return The controller.
	 */
	protected final C getController( ) {
		return ivController;
	}

	private String getBaseStylesheetUrl( ) {
		return ViewBase.class.getCanonicalName( ).replace( ".", "/" ) + ".css";
	}

	private String getStylsheetUrl( ) {
		return ClassUtil.getRealName( getClass( ) ).replace( ".", "/" ) + ".css";
	}

	/**
	 * This method can be used to send a parameter to the current view. The precise nature of the parameter (and all
	 * actions necessary to use it) depends on the view.
	 *
	 * @param aParameter
	 *            The parameter.
	 */
	public abstract void setParameter( Object aParameter );

}
