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

package kieker.diagnosis.frontend.dialog.manual;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import kieker.diagnosis.frontend.base.mixin.DialogMixin;
import kieker.diagnosis.frontend.base.mixin.ImageMixin;

public final class ManualDialog extends Alert implements DialogMixin, ImageMixin {

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
		addDefaultStylesheet( );
	}

	private Image createIcon( ) {
		final String iconPath = RESOURCE_BUNDLE.getString( "icon" );
		return loadImage( iconPath );
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
		getDialogPane( ).lookupButton( ButtonType.OK ).setId( "manualDialogOk" );
	}

	public String compileDocumentation( ) {
		final StringBuilder documentation = new StringBuilder( );
		documentation.append( "<html><body><div class=\"container\" style=\"font-family: Open Sans; font-size: 15;\">" );

		documentation.append( "<head><style>" );
		appendBootstrapCSS( documentation );
		documentation.append( "</head></style>" );
		appendDocumentationContent( documentation );
		documentation.append( "</div></body></html>" );

		return documentation.toString( );
	}

	private void appendBootstrapCSS( final StringBuilder documentation ) {
		try ( InputStream cssStream = getClass( ).getResourceAsStream( "html/css/bootstrap.min.css" ) ) {
			final byte[] allBytes = cssStream.readAllBytes( );
			documentation.append( new String( allBytes, Charset.forName( "UTF-8" ) ) );
		} catch ( final IOException ex ) {
			throw new RuntimeException( ex );
		}
	}

	private void appendDocumentationContent( final StringBuilder documentation ) {
		final Locale locale = Locale.getDefault( );
		final String suffix = locale == Locale.GERMAN || locale == Locale.GERMANY ? "_de" : "";

		try ( InputStream documentationStream = getClass( ).getResourceAsStream( "html/manual" + suffix + ".html" ) ) {
			final byte[] allBytes = documentationStream.readAllBytes( );
			documentation.append( new String( allBytes, Charset.forName( "ISO-8859-1" ) ) );
		} catch ( final IOException ex ) {
			throw new RuntimeException( ex );
		}
	}

}
