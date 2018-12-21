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

package kieker.diagnosis.architecture.service.properties;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.architecture.KiekerTraceDiagnosisArchitectureModule;

/**
 * Test class for {@link PropertiesService}.
 *
 * @author Nils Christian Ehmke
 */
public final class PropertiesServiceTest {

	private PropertiesService propertiesService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( new KiekerTraceDiagnosisArchitectureModule( ) );
		propertiesService = injector.getInstance( PropertiesService.class );
	}

	@Test
	public void testLoadApplicationProperty( ) {
		assertThat( propertiesService.loadApplicationProperty( SimpleBooleanApplicationProperty.class ), is( notNullValue( ) ) );
		assertThat( propertiesService.loadApplicationProperty( SimpleEnumApplicationProperty.class ), is( notNullValue( ) ) );
		assertThat( propertiesService.loadApplicationProperty( SimpleFloatApplicationProperty.class ), is( notNullValue( ) ) );
		assertThat( propertiesService.loadApplicationProperty( SimpleIntegerApplicationProperty.class ), is( notNullValue( ) ) );
	}

	@Test
	public void testSaveApplicationProperty( ) {
		propertiesService.saveApplicationProperty( SimpleStringApplicationProperty.class, "42" );
		assertThat( propertiesService.loadApplicationProperty( SimpleStringApplicationProperty.class ), is( "42" ) );
	}

	@Test
	public void testLoadSystemProperty( ) {
		System.setProperty( "SimpleBooleanSystemProperty", "true" );
		assertThat( propertiesService.loadSystemProperty( SimpleBooleanSystemProperty.class ), is( Boolean.TRUE ) );

		assertThat( propertiesService.loadSystemProperty( DevelopmentModeProperty.class ), is( Boolean.FALSE ) );
	}
}
