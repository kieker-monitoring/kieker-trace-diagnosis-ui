package kieker.diagnosis.architecture.monitoring;

public interface MonitoringProbeFactory {

	MonitoringProbe createMonitoringProbe( Class<?> aClass, String aMethod );

}
