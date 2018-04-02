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

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Singleton;

import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.JavaView;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import jfxtras.scene.layout.VBox;
import kieker.diagnosis.architecture.ui.DialogViewBase;

/**
 * The view of the user manual dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class ManualDialogView extends DialogViewBase<ManualDialogController, Void> implements JavaView<ManualDialogViewModel>, Initializable {

	@InjectViewModel
	private ManualDialogViewModel ivViewModel;

	private final WebView ivWebView;

	public ManualDialogView( ) {
		super( Modality.NONE, StageStyle.DECORATED, false );

		ivWebView = new WebView( );
		VBox.setVgrow( ivWebView, Priority.ALWAYS );

		getChildren( ).add( ivWebView );
	}

	@Override
	public void setParameter( final Object aParameter ) {
	}

	WebView getWebView( ) {
		return ivWebView;
	}

	@Override
	public void initialize( final URL aURL, final ResourceBundle aResourceBundle ) {
		ivViewModel.getDocumentationProperty( ).addListener( (ChangeListener<String>) ( aObservableValue, aOldValue, aNewValue ) -> {
			loadContentInWebEngine( aNewValue );
		} );
		// As the view model is currently initialized before the view, and we are only registered for changes, we have to update at this point manually.
		loadContentInWebEngine( ivViewModel.getDocumentationProperty( ).get( ) );
	}

	private void loadContentInWebEngine( final String aNewValue ) {
		final WebEngine webEngine = ivWebView.getEngine( );
		webEngine.loadContent( aNewValue );
	}

}
