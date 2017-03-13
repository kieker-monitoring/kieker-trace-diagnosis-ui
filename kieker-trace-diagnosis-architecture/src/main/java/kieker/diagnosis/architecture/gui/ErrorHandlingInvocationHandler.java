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

package kieker.diagnosis.architecture.gui;

import kieker.diagnosis.architecture.exception.BusinessException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.InvocationHandler;

/**
 * @author Nils Christian Ehmke
 */
class ErrorHandlingInvocationHandler implements InvocationHandler {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( ErrorHandlingInvocationHandler.class.getCanonicalName( ), Locale.getDefault( ) );
	private final String ivTitle = ivResourceBundle.getString( "error" );
	private final String ivHeader = ivResourceBundle.getString( "errorHeader" );

	private final Object ivDelegate;

	public ErrorHandlingInvocationHandler( final Object aDelegate ) {
		ivDelegate = aDelegate;
	}

	@Override
	public Object invoke( final Object aProxy, final Method aMethod, final Object[] aArgs ) {
		try {
			return aMethod.invoke( ivDelegate, aArgs );
		} catch ( final ReflectiveOperationException ex ) {
			final Throwable cause = getCause( ex );

			logError( cause );
			showAlertDialog( cause );

			return null;
		}
	}

	private void logError( final Throwable aEx ) {
		final Logger logger = LoggerFactory.getLogger( ivDelegate.getClass( ) );

		if ( aEx instanceof BusinessException ) {
			logger.info( aEx.getMessage( ), aEx );
		} else {
			logger.error( aEx.getMessage( ), aEx );
		}
	}

	private void showAlertDialog( final Throwable aEx ) {
		final AlertType alertType;
		final String contentText;

		if ( aEx instanceof BusinessException ) {
			alertType = AlertType.WARNING;
			contentText = aEx.getMessage( );
		} else {
			alertType = AlertType.ERROR;
			contentText = aEx.toString( );
		}

		final Alert alert = new Alert( alertType );

		alert.setContentText( contentText );
		alert.setTitle( ivTitle );
		alert.setHeaderText( ivHeader );

		final Window window = alert.getDialogPane( ).getScene( ).getWindow( );
		if ( window instanceof Stage ) {
			final Stage stage = (Stage) window;
			stage.getIcons( ).add( new Image( "kieker-logo.png" ) );
		}

		alert.showAndWait( );
	}

	private Throwable getCause( final Exception aEx ) {
		final Throwable exception;

		if ( ( aEx instanceof InvocationTargetException ) && ( aEx.getCause( ) != null ) ) {
			exception = aEx.getCause( );
		} else {
			exception = aEx;
		}

		return exception;
	}

}
