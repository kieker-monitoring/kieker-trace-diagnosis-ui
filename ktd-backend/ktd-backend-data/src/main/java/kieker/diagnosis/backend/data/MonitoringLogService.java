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

package kieker.diagnosis.backend.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.exception.BusinessException;
import kieker.diagnosis.backend.base.exception.BusinessRuntimeException;
import kieker.diagnosis.backend.base.exception.TechnicalException;
import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.reader.AsciiFileReader;
import kieker.diagnosis.backend.data.reader.BinaryFileReader;
import kieker.diagnosis.backend.data.reader.Reader;
import kieker.diagnosis.backend.data.reader.TemporaryRepository;

/**
 * This is the service responsible for importing monitoring logs and holding the necessary data from the import.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MonitoringLogService implements Service {

	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle( MonitoringLogService.class.getName( ) );

	private final List<MethodCall> traceRoots = new ArrayList<>( );
	private final List<AggregatedMethodCall> aggreatedMethods = new ArrayList<>( );
	private final List<MethodCall> methods = new ArrayList<>( );
	private long processDuration;
	private long processedBytes;
	private boolean dataAvailable = false;
	private int ignoredRecords;
	private int danglingRecords;
	private int incompleteTraces;
	private String directory;

	public void importMonitoringLog( final File directoryOrFile, final ImportType type ) {
		final long tin = System.currentTimeMillis( );

		File directory = null;
		try {
			clear( );

			directory = extractIfNecessary( directoryOrFile, type );

			// We use some helper classes to avoid having temporary fields in the service
			final TemporaryRepository temporaryRepository = new TemporaryRepository( this );

			final List<Reader> readerList = new ArrayList<>( );
			readerList.add( new BinaryFileReader( temporaryRepository ) );
			readerList.add( new AsciiFileReader( temporaryRepository ) );

			boolean directoryImported = false;

			for ( final Reader reader : readerList ) {
				if ( reader.shouldBeExecuted( directory ) ) {
					reader.readFromDirectory( directory );
					directoryImported = true;
				}
			}

			temporaryRepository.finish( );

			if ( !directoryImported ) {
				// No reader felt responsible for the import directory. We inform the user.
				throw new BusinessException( RESOURCES.getString( "errorMessageUnknownMonitoringLog" ) );
			}

			if ( ignoredRecords > 0 && traceRoots.size( ) == 0 ) {
				// No traces have been reconstructed and records have been ignored. We inform the user.
				final String msg = String.format( RESOURCES.getString( "errorMessageNoTraceAndRecordsIgnored" ), ignoredRecords );
				throw new BusinessException( msg );
			}

			setDataAvailable( directoryOrFile, tin );
		} catch ( final BusinessException ex ) {
			// A business exception means, that something went wrong, but that the data is partially available
			setDataAvailable( directoryOrFile, tin );

			throw new BusinessRuntimeException( ex );
		} catch ( final Exception ex ) {
			throw new TechnicalException( RESOURCES.getString( "errorMessageImportFailed" ), ex );
		} finally {
			// If necessary delete the temporary directory
			if ( type == ImportType.ZIP_FILE && directory != null ) {
				deleteDirectory( directory );
			}
		}
	}

	private File extractIfNecessary( final File directoryOrFile, final ImportType type ) throws ZipException, IOException {
		final File directory;
		switch ( type ) {
			case DIRECTORY:
				directory = directoryOrFile;
			break;
			case ZIP_FILE:
				directory = extractZIPFile( directoryOrFile );
			break;
			default:
				// Should not happen
				directory = null;
			break;

		}
		return directory;
	}

	private File extractZIPFile( final File file ) throws ZipException, IOException {
		final File tempDirectory = Files.createTempDir( );

		try ( final ZipFile zipFile = new ZipFile( file ) ) {
			final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries( );
			while ( zipEntries.hasMoreElements( ) ) {
				final ZipEntry zipEntry = zipEntries.nextElement( );
				try ( InputStream inputStream = zipFile.getInputStream( zipEntry ) ) {
					try ( final FileOutputStream outputStream = new FileOutputStream( new File( tempDirectory, zipEntry.getName( ) ) ) ) {
						ByteStreams.copy( inputStream, outputStream );
					}
				}
			}
		}

		return tempDirectory;
	}

	private void deleteDirectory( final File directory ) {
		final File[] children = directory.listFiles( );
		if ( children != null ) {
			for ( final File child : children ) {
				deleteDirectory( child );
			}
		}

		directory.delete( );
	}

	private void setDataAvailable( final File inputDirectory, final long tin ) {
		directory = inputDirectory.getAbsolutePath( );
		dataAvailable = true;

		final long tout = System.currentTimeMillis( );
		final long duration = tout - tin;
		processDuration = duration;
	}

	private void clear( ) {
		dataAvailable = false;
		traceRoots.clear( );
		aggreatedMethods.clear( );
		methods.clear( );
	}

	public void addTraceRoot( final MethodCall traceRoot ) {
		traceRoots.add( traceRoot );
	}

	public void addAggregatedMethods( final Collection<AggregatedMethodCall> aggregatedMethodCalls ) {
		aggreatedMethods.addAll( aggregatedMethodCalls );
	}

	public void addMethods( final Collection<MethodCall> methodCalls ) {
		methods.addAll( methodCalls );
	}

	public void setProcessedBytes( final long processedBytes ) {
		this.processedBytes = processedBytes;
	}

	public List<MethodCall> getTraceRoots( ) {
		return traceRoots;
	}

	public List<AggregatedMethodCall> getAggreatedMethods( ) {
		return aggreatedMethods;
	}

	public List<MethodCall> getMethods( ) {
		return methods;
	}

	public long getProcessDuration( ) {
		return processDuration;
	}

	public long getProcessedBytes( ) {
		return processedBytes;
	}

	public boolean isDataAvailable( ) {
		return dataAvailable;
	}

	public int getIgnoredRecords( ) {
		return ignoredRecords;
	}

	public void setIgnoredRecords( final int ignoredRecords ) {
		this.ignoredRecords = ignoredRecords;
	}

	public int getDanglingRecords( ) {
		return danglingRecords;
	}

	public void setDanglingRecords( final int danglingRecords ) {
		this.danglingRecords = danglingRecords;
	}

	public int getIncompleteTraces( ) {
		return incompleteTraces;
	}

	public void setIncompleteTraces( final int incompleteTraces ) {
		this.incompleteTraces = incompleteTraces;
	}

	public void setDataAvailable( final boolean dataAvailable ) {
		this.dataAvailable = dataAvailable;
	}

	public String getDirectory( ) {
		return directory;
	}

}
