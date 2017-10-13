package kieker.diagnosis.ui.manual;

import java.io.InputStream;

import com.google.inject.Singleton;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jfxtras.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.ViewBase;

@Singleton
public class ManualDialogView extends ViewBase<ManualDialogController> {

	private final WebView ivWebView;

	public ManualDialogView( ) {
		ivWebView = new WebView( );
		VBox.setVgrow( ivWebView, Priority.ALWAYS );

		getChildren( ).add( ivWebView );
	}

	@Override
	public void setParameter( final Object aParameter ) {
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
		stage.setResizable( true );
		stage.initModality( Modality.NONE );
		stage.initStyle( StageStyle.DECORATED );
		stage.initOwner( aParent );
		stage.getIcons( ).add( icon );
		stage.setTitle( getLocalizedString( "title" ) );
		stage.setScene( scene );

		getController( ).performRefresh( );

		stage.showAndWait( );
	}

	WebView getWebView( ) {
		return ivWebView;
	}

}
