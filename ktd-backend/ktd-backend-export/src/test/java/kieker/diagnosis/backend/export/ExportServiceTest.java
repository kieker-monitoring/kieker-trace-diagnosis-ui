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

package kieker.diagnosis.backend.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

/**
 * This is a unit test for {@link ExportService}.
 *
 * @author Nils Christian Ehmke
 */
public final class ExportServiceTest {

	@Test
	@ExtendWith ( TempDirectory.class )
	public void testSimpleExport( @TempDir final Path tempDir ) throws IOException {
		final Path csvFile = tempDir.resolve( "output.csv" );
		final CSVData csvData = new CSVData( );
		csvData.addHeader( "Header 1" );
		csvData.addHeader( "Header 2" );
		csvData.addRow( Arrays.asList( "A1", "B1" ) );
		csvData.addRow( Arrays.asList( "A2", "B2" ) );

		final ExportService exportService = new ExportService( );
		exportService.exportToCSV( csvFile, csvData );

		final List<String> allLines = Files.readAllLines( csvFile );
		assertThat( allLines ).containsExactly( "Header 1;Header 2", "A1;B1", "A2;B2" );
	}

	@Test
	@ExtendWith ( TempDirectory.class )
	public void testExportWithoutData( @TempDir final Path tempDir ) throws IOException {
		final Path csvFile = tempDir.resolve( "output.csv" );
		final CSVData csvData = new CSVData( );
		csvData.addHeader( "Header 1" );
		csvData.addHeader( "Header 2" );

		final ExportService exportService = new ExportService( );
		exportService.exportToCSV( csvFile, csvData );

		final List<String> allLines = Files.readAllLines( csvFile );
		assertThat( allLines ).containsExactly( "Header 1;Header 2" );
	}

	@Test
	@ExtendWith ( TempDirectory.class )
	public void testExportOnDirectory( @TempDir final Path tempDir ) throws IOException {
		final ExportService exportService = new ExportService( );

		assertThrows( IOException.class, ( ) -> exportService.exportToCSV( tempDir, new CSVData( ) ) );
	}

}
