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
 * Test class for the {@link ClassAppearance}.
 *
 * @author Nils Christian Ehmke
 */
public final class ClassAppearanceTest {

	@Test
	public void testConvertWithNullValue( ) {
		assertThat( ClassAppearance.SHORT.convert( null ) ).isNull( );
		assertThat( ClassAppearance.LONG.convert( null ) ).isNull( );
	}

	@Test
	public void testConvertWithLongAppearance( ) {
		assertThat( ClassAppearance.LONG.convert( "A.B.C" ) ).isEqualTo( "A.B.C" );
		assertThat( ClassAppearance.LONG.convert( "A" ) ).isEqualTo( "A" );
	}

	@Test
	public void testConvertWithShortAppearance( ) {
		assertThat( ClassAppearance.SHORT.convert( "A.B.C" ) ).isEqualTo( "C" );
		assertThat( ClassAppearance.SHORT.convert( "A" ) ).isEqualTo( "A" );
	}

}
