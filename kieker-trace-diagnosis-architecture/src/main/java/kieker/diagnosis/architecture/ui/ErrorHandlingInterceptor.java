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
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;

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

	private void handleException( final Throwable aThrowable, final Object aController ) {
		showExceptionDialog( aThrowable );
		logException( aThrowable, aController );
	}

	private void showExceptionDialog( final Throwable aThrowable ) {
		// Prepare the dialog
		final boolean isBusinessException = aThrowable instanceof BusinessRuntimeException;
		final Throwable exception = isBusinessException ? aThrowable.getCause( ) : aThrowable;
		final AlertType alertType = isBusinessException ? AlertType.WARNING : AlertType.ERROR;

		final Runnable runnable = ( ) -> {
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

		if ( Platform.isFxApplicationThread( ) ) {
			runnable.run( );
		} else {
			Platform.runLater( runnable );
		}
	}

	private void logException( final Throwable aThrowable, final Object aController ) {
		if ( !( aThrowable instanceof BusinessRuntimeException ) ) {
			// We have to use getSuperclass as all controllers are enhanced by Guice with an interceptor.
			final Class<?> controllerClass = aController.getClass( ).getSuperclass( );
			LogManager.getLogger( controllerClass ).error( ivResourceBundle.getString( "errorMessage" ), aThrowable );
		}
	}

}
