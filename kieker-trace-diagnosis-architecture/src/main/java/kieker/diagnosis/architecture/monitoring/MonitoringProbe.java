/*************************************************************************** 
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)         
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

package kieker.diagnosis.architecture.monitoring;

import kieker.common.record.IMonitoringRecord;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.diagnosis.architecture.common.ClassUtil;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.core.registry.TraceRegistry;

/**
 * This class is used as a monitoring probe within the application and uses {@code Kieker} to log this information. It sends only records if the monitoring is
 * active.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringProbe {

	private final IMonitoringController ivMonitoringController;

	private final Class<?> ivClass;
	private final String ivMethod;
	private Throwable ivThrowable;
	private boolean ivNewTrace;

	public MonitoringProbe( final Class<?> aClass, final String aMethod ) {
		ivClass = aClass;
		ivMethod = aMethod;

		ivMonitoringController = MonitoringControllerHolder.getMonitoringController( );
		fireBeforeEvent( );
	}

	public void fail( final Throwable aThrowable ) {
		ivThrowable = aThrowable;
	}

	public void stop( ) {
		fireAfterEvent( );
	}

	private void fireBeforeEvent( ) {
		if ( ivMonitoringController == null ) {
			return;
		}

		// Get the current trace or start a new one
		TraceMetadata trace = TraceRegistry.INSTANCE.getTrace( );
		if ( trace == null ) {
			// We have to remember that this is the start of a trace, as the trace has to be deregistered at the end.
			ivNewTrace = true;
			trace = TraceRegistry.INSTANCE.registerTrace( );

			// Write a record for the new trace
			ivMonitoringController.newMonitoringRecord( trace );
		} else {
			ivNewTrace = false;
		}

		final String className = ClassUtil.getRealName( ivClass );

		// Write a record for the start of the method
		final IMonitoringRecord event = new BeforeOperationEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), ivMethod, className );
		ivMonitoringController.newMonitoringRecord( event );
	}

	private void fireAfterEvent( ) {
		if ( ivMonitoringController == null ) {
			return;
		}

		final TraceMetadata trace = TraceRegistry.INSTANCE.getTrace( );
		final String className = ClassUtil.getRealName( ivClass );

		// Create the correct event depending on whether this method call failed or not
		final IMonitoringRecord event;
		if ( ivThrowable == null ) {
			event = new AfterOperationEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), ivMethod, className );
		} else {
			event = new AfterOperationFailedEvent( getCurrentTime( ), trace.getTraceId( ), trace.getNextOrderId( ), ivMethod, className, ivThrowable.toString( ) );
		}

		ivMonitoringController.newMonitoringRecord( event );

		// If this probe started the trace, it has to close it. Otherwise we could create a memory leak (and faulty monitoring behaviour).
		if ( ivNewTrace ) {
			TraceRegistry.INSTANCE.unregisterTrace( );
		}
	}

	private long getCurrentTime( ) {
		return ivMonitoringController.getTimeSource( ).getTime( );
	}

}
