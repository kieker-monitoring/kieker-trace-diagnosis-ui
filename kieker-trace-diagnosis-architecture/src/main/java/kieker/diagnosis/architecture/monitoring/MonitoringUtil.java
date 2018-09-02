package kieker.diagnosis.architecture.monitoring;

public class MonitoringUtil {

	private static volatile MonitoringProbeFactory cvMonitoringProbeFactory = new NoOpMonitoringProbeFactory( );

	public static MonitoringProbe createMonitoringProbe( final Class<?> aClass, final String aMethod ) {
		return cvMonitoringProbeFactory.createMonitoringProbe( aClass, aMethod );
	}

	public static void setMonitoringProbeFactory( final MonitoringProbeFactory aMonitoringProbeFactory ) {
		cvMonitoringProbeFactory = aMonitoringProbeFactory;
	}

}
