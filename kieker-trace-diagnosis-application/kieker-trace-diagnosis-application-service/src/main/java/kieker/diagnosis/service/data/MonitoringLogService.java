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

package kieker.diagnosis.service.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.inject.Singleton;

import kieker.diagnosis.architecture.exception.BusinessException;
import kieker.diagnosis.architecture.exception.BusinessRuntimeException;
import kieker.diagnosis.architecture.exception.TechnicalException;
import kieker.diagnosis.architecture.service.ServiceBase;
import kieker.diagnosis.service.data.reader.AsciiFileReader;
import kieker.diagnosis.service.data.reader.BinaryFileReader;
import kieker.diagnosis.service.data.reader.Reader;
import kieker.diagnosis.service.data.reader.TemporaryRepository;

/**
 * This is the service responsible for importing monitoring logs and holding the necessary data from the import.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public class MonitoringLogService extends ServiceBase {

	private final List<MethodCall> ivTraceRoots = new ArrayList<>( );
	private final List<AggregatedMethodCall> ivAggreatedMethods = new ArrayList<>( );
	private final List<MethodCall> ivMethods = new ArrayList<>( );
	private long ivProcessDuration;
	private long ivProcessedBytes;
	private boolean dataAvailable = false;
	private int ivIgnoredRecords;
	private int ivDanglingRecords;
	private int ivIncompleteTraces;
	private String ivDirectory;

	public MonitoringLogService( ) {
		// This is one of the few services that is allowed to store a state
		super( false );
	}

	public void importMonitoringLog( final File aDirectory ) throws BusinessException {
		final long tin = System.currentTimeMillis( );

		try {
			clear( );

			// We use some helper classes to avoid having temporary fields in the service
			final TemporaryRepository temporaryRepository = new TemporaryRepository( this );

			final List<Reader> readerList = new ArrayList<>( );
			readerList.add( new BinaryFileReader( temporaryRepository ) );
			readerList.add( new AsciiFileReader( temporaryRepository ) );

			boolean directoryImported = false;

			for ( final Reader reader : readerList ) {
				if ( reader.shouldBeExecuted( aDirectory ) ) {
					reader.readFromDirectory( aDirectory );
					directoryImported = true;
				}
			}

			temporaryRepository.finish( );

			if ( !directoryImported ) {
				// No reader felt responsible for the import directory. We inform the user.
				throw new BusinessException( getLocalizedString( "errorMessageUnknownMonitoringLog" ) );
			}

			setDataAvailable( aDirectory, tin );
		} catch ( final BusinessException ex ) {
			// A business exception means, that something went wrong, but that the data is partially available
			setDataAvailable( aDirectory, tin );

			throw new BusinessRuntimeException( ex );
		} catch ( final Exception ex ) {
			throw new TechnicalException( getLocalizedString( "errorMessageImportFailed" ), ex );
		}
	}

	private void setDataAvailable( final File aDirectory, final long aTin ) {
		ivDirectory = aDirectory.getAbsolutePath( );
		dataAvailable = true;

		final long tout = System.currentTimeMillis( );
		final long duration = tout - aTin;
		ivProcessDuration = duration;
	}

	private void clear( ) {
		dataAvailable = false;
		ivTraceRoots.clear( );
		ivAggreatedMethods.clear( );
		ivMethods.clear( );
	}

	public void addTraceRoot( final MethodCall aTraceRoot ) {
		ivTraceRoots.add( aTraceRoot );
	}

	public void addAggregatedMethods( final Collection<AggregatedMethodCall> aAggregatedMethodCalls ) {
		ivAggreatedMethods.addAll( aAggregatedMethodCalls );
	}

	public void addMethods( final Collection<MethodCall> aMethodCalls ) {
		ivMethods.addAll( aMethodCalls );
	}

	public void setProcessedBytes( final long aProcessedBytes ) {
		ivProcessedBytes = aProcessedBytes;
	}

	public List<MethodCall> getTraceRoots( ) {
		return ivTraceRoots;
	}

	public List<AggregatedMethodCall> getAggreatedMethods( ) {
		return ivAggreatedMethods;
	}

	public List<MethodCall> getMethods( ) {
		return ivMethods;
	}

	public long getProcessDuration( ) {
		return ivProcessDuration;
	}

	public long getProcessedBytes( ) {
		return ivProcessedBytes;
	}

	public boolean isDataAvailable( ) {
		return dataAvailable;
	}

	public int getIgnoredRecords( ) {
		return ivIgnoredRecords;
	}

	public void setIgnoredRecords( final int aIgnoredRecords ) {
		ivIgnoredRecords = aIgnoredRecords;
	}

	public int getDanglingRecords( ) {
		return ivDanglingRecords;
	}

	public void setDanglingRecords( final int aDanglingRecords ) {
		ivDanglingRecords = aDanglingRecords;
	}

	public int getIncompleteTraces( ) {
		return ivIncompleteTraces;
	}

	public void setIncompleteTraces( final int aIncompleteTraces ) {
		ivIncompleteTraces = aIncompleteTraces;
	}

	public void setDataAvailable( final boolean aDataAvailable ) {
		dataAvailable = aDataAvailable;
	}

	public String getDirectory( ) {
		return ivDirectory;
	}

}
