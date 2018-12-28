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

package kieker.diagnosis.backend.monitoring;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import kieker.diagnosis.backend.monitoring.MonitoringConfiguration;
import kieker.diagnosis.backend.monitoring.MonitoringControllerHolder;
import kieker.diagnosis.backend.monitoring.MonitoringService;
import kieker.diagnosis.backend.monitoring.Status;
import kieker.diagnosis.backend.monitoring.Timer;
import kieker.diagnosis.backend.monitoring.Writer;
import kieker.monitoring.core.controller.IMonitoringController;

/**
 * Test class for {@link MonitoringService}.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringServiceTest {

	@After
	public void After( ) {
		MonitoringControllerHolder.setMonitoringController( null );
		MonitoringControllerHolder.setCurrentConfiguration( null );
	}
	
	@Test
	public void testGetCurrentStatusNoMonitoring( ) {
		final MonitoringService monitoringService = new MonitoringService( );
		
		assertThat( monitoringService.getCurrentStatus( ), is( Status.NO_MONITORING ) );
	}
	
	@Test
	public void testGetCurrentStatusRunning( ) {
		final MonitoringService monitoringService = new MonitoringService( );
		
		final IMonitoringController controller = mock( IMonitoringController.class );
		when( controller.isMonitoringTerminated( ) ).thenReturn( Boolean.FALSE );
		MonitoringControllerHolder.setMonitoringController( controller );

		assertThat( monitoringService.getCurrentStatus( ), is( Status.RUNNING ) );
	}
	
	@Test
	public void testGetCurrentStatusTerminated( ) {
		final MonitoringService monitoringService = new MonitoringService( );
		
		final IMonitoringController controller = mock( IMonitoringController.class );
		when( controller.isMonitoringTerminated( ) ).thenReturn( Boolean.TRUE );
		MonitoringControllerHolder.setMonitoringController( controller );
		
		assertThat( monitoringService.getCurrentStatus( ), is( Status.TERMINATED ) );
	}
	
	@Test
	public void testGetCurrentConfigurationNotSet( ) {
		final MonitoringService monitoringService = new MonitoringService( );
		
		assertThat( monitoringService.getCurrentConfiguration( ), is( notNullValue( ) ) );
	}

	@Test
	public void testGetCurrentConfigurationSet( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		final MonitoringConfiguration configuration = new MonitoringConfiguration( );
		MonitoringControllerHolder.setCurrentConfiguration( configuration );
		
		assertThat( monitoringService.getCurrentConfiguration( ), is( configuration ) );
	}
	
	@Test
	public void testConfigureMonitoringActivateAndDeactivate( ) {
		final MonitoringService monitoringService = new MonitoringService( );

		final MonitoringConfiguration configuration = monitoringService.getCurrentConfiguration( );
		
		configuration.setActive( true );
		monitoringService.configureMonitoring( configuration );
		assertThat( monitoringService.getCurrentStatus( ), is( Status.RUNNING ) );
		
		configuration.setTimer( Timer.MILLIS );
		configuration.setWriter( Writer.ASCII_WRITER );
		monitoringService.configureMonitoring( configuration );
		assertThat( monitoringService.getCurrentStatus( ), is( Status.RUNNING ) );
		
		configuration.setActive( false );
		monitoringService.configureMonitoring( configuration );
		assertThat( monitoringService.getCurrentStatus( ), is( Status.NO_MONITORING ) );
	}
	
}
