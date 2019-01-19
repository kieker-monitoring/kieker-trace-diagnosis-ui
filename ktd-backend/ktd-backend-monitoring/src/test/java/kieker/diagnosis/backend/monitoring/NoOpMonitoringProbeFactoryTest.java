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

import org.junit.jupiter.api.Test;

/**
 * This is a unit test for {@link NoOpMonitoringProbeFactory}.
 *
 * @author Nils Christian Ehmke
 */
public final class NoOpMonitoringProbeFactoryTest {

	@Test
	public void factoryShouldReturnSameInstance( ) {
		final NoOpMonitoringProbeFactory probeFactory = new NoOpMonitoringProbeFactory( );

		assertThat( probeFactory.createMonitoringProbe( null, null ) ).isEqualTo( probeFactory.createMonitoringProbe( null, null ) );
	}

	@Test
	public void createdProbeShouldBeCallable( ) {
		final NoOpMonitoringProbeFactory probeFactory = new NoOpMonitoringProbeFactory( );

		final MonitoringProbe monitoringProbe = probeFactory.createMonitoringProbe( null, null );
		monitoringProbe.fail( new RuntimeException( ) );
		monitoringProbe.stop( );
	}

}
