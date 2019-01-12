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

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import kieker.diagnosis.backend.data.MonitoringLogService;
import kieker.diagnosis.backend.data.exception.CorruptStreamException;

/**
 * Test class for the {@link AsciiFileReader}.
 *
 * @author Nils Christian Ehmke
 */
public final class AsciiFileReaderTest {

	@Test
	public void testNormalLogs( ) throws URISyntaxException, IOException, CorruptStreamException {
		final MonitoringLogService monitoringLogService = new MonitoringLogService( );
		final TemporaryRepository temporaryRepository = new TemporaryRepository( monitoringLogService );
		final AsciiFileReader asciiFileReader = new AsciiFileReader( temporaryRepository );

		final URL logDirectoryUrl = getClass( ).getResource( "/kieker-log-ascii" );
		final File logDirectory = new File( logDirectoryUrl.toURI( ) );

		asciiFileReader.readFromDirectory( logDirectory );
		temporaryRepository.finish( );

		assertThat( monitoringLogService.getTraceRoots( ), hasSize( 3 ) );
		assertThat( monitoringLogService.getAggreatedMethods( ), hasSize( 4 ) );
		assertThat( monitoringLogService.getMethods( ), hasSize( 4 ) );
		assertThat( monitoringLogService.getIgnoredRecords( ), is( 1 ) );
	}

}
