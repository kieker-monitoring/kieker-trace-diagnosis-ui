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

package kieker.diagnosis.frontend.base.atom;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import javafx.scene.control.TextFormatter.Change;

/**
 * Test class for the {@link NumericIntegerFilter}.
 *
 * @author Nils Christian Ehmke
 */
public final class NumericIntegerFilterTest {

	@Test
	public void testPositiveCases( ) {
		final NumericIntegerFilter filter = new NumericIntegerFilter( );

		final Change change = mock( Change.class );

		when( change.getControlNewText( ) ).thenReturn( "" );
		assertThat( filter.apply( change ), is( change ) );

		when( change.getControlNewText( ) ).thenReturn( "42" );
		assertThat( filter.apply( change ), is( change ) );

		when( change.getControlNewText( ) ).thenReturn( "-42" );
		assertThat( filter.apply( change ), is( change ) );
	}

	@Test
	public void testNegativeCases( ) {
		final NumericIntegerFilter filter = new NumericIntegerFilter( );

		final Change change = mock( Change.class );

		when( change.getControlNewText( ) ).thenReturn( "42.5" );
		assertThat( filter.apply( change ), is( nullValue( ) ) );

		when( change.getControlNewText( ) ).thenReturn( "--42.5" );
		assertThat( filter.apply( change ), is( nullValue( ) ) );

		when( change.getControlNewText( ) ).thenReturn( "abc" );
		assertThat( filter.apply( change ), is( nullValue( ) ) );

		when( change.getControlNewText( ) ).thenReturn( "42.a5" );
		assertThat( filter.apply( change ), is( nullValue( ) ) );
	}

}
