package kieker.diagnosis.application.service.export;

import kieker.diagnosis.architecture.exception.TechnicalException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.springframework.stereotype.Component;

@Component
class ExportServiceImpl implements ExportService {

	private static final char COMMENT_LINES_PREFIX = '#';
	private static final char COLUMN_SEPARATOR = ';';
	private static final String cvLineSeparator = System.getProperty( "line.separator" );

	@Override
	public void exportToCSV( final CSVData aCSVData, final File aFile ) {
		try {
			final boolean fileCreated = aFile.createNewFile( );

			if ( !fileCreated ) {
				throw new IOException( String.format( "Could not create file '%s'", aFile.getName( ) ) );
			}

			try ( final OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( aFile ), "UTF-8" ) ) {
				writer.write( COMMENT_LINES_PREFIX );

				for ( final String header : aCSVData.getHeader( ) ) {
					writer.write( header );
					writer.write( COLUMN_SEPARATOR );
				}

				writer.write( cvLineSeparator );

				for ( final String[] row : aCSVData.getRows( ) ) {
					for ( final String column : row ) {
						if ( column != null ) {
							writer.write( column );
						}
						writer.write( COLUMN_SEPARATOR );
					}
					writer.write( cvLineSeparator );
				}
			}
		} catch ( final IOException ex ) {
			throw new TechnicalException( ex );
		}
	}

}
