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

package kieker.diagnosis.service.export;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import kieker.diagnosis.architecture.exception.BusinessException;

/**
 * Test class for the {@link ExportService}.
 *
 * @author Nils Christian Ehmke
 */
public final class ExportServiceTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder( );

	@Rule
	public ExpectedException expectedException = ExpectedException.none( );

	@Test
	public void testSimpleExport( ) throws IOException, BusinessException {
		final File csvFile = temporaryFolder.newFile( );
		final CSVData csvData = new CSVData( );
		csvData.setHeader( new String[] { "Header 1", "Header 2" } );
		csvData.setValues( new String[][] { { "A1", "A2" }, { "B1", "B2" } } );

		final ExportService exportService = new ExportService( );
		exportService.exportToCSV( csvFile, csvData );

		final List<String> allLines = Files.readAllLines( csvFile.toPath( ) );
		assertThat( allLines, contains( "Header 1;Header 2", "A1;B1", "A2;B2" ) );
	}

	@Test
	public void testExportWithoutData( ) throws IOException, BusinessException {
		final File csvFile = temporaryFolder.newFile( );
		final CSVData csvData = new CSVData( );
		csvData.setHeader( new String[] { "Header 1", "Header 2" } );
		csvData.setValues( new String[][] {} );

		final ExportService exportService = new ExportService( );
		exportService.exportToCSV( csvFile, csvData );

		final List<String> allLines = Files.readAllLines( csvFile.toPath( ) );
		assertThat( allLines, contains( "Header 1;Header 2" ) );
	}

	@Test
	public void testExportOnDirectory( ) throws IOException, BusinessException {
		final ExportService exportService = new ExportService( );

		expectedException.expect( BusinessException.class );
		expectedException.expectCause( is( instanceOf( IOException.class ) ) );
		exportService.exportToCSV( temporaryFolder.getRoot( ), new CSVData( ) );
	}

}
