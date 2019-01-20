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
 * Test class for the {@link BinaryReader}.
 *
 * @author Nils Christian Ehmke
 */
@DisplayName ( "Unit-Test for BinaryReader" )
public final class BinaryReaderTest {

	@Test
	@DisplayName ( "Test with normal logs" )
	public void testNormalLogs( ) throws URISyntaxException, IOException, CorruptStreamException, ImportFailedException {
		final Repository repository = new Repository( );
		final Reader reader = new Reader( );

		final URL logDirectoryUrl = getClass( ).getResource( "/kieker-log-binary" );
		final Path logDirectory = Paths.get( logDirectoryUrl.toURI( ) );

		reader.readRecursiveFromDirectory( logDirectory, repository );

		assertThat( repository.getTraceRoots( ) ).hasSize( 2 );
		assertThat( repository.getAggreatedMethods( ) ).hasSize( 3 );
		assertThat( repository.getMethods( ) ).hasSize( 3 );
	}

}
