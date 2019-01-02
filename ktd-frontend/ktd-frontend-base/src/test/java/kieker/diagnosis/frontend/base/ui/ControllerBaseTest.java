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

package kieker.diagnosis.frontend.base.ui;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.monitoring.MonitoringService;
import kieker.diagnosis.frontend.base.FrontendBaseModule;

/**
 * This is a unit test for {@link ControllerBase}.
 *
 * @author Nils Christian Ehmke
 */
public final class ControllerBaseTest {

	private TestController controller;

	@Before
	public void before( ) {
		final Injector injector = Guice.createInjector( new FrontendBaseModule( ) );
		controller = injector.getInstance( TestController.class );
	}

	@Test
	public void testGetLocalizedString( ) {
		assertThat( controller.getLocalizedString( "key" ), is( "value" ) );
	}

	@Test
	public void testGetViewModel( ) {
		assertThat( controller.getViewModel( ), is( instanceOf( TestViewModel.class ) ) );
	}

	@Test
	public void testGetService( ) {
		assertThat( controller.getService( MonitoringService.class ), is( instanceOf( MonitoringService.class ) ) );
	}

	@Test
	public void testGetController( ) {
		assertThat( controller.getController( TestController.class ), is( controller ) );
	}

	@Test
	public void testGetLogger( ) {
		final Logger logger = controller.getLogger( );
		assertThat( logger, is( notNullValue( ) ) );
		assertThat( controller.getLogger( ), is( logger ) );
	}

}
