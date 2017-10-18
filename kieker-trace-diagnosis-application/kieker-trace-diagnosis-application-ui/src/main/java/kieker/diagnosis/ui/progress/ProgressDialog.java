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

package kieker.diagnosis.ui.progress;

import java.io.InputStream;
import java.util.ResourceBundle;

import com.google.inject.Singleton;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

@Singleton
public class ProgressDialog extends VBox {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( getClass( ).getName( ) );

	private final ProgressIndicator ivProgressIndicator;

	private Label ivLabel;

	public ProgressDialog( ) {
		setAlignment( Pos.CENTER );
		setPadding( new Insets( 5 ) );
		setSpacing( 10 );

		setPrefHeight( 100 );
		setPrefWidth( 250 );

		{
			ivProgressIndicator = new ProgressIndicator( );

			getChildren( ).add( ivProgressIndicator );
		}

		{
			ivLabel = new Label( );
			VBox.setVgrow( ivLabel, Priority.ALWAYS );

			getChildren( ).add( ivLabel );
		}
	}

	public void open( final Window aParent ) {
		// Create a scene if necessary
		Scene scene = getScene( );
		if ( scene == null ) {
			scene = new Scene( this );
		}

		// Load the icon
		final String iconPath = getLocalizedString( "icon" );
		final InputStream iconStream = getClass( ).getClassLoader( ).getResourceAsStream( iconPath );
		final Image icon = new Image( iconStream );

		// Prepare and show the stage
		final Stage stage = new Stage( );
		stage.setResizable( false );
		stage.initModality( Modality.WINDOW_MODAL );
		stage.initStyle( StageStyle.DECORATED );
		stage.initOwner( aParent );
		stage.getIcons( ).add( icon );
		stage.setTitle( getLocalizedString( "title" ) );
		stage.setScene( scene );
		stage.setOnCloseRequest( aEvent -> aEvent.consume( ) );

		stage.showAndWait( );
	}

	private String getLocalizedString( final String aKey ) {
		return ivResourceBundle.getString( aKey );
	}

	public void close( ) {
		final Window window = getScene( ).getWindow( );
		window.hide( );
	}

	public void setMessage( final String aMessage ) {
		ivLabel.setText( aMessage );
		layout( );
	}

	public void setProgress( final double aProgress ) {
		ivProgressIndicator.setProgress( aProgress );
	}

}
