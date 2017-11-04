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

package kieker.diagnosis.service;

import com.google.inject.AbstractModule;

import kieker.diagnosis.architecture.KiekerTraceDiagnosisArchitectureModule;

/**
 * This is the Guice module for the service part of the application.
 *
 * @author Nils Christian Ehmke
 */
public class KiekerTraceDiagnosisServiceModule extends AbstractModule {

	@Override
	protected void configure( ) {
		// We need to make sure that the Guice module from the architecture sub-project is installed
		install( new KiekerTraceDiagnosisArchitectureModule( ) );
	}

}
