package kieker.diagnosis.util;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

@Aspect
public class ErrorHandlingAspect {

	private final ResourceBundle resourceBundle = ResourceBundle.getBundle("locale.kieker.diagnosis.util.errorhandling", Locale.getDefault());

	@Pointcut("execution(@kieker.diagnosis.util.ErrorHandling * *(..))")
	public void errorHandlingRequested() {
		// Aspect Declaration (MUST be empty)
	}

	@Around("errorHandlingRequested() && this(thisObject)")
	public Object methodHandling(final Object thisObject, final ProceedingJoinPoint thisJoinPoint) throws Throwable {
		try {
			return thisJoinPoint.proceed();
		} catch (final Exception ex) {
			final Logger logger = LogManager.getLogger(thisObject.getClass());
			logger.error(ex.getMessage(), ex);

			final Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText(ex.getLocalizedMessage());
			alert.setTitle(resourceBundle.getString("error"));
			alert.setHeaderText(resourceBundle.getString("errorHeader"));
			final Window window = alert.getDialogPane().getScene().getWindow();
			if (window instanceof Stage) {
				final Stage stage = (Stage) window;
				stage.getIcons().add(new Image("kieker-logo.png"));
			}
			alert.showAndWait();

			return null;
		}
	}

}
