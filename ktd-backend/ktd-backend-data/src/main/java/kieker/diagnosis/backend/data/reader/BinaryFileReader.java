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

package kieker.diagnosis.backend.data.reader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.carrotsearch.hppc.IntByteHashMap;
import com.carrotsearch.hppc.IntByteMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.backend.data.MethodCall;
import kieker.diagnosis.backend.monitoring.MonitoringProbe;
import kieker.diagnosis.backend.monitoring.MonitoringUtil;

/**
 * This is a reader to import files written with Kieker's binary file writer. It exchanges readability and
 * maintainability for performance and reduced memory consumption.
 *
 * @author Nils Christian Ehmke
 */
public final class BinaryFileReader extends Reader {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BinaryFileReader.class.getName( ) );

	private final IntByteMap ignoredRecordsSizeMap = new IntByteHashMap( );
	private IntObjectMap<String> ivStringMapping;

	private int ivBeforeOperationEventKey;
	private int ivAfterOperationEventKey;
	private int ivAfterOperationFailedEventKey;
	private int ivTraceMetadataKey;
	private int ivKiekerMetadataRecordKey;

	public BinaryFileReader( final TemporaryRepository aTemporaryRepository ) {
		super( aTemporaryRepository );
	}

	@Override
	public void readFromDirectory( final File aDirectory ) throws IOException {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "readFromDirectory(java.io.File)" );

		try {
			final List<File> directoriesToBeRead = findDirectoriesToBeRead( aDirectory );

			for ( final File directoryToBeRead : directoriesToBeRead ) {
				readNonRecursiveFromDirectory( directoryToBeRead );
			}

		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void readNonRecursiveFromDirectory( final File aDirectory ) throws IOException {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "readNonRecursiveFromDirectory(java.io.File)" );

		try {
			getTemporaryRepository( ).clearBeforeNextDirectory( );
			ignoredRecordsSizeMap.clear( );

			ivStringMapping = readMappingFile( aDirectory );
			extractImportKeysFromMapping( );

			final File[] binaryFiles = findFilesWithExtension( aDirectory, ".bin" );
			for ( final File binaryFile : binaryFiles ) {
				readBinaryFile( binaryFile.toPath( ) );
			}

		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void extractImportKeysFromMapping( ) {
		ivBeforeOperationEventKey = -1;
		ivAfterOperationEventKey = -1;
		ivAfterOperationFailedEventKey = -1;
		ivTraceMetadataKey = -1;
		ivKiekerMetadataRecordKey = -1;

		ivStringMapping.forEach( (Consumer<IntObjectCursor<String>>) aCursor -> {
			final int key = aCursor.key;
			final String value = aCursor.value;

			if ( ivBeforeOperationEventKey == -1 && BeforeOperationEvent.class.getName( ).equals( value ) ) {
				ivBeforeOperationEventKey = key;
			} else if ( ivAfterOperationEventKey == -1 && AfterOperationEvent.class.getName( ).equals( value ) ) {
				ivAfterOperationEventKey = key;
			} else if ( ivAfterOperationFailedEventKey == -1 && AfterOperationFailedEvent.class.getName( ).equals( value ) ) {
				ivAfterOperationFailedEventKey = key;
			} else if ( ivTraceMetadataKey == -1 && TraceMetadata.class.getName( ).equals( value ) ) {
				ivTraceMetadataKey = key;
			} else if ( ivKiekerMetadataRecordKey == -1 && KiekerMetadataRecord.class.getName( ).equals( value ) ) {
				ivKiekerMetadataRecordKey = key;
			}
		} );
	}

	private void readBinaryFile( final Path aBinaryFile ) throws IOException {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "readBinaryFile(java.nio.file.Path)" );

		try {
			final byte[] binaryContent = Files.readAllBytes( aBinaryFile );
			final ByteBuffer byteBuffer = ByteBuffer.wrap( binaryContent );

			try {
				while ( byteBuffer.hasRemaining( ) ) {
					final int recordKey = byteBuffer.getInt( );
					skipBytes( (byte) 8, byteBuffer ); // Ignore the logging timestamp

					if ( recordKey == ivBeforeOperationEventKey ) {
						readBeforeOperationEvent( byteBuffer );
					} else if ( recordKey == ivAfterOperationEventKey ) {
						readAfterOperationEvent( byteBuffer );
					} else if ( recordKey == ivAfterOperationFailedEventKey ) {
						readAfterOperationFailedEvent( byteBuffer );
					} else if ( recordKey == ivTraceMetadataKey ) {
						readTraceMetadata( byteBuffer );
					} else if ( recordKey == ivKiekerMetadataRecordKey ) {
						readKiekerMetadataRecord( byteBuffer );
					} else {
						// Expensive case. We have to find out which record we are dealing with and skip it
						readUnknownRecord( byteBuffer, recordKey );
					}
				}
			} catch ( final BufferUnderflowException | IllegalArgumentException ex ) {
				// The stream is incomplete. We still want to terminate the whole import in a useful manner.
				getTemporaryRepository( ).processException( ex );
			}

			getTemporaryRepository( ).processProcessedBytes( binaryContent.length );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void skipBytes( final byte aCountBytes, final ByteBuffer aByteBuffer ) {
		aByteBuffer.position( aByteBuffer.position( ) + aCountBytes );
	}

	private void readBeforeOperationEvent( final ByteBuffer aByteBuffer ) {
		final long timestamp = aByteBuffer.getLong( ); // Timestamp
		final long traceId = aByteBuffer.getLong( ); // Trace Id
		skipBytes( (byte) ( 3 * 4 ), aByteBuffer ); // Ignore order index, method name and class name

		getTemporaryRepository( ).processBeforeOperationEvent( timestamp, traceId );
	}

	private MethodCall readAfterOperationEvent( final ByteBuffer aByteBuffer ) {
		final long timestamp = aByteBuffer.getLong( ); // Timestamp
		final long traceId = aByteBuffer.getLong( ); // Trace Id
		skipBytes( (byte) 4, aByteBuffer ); // Ignore order index
		final String methodName = ivStringMapping.get( aByteBuffer.getInt( ) ); // Method name
		final String clazz = ivStringMapping.get( aByteBuffer.getInt( ) ); // Class name

		return getTemporaryRepository( ).processAfterOperationEvent( timestamp, traceId, methodName, clazz );
	}

	private void readAfterOperationFailedEvent( final ByteBuffer aByteBuffer ) {
		final MethodCall lastMethodCall = readAfterOperationEvent( aByteBuffer );

		final String exception = ivStringMapping.get( aByteBuffer.getInt( ) );

		// This can happen if the data is incomplete and we have a method call, but not a trace record

		if ( lastMethodCall != null ) {
			lastMethodCall.setException( exception );
		}
	}

	private void readTraceMetadata( final ByteBuffer aByteBuffer ) {
		final long traceId = aByteBuffer.getLong( );
		skipBytes( (byte) ( 8 + 4 ), aByteBuffer ); // Ignore thread id and session id

		final String host = ivStringMapping.get( aByteBuffer.getInt( ) ); // Hostname
		skipBytes( (byte) ( 8 + 4 ), aByteBuffer ); // Ignore parent trace Id and parent order Id

		getTemporaryRepository( ).processTraceMetadata( traceId, host );
	}

	private void readKiekerMetadataRecord( final ByteBuffer aByteBuffer ) {
		skipBytes( (byte) ( 4 * 4 + 1 + 8 ), aByteBuffer ); // Ignore a lot of fields...
		final String timeUnitName = ivStringMapping.get( aByteBuffer.getInt( ) ); // Time unit
		skipBytes( (byte) 8, aByteBuffer ); // Ignore the number of records

		getTemporaryRepository( ).processSourceTimeUnit( timeUnitName );
	}

	private void readUnknownRecord( final ByteBuffer aByteBuffer, final int aRecordKey ) {
		final byte size;

		if ( !ignoredRecordsSizeMap.containsKey( aRecordKey ) ) {
			final String recordName = ivStringMapping.get( aRecordKey );
			try {
				final Class<?> recordClass = Class.forName( recordName );
				final Field sizeField = recordClass.getDeclaredField( "SIZE" );
				size = (byte) (int) sizeField.get( null );

				ignoredRecordsSizeMap.put( aRecordKey, size );
			} catch ( final Exception ex ) {
				// We have no chance. We cannot skip the record, as we don't know its size.
				throw new RuntimeException( String.format( RESOURCE_BUNDLE.getString( "errorMessageUnknownRecord" ), recordName ), ex );
			}
		} else {
			size = ignoredRecordsSizeMap.get( aRecordKey );
		}

		getTemporaryRepository( ).processIgnoredRecord( );
		skipBytes( size, aByteBuffer );
	}

	@Override
	public boolean shouldBeExecuted( final File aDirectory ) throws IOException {
		final List<File> directoriesToBeRead = findDirectoriesToBeRead( aDirectory );
		return !directoriesToBeRead.isEmpty( );
	}

	private List<File> findDirectoriesToBeRead( final File aDirectory ) throws IOException {
		return findDirectoriesContainingFilesWithExtensions( aDirectory, ".map", ".bin" );
	}

}
