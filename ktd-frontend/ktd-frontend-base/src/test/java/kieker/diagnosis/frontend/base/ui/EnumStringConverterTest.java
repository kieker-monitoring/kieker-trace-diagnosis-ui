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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link EnumStringConverter}.
 *
 * @author Nils Christian Ehmke
 */
public class EnumStringConverterTest {

	private final EnumStringConverter<TestEnum> converter = new EnumStringConverter<>( TestEnum.class );

	@Test
	public void toStringShouldWork( ) {
		assertThat( converter.toString( TestEnum.ENUM_VALUE_1 ) ).isEqualTo( "Value 1" );
	}

	@Test
	public void toStringForNullValueShouldWork( ) {
		assertThat( converter.toString( null ) ).isEqualTo( "" );
	}

	@Test
	public void fromStringShouldWork( ) {
		assertThat( converter.fromString( "Value 2" ) ).isEqualTo( TestEnum.ENUM_VALUE_2 );
	}

	@Test
	public void fromStringForEmptyStringShouldWork( ) {
		assertThat( converter.fromString( "" ) ).isEqualTo( nullValue( ) );
	}

}

enum TestEnum {

	ENUM_VALUE_1, ENUM_VALUE_2

}
