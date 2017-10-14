package kieker.diagnosis.architecture.monitoring;

import kieker.monitoring.core.controller.IMonitoringController;

/**
 * This is a holder for the current monitoring configuration and the monitoring controller. This is necessary, as the monitoring probe is not in the CDI context
 * and because the monitoring controller can be exchanged during runtime.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringControllerHolder {

	private static volatile IMonitoringController cvMonitoringController;
	private static volatile MonitoringConfiguration cvMonitoringConfiguration;

	public static IMonitoringController getMonitoringController( ) {
		return cvMonitoringController;
	}

	public static void setMonitoringController( final IMonitoringController aMonitoringController ) {
		cvMonitoringController = aMonitoringController;
	}

	public static MonitoringConfiguration getCurrentConfiguration( ) {
		return cvMonitoringConfiguration;
	}

	public static void setCurrentConfiguration( final MonitoringConfiguration aMonitoringConfiguration ) {
		cvMonitoringConfiguration = aMonitoringConfiguration;
	}

	public static IMonitoringController clearMonitoringController( ) {
		final IMonitoringController controller = cvMonitoringController;
		cvMonitoringController = null;
		return controller;
	}

}
