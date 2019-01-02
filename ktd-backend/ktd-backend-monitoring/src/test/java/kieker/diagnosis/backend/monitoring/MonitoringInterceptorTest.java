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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;

/**
 * import kieker.diagnosis.backend.base.service.Service;
 *
 * /** This is a unit test for {@link MonitoringInterceptor}.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringInterceptorTest {

	private TestService testService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new MonitoringModule( ) );
		testService = injector.getInstance( TestService.class );
	}

	@After
	public void after( ) {
		MonitoringUtil.setMonitoringProbeFactory( new NoOpMonitoringProbeFactory( ) );
	}

	@Test
	public void testMonitoringInterceptorWithoutException( ) {
		final MonitoringProbeFactory probeFactory = mock( MonitoringProbeFactory.class );
		MonitoringUtil.setMonitoringProbeFactory( probeFactory );

		final MonitoringProbe probe = mock( MonitoringProbe.class );
		when( probeFactory.createMonitoringProbe( any( ), eq( "public void kieker.diagnosis.backend.monitoring.MonitoringInterceptorTest$TestService.method()" ) ) ).thenReturn( probe );

		testService.method( );

		verify( probe, never( ) ).fail( any( ) );
		verify( probe ).stop( );
	}

	@Test
	public void testMonitoringInterceptorWithException( ) {
		final MonitoringProbeFactory probeFactory = mock( MonitoringProbeFactory.class );
		MonitoringUtil.setMonitoringProbeFactory( probeFactory );

		final MonitoringProbe probe = mock( MonitoringProbe.class );
		when( probeFactory.createMonitoringProbe( any( ), eq( "public void kieker.diagnosis.backend.monitoring.MonitoringInterceptorTest$TestService.methodWithException()" ) ) ).thenReturn( probe );

		try {
			testService.methodWithException( );
		} catch ( final NullPointerException ex ) {
			// Ignore
		}

		verify( probe ).fail( any( NullPointerException.class ) );
		verify( probe ).stop( );
	}

	@Singleton
	public static class TestService implements Service {

		public void method( ) {
		}

		public void methodWithException( ) {
			throw new NullPointerException( );
		}

	}

}
