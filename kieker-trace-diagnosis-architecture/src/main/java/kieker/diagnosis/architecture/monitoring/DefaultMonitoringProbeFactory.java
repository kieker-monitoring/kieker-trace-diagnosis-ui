package kieker.diagnosis.architecture.monitoring;

public class DefaultMonitoringProbeFactory implements MonitoringProbeFactory {

	@Override
	public MonitoringProbe createMonitoringProbe( final Class<?> aClass, final String aMethod ) {
		return new DefaultMonitoringProbe( aClass, aMethod );
	}

}
