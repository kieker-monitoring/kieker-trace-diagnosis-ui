package kieker.diagnosis.application.service.export;

import java.io.File;

public interface ExportService {

	public void exportToCSV( CSVData aCSVData, File aFile );

}
