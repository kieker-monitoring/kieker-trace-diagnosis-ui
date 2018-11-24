package kieker.diagnosis.ui.dialogs.manual;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import kieker.diagnosis.architecture.exception.TechnicalException;

public final class ManualDialog extends Alert {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( ManualDialog.class.getCanonicalName( ) );

	public ManualDialog( ) {
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
		final WebView webView = new WebView( );
		final WebEngine webEngine = webView.getEngine( );
		webEngine.loadContent( compileDocumentation( ) );
		VBox.setVgrow( webView, Priority.ALWAYS );
		getDialogPane( ).setContent( webView );
	}

	private void addButtons( ) {
		getButtonTypes( ).add( ButtonType.OK );
	}

	public String compileDocumentation( ) {
		final StringBuilder documentation = new StringBuilder( );
		documentation.append( "<html><body><div class=\"container\">" );

		documentation.append( "<head><style>" );
		appendBootstrapCSS( documentation );
		documentation.append( "</head></style>" );
		appendDocumentationContent( documentation );
		documentation.append( "</div></body></html>" );

		return documentation.toString( );
	}

	private void appendBootstrapCSS( final StringBuilder documentation ) {
		try ( final InputStream cssStream = getClass( ).getResourceAsStream( "html/css/bootstrap.min.css" ) ) {
			final byte[] allBytes = cssStream.readAllBytes( );
			documentation.append( new String( allBytes, Charset.forName( "UTF-8" ) ) );
		} catch ( final IOException ex ) {
			throw new TechnicalException( ex );
		}
	}

	private void appendDocumentationContent( final StringBuilder documentation ) {
		final Locale locale = Locale.getDefault( );
		final String suffix = locale == Locale.GERMAN || locale == Locale.GERMANY ? "_de" : "";

		try ( final InputStream documentationStream = getClass( ).getResourceAsStream( "html/manual" + suffix + ".html" ) ) {
			final byte[] allBytes = documentationStream.readAllBytes( );
			documentation.append( new String( allBytes, Charset.forName( "ISO-8859-1" ) ) );
		} catch ( final IOException ex ) {
			throw new TechnicalException( ex );
		}
	}

}
