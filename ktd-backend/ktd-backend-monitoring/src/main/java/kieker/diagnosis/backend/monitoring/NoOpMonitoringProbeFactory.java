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

public final class NoOpMonitoringProbeFactory implements MonitoringProbeFactory {

	private static final MonitoringProbe NO_OP_MONITORING_PROBE = new NoOpMonitoringProbe( );

	@Override
	public MonitoringProbe createMonitoringProbe( final Class<?> aClass, final String aMethod ) {
		return NO_OP_MONITORING_PROBE;
	}

	private static class NoOpMonitoringProbe implements MonitoringProbe {

		@Override
		public void fail( final Throwable t ) {
		}

		@Override
		public void stop( ) {
		}

	}

}
