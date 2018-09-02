package kieker.diagnosis.architecture.monitoring;

public class NoOpMonitoringProbeFactory implements MonitoringProbeFactory {

	private static final MonitoringProbe NO_OP_MONITORING_PROBE = new NoOpMonitoringProbe( );

	@Override
	public MonitoringProbe createMonitoringProbe( final Class<?> aClass, final String aMethod ) {
		return NO_OP_MONITORING_PROBE;
	}

	private static class NoOpMonitoringProbe implements MonitoringProbe {

		@Override
		public void fail( final Throwable t ) {
		}

		@Override
		public void stop( ) {
		}

	}

}
