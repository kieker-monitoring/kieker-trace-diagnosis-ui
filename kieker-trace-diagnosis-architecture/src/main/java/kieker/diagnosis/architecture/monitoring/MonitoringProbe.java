package kieker.diagnosis.architecture.monitoring;

import kieker.common.configuration.Configuration;
import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.registry.TraceRegistry;
import kieker.monitoring.timer.SystemNanoTimer;
import kieker.monitoring.writer.filesystem.AsyncBinaryFsWriter;

/**
 * This class is used as a monitoring probe within the application and uses {@code Kieker} to log this information. It sends only records if the monitoring is
 * active.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringProbe {

	private static IMonitoringController cvMonitoringController;
	private static boolean cvMonitoringActive;
	private static final ThreadLocal<TraceMetadata> cvTrace = new ThreadLocal<>( );

	private final Class<?> ivClass;
	private final String ivMethod;
	private Throwable ivThrowable;
	private boolean ivNewTrace;

	static {
		cvMonitoringActive = "true".equalsIgnoreCase( System.getProperty( "monitoringActive" ) );

		if ( cvMonitoringActive ) {
			initializeMonitoring( );
		}
	}

	private static void initializeMonitoring( ) {
		final Configuration configuration = new Configuration( );

		// Timer
		configuration.setProperty( ConfigurationFactory.TIMER_CLASSNAME, SystemNanoTimer.class.getName( ) );
		configuration.setProperty( SystemNanoTimer.CONFIG_OFFSET, "0" );
		configuration.setProperty( SystemNanoTimer.CONFIG_UNIT, "0" );

		// Writer
		configuration.setProperty( ConfigurationFactory.WRITER_CLASSNAME, AsyncBinaryFsWriter.class.getName( ) );
		configuration.setProperty( AsyncBinaryFsWriter.class.getName( ) + "." + AsyncBinaryFsWriter.CONFIG_MAXENTRIESINFILE, "1000000" );
		configuration.setProperty( AsyncBinaryFsWriter.class.getName( ) + "." + AsyncBinaryFsWriter.CONFIG_QUEUESIZE, "1000000" );
		configuration.setProperty( AsyncBinaryFsWriter.CONFIG_BUFFER, "16384" );

		// Controller
		configuration.setProperty( ConfigurationFactory.CONTROLLER_NAME, "Kieker-Trace-Diagnosis" );
		configuration.setProperty( ConfigurationFactory.EXPERIMENT_ID, "0" );
		configuration.setProperty( ConfigurationFactory.PERIODIC_SENSORS_EXECUTOR_POOL_SIZE, "0" );
		configuration.setProperty( ConfigurationFactory.USE_SHUTDOWN_HOOK, "true" );
		configuration.setProperty( ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true" );
		configuration.setProperty( ConfigurationFactory.MONITORING_ENABLED, "true" );

		cvMonitoringController = MonitoringController.createInstance( configuration );
		MonitoringController.getInstance( ).terminateMonitoring( );
	}

	public MonitoringProbe( final Class<?> aClass, final String aMethod ) {
		ivClass = aClass;
		ivMethod = aMethod;

		fireBeforeEvent( );
	}

	public void fail( final Throwable aThrowable ) {
		ivThrowable = aThrowable;
	}

	public void stop( ) {
		fireAfterEvent( );
	}

	private void fireBeforeEvent( ) {
		if ( !cvMonitoringActive ) {
			return;
		}

		// Get the current trace or start a new one
		TraceMetadata trace = cvTrace.get( );
		if ( trace == null ) {
			// We have to remember that this is the start of a trace, as the trace has to be deregistered at the end.
			ivNewTrace = true;

			trace = TraceRegistry.INSTANCE.registerTrace( );
			cvTrace.set( trace );

			// Write a record for the new trace
			cvMonitoringController.newMonitoringRecord( trace );
		} else {
			ivNewTrace = false;
		}

		final String className = ClassUtil.getRealName( ivClass );

		// Write a record for the start of the method
		final IMonitoringRecord event = new BeforeOperationEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), ivMethod, className );
		cvMonitoringController.newMonitoringRecord( event );
	}

	private void fireAfterEvent( ) {
		if ( !cvMonitoringActive ) {
			return;
		}

		final TraceMetadata trace = cvTrace.get( );
		final String className = ClassUtil.getRealName( ivClass );

		// Create the correct event depending on whether this method call failed or not
		final IMonitoringRecord event;
		if ( ivThrowable == null ) {
			event = new AfterOperationEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), ivMethod, className );
		} else {
			event = new AfterOperationFailedEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), ivMethod, className, ivThrowable.toString( ) );
		}

		cvMonitoringController.newMonitoringRecord( event );

		// If this probe started the trace, it has to close it. Otherwise we could create a memory leak (and faulty monitoring behaviour).
		if ( ivNewTrace ) {
			cvTrace.set( null );
		}
	}

	private long getCurrentTime( ) {
		return cvMonitoringController.getTimeSource( ).getTime( );
	}

}
