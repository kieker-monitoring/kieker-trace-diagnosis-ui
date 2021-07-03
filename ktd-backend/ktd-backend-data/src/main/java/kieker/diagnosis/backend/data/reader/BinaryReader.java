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

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.carrotsearch.hppc.IntByteHashMap;
import com.carrotsearch.hppc.IntByteMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import kieker.common.record.flow.trace.ApplicationTraceMetadata;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.backend.data.MethodCall;

final class BinaryReader {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( BinaryReader.class.getName( ) );

	private final IntByteMap ignoredRecordsSizeMap = new IntByteHashMap( );

	private int beforeOperationEventKey;
	private int afterOperationEventKey;
	private int afterOperationFailedEventKey;
	private int traceMetadataKey;
	private int kiekerMetadataRecordKey;

	private final IntObjectMap<String> mapping;
	private final Repository repository;

	BinaryReader( final IntObjectMap<String> mapping, final Repository repository ) {
		this.mapping = mapping;
		this.repository = repository;
		findImportantKeysInMapping( );
	}

	private void findImportantKeysInMapping( ) {
		beforeOperationEventKey = -1;
		afterOperationEventKey = -1;
		afterOperationFailedEventKey = -1;
		traceMetadataKey = -1;
		kiekerMetadataRecordKey = -1;

		mapping.forEach( (Consumer<IntObjectCursor<String>>) aCursor -> {
			final int key = aCursor.key;
			final String value = aCursor.value;

			if ( beforeOperationEventKey == -1 && BeforeOperationEvent.class.getName( ).equals( value ) ) {
				beforeOperationEventKey = key;
			} else if ( afterOperationEventKey == -1 && AfterOperationEvent.class.getName( ).equals( value ) ) {
				afterOperationEventKey = key;
			} else if ( afterOperationFailedEventKey == -1 && AfterOperationFailedEvent.class.getName( ).equals( value ) ) {
				afterOperationFailedEventKey = key;
			} else if ( traceMetadataKey == -1 && ( TraceMetadata.class.getName( ).equals( value ) ||  ApplicationTraceMetadata.class.getName( ).equals( value ) ) ) {
				traceMetadataKey = key;
			} else if ( kiekerMetadataRecordKey == -1 && KiekerMetadataRecord.class.getName( ).equals( value ) ) {
				kiekerMetadataRecordKey = key;
			}
		} );
	}

	public void readFromFile( final Path binaryFile, final IntObjectMap<String> mapping ) throws IOException {
		final byte[] binaryContent = Files.readAllBytes( binaryFile );
		final ByteBuffer byteBuffer = ByteBuffer.wrap( binaryContent );

		try {
			while ( byteBuffer.hasRemaining( ) ) {
				final int recordKey = byteBuffer.getInt( );
				skipBytes( (byte) 8, byteBuffer ); // Ignore the logging timestamp

				if ( recordKey == beforeOperationEventKey ) {
					readBeforeOperationEvent( byteBuffer );
				} else if ( recordKey == afterOperationEventKey ) {
					readAfterOperationEvent( byteBuffer );
				} else if ( recordKey == afterOperationFailedEventKey ) {
					readAfterOperationFailedEvent( byteBuffer );
				} else if ( recordKey == traceMetadataKey ) {
					readTraceMetadata( byteBuffer );
				} else if ( recordKey == kiekerMetadataRecordKey ) {
					readKiekerMetadataRecord( byteBuffer );
				} else {
					// Expensive case. We have to find out which record we are dealing with and skip it
					readUnknownRecord( byteBuffer, recordKey );
				}
			}
		} catch ( final BufferUnderflowException | IllegalArgumentException ex ) {
			// The stream is incomplete. We still want to terminate the whole import in a useful manner.
			repository.processException( ex );
		}

		repository.processProcessedBytes( binaryContent.length );
	}

	private void skipBytes( final byte aCountBytes, final ByteBuffer aByteBuffer ) {
		aByteBuffer.position( aByteBuffer.position( ) + aCountBytes );
	}

	private void readBeforeOperationEvent( final ByteBuffer aByteBuffer ) {
		final long timestamp = aByteBuffer.getLong( ); // Timestamp
		final long traceId = aByteBuffer.getLong( ); // Trace Id
		skipBytes( (byte) ( 3 * 4 ), aByteBuffer ); // Ignore order index, method name and class name

		repository.processBeforeOperationEvent( timestamp, traceId );
	}

	private MethodCall readAfterOperationEvent( final ByteBuffer aByteBuffer ) {
		final long timestamp = aByteBuffer.getLong( ); // Timestamp
		final long traceId = aByteBuffer.getLong( ); // Trace Id
		skipBytes( (byte) 4, aByteBuffer ); // Ignore order index
		final String methodName = mapping.get( aByteBuffer.getInt( ) ); // Method name
		final String clazz = mapping.get( aByteBuffer.getInt( ) ); // Class name

		return repository.processAfterOperationEvent( timestamp, traceId, methodName, clazz );
	}

	private void readAfterOperationFailedEvent( final ByteBuffer aByteBuffer ) {
		final MethodCall lastMethodCall = readAfterOperationEvent( aByteBuffer );

		final String exception = mapping.get( aByteBuffer.getInt( ) );

		// This can happen if the data is incomplete and we have a method call, but not a trace record

		if ( lastMethodCall != null ) {
			lastMethodCall.setException( exception );
		}
	}

	private void readTraceMetadata( final ByteBuffer aByteBuffer ) {
		final long traceId = aByteBuffer.getLong( );
		skipBytes( (byte) ( 8 + 4 ), aByteBuffer ); // Ignore thread id and session id

		final String host = mapping.get( aByteBuffer.getInt( ) ); // Hostname
		skipBytes( (byte) ( 8 + 4 ), aByteBuffer ); // Ignore parent trace Id and parent order Id

		repository.processTraceMetadata( traceId, host );
	}

	private void readKiekerMetadataRecord( final ByteBuffer aByteBuffer ) {
		skipBytes( (byte) ( 4 * 4 + 1 + 8 ), aByteBuffer ); // Ignore a lot of fields...
		final String timeUnitName = mapping.get( aByteBuffer.getInt( ) ); // Time unit
		skipBytes( (byte) 8, aByteBuffer ); // Ignore the number of records

		repository.processSourceTimeUnit( timeUnitName );
	}

	private void readUnknownRecord( final ByteBuffer aByteBuffer, final int aRecordKey ) {
		final byte size;

		if ( !ignoredRecordsSizeMap.containsKey( aRecordKey ) ) {
			final String recordName = mapping.get( aRecordKey );
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

		repository.processIgnoredRecord( );
		skipBytes( size, aByteBuffer );
	}

}
