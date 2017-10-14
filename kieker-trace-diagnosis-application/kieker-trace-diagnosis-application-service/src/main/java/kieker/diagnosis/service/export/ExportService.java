package kieker.diagnosis.service.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.service.ServiceBase;

@Singleton
public class ExportService extends ServiceBase {

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
