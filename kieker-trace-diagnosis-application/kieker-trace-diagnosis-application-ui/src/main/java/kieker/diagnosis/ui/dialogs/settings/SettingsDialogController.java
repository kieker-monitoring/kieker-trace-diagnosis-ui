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

package kieker.diagnosis.ui.dialogs.settings;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.ui.ControllerBase;
import kieker.diagnosis.service.settings.Settings;
import kieker.diagnosis.service.settings.SettingsService;

/**
 * The controller of the settings dialog.
 *
 * @author Nils Christian Ehmke
 */
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
