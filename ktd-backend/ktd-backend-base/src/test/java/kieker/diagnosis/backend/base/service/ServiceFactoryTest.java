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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.inject.Guice;

import kieker.diagnosis.backend.base.ServiceBaseModule;

/**
 * This is a unit test for {@link ClassUtil}.
 *
 * @author Nils Christian Ehmke
 */
public final class ServiceFactoryTest {

	@Test
	public void getServiceShouldReturnServiceFromCDIContext( ) {
		Guice.createInjector( new ServiceBaseModule( ) );
		assertThat( ServiceFactory.getService( TestService.class ), is( notNullValue( ) ) );
	}

	private static final class TestService implements Service {
	}

}
