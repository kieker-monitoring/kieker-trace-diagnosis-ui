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

package kieker.diagnosis.architecture.service.monitoring;

import java.util.concurrent.LinkedBlockingQueue;

import com.google.inject.Singleton;

import kieker.common.configuration.Configuration;
import kieker.diagnosis.architecture.monitoring.MonitoringConfiguration;
import kieker.diagnosis.architecture.monitoring.MonitoringControllerHolder;
import kieker.diagnosis.architecture.monitoring.Status;
import kieker.diagnosis.architecture.monitoring.Timer;
import kieker.diagnosis.architecture.monitoring.Writer;
import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.controller.MonitoringController;
import kieker.monitoring.core.controller.WriterController;
import kieker.monitoring.timer.SystemMilliTimer;
import kieker.monitoring.timer.SystemNanoTimer;
import kieker.monitoring.writer.filesystem.AsciiFileWriter;
import kieker.monitoring.writer.filesystem.BinaryFileWriter;

/**
 * This service can be used to control the monitoring within the application (and from within the application).
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MonitoringService extends ServiceBase {

	/**
	 * Delivers the current status of the monitoring.
	 *
	 * @return The monitoring status.
	 */
	public Status getCurrentStatus( ) {
		final IMonitoringController monitoringController = MonitoringControllerHolder.getMonitoringController( );

		if ( monitoringController == null ) {
			return Status.NO_MONITORING;
		} else {
			if ( monitoringController.isMonitoringTerminated( ) ) {
				return Status.TERMINATED;
			} else {
				return Status.RUNNING;
			}
		}
	}

	/**
	 * Delivers the current configuration of the monitoring. If there is no such configuration, a suitable default configuration is returned.
	 *
	 * @return The monitoring configuration.
	 */
	public MonitoringConfiguration getCurrentConfiguration( ) {
		MonitoringConfiguration currentConfiguration = MonitoringControllerHolder.getCurrentConfiguration( );

		if ( currentConfiguration == null ) {
			// Prepare a default configuration
			currentConfiguration = new MonitoringConfiguration( );
			currentConfiguration.setActive( false );
			currentConfiguration.setBuffer( 16384 );
			currentConfiguration.setMaxEntriesPerFile( 1000000 );
			currentConfiguration.setOutputDirectory( System.getProperty( "java.io.tmpdir" ) );
			currentConfiguration.setQueueSize( 100000 );
			currentConfiguration.setTimer( Timer.NANO );
			currentConfiguration.setWriter( Writer.BINARY_WRITER );
		}

		return currentConfiguration;
	}

	/**
	 * Configures the application. That means, that the old monitoring is terminated and a new monitoring is started.
	 *
	 * @param aConfiguration
	 *            The monitoring configuration.
	 */
	public void configureMonitoring( final MonitoringConfiguration aConfiguration ) {
		// Terminate the old monitoring controller
		IMonitoringController monitoringController = MonitoringControllerHolder.clearMonitoringController( );
		if ( monitoringController != null ) {
			monitoringController.terminateMonitoring( );
		}

		// Terminate the singleton monitoring if necessary
		MonitoringController.getInstance( ).terminateMonitoring( );

		MonitoringControllerHolder.setCurrentConfiguration( aConfiguration );

		if ( aConfiguration.isActive( ) ) {
			// Create the new configuration
			final Configuration configuration = new Configuration( );

			// Timer
			if ( aConfiguration.getTimer( ) == Timer.MILLIS ) {
				configuration.setProperty( ConfigurationFactory.TIMER_CLASSNAME, SystemMilliTimer.class.getName( ) );
				configuration.setProperty( SystemMilliTimer.CONFIG_OFFSET, "0" );
				configuration.setProperty( SystemMilliTimer.CONFIG_UNIT, "0" );
			} else {
				configuration.setProperty( ConfigurationFactory.TIMER_CLASSNAME, SystemNanoTimer.class.getName( ) );
				configuration.setProperty( SystemNanoTimer.CONFIG_OFFSET, "0" );
				configuration.setProperty( SystemNanoTimer.CONFIG_UNIT, "0" );
			}

			// Writer
			final String maxEntriesPerFile = Integer.toString( aConfiguration.getMaxEntriesPerFile( ) );
			final String queueSize = Integer.toString( aConfiguration.getQueueSize( ) );
			final String bufferSize = Integer.toString( aConfiguration.getBuffer( ) );
			configuration.setProperty( WriterController.PREFIX + WriterController.RECORD_QUEUE_SIZE, queueSize );
			configuration.setProperty( WriterController.PREFIX + WriterController.RECORD_QUEUE_FQN, LinkedBlockingQueue.class.getName( ) );

			switch ( aConfiguration.getWriter( ) ) {
				case ASCII_WRITER:
					configuration.setProperty( ConfigurationFactory.WRITER_CLASSNAME, AsciiFileWriter.class.getName( ) );
					configuration.setProperty( AsciiFileWriter.CONFIG_PATH, aConfiguration.getOutputDirectory( ) );
					configuration.setProperty( AsciiFileWriter.CONFIG_MAXENTRIESINFILE, maxEntriesPerFile );
					configuration.setProperty( AsciiFileWriter.CONFIG_CHARSET_NAME, "UTF-8" );
					configuration.setProperty( AsciiFileWriter.CONFIG_FLUSH_MAPFILE, "true" );
				break;
				case BINARY_WRITER:
					configuration.setProperty( ConfigurationFactory.WRITER_CLASSNAME, BinaryFileWriter.class.getName( ) );
					configuration.setProperty( BinaryFileWriter.CONFIG_PATH, aConfiguration.getOutputDirectory( ) );
					configuration.setProperty( BinaryFileWriter.CONFIG_MAXENTRIESINFILE, maxEntriesPerFile );
					configuration.setProperty( BinaryFileWriter.CONFIG_BUFFERSIZE, bufferSize );
					configuration.setProperty( BinaryFileWriter.CONFIG_FLUSH_MAPFILE, "true" );
				break;
				default:
				break;

			}

			// Controller
			configuration.setProperty( ConfigurationFactory.CONTROLLER_NAME, "Kieker-Trace-Diagnosis" );
			configuration.setProperty( ConfigurationFactory.EXPERIMENT_ID, "0" );
			configuration.setProperty( ConfigurationFactory.PERIODIC_SENSORS_EXECUTOR_POOL_SIZE, "0" );
			// Never use a shutdown hook here. This will lead to a memory leak.
			configuration.setProperty( ConfigurationFactory.USE_SHUTDOWN_HOOK, "false" );
			configuration.setProperty( ConfigurationFactory.AUTO_SET_LOGGINGTSTAMP, "true" );
			configuration.setProperty( ConfigurationFactory.MONITORING_ENABLED, "true" );

			// Create the new monitoring controller
			monitoringController = MonitoringController.createInstance( configuration );
			MonitoringControllerHolder.setMonitoringController( monitoringController );
		}
	}

}
