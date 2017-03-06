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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Window;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Nils Christian Ehmke
 */
class ErrorHandlingInvocationHandler implements InvocationHandler {

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
		} catch ( final ReflectiveOperationException ex ) {
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
		final Throwable exception;

		if ( ( aEx instanceof InvocationTargetException ) && ( aEx.getCause( ) != null ) ) {
			exception = aEx.getCause( );
		} else {
			exception = aEx;
		}

		final Alert alert = new Alert( AlertType.ERROR );
		alert.setContentText( exception.toString( ) );
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
