package kieker.diagnosis.ui.about;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.ui.ControllerBase;

@Singleton
public class AboutDialogController extends ControllerBase<AboutDialogViewModel> {

	public void performClose( ) {
		getViewModel( ).close( );
	}

}
