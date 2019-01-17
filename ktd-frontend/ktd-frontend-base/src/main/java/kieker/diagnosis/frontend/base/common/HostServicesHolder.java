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

package kieker.diagnosis.frontend.base.common;

import javafx.application.HostServices;

/**
 * This class holds the host services of the application.
 *
 * @author Nils Christian Ehmke
 */
public final class HostServicesHolder {

	private static HostServices hostServices;

	private HostServicesHolder( ) {
	}

	public static void setHostServices( final HostServices hostServices ) {
		HostServicesHolder.hostServices = hostServices;
	}

	public static HostServices getHostServices( ) {
		return hostServices;
	}

}
