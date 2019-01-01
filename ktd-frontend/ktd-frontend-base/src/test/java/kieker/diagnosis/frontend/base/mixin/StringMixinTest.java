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

package kieker.diagnosis.frontend.base.mixin;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * This is a unit test for {@link StringMixin}.
 *
 * @author Nils Christian Ehmke
 */
public final class StringMixinTest implements StringMixin {

	@Test
	public void testTrimToNullWithNullString( ) {
		assertThat( trimToNull( null ), is( nullValue( ) ) );
	}

	@Test
	public void testTrimToNullWithEmptyString( ) {
		assertThat( trimToNull( "" ), is( nullValue( ) ) );
	}

	@Test
	public void testTrimToNullWithBlankString( ) {
		assertThat( trimToNull( "    " ), is( nullValue( ) ) );
	}

	@Test
	public void testTrimToNullWithWhitespaceString( ) {
		assertThat( trimToNull( "  A bc " ), is( "A bc" ) );
	}

	@Test
	public void testTrimToNullWithNormalString( ) {
		assertThat( trimToNull( "Abc" ), is( "Abc" ) );
	}

}
