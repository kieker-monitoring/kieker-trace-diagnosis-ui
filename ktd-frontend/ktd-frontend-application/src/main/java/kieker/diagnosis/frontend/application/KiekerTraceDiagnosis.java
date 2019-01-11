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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;
import kieker.diagnosis.frontend.main.complex.MainPane;

/**
 * This is the application's main class.
 *
 * @author Nils Christian Ehmke
 */
public final class KiekerTraceDiagnosis extends Application implements ImageMixin {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( KiekerTraceDiagnosis.class.getCanonicalName( ) );

	public static void main( final String[] aArgs ) {
		Application.launch( aArgs );
	}

	@Override
	public void start( final Stage aPrimaryStage ) throws Exception {
		startCdiContainer( );

		// Prepare the stage and show the window
		final MainPane mainPane = new MainPane( );
		final Scene scene = new Scene( mainPane );
		aPrimaryStage.setScene( scene );
		aPrimaryStage.setMaximized( true );
		aPrimaryStage.getIcons( ).add( createIcon( ) );
		aPrimaryStage.setTitle( RESOURCE_BUNDLE.getString( "title" ) );

		// Catch the default close event
		aPrimaryStage.setOnCloseRequest( e -> {
			e.consume( );
			mainPane.performClose( );
		} );

		aPrimaryStage.show( );
	}

	private void startCdiContainer( ) {
		final KiekerTraceDiagnosisModule module = new KiekerTraceDiagnosisModule( );
		Guice.createInjector( com.google.inject.Stage.PRODUCTION, module );
	}

	private Image createIcon( ) {
		final String iconPath = RESOURCE_BUNDLE.getString( "icon" );
		return loadImage( iconPath );
	}

}
