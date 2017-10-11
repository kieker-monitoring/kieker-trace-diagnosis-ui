package kieker.diagnosis.architecture.monitoring;

import kieker.monitoring.core.controller.IMonitoringController;

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
