package kieker.diagnosis.ui.settings;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.service.settings.Settings;
import kieker.diagnosis.service.settings.SettingsService;

@Singleton
class SettingsDialogController extends ControllerBase<SettingsDialogViewModel> {

	public void performRefresh( ) {
		// Get the current settings...
		final SettingsService settingsService = getService( SettingsService.class );
		final Settings settings = settingsService.loadSettings( );

		// ...and display them.
		getViewModel( ).updatePresentation( settings );
	}

	/**
	 * This action is performed, when the user wants to save and close the dialog.
	 *
	 * @return
	 */
	public void performSaveAndClose( ) {
		try {
			// Get the settings...
			final Settings settings = getViewModel( ).savePresentation( );

			// ...and save them
			final SettingsService settingsService = getService( SettingsService.class );
			settingsService.saveSettings( settings );

			getViewModel( ).close( );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performClose( ) {
		getViewModel( ).close( );
	}

}
