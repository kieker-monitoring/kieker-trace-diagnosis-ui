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

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;

/**
 * This is the service responsible for exporting data from the application into other files and formats.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class ExportService implements Service {

	/**
	 * Exports the given data into the given file as CSV data. The columns are separated with a semicolon.
	 *
	 * @param path
	 *            The path in which the data should be written.
	 * @param csvData
	 *            The CSV data to be written.
	 *
	 * @throws IOException
	 *             If something went wrong while writing the data.
	 */
	public void exportToCSV( final Path path, final CSVData csvData ) throws IOException {
		try ( Writer writer = Files.newBufferedWriter( path ) ) {
			writeHeader( writer, csvData.getHeaders( ) );
			writeValues( writer, csvData.getRows( ) );
		}
	}

	private void writeHeader( final Writer writer, final List<String> headers ) throws IOException {
		final String headerLine = String.join( ";", headers );
		writer.append( headerLine ).append( "\n" );
	}

	private void writeValues( final Writer writer, final List<List<String>> rows ) throws IOException {
		final List<String> rowLines = rows
				.stream( )
				.map( row -> String.join( ";", row ) )
				.collect( Collectors.toList( ) );
		final String csvString = String.join( "\n", rowLines );
		writer.append( csvString );
	}

}
