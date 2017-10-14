package kieker.diagnosis.ui.manual;

import java.net.URL;
import java.util.Locale;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.ui.ControllerBase;

@Singleton
public class ManualDialogController extends ControllerBase<ManualDialogViewModel> {

	public void performRefresh( ) {
		final Locale locale = Locale.getDefault( );
		final String suffix = locale == Locale.GERMAN || locale == Locale.GERMANY ? "_de" : "";
		final URL documentation = getClass( ).getClassLoader( ).getResource( "kieker/diagnosis/ui/manual/html/manual" + suffix + ".html" );

		getViewModel( ).updatePresentation( documentation );
	}

}
