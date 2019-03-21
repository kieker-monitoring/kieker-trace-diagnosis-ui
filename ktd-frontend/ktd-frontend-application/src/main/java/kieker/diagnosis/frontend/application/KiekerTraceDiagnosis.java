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

package kieker.diagnosis.frontend.application;

import java.util.ResourceBundle;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kieker.diagnosis.frontend.base.common.ExceptionUtil;
import kieker.diagnosis.frontend.base.common.HostServicesHolder;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;
import kieker.diagnosis.frontend.main.complex.MainPane;

/**
 * This is the application's main class.
 *
 * @author Nils Christian Ehmke
 */
public final class KiekerTraceDiagnosis extends Application implements ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( KiekerTraceDiagnosis.class.getCanonicalName( ) );

	public static void main( final String[] args ) {
		Application.launch( args );
	}

	@Override
	public void start( final Stage primaryStage ) throws Exception {
		initializeExceptionHandling( );
		final Injector injector = startCdiContainer( );
		initializeHostServicesHolder( );
		prepareAndShowStage( primaryStage, injector );
	}

	private void initializeExceptionHandling( ) {
		Thread.setDefaultUncaughtExceptionHandler( ( thread, throwable ) -> ExceptionUtil.handleException( throwable, KiekerTraceDiagnosis.class.getCanonicalName( ) ) );
	}

	private Injector startCdiContainer( ) {
		final KiekerTraceDiagnosisModule module = new KiekerTraceDiagnosisModule( );
		return Guice.createInjector( com.google.inject.Stage.PRODUCTION, module );
	}

	private void initializeHostServicesHolder( ) {
		HostServicesHolder.setHostServices( getHostServices( ) );
	}

	private void prepareAndShowStage( final Stage primaryStage, final Injector injector ) {
		final MainPane mainPane = injector.getInstance( MainPane.class );
		catchDefaultCloseEventWithMainPane( primaryStage, mainPane );

		primaryStage.setTitle( RESOURCE_BUNDLE.getString( "title" ) );
		primaryStage.setScene( new Scene( mainPane ) );
		primaryStage.getIcons( ).add( loadImage( "/kieker-logo.png" ) );
		primaryStage.setMaximized( true );

		primaryStage.show( );
	}

	private void catchDefaultCloseEventWithMainPane( final Stage primaryStage, final MainPane mainPane ) {
		primaryStage.setOnCloseRequest( e -> {
			e.consume( );
			mainPane.performClose( );
		} );
	}

}
