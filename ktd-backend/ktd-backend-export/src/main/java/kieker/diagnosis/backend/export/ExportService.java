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

package kieker.diagnosis.backend.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.exception.BusinessException;
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
	 * @param aFile
	 *            The file in which the data should be written.
	 * @param aCsvData
	 *            The CSV data to be written.
	 *
	 * @throws BusinessException
	 *             If an I/O error occurred while trying to export the data.
	 */
	public void exportToCSV( final File file, final CSVData csvData ) throws BusinessException {
		try ( final FileWriter fileWriter = new FileWriter( file ) ) {
			writeHeader( fileWriter, csvData.getHeader( ) );
			writeValues( fileWriter, csvData.getValues( ), csvData.getHeader( ) );
		} catch ( final IOException ex ) {
			throw new BusinessException( ex );
		}
	}

	private void writeHeader( final FileWriter fileWriter, final String[] header ) throws IOException {
		final String headerLine = String.join( ";", header );
		fileWriter.append( headerLine ).append( "\n" );
	}

	private void writeValues( final FileWriter fileWriter, final String[][] values, final String[] header ) throws IOException {
		final int valuesSize = values.length > 0 ? values[0].length : 0;
		for ( int rowIndex = 0; rowIndex < valuesSize; rowIndex++ ) {
			for ( int columnIndex = 0; columnIndex < header.length; columnIndex++ ) {
				if ( columnIndex != 0 ) {
					fileWriter.append( ";" );
				}
				fileWriter.append( values[columnIndex][rowIndex] );
			}
			fileWriter.append( "\n" );
		}
	}

}
