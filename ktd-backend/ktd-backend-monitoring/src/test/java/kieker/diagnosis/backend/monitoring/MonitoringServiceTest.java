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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import kieker.monitoring.core.controller.IMonitoringController;

/**
 * This is a unit test for {@link MonitoringService}.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringServiceTest {

	@AfterEach
	public void after( ) {
		MonitoringControllerHolder.setMonitoringController( null );
		MonitoringControllerHolder.setCurrentConfiguration( null );
	}

	@Test
	public void testGetCurrentStatusNoMonitoring( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		assertThat( monitoringService.getCurrentStatus( ) ).isEqualTo( Status.NO_MONITORING );
	}

	@Test
	public void testGetCurrentStatusRunning( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		final IMonitoringController controller = mock( IMonitoringController.class );
		when( controller.isMonitoringTerminated( ) ).thenReturn( Boolean.FALSE );
		MonitoringControllerHolder.setMonitoringController( controller );

		assertThat( monitoringService.getCurrentStatus( ) ).isEqualTo( Status.RUNNING );
	}

	@Test
	public void testGetCurrentStatusTerminated( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		final IMonitoringController controller = mock( IMonitoringController.class );
		when( controller.isMonitoringTerminated( ) ).thenReturn( Boolean.TRUE );
		MonitoringControllerHolder.setMonitoringController( controller );

		assertThat( monitoringService.getCurrentStatus( ) ).isEqualTo( Status.TERMINATED );
	}

	@Test
	public void testGetCurrentConfigurationNotSet( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		assertThat( monitoringService.getCurrentConfiguration( ) ).isNotNull( );
	}

	@Test
	public void testGetCurrentConfigurationSet( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		final MonitoringConfiguration configuration = new MonitoringConfiguration( );
		MonitoringControllerHolder.setCurrentConfiguration( configuration );

		assertThat( monitoringService.getCurrentConfiguration( ) ).isEqualTo( configuration );
	}

	@Test
	public void testConfigureMonitoringActivateAndDeactivate( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		final MonitoringConfiguration configuration = monitoringService.getCurrentConfiguration( );

		configuration.setActive( true );
		monitoringService.configureMonitoring( configuration );
		assertThat( monitoringService.getCurrentStatus( ) ).isEqualTo( Status.RUNNING );

		configuration.setTimer( Timer.MILLIS );
		configuration.setWriter( Writer.ASCII_WRITER );
		monitoringService.configureMonitoring( configuration );
		assertThat( monitoringService.getCurrentStatus( ) ).isEqualTo( Status.RUNNING );

		configuration.setActive( false );
		monitoringService.configureMonitoring( configuration );
		assertThat( monitoringService.getCurrentStatus( ) ).isEqualTo( Status.NO_MONITORING );
	}

}
