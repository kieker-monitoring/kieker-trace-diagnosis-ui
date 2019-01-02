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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.monitoring.core.controller.IMonitoringController;
import kieker.monitoring.timer.ITimeSource;

/**
 * This is a unit test for {@link DefaultMonitoringProbe}.
 *
 * @author Nils Christian Ehmke
 */
public final class DefaultMonitoringProbeTest {

	@After
	public void after( ) {
		MonitoringControllerHolder.setMonitoringController( null );
	}

	@Test
	public void testNormalBehaviour( ) {
		final ITimeSource timeSource = mock( ITimeSource.class );
		when( timeSource.getTime( ) ).thenReturn( 42L );
		final IMonitoringController monitoringController = mock( IMonitoringController.class );
		when( monitoringController.getTimeSource( ) ).thenReturn( timeSource );
		MonitoringControllerHolder.setMonitoringController( monitoringController );

		final DefaultMonitoringProbe probe = new DefaultMonitoringProbe( String.class, "toString()" );
		probe.stop( );

		verify( monitoringController ).newMonitoringRecord( any( TraceMetadata.class ) );
		verify( monitoringController ).newMonitoringRecord( any( BeforeOperationEvent.class ) );
		verify( monitoringController ).newMonitoringRecord( any( AfterOperationEvent.class ) );
	}

	@Test
	public void testExceptionBehaviour( ) {
		final ITimeSource timeSource = mock( ITimeSource.class );
		when( timeSource.getTime( ) ).thenReturn( 42L );
		final IMonitoringController monitoringController = mock( IMonitoringController.class );
		when( monitoringController.getTimeSource( ) ).thenReturn( timeSource );
		MonitoringControllerHolder.setMonitoringController( monitoringController );

		final DefaultMonitoringProbe probe = new DefaultMonitoringProbe( String.class, "toString()" );
		probe.fail( new RuntimeException( ) );
		probe.stop( );

		verify( monitoringController ).newMonitoringRecord( any( TraceMetadata.class ) );
		verify( monitoringController ).newMonitoringRecord( any( BeforeOperationEvent.class ) );
		verify( monitoringController ).newMonitoringRecord( any( AfterOperationFailedEvent.class ) );
	}

	@Test
	public void testStackBehaviour( ) {
		final ITimeSource timeSource = mock( ITimeSource.class );
		when( timeSource.getTime( ) ).thenReturn( 42L );
		final IMonitoringController monitoringController = mock( IMonitoringController.class );
		when( monitoringController.getTimeSource( ) ).thenReturn( timeSource );
		MonitoringControllerHolder.setMonitoringController( monitoringController );

		final DefaultMonitoringProbe fstProbe = new DefaultMonitoringProbe( String.class, "toString()" );
		final DefaultMonitoringProbe sndProbe = new DefaultMonitoringProbe( String.class, "toString()" );
		sndProbe.stop( );
		fstProbe.stop( );

		verify( monitoringController ).newMonitoringRecord( any( TraceMetadata.class ) );
		verify( monitoringController, times( 2 ) ).newMonitoringRecord( any( BeforeOperationEvent.class ) );
		verify( monitoringController, times( 2 ) ).newMonitoringRecord( any( AfterOperationEvent.class ) );
	}

	@Test
	public void testBehaviourWithoutController( ) {
		MonitoringControllerHolder.setMonitoringController( null );

		final DefaultMonitoringProbe probe = new DefaultMonitoringProbe( String.class, "toString()" );
		probe.fail( new NullPointerException( ) );
		probe.stop( );
	}

}
