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

package kieker.diagnosis.backend.data.reader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kieker.diagnosis.backend.data.exception.CorruptStreamException;
import kieker.diagnosis.backend.data.exception.ImportFailedException;

/**
 * Test class for the {@link AsciiReader}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for AsciiReader" )
public final class AsciiReaderTest {

	@Test
	@DisplayName ( "Test with normal logs (1.13)" )
	public void testNormalLogs113( ) throws URISyntaxException, IOException, CorruptStreamException, ImportFailedException {
		final Repository repository = new Repository( );
		final Reader reader = new Reader( );

		final URL logDirectoryUrl = getClass( ).getResource( "/kieker-log-ascii-1.13" );
		final Path logDirectory = Paths.get( logDirectoryUrl.toURI( ) );

		reader.readRecursiveFromDirectory( logDirectory, repository );

		assertThat( repository.getTraceRoots( ) ).hasSize( 3 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 4 );
		assertThat( repository.getMethods( ) ).hasSize( 4 );
		assertThat( repository.getIgnoredRecords( ) ).isEqualTo( 1 );
	}
	
	@Test
	@DisplayName ( "Test with normal logs (1.14)" )
	public void testNormalLogs114( ) throws URISyntaxException, IOException, CorruptStreamException, ImportFailedException {
		final Repository repository = new Repository( );
		final Reader reader = new Reader( );

		final URL logDirectoryUrl = getClass( ).getResource( "/kieker-log-ascii-1.14" );
		final Path logDirectory = Paths.get( logDirectoryUrl.toURI( ) );

		reader.readRecursiveFromDirectory( logDirectory, repository );

		assertThat( repository.getTraceRoots( ) ).hasSize( 3 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 4 );
		assertThat( repository.getMethods( ) ).hasSize( 4 );
		assertThat( repository.getIgnoredRecords( ) ).isEqualTo( 1 );
	}
	
}
