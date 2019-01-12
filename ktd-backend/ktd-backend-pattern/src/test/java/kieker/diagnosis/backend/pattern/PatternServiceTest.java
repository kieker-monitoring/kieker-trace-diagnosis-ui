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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * This is a unit test for the {@link PatternService}.
 *
 * @author Nils Christian Ehmke
 */
public final class PatternServiceTest {

	private final PatternService patternService = new PatternService( );

	@Test
	public void patternsShouldBeRecognizedAsValid( ) {
		assertThat( patternService.isValidPattern( ".*" ), is( true ) );
		assertThat( patternService.isValidPattern( "Test" ), is( true ) );
		assertThat( patternService.isValidPattern( ".*Test.*" ), is( true ) );
		assertThat( patternService.isValidPattern( "(\\d)" ), is( true ) );
		assertThat( patternService.isValidPattern( "[.*]" ), is( true ) );
	}

	@Test
	public void patternsShouldBeRecognizedAsInvalid( ) {
		assertThat( patternService.isValidPattern( "(" ), is( false ) );
		assertThat( patternService.isValidPattern( ")" ), is( false ) );
		assertThat( patternService.isValidPattern( "*" ), is( false ) );
		assertThat( patternService.isValidPattern( "[" ), is( false ) );
		assertThat( patternService.isValidPattern( null ), is( false ) );
	}

	@Test
	public void patternCompilationShouldWorkAsExpected( ) {
		assertThat( patternService.compilePattern( ".*" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( patternService.compilePattern( "Test" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( patternService.compilePattern( ".*Test.*" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( patternService.compilePattern( "(\\\\d)" ).matcher( "Test" ).matches( ), is( false ) );
		assertThat( patternService.compilePattern( "\\d*" ).matcher( "123" ).matches( ), is( true ) );
	}

}
