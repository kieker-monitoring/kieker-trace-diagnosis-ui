package kieker.diagnosis.application.service.export;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import kieker.diagnosis.application.service.ServiceTestConfiguration;
import kieker.diagnosis.architecture.exception.TechnicalException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.io.Files;

@RunWith ( SpringRunner.class )
@ContextConfiguration ( classes = ServiceTestConfiguration.class )
public class ExportServiceTest {

	@Rule
	public TemporaryFolder ivTemporaryFolder = new TemporaryFolder( );

	@Rule
	public ExpectedException ivExpectedException = ExpectedException.none( );

	@Autowired
	private ExportService ivExportService;

	@Test
	public void csvExportShouldWork( ) throws IOException {
		final CSVData csvData = new CSVData( new String[] { "H1", "H2" }, new String[][] { { "R1C1", "R1C2" }, { "R2C1", "R2C2" } } );
		final File exportFile = new File( ivTemporaryFolder.getRoot( ), "test.csv" );
		ivExportService.exportToCSV( csvData, exportFile );

		final List<String> writtenLines = Files.readLines( exportFile, Charset.forName( "UTF-8" ) );

		assertThat( writtenLines, hasSize( 3 ) );
		assertThat( writtenLines.get( 0 ), is( "#H1;H2;" ) );
		assertThat( writtenLines.get( 1 ), is( "R1C1;R1C2;" ) );
		assertThat( writtenLines.get( 2 ), is( "R2C1;R2C2;" ) );
	}

	@Test
	public void ioExceptionShouldThrowTechnicalException( ) throws IOException {
		final File exportFile = ivTemporaryFolder.newFile( );
		ivExpectedException.expect( TechnicalException.class );
		ivExportService.exportToCSV( null, exportFile );
	}

}
