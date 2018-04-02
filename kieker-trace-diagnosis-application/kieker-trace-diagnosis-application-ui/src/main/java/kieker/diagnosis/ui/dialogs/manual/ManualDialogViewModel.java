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

package kieker.diagnosis.ui.dialogs.manual;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import kieker.diagnosis.architecture.exception.TechnicalException;
import kieker.diagnosis.architecture.ui.ViewModelBase;

/**
 * The view model of the user manual dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class ManualDialogViewModel extends ViewModelBase<ManualDialogView> implements ViewModel {

	private final StringProperty ivDocumentationProperty = new SimpleStringProperty( );

	public StringProperty getDocumentationProperty( ) {
		return ivDocumentationProperty;
	}

	public void initialize( ) {
		final StringBuilder documentation = new StringBuilder( );
		documentation.append( "<html><body><div class=\"container\">" );

		documentation.append( "<head><style>" );
		appendBootstrapCSS( documentation );
		documentation.append( "</head></style>" );
		appendDocumentationContent( documentation );
		documentation.append( "</div></body></html>" );

		ivDocumentationProperty.set( documentation.toString( ) );
	}

	private void appendBootstrapCSS( final StringBuilder documentation ) {
		try ( final InputStream cssStream = getClass( ).getClassLoader( ).getResourceAsStream( "kieker/diagnosis/ui/dialogs/manual/html/css/bootstrap.min.css" ) ) {
			final byte[] allBytes = cssStream.readAllBytes( );
			documentation.append( new String( allBytes, Charset.forName( "UTF-8" ) ) );
		} catch ( final IOException ex ) {
			throw new TechnicalException( ex );
		}
	}

	private void appendDocumentationContent( final StringBuilder documentation ) {
		final Locale locale = Locale.getDefault( );
		final String suffix = locale == Locale.GERMAN || locale == Locale.GERMANY ? "_de" : "";

		try ( final InputStream documentationStream = getClass( ).getClassLoader( ).getResourceAsStream( "kieker/diagnosis/ui/dialogs/manual/html/manual" + suffix + ".html" ) ) {
			final byte[] allBytes = documentationStream.readAllBytes( );
			documentation.append( new String( allBytes, Charset.forName( "ISO-8859-1" ) ) );
		} catch ( final IOException ex ) {
			throw new TechnicalException( ex );
		}
	}

}
