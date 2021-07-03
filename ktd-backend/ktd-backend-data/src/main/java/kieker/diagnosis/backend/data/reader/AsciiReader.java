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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;

import kieker.common.record.flow.trace.ApplicationTraceMetadata;
import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.backend.data.MethodCall;

final class AsciiReader {

	private static final Pattern ASCII_FILE_ENTRY_PATTERN = Pattern.compile( "\\$(\\d*);\\d*;(.*)" );
	private static final Pattern ASCII_FILE_BEFORE_OPERATION_EVENT_PATTERN = Pattern.compile( "(\\d*);(-?\\d*).*" );
	private static final Pattern ASCII_FILE_AFTER_OPERATION_EVENT_PATTERN = Pattern.compile( "(\\d*);(-?\\d*);\\d*;([^;]*);([^;]*)" );
	private static final Pattern ASCII_FILE_AFTER_OPERATION_FAILED_EVENT_PATTERN = Pattern.compile( "(\\d*);(-?\\d*);\\d*;([^;]*);([^;]*);(.*)" );
	private static final Pattern ASCII_FILE_TRACE_METADATA_PATTERN = Pattern.compile( "(-?\\d*);\\d*;[^;]*;([^;]*).*" );
	private static final Pattern ASCII_FILE_KIEKER_METADATA_RECORD_PATTERN = Pattern.compile( "[^;]*;[^;]*;[^;]*;[^;]*;[^;]*;[^;]*;([^;]*).*" );

	private int beforeOperationEventKey;
	private int afterOperationEventKey;
	private int afterOperationFailedEventKey;
	private int traceMetadataKey;
	private int kiekerMetadataRecordKey;

	private final IntObjectMap<String> mapping;
	private final Repository repository;

	AsciiReader( final IntObjectMap<String> mapping, final Repository repository ) {
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

	public void readFromFile( final Path asciiFile ) throws IOException {
		final List<String> lines = Files.readAllLines( asciiFile, Charset.forName( "UTF-8" ) );

		for ( final String line : lines ) {
			final Matcher lineMatcher = ASCII_FILE_ENTRY_PATTERN.matcher( line );
			if ( lineMatcher.matches( ) ) {
				final String recordKeyStr = lineMatcher.group( 1 );
				final String remainingLine = lineMatcher.group( 2 );

				final int recordKey = Integer.parseInt( recordKeyStr );

				if ( recordKey == beforeOperationEventKey ) {
					readBeforeOperationEvent( remainingLine );
				} else if ( recordKey == afterOperationEventKey ) {
					readAfterOperationEvent( remainingLine );
				} else if ( recordKey == afterOperationFailedEventKey ) {
					readAfterOperationFailedEvent( remainingLine );
				} else if ( recordKey == traceMetadataKey ) {
					readTraceMetadata( remainingLine );
				} else if ( recordKey == kiekerMetadataRecordKey ) {
					readKiekerMetadataRecord( remainingLine );
				} else {
					// Skip the line
					repository.processIgnoredRecord( );
				}
			}
		}

		repository.processProcessedBytes( Files.size( asciiFile ) );
	}

	private void readBeforeOperationEvent( final String aAsciiContent ) {
		final Matcher matcher = ASCII_FILE_BEFORE_OPERATION_EVENT_PATTERN.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			final long timestamp = Long.parseLong( matcher.group( 1 ) ); // Timestamp
			final long traceId = Long.parseLong( matcher.group( 2 ) ); // Trace Id
			// The rest is ignored

			repository.processBeforeOperationEvent( timestamp, traceId );
		}
	}

	private void readAfterOperationEvent( final String aAsciiContent ) {
		final Matcher matcher = ASCII_FILE_AFTER_OPERATION_EVENT_PATTERN.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			// Logging timestamp is ignored
			final long timestamp = Long.parseLong( matcher.group( 1 ) ); // Timestamp
			final long traceId = Long.parseLong( matcher.group( 2 ) ); // Trace Id

			// Order index is ignored
			final String methodName = matcher.group( 3 ); // Method name
			final String clazz = matcher.group( 4 ); // Class name

			repository.processAfterOperationEvent( timestamp, traceId, methodName, clazz );
		}
	}

	private void readAfterOperationFailedEvent( final String aAsciiContent ) {
		final Matcher matcher = ASCII_FILE_AFTER_OPERATION_FAILED_EVENT_PATTERN.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			// Logging timestamp is ignored
			final long timestamp = Long.parseLong( matcher.group( 1 ) ); // Timestamp
			final long traceId = Long.parseLong( matcher.group( 2 ) ); // Trace Id

			// Order index is ignored
			final String methodName = matcher.group( 3 ); // Method name
			final String clazz = matcher.group( 4 ); // Class name
			final String exception = matcher.group( 5 );

			final MethodCall methodCall = repository.processAfterOperationEvent( timestamp, traceId, methodName, clazz );
			if ( methodCall != null ) {
				methodCall.setException( exception );
			}
		}
	}

	private void readTraceMetadata( final String aAsciiContent ) {
		final Matcher matcher = ASCII_FILE_TRACE_METADATA_PATTERN.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			// Logging timestamp is ignored
			final long traceId = Long.parseLong( matcher.group( 1 ) ); // Trace Id
			// Thread id and session id is ignored
			final String host = matcher.group( 2 ); // Hostname
			// The rest is ignored

			repository.processTraceMetadata( traceId, host );
		}
	}

	private void readKiekerMetadataRecord( final String aAsciiContent ) {
		final Matcher matcher = ASCII_FILE_KIEKER_METADATA_RECORD_PATTERN.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			final String timeUnitName = matcher.group( 1 ); // Time unit
			// The rest is ignored

			repository.processSourceTimeUnit( timeUnitName );
		}
	}

}
