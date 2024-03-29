/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextFormatter.Change;

/**
 * Test class for the {@link NumericFloatingPointFilter}.
 *
 * @author Nils Christian Ehmke
 */
public final class NumericFloatingPointFilterTest {

	@Test
	public void testPositiveCases( ) {
		final NumericFloatingPointFilter filter = new NumericFloatingPointFilter( );

		final Change change = mock( Change.class );

		when( change.getControlNewText( ) ).thenReturn( "" );
		assertThat( filter.apply( change ) ).isEqualTo( change );

		when( change.getControlNewText( ) ).thenReturn( "42" );
		assertThat( filter.apply( change ) ).isEqualTo( change );

		when( change.getControlNewText( ) ).thenReturn( "42.5" );
		assertThat( filter.apply( change ) ).isEqualTo( change );

		when( change.getControlNewText( ) ).thenReturn( "-42" );
		assertThat( filter.apply( change ) ).isEqualTo( change );

		when( change.getControlNewText( ) ).thenReturn( "-42.5" );
		assertThat( filter.apply( change ) ).isEqualTo( change );
	}

	@Test
	public void testNegativeCases( ) {
		final NumericFloatingPointFilter filter = new NumericFloatingPointFilter( );

		final Change change = mock( Change.class );

		when( change.getControlNewText( ) ).thenReturn( "--42.5" );
		assertThat( filter.apply( change ) ).isNull( );

		when( change.getControlNewText( ) ).thenReturn( "abc" );
		assertThat( filter.apply( change ) ).isNull( );

		when( change.getControlNewText( ) ).thenReturn( "42.a5" );
		assertThat( filter.apply( change ) ).isNull( );
	}

}
