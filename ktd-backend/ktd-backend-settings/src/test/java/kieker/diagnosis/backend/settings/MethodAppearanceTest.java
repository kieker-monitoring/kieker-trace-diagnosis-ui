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

package kieker.diagnosis.backend.settings;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link MethodAppearance}.
 *
 * @author Nils Christian Ehmke
 */
public final class MethodAppearanceTest {

	@Test
	public void testConvertWithNullValue( ) {
		assertThat( MethodAppearance.SHORT.convert( null ) ).isNull( );
		assertThat( MethodAppearance.LONG.convert( null ) ).isNull( );
	}

	@Test
	public void testConvertWithLongAppearance( ) {
		assertThat( MethodAppearance.LONG.convert( "A.B.C.d()" ) ).isEqualTo( "A.B.C.d()" );
		assertThat( MethodAppearance.LONG.convert( "A.d()" ) ).isEqualTo( "A.d()" );
	}

	@Test
	public void testConvertWithShortAppearance( ) {
		assertThat( MethodAppearance.SHORT.convert( "A.B.C.d()" ) ).isEqualTo( "d(...)" );
		assertThat( MethodAppearance.SHORT.convert( "d()" ) ).isEqualTo( "d(...)" );
		assertThat( MethodAppearance.SHORT.convert( "A.B.C.d(int, String)" ) ).isEqualTo( "d(...)" );
		assertThat( MethodAppearance.SHORT.convert( "e" ) ).isEqualTo( "e" );
	}

}
