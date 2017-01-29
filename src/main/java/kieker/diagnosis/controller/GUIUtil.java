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

package kieker.diagnosis.controller;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.util.Context;
import kieker.diagnosis.util.ContextEntry;

/**
 * @author Nils Christian Ehmke
 */
public class GUIUtil {

	private static final Map<Class<?>, LoadedView> cvLoadedViewCache = new HashMap<>( );

	private GUIUtil( ) {
	}

	public static void clearCache( ) {
		cvLoadedViewCache.clear( );
	}

	public static <T extends AbstractController> void loadView( final Class<T> aControllerClass, final Stage aRootStage ) throws Exception {
		LoadedView loadedView = getLoadedViewFromCache( aControllerClass );

		if ( loadedView == null ) {
			loadedView = createLoadedView( aControllerClass );
		}

		applyLoadedView( loadedView, aRootStage );
	}

	public static <T extends AbstractController> void loadView( final Class<T> aControllerClass, final AnchorPane aRootStage, final ContextEntry... aArguments )
			throws Exception {
		LoadedView loadedView = getLoadedViewFromCache( aControllerClass, aArguments );

		if ( loadedView == null ) {
			loadedView = createLoadedView( aControllerClass, aArguments );
		}

		applyLoadedView( loadedView, aRootStage );
	}

	public static <T extends AbstractController> void loadDialog( final Class<T> aControllerClass, final Window aOwner ) throws Exception {
		LoadedView loadedView = getLoadedViewFromCache( aControllerClass );

		if ( loadedView == null ) {
			loadedView = createLoadedView( aControllerClass );
		}

		// We have to reuse the scene if necessary. Otherwise we get problems when we used the cache views.
		final Parent parent = (Parent) loadedView.getNode( );
		Scene scene = parent.getScene( );
		if ( scene == null ) {
			scene = new Scene( parent );
		}

		scene.getStylesheets( ).add( loadedView.ivStylesheetURL );

		final Stage dialogStage = new Stage( );
		dialogStage.getIcons( ).add( new Image( "kieker-logo.png" ) );
		dialogStage.setTitle( loadedView.getTitle( ) );
		dialogStage.setResizable( false );
		dialogStage.initModality( Modality.WINDOW_MODAL );
		dialogStage.initOwner( aOwner );
		dialogStage.setScene( scene );
		dialogStage.showAndWait( );
	}

	private static LoadedView getLoadedViewFromCache( final Class<?> aControllerClass, final ContextEntry... aArguments ) {
		// If we should not cache the views, we do not access the cache
		if ( !PropertiesModel.getInstance( ).isCacheViews( ) ) {
			return null;
		}

		// If there are arguments for the controller, we have to create a new controller
		if ( (aArguments != null) && (aArguments.length > 0) ) {
			return null;
		}

		// Otherwise we can check whether the cache contains the view already
		return cvLoadedViewCache.get( aControllerClass );
	}

	private static LoadedView createLoadedView( final Class<?> aControllerClass, final ContextEntry... aArguments ) throws Exception {
		final String baseName = aControllerClass.getCanonicalName( ).replace( "Controller", "" ).replace( ".controller.", ".view." );

		// Get the FXML file
		final String viewFXMLName = baseName.replace( ".", "/" ) + ".fxml";
		final URL viewResource = GUIUtil.class.getClassLoader( ).getResource( viewFXMLName );

		// Get the CSS file name
		final String cssName = baseName.replace( ".", "/" ) + ".css";
		final URL cssResource = GUIUtil.class.getClassLoader( ).getResource( cssName );

		// Get the resource bundle
		final String bundleBaseName = baseName.toLowerCase( Locale.ROOT );
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( bundleBaseName, Locale.getDefault( ) );

		// Create the controller
		final Constructor<?> constructor = aControllerClass.getConstructor( Context.class );
		final Context context = new Context( aArguments );
		final Object controller = constructor.newInstance( context );

		// Load the FXML file
		final FXMLLoader loader = new FXMLLoader( );
		loader.setController( controller );
		loader.setLocation( viewResource );
		loader.setResources( resourceBundle );
		final Node node = (Node) loader.load( );

		final String title = (resourceBundle.containsKey( "title" ) ? resourceBundle.getString( "title" ) : "");
		final LoadedView loadedView = new LoadedView( node, title, cssResource.toExternalForm( ) );

		if ( PropertiesModel.getInstance( ).isCacheViews( ) ) {
			cvLoadedViewCache.put( aControllerClass, loadedView );
		}

		return loadedView;
	}

	private static void applyLoadedView( final LoadedView aLoadedView, final Stage aRootStage ) {
		final Scene scene = new Scene( (Parent) aLoadedView.getNode( ) );
		aRootStage.setScene( scene );

		aRootStage.getIcons( ).add( new Image( "kieker-logo.png" ) );
		aRootStage.setTitle( "Kieker Trace Diagnosis - 1.2.0-SNAPSHOT" );
		aRootStage.setMaximized( true );

		showSplashScreen( scene );

		aRootStage.show( );
	}

	private static void showSplashScreen( final Scene aRoot ) {
		final ImageView imageView = new ImageView( "splashscreen.png" );
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

	private static void applyLoadedView( final LoadedView aLoadedView, final AnchorPane aRootPane ) {
		final Node node = aLoadedView.getNode( );

		// Add the node as children of the root pane
		aRootPane.getChildren( ).clear( );
		aRootPane.getChildren( ).add( node );

		// Add the corresponding stylesheets of the node
		aRootPane.getStylesheets( ).clear( );
		aRootPane.getStylesheets( ).add( aLoadedView.getStylesheetURL( ) );

		// Make sure that the node is display in full view in the root pane
		AnchorPane.setLeftAnchor( node, 0.0 );
		AnchorPane.setBottomAnchor( node, 0.0 );
		AnchorPane.setRightAnchor( node, 0.0 );
		AnchorPane.setTopAnchor( node, 0.0 );
	}

	private static class LoadedView {

		private final Node ivNode;
		private final String ivStylesheetURL;
		private final String ivTitle;

		public LoadedView( final Node aNode, final String aTitle, final String aStylesheetURL ) {
			ivNode = aNode;
			ivTitle = aTitle;
			ivStylesheetURL = aStylesheetURL;
		}

		public Node getNode( ) {
			return ivNode;
		}

		public String getTitle( ) {
			return ivTitle;
		}

		public String getStylesheetURL( ) {
			return ivStylesheetURL;
		}

	}

}
