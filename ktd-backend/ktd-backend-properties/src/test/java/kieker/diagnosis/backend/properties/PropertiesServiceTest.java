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

package kieker.diagnosis.backend.properties;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Test class for {@link PropertiesService}.
 *
 * @author Nils Christian Ehmke
 */
public final class PropertiesServiceTest {

	private PropertiesService propertiesService;

	@BeforeEach
	public void setUp( ) {
		final Injector injector = Guice.createInjector( );
		propertiesService = injector.getInstance( PropertiesService.class );
	}

	@Test
	public void testLoadApplicationProperty( ) {
		assertThat( propertiesService.loadApplicationProperty( SimpleBooleanApplicationProperty.class ) ).isNotNull( );
		assertThat( propertiesService.loadApplicationProperty( SimpleEnumApplicationProperty.class ) ).isNotNull( );
		assertThat( propertiesService.loadApplicationProperty( SimpleFloatApplicationProperty.class ) ).isNotNull( );
		assertThat( propertiesService.loadApplicationProperty( SimpleIntegerApplicationProperty.class ) ).isNotNull( );
	}

	@Test
	public void testSaveApplicationProperty( ) {
		propertiesService.saveApplicationProperty( SimpleStringApplicationProperty.class, "42" );
		assertThat( propertiesService.loadApplicationProperty( SimpleStringApplicationProperty.class ) ).isEqualTo( "42" );
	}

	@Test
	public void testLoadSystemProperty( ) {
		System.setProperty( "SimpleBooleanSystemProperty", "true" );
		assertThat( propertiesService.loadSystemProperty( SimpleBooleanSystemProperty.class ) ).isEqualTo( Boolean.TRUE );

		assertThat( propertiesService.loadSystemProperty( DevelopmentModeProperty.class ) ).isEqualTo( Boolean.FALSE );
	}
}
