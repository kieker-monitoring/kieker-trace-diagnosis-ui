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

import kieker.diagnosis.architecture.service.properties.LogoProperty;
import kieker.diagnosis.architecture.service.properties.PropertiesService;
import kieker.diagnosis.architecture.service.properties.SplashscreenProperty;
import kieker.diagnosis.architecture.service.properties.TitleProperty;

import java.util.Optional;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class GuiLoaderImpl implements GuiLoader {

	@Autowired
	private BeanFactory ivBeanFactory;

	@Autowired
	private PropertiesService ivPropertiesService;

	@Override
	public <C extends AbstractController<?>> void loadAsMainView( final Class<C> aControllerClass, final Stage aPrimaryStage, final Optional<?> aParameter ) {
		final AnchorPane rootPane = new AnchorPane( );
		final Scene scene = new Scene( rootPane );
		aPrimaryStage.setScene( scene );

		final String logo = ivPropertiesService.loadSystemProperty( LogoProperty.class );
		final String title = ivPropertiesService.loadSystemProperty( TitleProperty.class );

		aPrimaryStage.getIcons( ).add( new Image( logo ) );
		aPrimaryStage.setTitle( title );
		aPrimaryStage.setMaximized( true );

		showSplashScreen( scene );

		loadInPane( aControllerClass, rootPane, aParameter );

		aPrimaryStage.show( );
	}

	@Override
	public <C extends AbstractController<?>> void loadAsMainView( final Class<C> aControllerClass, final Stage aPrimaryStage ) {
		loadAsMainView( aControllerClass, aPrimaryStage, Optional.empty( ) );
	}

	private void showSplashScreen( final Scene aRoot ) {
		final String splashscreen = ivPropertiesService.loadSystemProperty( SplashscreenProperty.class );

		final ImageView imageView = new ImageView( splashscreen );
		final Pane parent = new Pane( imageView );
		final Scene scene = new Scene( parent );

		final Stage stage = new Stage( );
		stage.setResizable( false );
		stage.initStyle( StageStyle.UNDECORATED );
		stage.initModality( Modality.WINDOW_MODAL );
		stage.initOwner( aRoot.getWindow( ) );
		stage.setScene( scene );

		final FadeTransition transition = new FadeTransition( Duration.millis( 3000 ), stage.getScene( ).getRoot( ) );
		transition.setFromValue( 1.0 );
		transition.setToValue( 0.0 );
		final EventHandler<ActionEvent> handler = t -> stage.hide( );
		transition.setOnFinished( handler );
		transition.play( );

		stage.showAndWait( );
	}

	@Override
	public <C extends AbstractController<?>> void loadAsDialog( final Class<C> aControllerClass, final Window aOwner, final Optional<?> aParameter ) {
		final C controller = ivBeanFactory.getBean( aControllerClass );

		// Create the scene if necessary
		final Parent parent = (Parent) controller.getView( ).getNode( );
		Scene scene = parent.getScene( );
		if ( scene == null ) {
			scene = new Scene( parent );
		}

		// Inform the controller about the loading
		controller.doInitialize( aParameter );

		scene.getStylesheets( ).add( controller.getStylesheet( ) );

		final String title = controller.getResourceBundle( ).getString( "title" );
		final String logo = ivPropertiesService.loadSystemProperty( LogoProperty.class );

		final Stage dialogStage = new Stage( );
		dialogStage.getIcons( ).add( new Image( logo ) );
		dialogStage.setTitle( title );
		dialogStage.setResizable( false );
		dialogStage.initModality( Modality.WINDOW_MODAL );
		dialogStage.initOwner( aOwner );
		dialogStage.setScene( scene );
		dialogStage.showAndWait( );
	}

	@Override
	public <C extends AbstractController<?>> void loadAsDialog( final Class<C> aControllerClass, final Window aOwner ) {
		loadAsDialog( aControllerClass, aOwner, Optional.empty( ) );
	}

	@Override
	public <C extends AbstractController<?>> void loadInPane( final Class<C> aControllerClass, final AnchorPane aPane, final Optional<?> aParameter ) {
		// Remove existing elements from the pane
		aPane.getChildren( ).clear( );
		aPane.getStylesheets( ).clear( );

		// Now load the controller and add the new elements
		final C controller = ivBeanFactory.getBean( aControllerClass );

		aPane.getChildren( ).add( controller.getView( ).getNode( ) );
		aPane.getStylesheets( ).add( controller.getStylesheet( ) );

		// Inform the controller about the loading
		controller.doInitialize( aParameter );
	}

	@Override
	public <C extends AbstractController<?>> void loadInPane( final Class<C> aControllerClass, final AnchorPane aPane ) {
		loadInPane( aControllerClass, aPane, Optional.empty( ) );
	}

}
