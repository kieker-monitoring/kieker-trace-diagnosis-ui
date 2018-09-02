package kieker.diagnosis.architecture.monitoring;

public interface MonitoringProbe {

	void fail( Throwable t );

	void stop( );

}
