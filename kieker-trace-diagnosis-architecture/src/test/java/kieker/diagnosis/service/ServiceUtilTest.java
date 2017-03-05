/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ServiceUtilTest {

	@Test
	public void returnedInstanceShouldBeOfCorrectType( ) {
		assertThat( ServiceUtil.getService( MyFirstService.class ), is( instanceOf( MyFirstService.class ) ) );
	}

	@Test
	public void serviceShouldBeSingleton( ) {
		assertThat( ServiceUtil.getService( MyFirstService.class ), is( ServiceUtil.getService( MyFirstService.class ) ) );
	}

	@Test
	public void serviceInjectionShouldWork( ) {
		final MySecondService mySecondService = ServiceUtil.getService( MySecondService.class );
		final MyFirstService myFirstService = mySecondService.getMyFirstService( );

		assertThat( myFirstService, is( notNullValue( ) ) );
	}

	@Test
	public void serviceInjectionShouldReturnSingleton( ) {
		final MySecondService mySecondService = ServiceUtil.getService( MySecondService.class );
		final MyFirstService myFirstService = mySecondService.getMyFirstService( );

		assertThat( myFirstService, is( ServiceUtil.getService( MyFirstService.class ) ) );
	}

	@Test
	public void serviceInjectionShouldResolveCyclicDependencies( ) {
		final MySecondService mySecondService = ServiceUtil.getService( MySecondService.class );
		final MyFirstService myFirstService = ServiceUtil.getService( MyFirstService.class );

		assertThat( mySecondService.getMyFirstService( ), is( ServiceUtil.getService( MyFirstService.class ) ) );
		assertThat( myFirstService.getMySecondService( ), is( ServiceUtil.getService( MySecondService.class ) ) );
	}
}
