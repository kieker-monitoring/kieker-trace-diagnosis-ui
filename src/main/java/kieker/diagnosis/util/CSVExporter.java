/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public final class CSVExporter {

	private CSVExporter( ) {
	}

	public static void exportToCSV( final CSVData aCSVData, final File aFile ) throws IOException {
		aFile.createNewFile( );

		try ( final FileWriter writer = new FileWriter( aFile ) ) {
			writer.write( "#" );

			for ( final String header : aCSVData.getHeader( ) ) {
				writer.write( header );
				writer.write( ";" );
			}
			writer.write( "\n" );

			for ( final String[] row : aCSVData.getRows( ) ) {
				for ( final String column : row ) {
					if ( column != null ) {
						writer.write( column );
					}
					writer.write( ";" );
				}
				writer.write( "\n" );
			}
		}
	}

}
