package kieker.diagnosis.architecture.ui;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ResourceBundle;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
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
import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;

/**
 * This is an interceptor which handles all kind of {@link Throwable Throwables}. It is usually used around controller actions. If an exception occurs, the
 * interceptor logs the exception, shows an error dialog, and returns {@code null}. If the exception is a {@link BusinessRuntimeException}, the error is not
 * logged and the error dialog indicates that the error is a business error.
 *
 * @author Nils Christian Ehmke
 */
public final class ErrorHandlingInterceptor implements MethodInterceptor {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( getClass( ).getName( ) );

	@Override
	public Object invoke( final MethodInvocation aMethodInvocation ) throws Throwable {
		try {
			return aMethodInvocation.proceed( );
		} catch ( final Throwable ex ) {
			handleException( ex, aMethodInvocation.getThis( ) );
			return null;
		}
	}

	private void handleException( final Throwable aThrowable, final Object aObject ) {
		showExceptionDialog( aThrowable );
		logException( aThrowable, aObject );
	}

	private void showExceptionDialog( final Throwable aThrowable ) {
		// Find out whether this is a business exception or not
		final boolean isBusinessException = aThrowable instanceof BusinessRuntimeException;
		final Throwable exception = isBusinessException ? aThrowable.getCause( ) : aThrowable;
		final AlertType alertType = isBusinessException ? AlertType.WARNING : AlertType.ERROR;

		// Keep in mind that some controllers start a new thread. As this might also lead to exceptions, the whole dialog showing has to be performed in the
		// JavaFX application thread.
		final Runnable runnable = ( ) -> {
			// Prepare the dialog
			final Alert alert = new Alert( alertType );
			alert.setTitle( ivResourceBundle.getString( "errorTitle" ) );
			alert.setHeaderText( ivResourceBundle.getString( "errorMessage" ) );
			alert.setContentText( exception.getMessage( ) );

			if ( !isBusinessException ) {
				// Convert the throwable into a string
				final StringWriter sw = new StringWriter( );
				final PrintWriter pw = new PrintWriter( sw );
				exception.printStackTrace( pw );
				final String exceptionText = sw.toString( );

				// Prepare the remaining components
				final Label label = new Label( ivResourceBundle.getString( "errorDescription" ) );

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
			final String iconPath = ivResourceBundle.getString( "errorIcon" );
			final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
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

	private void logException( final Throwable aThrowable, final Object aObject ) {
		if ( !( aThrowable instanceof BusinessRuntimeException ) ) {
			final Class<?> controllerClass = ClassUtil.getRealClass( aObject.getClass( ) );
			LogManager.getLogger( controllerClass ).error( ivResourceBundle.getString( "errorMessage" ), aThrowable );
		}
	}

}
