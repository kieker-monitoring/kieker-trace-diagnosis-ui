/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
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

import java.io.InputStream;
import java.util.ResourceBundle;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import kieker.diagnosis.ui.main.MainController;
import kieker.diagnosis.ui.main.MainView;

/**
 * This is the application's main class.
 *
 * @author Nils Christian Ehmke
 */
public class KiekerTraceDiagnosis extends Application {

	public static void main( final String[] aArgs ) {
		Application.launch( aArgs );
	}

	@Override
	public void start( final Stage aPrimaryStage ) throws Exception {
		// Load the CDI container
		final KiekerTraceDiagnosisUIModule module = new KiekerTraceDiagnosisUIModule( );
		final Injector injector = Guice.createInjector( com.google.inject.Stage.PRODUCTION, module );
		final MainView mainView = injector.getInstance( MainView.class );
		final MainController mainController = injector.getInstance( MainController.class );

		// Load the icon for the window
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( getClass( ).getCanonicalName( ) );
		final String iconPath = resourceBundle.getString( "icon" );
		final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
		final Image icon = new Image( iconStream );

		// Prepare the stage and show the window
		final Scene scene = new Scene( mainView );
		aPrimaryStage.getIcons( ).add( icon );
		aPrimaryStage.setTitle( resourceBundle.getString( "title" ) );
		aPrimaryStage.setScene( scene );
		aPrimaryStage.setMaximized( true );

		// Catch the default close event
		aPrimaryStage.setOnCloseRequest( e -> {
			e.consume( );
			mainController.performClose( );
		} );

		aPrimaryStage.show( );
	}

}
