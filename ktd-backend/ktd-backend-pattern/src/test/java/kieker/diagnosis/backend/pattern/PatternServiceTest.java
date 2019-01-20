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

package kieker.diagnosis.backend.pattern;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * This is a unit test for the {@link PatternService}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for PatternService" )
public final class PatternServiceTest {

	private final PatternService patternService = new PatternService( );

	@Test
	@DisplayName ( "Patterns should be rcognized as valid" )
	public void patternsShouldBeRecognizedAsValid( ) {
		assertThat( patternService.isValidPattern( ".*" ) ).isTrue( );
		assertThat( patternService.isValidPattern( "Test" ) ).isTrue( );
		assertThat( patternService.isValidPattern( ".*Test.*" ) ).isTrue( );
		assertThat( patternService.isValidPattern( "(\\d)" ) ).isTrue( );
		assertThat( patternService.isValidPattern( "[.*]" ) ).isTrue( );
	}

	@Test
	@DisplayName ( "Patterns should be rcognized as invalid" )
	public void patternsShouldBeRecognizedAsInvalid( ) {
		assertThat( patternService.isValidPattern( "(" ) ).isFalse( );
		assertThat( patternService.isValidPattern( ")" ) ).isFalse( );
		assertThat( patternService.isValidPattern( "*" ) ).isFalse( );
		assertThat( patternService.isValidPattern( "[" ) ).isFalse( );
		assertThat( patternService.isValidPattern( null ) ).isFalse( );
	}

	@Test
	@DisplayName ( "Pattern compilation should work as expected" )
	public void patternCompilationShouldWorkAsExpected( ) {
		assertThat( patternService.compilePattern( ".*" ).matcher( "Test" ).matches( ) ).isTrue( );
		assertThat( patternService.compilePattern( "Test" ).matcher( "Test" ).matches( ) ).isTrue( );
		assertThat( patternService.compilePattern( ".*Test.*" ).matcher( "Test" ).matches( ) ).isTrue( );
		assertThat( patternService.compilePattern( "(\\\\d)" ).matcher( "Test" ).matches( ) ).isFalse( );
		assertThat( patternService.compilePattern( "\\d*" ).matcher( "123" ).matches( ) ).isTrue( );
	}

}
