/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis;

import java.util.ResourceBundle;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kieker.diagnosis.ui.complex.main.MainController;
import kieker.diagnosis.ui.complex.main.MainView;
import kieker.diagnosis.ui.mixin.ImageMixin;

/**
 * This is the application's main class.
 *
 * @author Nils Christian Ehmke
 */
public final class KiekerTraceDiagnosis extends Application implements ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( KiekerTraceDiagnosis.class.getCanonicalName( ) );

	private Injector injector;
	
	public static void main( final String[] aArgs ) {
		Application.launch( aArgs );
	}

	@Override
	public void start( final Stage aPrimaryStage ) throws Exception {
		injector = startCdiContainer( );
		final MainView mainView = injector.getInstance( MainView.class );
		final MainController mainController = injector.getInstance( MainController.class );

		// Prepare the stage and show the window
		final Scene scene = new Scene( mainView );
		aPrimaryStage.setScene( scene );
		aPrimaryStage.setMaximized( true );
		aPrimaryStage.getIcons( ).add( createIcon( ) );
		aPrimaryStage.setTitle( RESOURCE_BUNDLE.getString( "title" ) );

		// Catch the default close event
		aPrimaryStage.setOnCloseRequest( e -> {
			e.consume( );
			mainController.performClose( );
		} );

		aPrimaryStage.show( );
	}

	private Injector startCdiContainer( ) {
		final KiekerTraceDiagnosisUIModule module = new KiekerTraceDiagnosisUIModule( );
		return Guice.createInjector( com.google.inject.Stage.PRODUCTION, module );
	}

	private Image createIcon( ) {
		final String iconPath = RESOURCE_BUNDLE.getString( "icon" );
		return loadImage( iconPath );
	}
	
	/**
	 * This method is only to be used from the UI tests to bypass the native dialogs in the test environments.
	 */
	public Injector getInjector() {
		return injector;
	}

}
