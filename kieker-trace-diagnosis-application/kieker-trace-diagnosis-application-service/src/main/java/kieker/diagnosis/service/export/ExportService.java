/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.service.ServiceBase;

/**
 * This is the service responsible for exporting data from the application into other files and formats.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class ExportService extends ServiceBase {

	/**
	 * Exports the given data into the given file as CSV data. The columns are separated with a semicolon.
	 *
	 * @param aFile
	 *            The file in which the data should be written.
	 * @param aCsvData
	 *            The CSV data to be written.
	 *
	 * @throws BusinessException
	 *             If an I/O error occured while trying to export the data.
	 */
	public void exportToCSV( final File aFile, final CSVData aCsvData ) throws BusinessException {
		try ( final FileWriter fileWriter = new FileWriter( aFile ) ) {
			final String[] headers = aCsvData.getHeader( );
			final int columnsSize = headers.length;

			// The header first
			for ( int columnIndex = 0; columnIndex < columnsSize; columnIndex++ ) {
				if ( columnIndex != 0 ) {
					fileWriter.append( ";" );
				}
				fileWriter.append( headers[columnIndex] );
			}
			fileWriter.append( "\n" );

			// Now the values
			final String[][] values = aCsvData.getValues( );
			final int valuesSize = values.length > 0 ? values[0].length : 0;
			for ( int rowIndex = 0; rowIndex < valuesSize; rowIndex++ ) {
				for ( int columnIndex = 0; columnIndex < columnsSize; columnIndex++ ) {
					if ( columnIndex != 0 ) {
						fileWriter.append( ";" );
					}
					fileWriter.append( values[columnIndex][rowIndex] );
				}
				fileWriter.append( "\n" );
			}
		} catch ( final IOException ex ) {
			throw new BusinessException( ex );
		}
	}

}
