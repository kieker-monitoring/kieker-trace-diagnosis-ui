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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.inject.Guice;

/**
 * Test class for {@link PropertiesService}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for PropertiesService" )
public final class PropertiesServiceTest {

	private final PropertiesService propertiesService = Guice.createInjector( ).getInstance( PropertiesService.class );

	@Test
	@DisplayName ( "Application properties should initially not be null" )
	public void applicationPropertiesShouldInitiallyNotBeNull( ) {
		assertThat( propertiesService.loadApplicationProperty( SimpleBooleanApplicationProperty.class ) ).isNotNull( );
		assertThat( propertiesService.loadApplicationProperty( SimpleEnumApplicationProperty.class ) ).isNotNull( );
		assertThat( propertiesService.loadApplicationProperty( SimpleFloatApplicationProperty.class ) ).isNotNull( );
		assertThat( propertiesService.loadApplicationProperty( SimpleIntegerApplicationProperty.class ) ).isNotNull( );
	}

	@Test
	@DisplayName ( "Application properties should be saveable and loadable" )
	public void applicationPropertiesShouldBeSaveableAndLoadable( ) {
		propertiesService.saveApplicationProperty( SimpleStringApplicationProperty.class, "42" );
		assertThat( propertiesService.loadApplicationProperty( SimpleStringApplicationProperty.class ) ).isEqualTo( "42" );
	}

	@Test
	@DisplayName ( "System properties should be loadable" )
	public void systemPropertiesShouldBeLoadable( ) {
		System.setProperty( "SimpleBooleanSystemProperty", "true" );
		assertThat( propertiesService.loadSystemProperty( SimpleBooleanSystemProperty.class ) ).isEqualTo( Boolean.TRUE );

		assertThat( propertiesService.loadSystemProperty( DevelopmentModeProperty.class ) ).isEqualTo( Boolean.FALSE );
	}
}
