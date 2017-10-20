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

package kieker.diagnosis.service.data.reader;

import java.io.File;
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

import kieker.common.record.flow.trace.TraceMetadata;
import kieker.common.record.flow.trace.operation.AfterOperationEvent;
import kieker.common.record.flow.trace.operation.AfterOperationFailedEvent;
import kieker.common.record.flow.trace.operation.BeforeOperationEvent;
import kieker.common.record.misc.KiekerMetadataRecord;
import kieker.diagnosis.architecture.monitoring.MonitoringProbe;
import kieker.diagnosis.service.data.MethodCall;

/**
 * This is a reader to import files written with Kieker's ascii file writer. It exchanges readability and maintainability for performance and reduced memory
 * consumption.
 *
 * @author Nils Christian Ehmke
 */
public final class AsciiFileReader extends Reader {

	private static final Pattern cvAsciiFileEntryPattern = Pattern.compile( "\\$(\\d*);\\d*;(.*)" );
	private static final Pattern cvAsciiFileBeforeOperationEventPattern = Pattern.compile( "(\\d*);(-?\\d*);\\d*;([^;]*);([^;]*)" );
	private static final Pattern cvAsciiFileAfterOperationEventPattern = Pattern.compile( "(\\d*);(-?\\d*).*" );
	private static final Pattern cvAsciiFileAfterOperationFailedEventPattern = Pattern.compile( "(\\d*);(-?\\d*);\\d*;[^;]*;[^;]*;(.*)" );
	private static final Pattern cvAsciiFileTraceMetadataPattern = Pattern.compile( "(-?\\d*);\\d*;[^;]*;([^;]*).*" );
	private static final Pattern cvAsciiFileKiekerMetadataRecordPattern = Pattern.compile( "[^;]*;[^;]*;[^;]*;[^;]*;[^;]*;([^;]*).*" );

	private int ivBeforeOperationEventKey;
	private int ivAfterOperationEventKey;
	private int ivAfterOperationFailedEventKey;
	private int ivTraceMetadataKey;
	private int ivKiekerMetadataRecordKey;

	private IntObjectMap<String> ivStringMapping;

	public AsciiFileReader( final TemporaryRepository aTemporaryRepository ) {
		super( aTemporaryRepository );
	}

	@Override
	public void readFromDirectory( final File aDirectory ) throws IOException {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "readFromDirectory(java.io.File)" );

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
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "readNonRecursiveFromDirectory(java.io.File)" );

		try {
			ivTemporaryRepository.clearBeforeNextDirectory( );

			ivStringMapping = readMappingFile( aDirectory );
			extractImportKeysFromMapping( );

			final File[] asciiFiles = findFilesWithExtension( aDirectory, ".dat" );
			for ( final File asciiFile : asciiFiles ) {
				readAsciiFile( asciiFile.toPath( ) );
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

	private void readAsciiFile( final Path aAsciiFile ) throws IOException {
		final MonitoringProbe probe = new MonitoringProbe( getClass( ), "readAsciiFile(java.nio.file.Path)" );

		try {
			final List<String> lines = Files.readAllLines( aAsciiFile, Charset.forName( "UTF-8" ) );

			for ( final String line : lines ) {
				final Matcher lineMatcher = cvAsciiFileEntryPattern.matcher( line );
				if ( lineMatcher.matches( ) ) {
					final String recordKeyStr = lineMatcher.group( 1 );
					final String remainingLine = lineMatcher.group( 2 );

					final int recordKey = Integer.parseInt( recordKeyStr );

					if ( recordKey == ivBeforeOperationEventKey ) {
						readBeforeOperationEvent( remainingLine );
					} else if ( recordKey == ivAfterOperationEventKey ) {
						readAfterOperationEvent( remainingLine );
					} else if ( recordKey == ivAfterOperationFailedEventKey ) {
						readAfterOperationFailedEvent( remainingLine );
					} else if ( recordKey == ivTraceMetadataKey ) {
						readTraceMetadata( remainingLine );
					} else if ( recordKey == ivKiekerMetadataRecordKey ) {
						readKiekerMetadataRecord( remainingLine );
					} else {
						// Skip the line
						ivTemporaryRepository.processIgnoredRecord( );
					}
				}
			}

			ivTemporaryRepository.processProcessedBytes( Files.size( aAsciiFile ) );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private void readBeforeOperationEvent( final String aAsciiContent ) {
		final Matcher matcher = cvAsciiFileBeforeOperationEventPattern.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			final long timestamp = Long.parseLong( matcher.group( 1 ) ); // Timestamp
			final long traceId = Long.parseLong( matcher.group( 2 ) ); // Trace Id
			// Order index is ignored
			final String methodName = matcher.group( 3 ); // Method name
			final String clazz = matcher.group( 4 ); // Class name

			ivTemporaryRepository.processBeforeOperationEvent( timestamp, traceId, methodName, clazz );
		}
	}

	private void readAfterOperationEvent( final String aAsciiContent ) {
		final Matcher matcher = cvAsciiFileAfterOperationEventPattern.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			// Logging timestamp is ignored
			final long timestamp = Long.parseLong( matcher.group( 1 ) ); // Timestamp
			final long traceId = Long.parseLong( matcher.group( 2 ) ); // Trace Id
			// The rest is ignored

			ivTemporaryRepository.processAfterOperationEvent( timestamp, traceId );
		}
	}

	private void readAfterOperationFailedEvent( final String aAsciiContent ) {
		final Matcher matcher = cvAsciiFileAfterOperationFailedEventPattern.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			// Logging timestamp is ignored
			final long timestamp = Long.parseLong( matcher.group( 1 ) ); // Timestamp
			final long traceId = Long.parseLong( matcher.group( 2 ) ); // Trace Id
			final String exception = matcher.group( 3 );
			// The rest is ignored

			final MethodCall methodCall = ivTemporaryRepository.processAfterOperationEvent( timestamp, traceId );
			if ( methodCall != null ) {
				methodCall.setException( exception );
			}
		}
	}

	private void readTraceMetadata( final String aAsciiContent ) {
		final Matcher matcher = cvAsciiFileTraceMetadataPattern.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			// Logging timestamp is ignored
			final long traceId = Long.parseLong( matcher.group( 1 ) ); // Trace Id
			// Thread id and session id is ignored
			final String host = matcher.group( 2 ); // Hostname
			// The rest is ignored

			ivTemporaryRepository.processTraceMetadata( traceId, host );
		}
	}

	private void readKiekerMetadataRecord( final String aAsciiContent ) {
		final Matcher matcher = cvAsciiFileKiekerMetadataRecordPattern.matcher( aAsciiContent );
		if ( matcher.matches( ) ) {
			final String timeUnitName = matcher.group( 1 ); // Time unit
			// The rest is ignored

			ivTemporaryRepository.processSourceTimeUnit( timeUnitName );
		}
	}

	@Override
	public boolean shouldBeExecuted( final File aDirectory ) throws IOException {
		final List<File> directoriesToBeRead = findDirectoriesToBeRead( aDirectory );
		return !directoriesToBeRead.isEmpty( );
	}

	private List<File> findDirectoriesToBeRead( final File aDirectory ) throws IOException {
		return findDirectoriesContainingFilesWithExtensions( aDirectory, ".map", ".dat" );
	}

}
