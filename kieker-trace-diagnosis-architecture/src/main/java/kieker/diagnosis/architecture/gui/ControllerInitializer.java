/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.architecture.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class ControllerInitializer implements BeanPostProcessor {

	@Autowired
	private ViewInitializer ivViewInitializer;

	@Override
	public Object postProcessBeforeInitialization( final Object aBean, final String aBeanName ) throws BeansException {
		if ( aBean instanceof AbstractController ) {
			try {
				final AbstractController<?> controller = (AbstractController<?>) aBean;
				final AbstractView view = controller.getView( );
				final String baseName = view.getClass( ).getCanonicalName( );
				final Object controllerProxy = Enhancer.create( controller.getClass( ), new ErrorHandlingInvocationHandler( controller ) );

				final ResourceBundle resourceBundle = ResourceBundle.getBundle( baseName, Locale.getDefault( ) );
				controller.setResourceBundle( resourceBundle );

				// Get the CSS stylesheet
				final String cssName = ResourceUtils.CLASSPATH_URL_PREFIX + baseName.replace( ".", "/" ) + ".css";
				final URL cssResource = ResourceUtils.getURL( cssName );
				final String stylesheet = cssResource.toExternalForm( );
				controller.setStylesheet( stylesheet );

				// Load the actual view
				final String viewFileName = ResourceUtils.CLASSPATH_URL_PREFIX + baseName.replace( '.', '/' ) + ".fxml";
				final URL viewFile = ResourceUtils.getURL( viewFileName );

				final FXMLLoader loader = new FXMLLoader( );
				loader.setController( controllerProxy );
				loader.setLocation( viewFile );
				loader.setResources( resourceBundle );
				final Node node = loader.load( );
				node.applyCss( );

				// Prepare the node to be used in an anchor pane
				AnchorPane.setTopAnchor( node, 0.0 );
				AnchorPane.setLeftAnchor( node, 0.0 );
				AnchorPane.setRightAnchor( node, 0.0 );
				AnchorPane.setBottomAnchor( node, 0.0 );

				view.setNode( node );

				ivViewInitializer.initialize( view );
			} catch ( final IOException | MissingResourceException ex ) {
				throw new BeanInitializationException( "Resource could not be loaded", ex );
			}
		}
		return aBean;
	}

	@Override
	public Object postProcessAfterInitialization( final Object aBean, final String aBeanName ) throws BeansException {
		return aBean;
	}

}
