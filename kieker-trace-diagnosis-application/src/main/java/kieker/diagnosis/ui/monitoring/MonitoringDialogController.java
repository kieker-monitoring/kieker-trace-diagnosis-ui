package kieker.diagnosis.ui.monitoring;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.monitoring.MonitoringConfiguration;
import kieker.diagnosis.architecture.monitoring.Status;
import kieker.diagnosis.architecture.service.monitoring.MonitoringService;
import kieker.diagnosis.architecture.ui.ControllerBase;

@Singleton
public class MonitoringDialogController extends ControllerBase<MonitoringDialogViewModel> {

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
		// Get the configuration...
		final MonitoringConfiguration configuration = getViewModel( ).savePresentationConfiguration( );

		// ...and apply it
		final MonitoringService monitoringService = getService( MonitoringService.class );
		monitoringService.configureMonitoring( configuration );

		getViewModel( ).close( );
	}

	public void performClose( ) {
		getViewModel( ).close( );
	}

}