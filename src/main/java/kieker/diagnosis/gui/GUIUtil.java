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

package kieker.diagnosis.gui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import kieker.diagnosis.common.TechnicalException;
import kieker.diagnosis.gui.main.MainController;
import kieker.diagnosis.service.InjectService;
import kieker.diagnosis.service.ServiceIfc;
import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.properties.PropertiesService;

/**
 * @author Nils Christian Ehmke
 */
public class GUIUtil {

	private static final Map<Class<?>, LoadedView> cvLoadedViewCache = new HashMap<>( );
	private static MainController mainController;

	private GUIUtil( ) {
	}

	public static void clearCache( ) {
		cvLoadedViewCache.clear( );
	}

	public static <V extends AbstractView, C extends AbstractController<V>> void loadView( final Class<C> aControllerClass, final Stage aRootStage )
			throws Exception {
		LoadedView loadedView = getLoadedViewFromCache( aControllerClass, new ContextEntry[0] );

		if ( loadedView == null ) {
			loadedView = createLoadedView( aControllerClass, new ContextEntry[0] );
		}

		applyLoadedView( loadedView, aRootStage );
	}

	public static <V extends AbstractView, C extends AbstractController<V>> void loadView( final Class<C> aControllerClass, final AnchorPane aRootStage,
			final ContextEntry[] aArguments ) throws Exception {
		LoadedView loadedView = getLoadedViewFromCache( aControllerClass, aArguments );

		if ( loadedView == null ) {
			loadedView = createLoadedView( aControllerClass, aArguments );
		}

		applyLoadedView( loadedView, aRootStage );
	}

	public static <V extends AbstractView, C extends AbstractController<V>> void loadDialog( final Class<C> aControllerClass, final Window aOwner )
			throws Exception {
		LoadedView loadedView = getLoadedViewFromCache( aControllerClass, new ContextEntry[0] );

		if ( loadedView == null ) {
			loadedView = createLoadedView( aControllerClass, new ContextEntry[0] );
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

	private static LoadedView getLoadedViewFromCache( final Class<?> aControllerClass, final ContextEntry[] aArguments ) throws Exception {
		// If we should not cache the views, we do not access the cache
		final PropertiesService propertiesService = ServiceUtil.getService( PropertiesService.class );
		if ( !propertiesService.isCacheViews( ) ) {
			return null;
		}

		// If there are arguments for the controller, we have to create a new controller
		if ( (aArguments != null) && (aArguments.length > 0) ) {
			return null;
		}

		// Otherwise we can check whether the cache contains the view already
		return cvLoadedViewCache.get( aControllerClass );
	}

	private static <V extends AbstractView, C extends AbstractController<V>> LoadedView createLoadedView( final Class<C> aControllerClass,
			final ContextEntry[] aArguments ) throws Exception {
		final ClassLoader classLoader = GUIUtil.class.getClassLoader( );

		final String baseName = aControllerClass.getCanonicalName( ).replace( "Controller", "" );

		// Get the FXML file
		final String viewFXMLName = baseName.replace( ".", "/" ) + ".fxml";
		final URL viewResource = classLoader.getResource( viewFXMLName );

		// Get the CSS file name
		final String cssName = baseName.replace( ".", "/" ) + ".css";
		final URL cssResource = classLoader.getResource( cssName );

		// Get the resource bundle
		final String bundleBaseName = baseName.toLowerCase( Locale.ROOT );
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( bundleBaseName, Locale.getDefault( ) );

		// Create the controller
		final Constructor<C> controllerConstructor = aControllerClass.getConstructor( Context.class );
		final Context context = new Context( aArguments );
		final C controller = controllerConstructor.newInstance( context );

		// Create the controller proxy
		final String controllerIfcName = aControllerClass.getCanonicalName( ) + "Ifc";
		final Class<?> controllerIfc = classLoader.loadClass( controllerIfcName );
		final Object contrProxy = Proxy.newProxyInstance( classLoader, new Class<?>[] { controllerIfc }, new ErrorHandlingInvocationHandler( controller ) );

		// Load the FXML file
		final FXMLLoader loader = new FXMLLoader( );
		loader.setController( contrProxy );
		loader.setLocation( viewResource );
		loader.setResources( resourceBundle );
		final Node node = (Node) loader.load( );

		// Create the view
		final String viewName = aControllerClass.getCanonicalName( ).replace( "Controller", "View" );
		@SuppressWarnings ( "unchecked" )
		final Class<V> viewClass = (Class<V>) classLoader.loadClass( viewName );
		final V view = viewClass.newInstance( );
		view.setResourceBundle( resourceBundle );
		controller.setView( view );

		// Now inject the fields of the view
		node.applyCss( );
		final Field[] declaredViewFields = viewClass.getDeclaredFields( );
		for ( final Field field : declaredViewFields ) {
			// Inject only JavaFX based fields
			if ( Node.class.isAssignableFrom( field.getType( ) ) ) {
				field.setAccessible( true );

				final String fieldName = "#" + field.getName( );
				Object fieldValue = node.lookup( fieldName );
				if ( fieldValue == null ) {
					// In some cases (TitledPane in dialogs which are not visible yet), the lookup does not work.
					// We correct this for the cases we know of.
					fieldValue = lookup( node, fieldName );
				}
				field.set( view, fieldValue );
			}
		}

		// Inject the fields of the controller
		final Field[] declaredControllerFields = aControllerClass.getDeclaredFields( );
		for ( final Field field : declaredControllerFields ) {
			// Inject services
			final Class<?> fieldType = field.getType( );

			if ( field.isAnnotationPresent( InjectService.class ) ) {
				field.setAccessible( true );

				if ( !ServiceIfc.class.isAssignableFrom( fieldType ) ) {
					throw new TechnicalException( "Type '" + fieldType + "' is not a service class." );
				}

				@SuppressWarnings ( "unchecked" )
				final Object service = ServiceUtil.getService( (Class<? extends ServiceIfc>) fieldType );
				field.set( controller, service );
			}

			// Inject the main controller
			if ( MainController.class.isAssignableFrom( fieldType ) ) {
				field.setAccessible( true );

				field.set( controller, mainController );
			}
		}

		// Now that everything should be set, the controller can be initialized
		controller.doInitialize( );

		final String title = (resourceBundle.containsKey( "title" ) ? resourceBundle.getString( "title" ) : "");
		final LoadedView loadedView = new LoadedView( node, title, cssResource.toExternalForm( ), null );

		final PropertiesService propertiesService = ServiceUtil.getService( PropertiesService.class );
		if ( propertiesService.isCacheViews( ) ) {
			cvLoadedViewCache.put( aControllerClass, loadedView );
		}

		if ( aControllerClass == MainController.class ) {
			mainController = (MainController) controller;
		}

		return loadedView;
	}

	private static Object lookup( final Node aNode, final String aFieldName ) {
		Object element = aNode.lookup( aFieldName );

		if ( element == null ) {
			if ( aNode instanceof Pane ) {
				final Pane pane = (Pane) aNode;

				for ( final Node child : pane.getChildren( ) ) {
					element = lookup( child, aFieldName );
					if ( element != null ) {
						break;
					}
				}
			}
			else if ( aNode instanceof TitledPane ) {
				final TitledPane titledPane = (TitledPane) aNode;
				final Node content = titledPane.getContent( );

				if ( content != null ) {
					element = lookup( content, aFieldName );
				}
			}
		}

		return element;
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

		public LoadedView( final Node aNode, final String aTitle, final String aStylesheetURL, final Supplier<Void> aInitializer ) {
			ivNode = aNode;
			ivTitle = aTitle;
			ivStylesheetURL = aStylesheetURL;
			setInitializer( aInitializer );
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

		public void setInitializer( final Supplier<Void> aInitializer ) {
		}

	}

	private static class ErrorHandlingInvocationHandler implements InvocationHandler {

		private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( "kieker.diagnosis.gui.util.errorhandling", Locale.getDefault( ) );
		private final String ivTitle = ivResourceBundle.getString( "error" );
		private final String ivHeader = ivResourceBundle.getString( "errorHeader" );

		private final Object ivDelegate;

		public ErrorHandlingInvocationHandler( final Object aDelegate ) {
			ivDelegate = aDelegate;
		}

		@Override
		public Object invoke( final Object aProxy, final Method aMethod, final Object[] aArgs ) throws Throwable {
			try {
				return aMethod.invoke( ivDelegate, aArgs );
			}
			catch ( final Exception ex ) {
				logError( ex );
				showAlertDialog( ex );

				return null;
			}
		}

		private void logError( final Exception aEx ) {
			final Logger logger = LogManager.getLogger( ivDelegate.getClass( ) );
			logger.error( aEx.getMessage( ), aEx );
		}

		private void showAlertDialog( final Exception aEx ) {
			final Alert alert = new Alert( AlertType.ERROR );
			alert.setContentText( aEx.toString( ) );
			alert.setTitle( ivTitle );
			alert.setHeaderText( ivHeader );

			final Window window = alert.getDialogPane( ).getScene( ).getWindow( );
			if ( window instanceof Stage ) {
				final Stage stage = (Stage) window;
				stage.getIcons( ).add( new Image( "kieker-logo.png" ) );
			}

			alert.showAndWait( );
		}

	}
}
