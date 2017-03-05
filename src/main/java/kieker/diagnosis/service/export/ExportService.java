package kieker.diagnosis.service.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import kieker.diagnosis.service.ServiceIfc;

public class ExportService implements ServiceIfc {

	public void exportToCSV( final CSVData aCSVData, final File aFile ) throws IOException {
		final boolean fileCreated = aFile.createNewFile( );

		if ( !fileCreated ) {
			throw new IOException( String.format( "Could not create file '%s'", aFile.getName( ) ) );
		}

		try ( final OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( aFile ), "UTF-8" ) ) {
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
