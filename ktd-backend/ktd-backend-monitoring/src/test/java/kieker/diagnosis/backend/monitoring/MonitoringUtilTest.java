/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * This is a unit test for {@link MonitoringUtil}.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringUtilTest {

	@AfterEach
	public void after( ) {
		MonitoringUtil.setMonitoringProbeFactory( new NoOpMonitoringProbeFactory( ) );
	}

	@Test
	public void testMonitoringUtil( ) {
		final MonitoringProbeFactory probeFactory = mock( MonitoringProbeFactory.class );
		MonitoringUtil.setMonitoringProbeFactory( probeFactory );

		MonitoringUtil.createMonitoringProbe( String.class, "toString()" );
		verify( probeFactory ).createMonitoringProbe( String.class, "toString()" );
	}

}
