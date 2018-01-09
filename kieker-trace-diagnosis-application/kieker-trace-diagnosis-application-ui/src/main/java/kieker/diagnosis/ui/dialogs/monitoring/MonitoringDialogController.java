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

package kieker.diagnosis.ui.dialogs.monitoring;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.monitoring.MonitoringConfiguration;
import kieker.diagnosis.architecture.monitoring.Status;
import kieker.diagnosis.architecture.service.monitoring.MonitoringService;
import kieker.diagnosis.architecture.ui.ControllerBase;

/**
 * The controller of the monitoring dialog.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
class MonitoringDialogController extends ControllerBase<MonitoringDialogViewModel> {

	public void performRefresh( ) {
		// Get the current configuration and status...
		final MonitoringService monitoringService = getService( MonitoringService.class );
		final MonitoringConfiguration configuration = monitoringService.getCurrentConfiguration( );
		final Status status = monitoringService.getCurrentStatus( );

		// ...and display them.
		getViewModel( ).updatePresentationConfiguration( configuration );
		getViewModel( ).updatePresentationStatus( status );
	}

	/**
	 * This action is performed, when the user wants to save and close the dialog.
	 *
	 * @return
	 */
	public void performSaveAndClose( ) {
		try {
			// Get the configuration...
			final MonitoringConfiguration configuration = getViewModel( ).savePresentationConfiguration( );

			// ...and apply it
			final MonitoringService monitoringService = getService( MonitoringService.class );
			monitoringService.configureMonitoring( configuration );

			getViewModel( ).close( );
		} catch ( final BusinessException ex ) {
			throw new BusinessRuntimeException( ex );
		}
	}

	public void performClose( ) {
		getViewModel( ).close( );
	}

}
