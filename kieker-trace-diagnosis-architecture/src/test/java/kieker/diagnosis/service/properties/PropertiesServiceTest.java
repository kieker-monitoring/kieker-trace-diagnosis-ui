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

package kieker.diagnosis.service.properties;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import kieker.diagnosis.common.TechnicalException;

public class PropertiesServiceTest {

	@Rule
	public final ExpectedException expectedException = ExpectedException.none( );

	@Test
	public void propertiesShouldBePersisted( ) {
		final PropertiesService fstService = new PropertiesService( );
		fstService.saveProperty( MyFirstProperty.class, Boolean.TRUE );
		fstService.saveProperty( MyFirstProperty.class, Boolean.FALSE );

		final PropertiesService sndService = new PropertiesService( );
		assertThat( sndService.loadProperty( MyFirstProperty.class ), is( Boolean.FALSE ) );
	}

	@Test
	public void versionShouldChange( ) {
		final PropertiesService service = new PropertiesService( );
		final long preVersion = service.getVersion( );
		service.saveProperty( MyFirstProperty.class, Boolean.TRUE );
		final long postVersion = service.getVersion( );

		assertTrue( postVersion > preVersion );
	}

	@Test
	public void defaultValueShouldWork( ) {
		final PropertiesService service = new PropertiesService( );
		assertThat( service.loadProperty( MySecondProperty.class ), is( Boolean.TRUE ) );
	}

	@Test
	public void primitiveLoadingShouldWork( ) {
		final PropertiesService service = new PropertiesService( );
		assertThat( Boolean.valueOf( service.loadPrimitiveProperty( MySecondProperty.class ) ), is( Boolean.TRUE ) );
	}

	@Test
	public void instantiationExceptionShouldBeHandled( ) {
		expectedException.expect( TechnicalException.class );
		final PropertiesService service = new PropertiesService( );
		service.loadProperty( MyThirdProperty.class );
	}

	@Test
	public void systemPropertiesShouldWork( ) {
		final PropertiesService service = new PropertiesService( );
		final Integer value = service.loadSystemProperty( MyFourthProperty.class );
		assertThat( value, is( 42 ) );
	}

}
