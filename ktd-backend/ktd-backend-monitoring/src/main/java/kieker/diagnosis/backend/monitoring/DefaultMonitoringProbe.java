/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.monitoring;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.backend.base.common.ClassUtil;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.registry.TraceRegistry;

/**
 * This class is used as a monitoring probe within the application and uses {@code Kieker} to log this information. It
 * sends only records if the monitoring is active.
 *
 * @author Nils Christian Ehmke
 */
public final class DefaultMonitoringProbe implements MonitoringProbe {

	private final IMonitoringController monitoringController;

	private final Class<?> clazz;
	private final String method;
	private Throwable throwable;
	private boolean newTrace;

	public DefaultMonitoringProbe( final Class<?> clazz, final String method ) {
		this.clazz = clazz;
		this.method = method;

		monitoringController = MonitoringControllerHolder.getMonitoringController( );
		fireBeforeEvent( );
	}

	@Override
	public void fail( final Throwable throwable ) {
		this.throwable = throwable;
	}

	@Override
	public void stop( ) {
		fireAfterEvent( );
	}

	private void fireBeforeEvent( ) {
		if ( monitoringController == null ) {
			return;
		}

		// Get the current trace or start a new one
		TraceMetadata trace = TraceRegistry.INSTANCE.getTrace( );
		if ( trace == null ) {
			// We have to remember that this is the start of a trace, as the trace has to be deregistered at the end.
			newTrace = true;
			trace = TraceRegistry.INSTANCE.registerTrace( );

			// Write a record for the new trace
			monitoringController.newMonitoringRecord( trace );
		} else {
			newTrace = false;
		}

		final String className = ClassUtil.getRealName( clazz );

		// Write a record for the start of the method
		final IMonitoringRecord event = new BeforeOperationEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), method, className );
		monitoringController.newMonitoringRecord( event );
	}

	private void fireAfterEvent( ) {
		if ( monitoringController == null ) {
			return;
		}

		final TraceMetadata trace = TraceRegistry.INSTANCE.getTrace( );
		final String className = ClassUtil.getRealName( clazz );

		// Create the correct event depending on whether this method call failed or not
		final IMonitoringRecord event;
		if ( throwable == null ) {
			event = new AfterOperationEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), method, className );
		} else {
			event = new AfterOperationFailedEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), method, className, throwable.toString( ) );
		}

		monitoringController.newMonitoringRecord( event );

		// If this probe started the trace, it has to close it. Otherwise we could create a memory leak (and faulty
		// monitoring
		// behaviour).
		if ( newTrace ) {
			TraceRegistry.INSTANCE.unregisterTrace( );
		}
	}

	private long getCurrentTime( ) {
		return monitoringController.getTimeSource( ).getTime( );
	}

}
