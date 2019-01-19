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

import kieker.monitoring.core.controller.IMonitoringController;

/**
 * This is a holder for the current monitoring configuration and the monitoring controller. This is necessary, as the
 * monitoring probe is not in the CDI context and because the monitoring controller can be exchanged during runtime.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringControllerHolder {

	private static volatile IMonitoringController monitoringController;
	private static volatile MonitoringConfiguration monitoringConfiguration;

	private MonitoringControllerHolder( ) {
		// Avoid instantiation
	}

	public static IMonitoringController getMonitoringController( ) {
		return monitoringController;
	}

	public static void setMonitoringController( final IMonitoringController aMonitoringController ) {
		monitoringController = aMonitoringController;
	}

	public static MonitoringConfiguration getCurrentConfiguration( ) {
		return monitoringConfiguration;
	}

	public static void setCurrentConfiguration( final MonitoringConfiguration aMonitoringConfiguration ) {
		monitoringConfiguration = aMonitoringConfiguration;
	}

	public static IMonitoringController clearMonitoringController( ) {
		final IMonitoringController controller = monitoringController;
		monitoringController = null;
		return controller;
	}

}
