/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.base.common;

import java.io.InputStream;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * A util class to handle exceptions.
 *
 * @author Nils Christian Ehmke
 */
public final class ExceptionUtil {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( ExceptionUtil.class.getName( ) );

	private ExceptionUtil( ) {
		// Avoid instantiation
	}

	/**
	 * This method handles an exception. The exception is logged and an error dialog is shown. If the exception is a
	 * {@link BusinessRuntimeException}, the error is not logged and the error dialog indicates that the error is a
	 * business error.
	 *
	 * @param throwable
	 *            The exception to handle.
	 * @param loggerName
	 *            The name of the logger, the exception will be logged in.
	 */
	public static void handleException( final Throwable throwable, final String loggerName ) {
		showExceptionDialog( throwable );
		logException( throwable, loggerName );
	}

	private static void showExceptionDialog( final Throwable throwable ) {
		final boolean isDelegateException = throwable instanceof DelegateException;
		final Throwable exception = isDelegateException ? throwable.getCause( ) : throwable;

		// Keep in mind that some controllers start a new thread. As this might also lead to exceptions, the whole
		// dialog showing has to be performed in the JavaFX application thread.
		final Runnable runnable = ( ) -> {
			// Prepare the dialog
			final Alert alert = new Alert( AlertType.ERROR );
			alert.setTitle( RESOURCE_BUNDLE.getString( "errorTitle" ) );
			alert.setHeaderText( RESOURCE_BUNDLE.getString( "errorMessage" ) );
			alert.setContentText( exception.getMessage( ) );

			// Make sure that the alert dialog resizes to the required height (usually just necessary for Linux systems)
			alert.getDialogPane( ).setMinHeight( Region.USE_PREF_SIZE );

			// Add the logo
			final String iconPath = RESOURCE_BUNDLE.getString( "errorIcon" );
			final InputStream iconStream = ExceptionUtil.class.getClassLoader( ).getResourceAsStream( iconPath );
			final Image icon = new Image( iconStream );
			final Stage stage = (Stage) alert.getDialogPane( ).getScene( ).getWindow( );
			stage.getIcons( ).add( icon );

			alert.showAndWait( );
		};

		// Now decide whether we need to execute this in the JavaFX thread or whether we are already in this thread.
		if ( Platform.isFxApplicationThread( ) ) {
			runnable.run( );
		} else {
			Platform.runLater( runnable );
		}
	}

	private static void logException( final Throwable throwable, final String loggerName ) {
		final boolean isDelegateException = throwable instanceof DelegateException;
		final Throwable exception = isDelegateException ? throwable.getCause( ) : throwable;

		LogManager.getLogger( loggerName ).error( RESOURCE_BUNDLE.getString( "errorMessage" ), exception );
	}

}
