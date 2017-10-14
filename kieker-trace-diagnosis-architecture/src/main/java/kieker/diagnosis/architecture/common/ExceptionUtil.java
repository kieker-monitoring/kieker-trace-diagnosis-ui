package kieker.diagnosis.architecture.common;

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
import javafx.stage.Stage;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;

/**
 * A util class to handle exceptions
 *
 * @author Nils Christian Ehmke
 */
public class ExceptionUtil {

	private static final ResourceBundle cvResourceBundle = ResourceBundle.getBundle( ExceptionUtil.class.getName( ) );

	private ExceptionUtil( ) {
		// Avoid instantiation
	}

	/**
	 * This method handles an exception. The exception is logged and an error dialog is shown. If the exception is a {@link BusinessRuntimeException}, the error
	 * is not logged and the error dialog indicates that the error is a business error.
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
		// Find out whether this is a business exception or not
		final boolean isBusinessException = aThrowable instanceof BusinessRuntimeException;
		final Throwable exception = isBusinessException ? aThrowable.getCause( ) : aThrowable;
		final AlertType alertType = isBusinessException ? AlertType.WARNING : AlertType.ERROR;

		// Keep in mind that some controllers start a new thread. As this might also lead to exceptions, the whole dialog showing has to be performed in the
		// JavaFX application thread.
		final Runnable runnable = ( ) -> {
			// Prepare the dialog
			final Alert alert = new Alert( alertType );
			alert.setTitle( cvResourceBundle.getString( "errorTitle" ) );
			alert.setHeaderText( cvResourceBundle.getString( "errorMessage" ) );
			alert.setContentText( exception.getMessage( ) );

			if ( !isBusinessException ) {
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
			}

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

	private static void logException( final Throwable aThrowable, final String aLoggerName ) {
		if ( !( aThrowable instanceof BusinessRuntimeException ) ) {
			LogManager.getLogger( aLoggerName ).error( cvResourceBundle.getString( "errorMessage" ), aThrowable );
		}
	}

}
