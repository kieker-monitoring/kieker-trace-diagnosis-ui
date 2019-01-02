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

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import kieker.diagnosis.backend.pattern.PatternService;

/**
 * Test class for the {@link PatternService}.
 *
 * @author Nils Christian Ehmke
 */
public class PatternServiceTest {

	private PatternService ivPatternService;

	@Before
	public void setUp( ) {
		final Injector injector = Guice.createInjector( );
		ivPatternService = injector.getInstance( PatternService.class );
	}

	@Test
	public void testVariousValidPattern( ) {
		assertThat( ivPatternService.isValidPattern( ".*" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( "Test" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( ".*Test.*" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( "(\\d)" ), is( true ) );
		assertThat( ivPatternService.isValidPattern( "[.*]" ), is( true ) );
	}

	@Test
	public void testVariousInvalidPattern( ) {
		assertThat( ivPatternService.isValidPattern( "(" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( ")" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( "*" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( "[" ), is( false ) );
		assertThat( ivPatternService.isValidPattern( null ), is( false ) );
	}

	@Test
	public void testCompilePatternWithVariousPattern( ) {
		assertThat( ivPatternService.compilePattern( ".*" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( ivPatternService.compilePattern( "Test" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( ivPatternService.compilePattern( ".*Test.*" ).matcher( "Test" ).matches( ), is( true ) );
		assertThat( ivPatternService.compilePattern( "(\\\\d)" ).matcher( "Test" ).matches( ), is( false ) );
		assertThat( ivPatternService.compilePattern( "\\d*" ).matcher( "123" ).matches( ), is( true ) );
	}

}
