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

package kieker.diagnosis.frontend.base.common;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * A util class to handle exceptions.
 *
 * @author Nils Christian Ehmke
 */
public final class ExceptionUtil {

	private static final ResourceBundle cvResourceBundle = ResourceBundle.getBundle( ExceptionUtil.class.getName( ) );

	private ExceptionUtil( ) {
		// Avoid instantiation
	}

	/**
	 * This method handles an exception. The exception is logged and an error dialog is shown. If the exception is a
	 * {@link BusinessRuntimeException}, the error is not logged and the error dialog indicates that the error is a
	 * business error.
	 *
	 * @param aThrowable
	 *            The exception to handle.
	 * @param aLoggerName
	 *            The name of the logger, the exception will be logged in.
	 */
	public static void handleException( final Throwable aThrowable, final String aLoggerName ) {
		showExceptionDialog( aThrowable );
		logException( aThrowable, aLoggerName );
	}

	private static void showExceptionDialog( final Throwable aThrowable ) {
		final boolean isDelegateException = aThrowable instanceof DelegateException;
		final Throwable exception = isDelegateException ? aThrowable.getCause( ) : aThrowable;

		// Keep in mind that some controllers start a new thread. As this might also lead to exceptions, the whole
		// dialog showing has to be performed in the
		// JavaFX application thread.
		final Runnable runnable = ( ) -> {
			// Prepare the dialog
			final Alert alert = new Alert( AlertType.ERROR );
			alert.setTitle( cvResourceBundle.getString( "errorTitle" ) );
			alert.setHeaderText( cvResourceBundle.getString( "errorMessage" ) );
			alert.setContentText( exception.getMessage( ) );

			// Make sure that the alert dialog resizes to the required height (usually just necessary for Linux systems)
			alert.getDialogPane( ).setMinHeight( Region.USE_PREF_SIZE );

			// Convert the throwable into a string
			final StringWriter sw = new StringWriter( );
			final PrintWriter pw = new PrintWriter( sw );
			exception.printStackTrace( pw );
			final String exceptionText = sw.toString( );

			// Prepare the remaining components
			final Label label = new Label( cvResourceBundle.getString( "errorDescription" ) );

			final TextArea textArea = new TextArea( exceptionText );
			textArea.setEditable( false );
			textArea.setWrapText( true );

			textArea.setMaxWidth( Double.MAX_VALUE );
			textArea.setMaxHeight( Double.MAX_VALUE );
			GridPane.setVgrow( textArea, Priority.ALWAYS );
			GridPane.setHgrow( textArea, Priority.ALWAYS );

			final GridPane expContent = new GridPane( );
			expContent.setMaxWidth( Double.MAX_VALUE );
			expContent.add( label, 0, 0 );
			expContent.add( textArea, 0, 1 );

			alert.getDialogPane( ).setExpandableContent( expContent );

			// Add the logo
			final String iconPath = cvResourceBundle.getString( "errorIcon" );
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

		LogManager.getLogger( loggerName ).error( cvResourceBundle.getString( "errorMessage" ), exception );
	}

}
