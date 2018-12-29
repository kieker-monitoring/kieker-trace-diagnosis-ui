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

package kieker.diagnosis.backend.base.service;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * This factory can be used to retrieve a service from the CDI context, even if the requesting class is not in the
 * context.
 *
 * @author Nils Christian Ehmke
 */
public final class ServiceFactory {

	@Inject
	private static Injector injector;

	private ServiceFactory( ) {
		// Avoid instantiation
	}

	public static <S extends Service> S getService( final Class<S> serviceClass ) {
		return injector.getInstance( serviceClass );
	}

}
