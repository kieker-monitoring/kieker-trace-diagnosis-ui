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

package kieker.diagnosis.util;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * This is an aspect adding error handling where it is requested. The advice is added around methods marked with {@link ErrorHandling} during the compiling. All exceptions (instances of
 * {@link Exception} and its subclasses) are logged via the logger instance of the called object. A JavaFX alert dialog is shown on the screen and informs the user about the error. The
 * annotated method returns {@code null} afterwards.
 *
 * @see ErrorHandling
 *
 * @author Nils Christian Ehmke
 */
@Aspect
public final class ErrorHandlingAspect {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle("kieker.diagnosis.view.util.errorhandling", Locale.getDefault());
	private final String ivTitle = this.ivResourceBundle.getString("error");
	private final String ivHeader = this.ivResourceBundle.getString("errorHeader");

	@Pointcut("execution(@kieker.diagnosis.util.ErrorHandling * *(..))")
	public void errorHandlingRequested() {
	}

	@Around("errorHandlingRequested() && this(aThisObject)")
	public Object methodHandling(final Object aThisObject, final ProceedingJoinPoint aThisJoinPoint) throws Throwable {
		try {
			return aThisJoinPoint.proceed();
		} catch (final Exception ex) {
			this.logError(aThisObject, ex);
			this.showAlertDialog(ex);

			return null;
		}
	}

	private void logError(final Object aThisObject, final Exception aEx) {
		final Logger logger = LogManager.getLogger(aThisObject.getClass());
		logger.error(aEx.getMessage(), aEx);
	}

	private void showAlertDialog(final Exception aEx) {
		final Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText(aEx.getLocalizedMessage());
		alert.setTitle(this.ivTitle);
		alert.setHeaderText(this.ivHeader);
		final Window window = alert.getDialogPane().getScene().getWindow();
		if (window instanceof Stage) {
			final Stage stage = (Stage) window;
			stage.getIcons().add(new Image("kieker-logo.png"));
		}
		alert.showAndWait();
	}

}
