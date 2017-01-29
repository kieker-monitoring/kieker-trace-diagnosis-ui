package kieker.diagnosis.service.export;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import kieker.diagnosis.service.ServiceIfc;

public class ExportService implements ServiceIfc {

	public void exportToCSV( final CSVData aCSVData, final File aFile ) throws IOException {
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
