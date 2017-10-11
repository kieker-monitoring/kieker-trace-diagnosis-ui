package kieker.diagnosis.ui.about;

import java.io.InputStream;

import com.google.inject.Singleton;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import jfxtras.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.ViewBase;

@Singleton
public class AboutDialogView extends ViewBase<AboutDialogController> {

	public AboutDialogView( ) {
		setSpacing( 10 );

		{
			final Label label = new Label( );
			label.setText( getLocalizedString( "description" ) );
			VBox.setMargin( label, new Insets( 10, 10, 0, 10 ) );

			getChildren( ).add( label );
		}

		{
			final Separator separator = new Separator( );

			getChildren( ).add( separator );
		}

		{
			final ButtonBar buttonBar = new ButtonBar( );
			VBox.setMargin( buttonBar, new Insets( 0, 10, 0, 0 ) );

			{
				final Button button = new Button( );
				button.setText( getLocalizedString( "ok" ) );
				button.setDefaultButton( true );
				button.setCancelButton( true );
				button.setOnAction( e -> getController( ).performClose( ) );

				buttonBar.getButtons( ).add( button );
			}

			getChildren( ).add( buttonBar );
		}
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
		stage.setResizable( false );
		stage.initModality( Modality.WINDOW_MODAL );
		stage.initStyle( StageStyle.DECORATED );
		stage.initOwner( aParent );
		stage.getIcons( ).add( icon );
		stage.setTitle( getLocalizedString( "title" ) );
		stage.setScene( scene );

		stage.showAndWait( );
	}

}
