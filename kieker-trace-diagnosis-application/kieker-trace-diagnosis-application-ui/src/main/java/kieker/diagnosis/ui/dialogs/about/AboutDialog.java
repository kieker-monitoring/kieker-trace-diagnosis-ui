package kieker.diagnosis.ui.dialogs.about;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The about dialog shows some information (like the license) about the tool.
 *
 * @author Nils Christian Ehmke
 */
public final class AboutDialog extends Alert {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( AboutDialog.class.getCanonicalName( ) );

	public AboutDialog( ) {
		super( AlertType.NONE );

		configureDialog( );
		addComponents( );
		addButtons( );
	}

	private void configureDialog( ) {
		setTitle( RESOURCE_BUNDLE.getString( "title" ) );
		getStage( ).getIcons( ).add( createIcon( ) );
		final URL cssURL = getClass( ).getResource( getClass( ).getSimpleName( ) + ".css" );
		final String cssExternalForm = cssURL.toExternalForm( );
		getDialogPane( ).getStylesheets( ).add( cssExternalForm );
	}

	private Stage getStage( ) {
		final Scene scene = getDialogPane( ).getScene( );
		return ( Stage ) scene.getWindow( );
	}

	private Image createIcon( ) {
		final String iconPath = RESOURCE_BUNDLE.getString( "icon" );
		final URL iconURL = getClass( ).getResource( iconPath );
		final String iconExternalForm = iconURL.toExternalForm( );
		return new Image( iconExternalForm );
	}

	private void addComponents( ) {
		final Label label = new Label( );
		label.setText( RESOURCE_BUNDLE.getString( "description" ) );
		getDialogPane( ).setContent( label );
	}

	private void addButtons( ) {
		getButtonTypes( ).add( ButtonType.OK );
	}

}
