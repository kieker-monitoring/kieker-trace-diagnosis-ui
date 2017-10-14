package kieker.diagnosis.ui.manual;

import java.net.URL;

import com.google.inject.Singleton;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import kieker.diagnosis.architecture.ui.ViewModelBase;

@Singleton
public class ManualDialogViewModel extends ViewModelBase<ManualDialogView> {

	public void updatePresentation( final URL aUrl ) {
		final WebView webView = getView( ).getWebView( );
		final WebEngine engine = webView.getEngine( );
		engine.load( aUrl.toExternalForm( ) );

	}

}
